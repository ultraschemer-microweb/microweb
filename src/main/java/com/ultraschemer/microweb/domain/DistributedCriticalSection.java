package com.ultraschemer.microweb.domain;

import com.google.common.base.Throwables;
import com.ultraschemer.microweb.domain.error.CriticalSectionAcquiringFailureException;
import com.ultraschemer.microweb.domain.error.CriticalSectionExitFailureException;
import com.ultraschemer.microweb.domain.error.UnableToEnterCriticalSectionException;
import com.ultraschemer.microweb.domain.error.UnableToExitCriticalSectionException;
import com.ultraschemer.microweb.entity.LockControl;
import com.ultraschemer.microweb.persistence.EntityUtil;
import com.ultraschemer.microweb.utils.MachineIdentification;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.PersistenceException;
import java.util.Calendar;
import java.util.Date;

/**
 * In a distributed system, to deal with sequential operations can be tricky. The two most used ways to deal with
 * such kinds of operations are:
 *
 * <ul>
 *     <li>Create sequential queues to deal with them.</li>
 *     <li>ACID database transactions.</li>
 * </ul>
 *
 * The first approach, sometimes, is overly complex to solve simple sequential operations. The second approach ensures
 * update and writing consistency, but it's not enough to ensure sequentiality when we need read and write operations
 * in an atomic operation - sometimes with ordering needed to be ensured.
 *
 * To deal with ording sequential operations on a distributed system, this simple DistributedCriticalSection is
 * introduced. Its performance is poor, since it is bound to relational database operations and non-occupied loops,
 * but for the majority of simple cases, it's enough. For more complex scenarios, use distributed queue systems, as
 * ZeroMQ, or AMQP.
 *
 */
public class DistributedCriticalSection {
    private int expiration;
    private String name;
    private int wait;
    private boolean raiseException;
    private String owner;
    private long sleepTime;

    /**
     * Constructor using default configurations.
     */
    public DistributedCriticalSection(String name) {
        this.expiration = 2;
        this.wait = 5;
        this.raiseException = false;
        this.name = name;
        this.owner = MachineIdentification.getFullTreadIdentification();

        // By default, eight tries per second to acquire the critical section:
        this.sleepTime = 125;
    }

    /**
     * Get the sleep time between critical section acquiring tries.
     * @return
     */
    public long getSleepTime() {
        return sleepTime;
    }

    /**
     * Set the sleep time between critical section acquiring tries. The smaller this value, more processing and database
     * loads will be required, but the locking and unlocking process will be more refined.
     *
     * @param sleepTime Time between acquiring tries.
     */
    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    /**
     * Get the critical section expiration time, in seconds.
     *
     * @return The maximum amount of time which the critical section will block.
     */
    public int getExpiration() {
        return expiration;
    }

    /**
     *
     * Set the critical section expiration time, in seconds.
     *
     * @param expiration The amount of time which the acquired critical section will lock, to other processes and threads if it's acquired.
     */
    public void setExpiration(int expiration) {
        this.expiration = expiration;
    }

    /**
     * Get the critical section name.
     *
     * @return The critical section name.
     */
    public String getName() {
        return name;
    }

    /**
     * Attribue the name of critical section.
     *
     * @param name The name of critical to be acquired, or which name a new critical section will have, if acquired.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The maximum waiting time to acquire the critical section.
     * @return O valor do tempo de espera.
     */
    public int getWait() {
        return wait;
    }

    /**
     * Set the maximum waiting time to acquire the critical section.
     * @param wait The waiting time value.
     */
    public void setWait(int wait) {
        this.wait = wait;
    }

    /**
     * Get the flag which says if the critical section object will raise an exception if it's not acquired successfully.
     * @return If "true", then the object will raise an exception if it's not acquired. If "false", the acquiring try will only return false.
     */
    public boolean isRaiseException() {
        return raiseException;
    }

    /**
     * Set the flag to raise exceptions or not if there is a failure acquiring the critical section.
     * @param raiseException
     */
    public void setRaiseException(boolean raiseException) {
        this.raiseException = raiseException;
    }

    /**
     * Method executed to acquire a critical section. This blocks until the critical section is acquired, or until the
     * expiration time is reached.
     *
     * @return If raiseException = false, the return value is false if the critical section is not acquired. If true, the critical section has been acquired.
     */
    public boolean enterCriticalSection() throws CriticalSectionAcquiringFailureException, UnableToEnterCriticalSectionException {
        try(Session session = EntityUtil.openTransactionSession()) {
            Transaction transaction = session.getTransaction();

            Calendar waitDate = Calendar.getInstance();
            waitDate.add(Calendar.SECOND, wait);

            // Try to create the critical section register in database and ignore any kind of error:
            LockControl control = new LockControl();
            control.setName(name);
            control.setStatus("F");
            control.setExpiration(new Date());
            control.setOwner(owner);

            try {
                session.persist(control);
                transaction.commit();
            } catch (PersistenceException pe) {
                // Do nothing, because, in case of PersistenceException, it means the lock register already exists and
                // it doesn't need to be created again.
                pe.printStackTrace();
            }


            boolean exit = false;
            while (!exit) {
                Calendar expirationDate = Calendar.getInstance();

                expirationDate.add(Calendar.SECOND, expiration);

                if (!transaction.isActive()) {
                    transaction.begin();
                }

                String queryText = "Update LockControl set status = :status, expiration = :expiration, owner = :owner " +
                        "Where (name = :name and status = :free_status) or (name = :name and expiration < :current_date)";
                int updates =
                        session.createQuery(queryText)
                                .setParameter("status", "L")
                                .setParameter("free_status", "F")
                                .setParameter("expiration", expirationDate.getTime())
                                .setParameter("current_date", new Date())
                                .setParameter("name", name)
                                .setParameter("owner", owner)
                                .executeUpdate();
                transaction.commit();

                if (updates > 0) {
                    session.close();
                    return true;
                }

                if (!Calendar.getInstance().after(waitDate)) {
                    // By default, eight tries per second to acquire a lock. If sleepTime is changed, this amount of tries
                    // can vary.
                    try {
                        Thread.sleep(getSleepTime());
                    } catch (InterruptedException e) {
                        // Do nothing, because this error is irrelevant to this feature context.
                    }
                } else {
                    // It has been not possible to acquire lock, in the waiting time, so don't try anymore:
                    exit = true;
                }
            }
        } catch(PersistenceException pe) {
            throw new UnableToEnterCriticalSectionException("It wasn't possible to enter in the critical section: " +
                    pe.getLocalizedMessage() + "\nStack Trace: " + Throwables.getStackTraceAsString(pe));
        }

        if(raiseException) {
            String message = "It has been not possible to acquire critical section with name: " + name;
            throw new CriticalSectionAcquiringFailureException(message);
        }

        return false;
    }

    /**
     * Exit from critical section, releasing it to other process.
     * @return If raiseException = true, then if the return is false, it means the critical section has a failure on it's release. Otherwise, the critical section has been exited correctly.
     */
    public boolean exitCriticalSection() throws CriticalSectionExitFailureException, UnableToExitCriticalSectionException {
        try(Session session = EntityUtil.openTransactionSession()) {
            String queryText = "Update LockControl set status = :status, expiration = :expiration " +
                    " where name = :name and status = :blocked_status and owner = :owner";
            int updates =
                    session.createQuery(queryText)
                            .setParameter("status", "F")
                            .setParameter("expiration", new Date())
                            .setParameter("name", name)
                            .setParameter("blocked_status", "L")
                            .setParameter("owner", owner)
                            .executeUpdate();

            session.getTransaction().commit();

            if (updates > 0) {
                return true;
            }

            if (raiseException) {
                String message = "It has been not possible to release critical section with name: " +
                        name +
                        ". Probably the critical section already expired and has not been acquired.";
                throw new CriticalSectionExitFailureException(message);
            }
        } catch(PersistenceException pe) {
            throw new UnableToExitCriticalSectionException("It wasn't possible to exit the critical section :" +
                    pe.getLocalizedMessage() + "\nStack Trace: " + Throwables.getStackTraceAsString(pe));
        }

        return false;
    }
}

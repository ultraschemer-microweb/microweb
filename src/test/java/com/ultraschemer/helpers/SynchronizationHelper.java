package com.ultraschemer.helpers;

import com.ultraschemer.microweb.persistence.EntityUtil;
import org.hibernate.Session;

public class SynchronizationHelper {
    public static void clearRuntime() {
        Session session = EntityUtil.openTransactionSession();
        session.createQuery("delete from Runtime").executeUpdate();
        session.getTransaction().commit();
    }

    public static void clearLockControl() {
        Session session = EntityUtil.openTransactionSession();
        session.createQuery("delete from LockControl").executeUpdate();
        session.getTransaction().commit();
    }
}

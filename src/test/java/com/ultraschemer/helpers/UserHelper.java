package com.ultraschemer.helpers;

import com.github.javafaker.Faker;
import com.ultraschemer.microweb.entity.Person;
import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.persistence.EntityUtil;
import com.ultraschemer.microweb.utils.Security;
import org.hibernate.Session;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Locale;

public class UserHelper {
    public static User generate(Person p, String password)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        User u = new User();

        Faker faker =  new Faker(new Locale("pt-BR"));
        u.setName(faker.name().firstName());
        u.setAlias(faker.name().lastName());
        u.setPassword(Security.hashade(password));
        u.setPersonId(p.getId());
        u.setStatus("new");

        return u;
    }

    public static User generateAndPersist(Person p, String password)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        User u = generate(p, password);

        Session session = EntityUtil.openTransactionSession();
        session.save(u);
        session.getTransaction().commit();

        return u;
    }

    public static void clear(User u) {
        Session session = EntityUtil.openTransactionSession();
        session.delete(u);
        session.getTransaction().commit();
    }

    public static void clearAll() {
        Session session = EntityUtil.openTransactionSession();
        session.createQuery("delete from User_Role").executeUpdate();
        session.createQuery("delete from Role").executeUpdate();
        session.createQuery("delete from User").executeUpdate();
        session.getTransaction().commit();
    }
}

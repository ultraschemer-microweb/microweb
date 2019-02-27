package com.ultraschemer.helpers;

import com.github.javafaker.Faker;
import com.ultraschemer.microweb.entity.Person;
import com.ultraschemer.microweb.persistence.EntityUtil;
import org.hibernate.Session;

import java.util.Locale;

public class PersonHelper {
    public static Person generate() {
        Person p = new Person();

        Faker faker =  new Faker(new Locale("pt-BR"));
        p.setName(faker.name().firstName().toUpperCase());
        p.setBirthday(faker.date().birthday());
        p.setStatus("alive");

        return p;
    }

    public static Person generateAndPersist() {
        Person p = generate();

        try(Session session = EntityUtil.openTransactionSession()) {
            session.save(p);
            session.getTransaction().commit();
        }

        return p;
    }

    public static void clear(Person p) {
        try(Session session = EntityUtil.openTransactionSession()) {
            session.delete(p);
            session.getTransaction().commit();
        }
    }

    public static void clearAll() {
        try(Session session = EntityUtil.openTransactionSession()) {
            session.createQuery("delete from Person").executeUpdate();
            session.getTransaction().commit();
        }
    }
}

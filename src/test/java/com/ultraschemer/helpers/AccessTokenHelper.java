package com.ultraschemer.helpers;

import org.hibernate.Session;
import com.ultraschemer.microweb.persistence.EntityUtil;

public class AccessTokenHelper {
    public static void clearAll() {
        Session session = EntityUtil.openTransactionSession();
        session.createQuery("delete from AccessToken").executeUpdate();
        session.getTransaction().commit();
    }
}

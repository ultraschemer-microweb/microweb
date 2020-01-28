package com.ultraschemer.tests;

import com.ultraschemer.microweb.domain.CentralUserRepositoryManagement;
import com.ultraschemer.microweb.domain.bean.CentralUserDataObject;
import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.persistence.EntityUtil;
import org.hibernate.Session;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;

/**
 * Testing class for the reimplementation of user management over KeyCloak, instead of internal user management
 * implementation.
 */
public class CentralUserRepositoryManagementTest {
    static {
        EntityUtil.initialize();
    }

    @BeforeClass
    public static void init() throws StandardException
    {
        // Initialize wellKnown method data, to optimize the tests
        CentralUserRepositoryManagement.wellKnown();
    }

    @After
    public void cleanUp() {
        try(Session s = EntityUtil.openTransactionSession()) {
            // Delete user data and permissions
            s.createQuery("delete from User_EmailAddress").executeUpdate();
            s.createQuery("delete from User_Role").executeUpdate();
            s.createQuery("delete from EmailAddress").executeUpdate();
            s.createQuery("delete from Role").executeUpdate();
            s.createQuery("delete from User").executeUpdate();
        }
    }

    /**
     * This test create and update local user data obtained from central KeyCloak user management system.
     * The only relevant data in this test are user basic information and his/her roles.
     */
    @Test
    public void evaluateUserAndRolesEmptyDatabase() throws Throwable {
        // Create user with previously unknown roles:
        CentralUserDataObject userDataObject = new CentralUserDataObject();
        userDataObject.setCentralControlId(UUID.fromString("56ecb69e-2675-43c6-be83-073faad6a580"));
        userDataObject.setAlias("User Name"); // Given name + Family Name
        userDataObject.setGivenName("User"); // Given name
        userDataObject.setFamilyName("Name"); // Family name
        userDataObject.setName("user"); // Preferred user name
        userDataObject.seteMail("user@test.com");
        userDataObject.seteMailStatus("verified"); // If e-mail is verified. Otherwise, set it "unverified"

        List<String> userRoles = Arrays.asList("bank_client", "trading_platform_client", "bank_manager");

        User u = CentralUserRepositoryManagement.evaluateUserAndRoles(userDataObject, userRoles);
        assertNotNull(u);

        // Create a new user with known roles and unknown roles:
        CentralUserDataObject userDataObject2 = new CentralUserDataObject();
        userDataObject2.setCentralControlId(UUID.fromString("13aca611-4d96-4d3a-b31e-7f4b9bcc9f4d"));
        userDataObject2.setAlias("User2 Name2"); // Given name + Family Name
        userDataObject2.setGivenName("User2"); // Given name
        userDataObject2.setFamilyName("Name2"); // Family name
        userDataObject2.setName("user2"); // Preferred user name
        userDataObject2.seteMail("user2@test.com");
        userDataObject2.seteMailStatus("verified"); // If e-mail is verified. Otherwise, set it "unverified"

        List<String> userRoles2 = Arrays.asList("bank_client", "trading_platform_client", "user_manager");

        User u2 = CentralUserRepositoryManagement.evaluateUserAndRoles(userDataObject2, userRoles2);
        assertNotNull(u2);

        // Unlink roles from user:
        userRoles = Arrays.asList("bank_client", "trading_platform_client");
        u = CentralUserRepositoryManagement.evaluateUserAndRoles(userDataObject, userRoles);
        assertNotNull(u);

        // Register user with no e-mail address:
        CentralUserDataObject userDataObject3 = new CentralUserDataObject();
        userDataObject3.setCentralControlId(UUID.fromString("be25edc2-629d-4430-9fb1-f7e100a1e6db"));
        userDataObject3.setAlias("User3 Name3"); // Given name + Family Name
        userDataObject3.setGivenName("User3"); // Given name
        userDataObject3.setFamilyName("Name3"); // Family name
        userDataObject3.setName("user3"); // Preferred user name

        List<String> userRoles3 = Collections.singletonList("bank_client");
        User u3 = CentralUserRepositoryManagement.evaluateUserAndRoles(userDataObject3, userRoles3);
        assertNotNull(u3);
    }
}



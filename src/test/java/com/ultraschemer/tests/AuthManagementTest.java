package com.ultraschemer.tests;

import com.github.javafaker.Faker;
import com.ultraschemer.helpers.PersonHelper;
import com.ultraschemer.microweb.entity.Person;
import org.junit.*;
import com.ultraschemer.helpers.AccessTokenHelper;
import com.ultraschemer.helpers.UserHelper;
import com.ultraschemer.microweb.domain.AuthManagement;
import com.ultraschemer.microweb.domain.bean.AuthenticationData;
import com.ultraschemer.microweb.domain.bean.AuthorizationData;
import com.ultraschemer.microweb.domain.error.UnauthorizedException;
import com.ultraschemer.microweb.entity.User;

import java.util.Locale;

import static org.junit.Assert.*;

public class AuthManagementTest {
    private static Person person;
    private static User user;
    private static String USER_PASSWORD = "USER_PASSWORD";

    @BeforeClass
    public static void fixtureSetup() throws Exception {
        // Cria, aqui, o usuário que vai ser validado dentro do sistema.
        person = PersonHelper.generateAndPersist();
        user = UserHelper.generateAndPersist(person, USER_PASSWORD);
    }

    @AfterClass
    public static void fixtureTearDown() {
        // Remove os dados usados para testar a autenticação.
        AccessTokenHelper.clearAll();
        UserHelper.clearAll();
        PersonHelper.clearAll();
    }

    @Test
    public void testAuthenticationOfExistentUser() throws Exception {
        AuthenticationData authenticationData = new AuthenticationData();
        authenticationData.setName(user.getName());
        authenticationData.setPassword(USER_PASSWORD);

        // Começa testando a autenticação de usuário
        AuthorizationData authorizationData = AuthManagement.authenticate(authenticationData);

        assertNotNull(authorizationData);
        assertNotNull(authorizationData.getAccessToken());
        assertNotEquals(0, authorizationData.getTtl());

        // Então, verifica se a autorização está ocorrendo corretamente:
        User authorizedUser = AuthManagement.authorize(authorizationData.getAccessToken());

        assertNotNull(authorizedUser);
        assertEquals(user.getName(), authorizedUser.getName());

        // Então desautoriza o usuário (logoff) e verifica se o usuário não consegue obter dados de autorização:
        AuthManagement.unauthorize(authorizationData.getAccessToken());

        try {
            AuthManagement.authorize(authorizationData.getAccessToken());
            fail("Não pode chegar aqui.");
        } catch (UnauthorizedException ue) {
            assertTrue(true);
        }
    }

    @Test
    public void testAuthenticationOfExistentUserWrongPassword() {
        System.out.println("testAuthorization");
    }

    @Test
    public void testAuthenticationOfInexistentUser() throws Exception {
        Faker faker = new Faker(new Locale("pt-BR"));
        AuthenticationData authData = new AuthenticationData();
        authData.setName(faker.name().firstName().toUpperCase());
        authData.setPassword(faker.lorem().word());

        try {
            AuthManagement.authenticate(authData);
            fail("Não pode chegar aqui.");
        } catch (UnauthorizedException ue) {
            assertTrue(true);
        }
    }
}

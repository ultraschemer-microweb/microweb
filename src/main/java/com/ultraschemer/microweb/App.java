package com.ultraschemer.microweb;

import com.ultraschemer.microweb.controller.*;
import com.ultraschemer.microweb.domain.RoleManagement;
import com.ultraschemer.microweb.domain.UserManagement;
import com.ultraschemer.microweb.persistence.EntityUtil;
import com.ultraschemer.microweb.vertx.WebAppVerticle;
import io.vertx.core.http.HttpMethod;

import java.util.HashSet;

/*
 * Entry point principal da aplicação:
 */
public class App extends WebAppVerticle {
    static {
        // Initialize default entity util here:
        EntityUtil.initialize();
    }

    @Override
    public void initialization() {
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Use this block only if you'll not use OpenID with KeyCloak:
        //

        // Verify the default user and the default role:
        UserManagement.initializeRoot();

        // Initialize additional roles (if not using KeyCloak):
        RoleManagement.initializeDefault();

        // Registra os filtros de inicialização:
        registerFilter(new AuthorizationFilter());

        //
        // End of Block
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Use this block only if you're disabling CORS - or customize it if you want to customize CORS handling:
        //

        HashSet<String> allowedHeaders = new HashSet<>();

        // Standard request fields
        allowedHeaders.add("A-IM");
        allowedHeaders.add("Accept");
        allowedHeaders.add("Accept-Charset");
        allowedHeaders.add("Accept-Datetime");
        allowedHeaders.add("Accept-Encoding");
        allowedHeaders.add("Accept-Language");
        allowedHeaders.add("Access-Control-Request-Method");
        allowedHeaders.add("Access-Control-Request-Headers");
        allowedHeaders.add("Authorization");
        allowedHeaders.add("Cache-Control");
        allowedHeaders.add("Connection");
        allowedHeaders.add("Content-Length");
        allowedHeaders.add("Content-MD5");
        allowedHeaders.add("Content-Type");
        allowedHeaders.add("Cookie");
        allowedHeaders.add("Date");
        allowedHeaders.add("Expect");
        allowedHeaders.add("Forwarded");
        allowedHeaders.add("From");
        allowedHeaders.add("Host");
        allowedHeaders.add("HTTP2-Settings");
        allowedHeaders.add("If-Match");
        allowedHeaders.add("If-Modified-Since");
        allowedHeaders.add("If-None-Match");
        allowedHeaders.add("If-Range");
        allowedHeaders.add("If-Unmodified-Since");
        allowedHeaders.add("Max-Forwards");
        allowedHeaders.add("Origin");
        allowedHeaders.add("Pragma");
        allowedHeaders.add("Proxy-Authorization");
        allowedHeaders.add("Range");
        allowedHeaders.add("Referer");
        allowedHeaders.add("TE");
        allowedHeaders.add("Trailer");
        allowedHeaders.add("Transfer-Encoding");
        allowedHeaders.add("User-Agent");
        allowedHeaders.add("Upgrade");
        allowedHeaders.add("Via");
        allowedHeaders.add("Warning");

        // Common non-standard request fields
        allowedHeaders.add("Access-Control-Allow-Origin");
        allowedHeaders.add("Upgrade-Insecure-Requests");
        allowedHeaders.add("X-Requested-With");
        allowedHeaders.add("DNT");
        allowedHeaders.add("X-Forwarded-For");
        allowedHeaders.add("X-Forwarded-Host");
        allowedHeaders.add("X-Forwarded-Proto");
        allowedHeaders.add("Front-End-Https");
        allowedHeaders.add("X-Http-Method-Override");
        allowedHeaders.add("X-ATT-DeviceId");
        allowedHeaders.add("X-Wap-Profile");
        allowedHeaders.add("Proxy-Connection");
        allowedHeaders.add("X-UIDH");
        allowedHeaders.add("X-Csrf-Token");
        allowedHeaders.add("X-Request-ID");
        allowedHeaders.add("X-Correlation-ID");
        allowedHeaders.add("Save-Data");

        HashSet<HttpMethod> allowedMethods = new HashSet<>();
        allowedMethods.add(HttpMethod.GET);
        allowedMethods.add(HttpMethod.POST);
        allowedMethods.add(HttpMethod.PUT);
        allowedMethods.add(HttpMethod.DELETE);

        // Enable CORS:
        setCors("*", allowedMethods, allowedHeaders);

        //
        // End of block
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Use this block only if you are using Oauth2 features:
        //

        // OAuth2 specific controllers:
        registerController(HttpMethod.GET, "/v0/finish-consent", new FinishConsentController());
        registerController(HttpMethod.GET, "/v0/wellKnown", new WellKnownController());


        // Register the controllers:
        registerController(HttpMethod.POST, "/v0/login", new LoginController());
        registerController(HttpMethod.GET, "/v0/logoff", new LogoffController());

        //
        // End of block
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// User management calls - The controllers are only useful if you won't use OpenID with KeyCloak.
        // The business rules these controllers expose can be useful even if KeyCloak is used

        registerController(HttpMethod.GET, "/v0/user/:userIdOrName", new OtherUsersController());
        registerController(HttpMethod.GET, "/v0/user", new UserController());
        registerController(HttpMethod.GET, "/v0/role", new RoleController());
        registerController(HttpMethod.GET, "/v0/role/:roleIdOrName", new RoleController());
        registerController(HttpMethod.PUT, "/v0/user/password", new UserPasswordUpdateController());
        registerController(HttpMethod.PUT, "/v0/user/alias", new UserAliasUpdateController());
        registerController(HttpMethod.POST, "/v0/user", new UserCreationController());
        registerController(HttpMethod.GET, "/v0/users", new UserListController());
        registerController(HttpMethod.GET, "/v0/users/:userIdOrName",new UserListController());

        //
        // End of Block
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // Registra os filtros de finalização:
        // Bem... eles ainda não existem...
    }
}

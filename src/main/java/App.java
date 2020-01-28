import com.ultraschemer.microweb.controller.*;
import com.ultraschemer.microweb.domain.RoleManagement;
import com.ultraschemer.microweb.domain.UserManagement;
import com.ultraschemer.microweb.persistence.EntityUtil;
import com.ultraschemer.microweb.vertx.WebAppVerticle;
import io.vertx.core.http.HttpMethod;

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
        // Verify the default user and the default role:
        UserManagement.initializeRoot();

        // Initialize additional roles (if not using KeyCloak):
        RoleManagement.initializeDefault();

        // Registra os filtros de inicialização:
        registerFilter(new AuthorizationFilter());

        // Registra os controllers:
        registerController(HttpMethod.POST, "/v0/login", new LoginController());
        registerController(HttpMethod.GET, "/v0/logoff", new LogoffController());

		// Chamadas de gerenciamento de usuário:
        registerController(HttpMethod.GET, "/v0/user/:userIdOrName", new OtherUsersController());
        registerController(HttpMethod.GET, "/v0/user", new UserController());
        registerController(HttpMethod.GET, "/v0/role", new RoleController());
        registerController(HttpMethod.GET, "/v0/role/:roleIdOrName", new RoleController());
        registerController(HttpMethod.PUT, "/v0/user/password", new UserPasswordUpdateController());
        registerController(HttpMethod.PUT, "/v0/user/alias", new UserAliasUpdateController());
        registerController(HttpMethod.POST, "/v0/user", new UserCreationController());
        registerController(HttpMethod.GET, "/v0/users", new UserListController());
        registerController(HttpMethod.GET, "/v0/users/:userIdOrName",new UserListController());
		
        // Registra os filtros de finalização:
        // Bem... eles ainda não existem...
    }
}

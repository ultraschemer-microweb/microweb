import com.ultraschemer.microweb.controller.AuthorizationFilter;
import com.ultraschemer.microweb.controller.LoginController;
import com.ultraschemer.microweb.controller.LogoffController;
import com.ultraschemer.microweb.domain.UserManagement;
import com.ultraschemer.microweb.vertx.WebAppVerticle;

/*
 * Entry point principal da aplicação:
 */
public class App extends WebAppVerticle {
    @Override
    public void initialization() {
        // Verify the default user and the default role:
        UserManagement.initializeRoot();

        // Registra os filtros de inicialização:
        registerFilter(new AuthorizationFilter());

        // Registra os controllers:
        registerController(HttpMethod.POST, "/v0/login", new LoginController());
        registerController(HttpMethod.GET, "/v0/logoff", new LogoffController());

        // Registra os filtros de finalização:
        // Bem... eles ainda não existem...
    }
}

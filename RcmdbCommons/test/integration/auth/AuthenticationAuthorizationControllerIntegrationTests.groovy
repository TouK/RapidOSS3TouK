package auth

import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import org.springframework.context.ApplicationContext
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.plugins.web.filters.CompositeInterceptor
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.codehaus.groovy.grails.plugins.web.filters.FilterToHandlerAdapter
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.jsecurity.SecurityUtils
import com.ifountain.rcmdb.test.util.IntegrationTestUtils
import org.jsecurity.mgt.SecurityManager

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 23, 2009
* Time: 11:24:42 AM
* To change this template use File | Settings | File Templates.
*/
class AuthenticationAuthorizationControllerIntegrationTests extends RapidCmdbIntegrationTestCase{
    def transactional = false

    public void testAuth()
    {
        CompositeInterceptor interceptor = ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT).getBean('filterInterceptor')
        MockHttpServletRequest req = new MockHttpServletRequest(ServletContextHolder.getServletContext(), "post", "/script/run/HelloWorld")
        MockHttpServletResponse resp = new MockHttpServletResponse();
        Map<String, FilterToHandlerAdapter> securityFilterHandlers = new HashMap<String, FilterToHandlerAdapter>()
        interceptor.handlers.each{FilterToHandlerAdapter adapter->
            if(adapter.getConfigClass().class.name == "SecurityFilters")
            {
                securityFilterHandlers[adapter.getFilterConfig().name] = adapter;
            }
        };
        assertEquals (4, securityFilterHandlers.size());
        FilterToHandlerAdapter authenticationAdapter = securityFilterHandlers["authentication"]; 
        assertEquals ("/**", authenticationAdapter.getUriPattern());
        assertNotNull(authenticationAdapter.getFilterConfig().before);
        assertNull(authenticationAdapter.getFilterConfig().after);
        assertNull(authenticationAdapter.getFilterConfig().afterView);

        String userPassword = "password";
        def userController = new RsUserController();
        IntegrationTestUtils.resetController (userController);
        userController.params["username"] = "user1"
        userController.params["password1"] = userPassword
        userController.params["password2"] = userPassword
        userController.save();
        def rsUser = RsUser.get(username:"user1").update(groups:[]);
        assertFalse (rsUser.hasErrors());


        def authCont = new AuthController();
        IntegrationTestUtils.resetController (authCont);
        authCont.jsecSecurityManager = ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT).getBean ("jsecSecurityManager");
        authCont.logout();
        IntegrationTestUtils.resetController (authCont);
        authCont.params["login"] = rsUser.username
        authCont.params["password"] = userPassword
        authCont.signIn();

        IntegrationTestUtils.resetController (authCont);
        assertFalse(authenticationAdapter.preHandle (req, resp, null));
        IntegrationTestUtils.resetController (authCont);
        authCont.logout();
        IntegrationTestUtils.resetController (authCont);
        authCont.params["login"] = RsUser.RSADMIN
        authCont.params["password"] = RsUser.DEFAULT_PASSWORD;
        authCont.signIn();
    }

//    public void testControllerAuthorization()
//    {
//        fail("Implement later");
//        List controllers = ApplicationHolder.application.getControllerClasses();
//        Map controllersAuthorization = [];
//        int numberOfConfiguredControllers = 0;
//        controllersAuthorization.each{Map controllerAuthorizationConfiguration,
//
//        }
//        assertEquals (controllers.size());
//    }
}
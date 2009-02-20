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
    static transactional = false

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
        
        String username = "user1";
        String userPassword = "password";
        RsUser rsUser = createUser(username, userPassword);

        loginAs(rsUser.username, userPassword);
        assertFalse(authenticationAdapter.preHandle (req, resp, null));
        loginAs (RsUser.RSADMIN, RsUser.DEFAULT_PASSWORD);
    }

    private createUser(String username, String userPassword)
    {
        def userController = new RsUserController();
        IntegrationTestUtils.resetController (userController);
        userController.params["username"] = username
        userController.params["password1"] = userPassword
        userController.params["password2"] = userPassword
        userController.save();
        def rsUser = RsUser.get(username:username).update(groups:[]);
        assertFalse (rsUser.hasErrors());
        return rsUser;
    }

    private loginAs(String username, String password)
    {
        def authCont = new AuthController();
        IntegrationTestUtils.resetController (authCont);
        authCont.jsecSecurityManager = ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT).getBean ("jsecSecurityManager");
        authCont.logout();
        IntegrationTestUtils.resetController (authCont);
        authCont.params["login"] = username
        authCont.params["password"] = password
        authCont.signIn();
        IntegrationTestUtils.resetController (authCont);
    }
}
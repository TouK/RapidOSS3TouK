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
        assertNotNull(authenticationAdapter.getFilterConfig().before);
        assertNull(authenticationAdapter.getFilterConfig().after);
        assertNull(authenticationAdapter.getFilterConfig().afterView);
        
        String username = "user1";
        String userPassword = "password";
        RsUser rsUser = createUser(username, userPassword);

        loginAs(rsUser.username, userPassword);
        assertFalse(authenticationAdapter.preHandle (req, resp, null));
        loginAs (RsUser.RSADMIN, RsUser.DEFAULT_PASSWORD);
        assertEquals ("/**", authenticationAdapter.getUriPattern());
    }

    private createUser(String username, String userPassword)
    {
        def grName = "gr1";
        def groupController = new GroupController();
        IntegrationTestUtils.resetController (groupController);
        groupController.params["name"] = grName
        groupController.save();
        def group = Group.get(name:grName);
        assertNotNull (group);
        
        def userController = new RsUserController();
        IntegrationTestUtils.resetController (userController);

        userController.params["username"] = username
        userController.params["password1"] = userPassword
        userController.params["password2"] = userPassword
        userController.params["groups"] = String.valueOf(group.id)
        userController.params["groups.id"] = String.valueOf(group.id)
        userController.save();
        def rsUser = RsUser.get(username:username);
        assertNotNull(rsUser);
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
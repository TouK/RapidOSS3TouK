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

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 23, 2009
* Time: 11:24:42 AM
* To change this template use File | Settings | File Templates.
*/
class AuthenticationAuthorizationControllerIntegrationTests extends RapidCmdbIntegrationTestCase{
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
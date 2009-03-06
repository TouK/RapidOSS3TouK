package com.ifountain.servlet;

import com.ifountain.session.SessionManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.codehaus.groovy.grails.plugins.web.filters.FiltersGrailsPlugin;
import org.codehaus.groovy.grails.plugins.web.filters.CompositeInterceptor;
import org.codehaus.groovy.grails.plugins.web.filters.FilterToHandlerAdapter;
import org.codehaus.groovy.grails.web.context.ServletContextHolder;
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes;
import org.springframework.context.ApplicationContext;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 15, 2009
 * Time: 4:17:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class GroovyPagesServlet extends org.codehaus.groovy.grails.web.pages.GroovyPagesServlet  {
    public void doPage(HttpServletRequest request, HttpServletResponse response) throws ServletException , IOException {
        CompositeInterceptor interceptor = getFilterInterceptor();
        try
        {

            if(interceptor != null && !interceptor.preHandle(request, response, null)) return;
            super.doPage(request, response);
        }finally {
            if(interceptor != null)
            {
                try
                {
                    interceptor.afterCompletion(request, response, null, null);
                }
                catch(Exception e)
                {
                    org.apache.log4j.Logger.getRootLogger().debug("[GroovyPagesServlet]: Error in interceptor.afterCompletion . Reason :"+e.toString(),e);
                }
            }
        }

    }

    private CompositeInterceptor getFilterInterceptor()
    {
        if(ServletContextHolder.getServletContext() != null)
        {
            ApplicationContext context = (ApplicationContext)ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT);
            if(context != null)
            {
                return (CompositeInterceptor)context.getBean("filterInterceptor");

            }
        }
        return null;
    }
}
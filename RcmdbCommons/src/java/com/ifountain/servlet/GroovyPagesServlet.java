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
        List<FilterToHandlerAdapter> filters = getFilters();
        try
        {

            for(int i=0; i < filters.size(); i++)
            {
                FilterToHandlerAdapter adapter = filters.get(i);
                adapter.preHandle(request, response, null);
            }
            super.doPage(request, response);
        }finally {
            for(int i=0; i < filters.size(); i++)
            {
                FilterToHandlerAdapter adapter = filters.get(i);
                try
                {
                    adapter.afterCompletion(request, response, null, null);
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

    }

    private List<FilterToHandlerAdapter> getFilters()
    {
        if(ServletContextHolder.getServletContext() != null)
        {
            ApplicationContext context = (ApplicationContext)ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT);
            if(context != null)
            {
                CompositeInterceptor interceptor = (CompositeInterceptor)context.getBean("filterInterceptor");
                if(interceptor != null)
                {
                    return (List)interceptor.getHandlers();

                }
            }
        }
        return new ArrayList();
    }
}
package com.ifountain.servlet;

import com.ifountain.session.SessionManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 15, 2009
 * Time: 4:17:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class GroovyPagesServlet extends org.codehaus.groovy.grails.web.pages.GroovyPagesServlet  {
    public void doPage(HttpServletRequest request, HttpServletResponse response) throws ServletException , IOException {
        try
        {
            super.doPage(request, response);
        }finally {
            SessionManager.getInstance().endSession();    
        }

    }
}
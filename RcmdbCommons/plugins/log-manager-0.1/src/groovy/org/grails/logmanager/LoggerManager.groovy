package org.grails.logmanager

import org.apache.log4j.Appender
import org.apache.log4j.Category
import org.apache.log4j.FileAppender
import org.apache.log4j.LogManager
import org.apache.log4j.spi.HierarchyEventListener

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 16, 2009
 * Time: 12:32:24 PM
 * To change this template use File | Settings | File Templates.
 */

public class LoggerManager implements HierarchyEventListener {
    private static LoggerManager loggerManager;
    private boolean isDestroyed = false;
    public synchronized static LoggerManager getInstance() {
        if (loggerManager == null) {
            loggerManager = new LoggerManager();
        }
        return loggerManager;
    }

    public synchronized static void destroyInstance() {
        if (loggerManager != null) {
            loggerManager.destroy();
            loggerManager = null;
        }
    }

    private LoggerManager() {
        LogManager.getLoggerRepository().addHierarchyEventListener(this);
        LogManager.getLoggerRepository().getCurrentCategories().each {Category category ->
            def appenders = [];
            category.getAllAppenders().each {appenders.add(it)}
            appenders.each {Appender appender ->
                addAppenderEvent(category, appender);
            }
        }
    }
    public boolean isDestroyed()
    {
        return isDestroyed;
    }
    private void destroy()
    {
        isDestroyed = true;
    }

    public List getAllAppenders()
    {
        def allAppenders = [];
        LogManager.getLoggerRepository().getCurrentCategories().each {Category category ->
            allAppenders.addAll(category.getAllAppenders().toList());
        }
        return allAppenders;
    }
    public synchronized void addAppenderEvent(Category category, Appender appender) {
        if(!isDestroyed)
        {
            if (appender instanceof FileAppender) {
                category.removeAppender(appender);
                def appenderMock = new RollingFileAppenderWrapper(appender, "yyyy_MM_dd_HH_mm_ss_SSS");
                category.addAppender(appenderMock);
            }
        }
    }

    public void removeAppenderEvent(Category category, Appender appender) {
    }

    public List getLoggers()
    {
        return LogManager.getCurrentLoggers().toList();
    }


}
package org.grails.logmanager

import java.text.SimpleDateFormat
import org.apache.log4j.Appender
import org.apache.log4j.FileAppender
import org.apache.log4j.Layout
import org.apache.log4j.spi.ErrorHandler
import org.apache.log4j.spi.Filter
import org.apache.log4j.spi.LoggingEvent

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 16, 2009
 * Time: 12:35:23 PM
 * To change this template use File | Settings | File Templates.
 */

public class RollingFileAppenderWrapper implements Appender {

    private FileAppender appender;
    private SimpleDateFormat df;
    private List tempMessageArea = [];
    private boolean isRolling = false;

    def RollingFileAppenderWrapper(FileAppender appender, String rollDateFormat) {
        this.appender = appender;
        df = new SimpleDateFormat(rollDateFormat);
    }

    public org.apache.log4j.Appender getAppender()
    {
        return  appender;
    }
    public void addFilter(Filter filter) {
        appender.addFilter(filter);
    }

    public Filter getFilter() {
        return appender.getFilter(); //To change body of implemented methods use File | Settings | File Templates.
    }

    public void clearFilters() {
        appender.clearFilters();
    }

    public String getName() {
        return appender.getName(); //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        appender.setErrorHandler(errorHandler);
    }

    public ErrorHandler getErrorHandler() {
        return appender.getErrorHandler(); //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setLayout(Layout layout) {
        appender.setLayout(layout);
    }

    public Layout getLayout() {
        return appender.getLayout(); //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setName(String s) {
        appender.setName(s);
    }

    public boolean requiresLayout() {
        return appender.requiresLayout(); //To change body of implemented methods use File | Settings | File Templates.
    }

    public void close()
    {
        while(isRolling)
        {
            Thread.sleep(15);
        }
        appender.close();
    }

    public void doAppend(LoggingEvent loggingEvent) {
        synchronized (tempMessageArea) {
            if (!isRolling) {
                appender.doAppend(loggingEvent);
            }
            else {
                tempMessageArea.add(loggingEvent);
            }
        }

    }

    public void roll() {
        synchronized (tempMessageArea) {
            isRolling = true;
        }
        org.apache.log4j.FileAppender fa = appender;
        String filePath = fa.getFile();
        fa.setWriter(new PrintWriter(new StringWriter()))
        def file = new File(filePath);
        if (file.exists()) {
            moveFile(file, new File(file.getPath() + "." + df.format(new Date())));
        }
        fa.setWriter(new FileWriter(filePath));
        synchronized (tempMessageArea) {
            tempMessageArea.each {
                fa.append(it);
            }
            isRolling = false;
        }
    }

    protected void moveFile(File src, File target) {
        src.renameTo(target);
    }


}
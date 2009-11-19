package org.grails.logmanager

import org.apache.commons.io.FileUtils
import org.apache.log4j.ConsoleAppender
import org.apache.log4j.DailyRollingFileAppender
import org.apache.log4j.Logger
import org.apache.log4j.SimpleLayout
import org.grails.logmanager.utils.LogUtils

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 16, 2009
 * Time: 12:46:32 PM
 * To change this template use File | Settings | File Templates.
 */

public class LoggerManagerTest extends GroovyTestCase {
    static final String testoutputDir = "../testoutput"
    Logger logger;
    protected void setUp() {
        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
        FileUtils.deleteDirectory(new File(testoutputDir));
        logger = Logger.getLogger(LoggerManagerTest.class .name+"testlogger2");
        LogUtils.removeAllAppenders();
    }

    protected void tearDown() {
        super.tearDown();    //To change body of overridden methods use File | Settings | File Templates.
        LogUtils.removeAllAppenders();
    }

    public void testGetAndDestroyInstance() {
        LoggerManager logManager1 = LoggerManager.getInstance();
        LoggerManager logManager2 = LoggerManager.getInstance();
        assertSame(logManager1, logManager2);
        LoggerManager.destroyInstance();
        //multiple call doesnot cause exception
        LoggerManager.destroyInstance();
        assertTrue(logManager1.isDestroyed()); 

        LoggerManager logManager3 = LoggerManager.getInstance();
        assertNotSame(logManager2, logManager3);

    }

    public void testCreateWrapperForExistingFileAppenders() {
        LoggerManager.destroyInstance();
        File logFile = new File("${testoutputDir}/logs/file.log");
        logger.removeAllAppenders();
        def appender1 = new DailyRollingFileAppender(new SimpleLayout(), logFile.path, "yyyy-MM-dd");
        def appender2 = new DailyRollingFileAppender(new SimpleLayout(), logFile.path, "yyyy-MM-dd");
        logger.addAppender(appender1);
        logger.addAppender(appender2);
        logger.getAllAppenders().each {
            assertFalse(it instanceof RollingFileAppenderWrapper);
        }

        LoggerManager.getInstance();
        logger.getAllAppenders().each {
            assertTrue(it instanceof RollingFileAppenderWrapper);
        }

    }

    public void testLogManagerCreatesLoggerWrapperForNewlyCreatedFileAppender() {
        File logFile = new File("${testoutputDir}/logs/file.log");

        LoggerManager logManager = LoggerManager.getInstance();
        logger.removeAllAppenders();
        def appender = new DailyRollingFileAppender(new SimpleLayout(), logFile.path, "yyyy-MM-dd");
        logger.addAppender(appender);
        logger.getAllAppenders().each {
            assertTrue(it instanceof RollingFileAppenderWrapper);
        }
    }

    public void testLogManagerIgnoresNonFileAppenders() {

        LoggerManager logManager = LoggerManager.getInstance();
        logger.removeAllAppenders();
        def appender = new ConsoleAppender();
        logger.addAppender(appender);
        logger.getAllAppenders().each {
            assertFalse(it instanceof RollingFileAppenderWrapper);
        }
    }

    public void testGetAllAppenders()
    {
        File logFile = new File("${testoutputDir}/logs/file.log");
        def appender1 = new DailyRollingFileAppender(new SimpleLayout(), logFile.path, "yyyy-MM-dd");
        def appender2 = new DailyRollingFileAppender(new SimpleLayout(), logFile.path, "yyyy-MM-dd");
        def appender3 = new ConsoleAppender();
        logger.addAppender(appender1);
        logger.addAppender(appender2);
        logger.addAppender(appender3);
        def appenders1 = LoggerManager.getInstance().getAllAppenders();
        def appenders2 = LoggerManager.getInstance().getAllAppenders();
        assertNotSame(appenders1, appenders2);
        assertEquals(3, appenders1.size());
        assertTrue(appenders1.contains(appender3));

        RollingFileAppenderWrapper appender1Wrapper = appenders1.find {it instanceof RollingFileAppenderWrapper && it.getAppender() == appender1};
        RollingFileAppenderWrapper appender2Wrapper = appenders1.find {it instanceof RollingFileAppenderWrapper && it.getAppender() == appender2};
        assertNotSame(appender1Wrapper, appender2Wrapper); 
        assertNotNull(appender1Wrapper);
        assertNotNull(appender2Wrapper); 

    }

    public void testGetAllLoggers()
    {
        def previousLoggers = LogUtils.getLoggers()
        logger = Logger.getLogger(LoggerManagerTest.class .name+"testGetAllLoggerslogger1");
        def appender1 = new ConsoleAppender();
        logger.addAppender(appender1);
        def appender2 = new ConsoleAppender();
        Logger logger2 = Logger.getLogger("testGetAllLoggerslogger2")
        logger.addAppender(appender1);

        Logger logger3 = Logger.getLogger("non_configured_logger")

        List loggers = LoggerManager.getInstance().getLoggers();
        assertEquals(previousLoggers.size()+3, loggers.size());
        assertTrue(loggers.contains(logger));
        assertTrue(loggers.contains(logger2)); 
        assertTrue(loggers.contains(logger3)); 
    }
}
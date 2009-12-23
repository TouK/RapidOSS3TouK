package org.grails.logmanager

import org.apache.commons.io.FileUtils
import org.apache.log4j.DailyRollingFileAppender
import org.apache.log4j.FileAppender
import org.apache.log4j.Logger
import org.apache.log4j.SimpleLayout

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 16, 2009
 * Time: 12:47:59 PM
 * To change this template use File | Settings | File Templates.
 */

public class RollingFileAppenderWrapperTest extends GroovyTestCase {
    static final String testoutputDir = "../testoutput"
    Logger logger;

    protected void setUp() {
        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
        FileUtils.deleteDirectory(new File(testoutputDir));
        logger = Logger.getLogger("logger1");

    }

    protected void tearDown() {
        super.tearDown();    //To change body of overridden methods use File | Settings | File Templates.
        logger.removeAllAppenders();
//    FileUtils.deleteDirectory(new File(testoutputDir));

    }

    public void testRoll() {
        File logFile = new File("${testoutputDir}/logs/file.log");
        def appender = new DailyRollingFileAppender(new SimpleLayout(), logFile.path, "yyyy-MM-dd");
        def appenderWrapper = new RollingFileAppenderWrapper(appender, "yyyy_MM_dd");
        try
        {
            logger.addAppender(appenderWrapper);
            def messagesWritten = ["message1", "message2"];
            logger.warn(messagesWritten[0])
            logger.warn(messagesWritten[1])
            String textBeforeRoll = logFile.getText();
            appenderWrapper.roll();
            def logDir = logFile.parentFile;
            def files = Arrays.asList(logDir.listFiles()).sort{it.name};

            assertEquals(2, files.size());
            assertEquals("", files[0].getText());
            assertEquals(textBeforeRoll, files[1].getText());

            logger.warn("message3");

            assertEquals("WARN - message3", files[0].getText().trim());
            assertEquals(textBeforeRoll, files[1].getText());
        }finally{
            appenderWrapper.close();            
        }
    }

    
    public void testRollBuffersTheMessageWhileRollingLogFile() {
        File logFile = new File("${testoutputDir}/logs/file.log");
        def appender = new DailyRollingFileAppender(new SimpleLayout(), logFile.path, "yyyy-MM-dd");
        def appenderWrapper = new RollingFileAppenderWrapperMock(appender, "yyyy_MM_dd");
        try
        {
            logger.addAppender(appenderWrapper);

            def messagesWritten = ["message1", "message2"];
            logger.warn(messagesWritten[0]);
            logger.warn(messagesWritten[1]);

            String textBeforeRoll = logFile.getText();
            Thread t1 = Thread.start{
                appenderWrapper.roll();
            }
            while(!appenderWrapper.isWaiting)
            {
                Thread.sleep(20);
            }
            Thread.sleep(50);
            logger.warn("message4");
            assertEquals(textBeforeRoll, logFile.getText());

            //test close waits roll method to finish
            boolean isClosed = false;
            Thread t2 = Thread.start{
                appenderWrapper.close();
                isClosed = true;
            }

            Thread.sleep(2000);
            assertFalse(isClosed); 
            synchronized (appenderWrapper.waitLock)
            {
                appenderWrapper.waitLock.notifyAll();
            }

            t1.join();
            t2.join(); 
            def listOfFiles = Arrays.asList(logFile.parentFile.listFiles()).sort{it.name}
            assertEquals("WARN - message4", logFile.getText().trim());
            assertEquals(textBeforeRoll, listOfFiles[1].getText());
        }finally{
            appenderWrapper.close();            
        }
    }
}

class RollingFileAppenderWrapperMock extends RollingFileAppenderWrapper {
    Object waitLock = new Object();
    def isWaiting = false;
    def RollingFileAppenderWrapperMock(FileAppender appender, String dateFormat) {
        super(appender, dateFormat);
    }

    protected void moveFile(File src, File target) {
        synchronized (waitLock)
        {
            isWaiting = true;
            waitLock.wait();
        }
        super.moveFile(src, target);    //To change body of overridden methods use File | Settings | File Templates.
    }

}
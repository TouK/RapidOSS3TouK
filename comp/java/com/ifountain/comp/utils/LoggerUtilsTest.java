package com.ifountain.comp.utils;
import com.ifountain.comp.test.util.RCompTestCase;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.DailyRollingFileAppender;

/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Nov 6, 2008
 * Time: 2:09:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoggerUtilsTest extends RCompTestCase {
    
    public void testConfigureLoggerWithAllParams(){
         Logger logger=Logger.getLogger("testlogger");
         String logFile="testlogfile";


         LoggerUtils.configureLogger(logger,Level.DEBUG,logFile,false);
         assertEquals(logger.getLevel(),Level.DEBUG);
         assertFalse(logger.getAllAppenders().hasMoreElements());

         LoggerUtils.configureLogger(logger,Level.INFO,logFile,false);
         assertEquals(logger.getLevel(),Level.INFO);
         assertFalse(logger.getAllAppenders().hasMoreElements());

        LoggerUtils.configureLogger(logger,Level.INFO,logFile,true);
        assertEquals(logger.getLevel(),Level.INFO);
        assertTrue(logger.getAllAppenders().hasMoreElements());

        assertEquals(logger.getAllAppenders().nextElement().getClass(),(new DailyRollingFileAppender()).getClass());

    }
    
    
}

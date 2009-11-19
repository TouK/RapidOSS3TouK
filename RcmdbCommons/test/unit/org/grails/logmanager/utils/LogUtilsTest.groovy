package org.grails.logmanager.utils

import org.apache.commons.io.FileUtils
import org.apache.log4j.ConsoleAppender
import org.apache.log4j.Logger
import org.grails.logmanager.utils.LogUtils

/**
 * Created by IntelliJ IDEA.
 * User: mustafa seker
 * Date: Feb 16, 2009
 * Time: 12:15:15 AM
 * To change this template use File | Settings | File Templates.
 */

public class LogUtilsTest extends GroovyTestCase{
    static final String testoutputDir = "../testoutput"

    protected void setUp() {
        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
        def testoutputDirFile = new File(testoutputDir)
        FileUtils.deleteDirectory(testoutputDirFile);
        testoutputDirFile.mkdirs();

    }

    protected void tearDown() {
        super.tearDown();    //To change body of overridden methods use File | Settings | File Templates.
    }
    public void testGetLastNLineOffset()
    {
        String logFilePath = "${testoutputDir}/logfile.log";
        File logFile = new File(logFilePath);
        def sentences = ["this is a line", "this is another line"]
        logFile.setText(sentences.join("\n"))

        long newOffset = LogUtils.getLastNLineOffset(logFilePath, 1);
        assertEquals (sentences[0].length()+1, newOffset);
        newOffset = LogUtils.getLastNLineOffset(logFilePath, 2);
        assertEquals (0, newOffset);
        newOffset = LogUtils.getLastNLineOffset(logFilePath, 3);
        assertEquals (0, newOffset);
        try{
            LogUtils.getLastNLineOffset(logFilePath, 0);
            fail("Should throw exception")
        }catch(Exception e)
        {
            assertEquals ("number of lines should be gerater than 0", e.getMessage())    
        }
        try{
            LogUtils.getLastNLineOffset(logFilePath, -1);
            fail("Should throw exception")
        }catch(Exception e)
        {
            assertEquals ("number of lines should be gerater than 0", e.getMessage())
        }
    }
    public void testGetLastNLineOffsetWithNonExistingFile()
    {
        String logFilePath = "${testoutputDir}/logfile.log";
        File logFile = new File(logFilePath);

        try{
            LogUtils.getLastNLineOffset(logFilePath, 1);
            fail("Should throw exception")
        }catch(FileNotFoundException e)
        {
        }
    }
    public void testGetLoggers()
    {
        def loggerName = "trial.x";
        Logger logger = Logger.getLogger(loggerName)
        logger.addAppender(new ConsoleAppender());
        List loggers = LogUtils.getLoggers();
        loggers = loggers.findAll {it.name == loggerName}
        assertEquals(1, loggers.size());

        def loggerName2 = "trial2.x";
        logger = Logger.getLogger(loggerName2)
        logger.addAppender(new ConsoleAppender());
        loggers = LogUtils.getLoggers();

        Logger foundLogger = loggers.find {it.name == loggerName}
        assertNotNull(foundLogger);

        foundLogger = loggers.find {it.name == loggerName2}
        assertNotNull(foundLogger);

        def loggerName3 = "trial3.x";
        logger = Logger.getLogger(loggerName2)
        foundLogger = loggers.find {it.name == loggerName3}
        assertNull(foundLogger);
    }

    public void testReadLog()
    {
        String logFilePath = "${testoutputDir}/logfile.log";
        File logFile = new File(logFilePath);
        def sentences = ["this is a line", "this is another line"]
        logFile.setText(sentences.join("\n"))
        List lines = [];

        long newOffset = LogUtils.readLog(logFilePath, 0, 10, lines);
        assertEquals(2, lines.size());
        assertEquals(sentences[0]+"\n", lines[0]);
        assertEquals(sentences[1], lines[1]);
        assertEquals(logFile.length(), newOffset);

        lines = [];
        def offset = 4;
        newOffset = LogUtils.readLog(logFilePath, offset, 1, lines);
        assertEquals(1, lines.size());
        assertEquals(sentences[0].substring(offset)+"\n", lines[0]);
        assertEquals(newOffset, sentences[0].length()+1);
    }

    public void testReadLogReturnFileLengthIfOffSetBeyongLength()
    {
        String logFilePath = "${testoutputDir}/logfile.log";
        File logFile = new File(logFilePath);
        def sentences = ["this is a line", "this is another line"]
        logFile.setText(sentences.join("\n"))
        List lines = [];
        def lengthOfFile = logFile.length();
        long newOffset = LogUtils.readLog(logFilePath, 500, 10, lines);
        assertEquals(lengthOfFile, newOffset);
        assertTrue(lines.isEmpty()); 
    }


}
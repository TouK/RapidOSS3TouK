package org.grails.logmanager.utils

import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.FalseFileFilter
import org.apache.commons.io.filefilter.TrueFileFilter

/**
 * Created by IntelliJ IDEA.
 * User: mustafa seker
 * Date: Feb 22, 2009
 * Time: 9:34:27 PM
 * To change this template use File | Settings | File Templates.
 */

public class LogManagerUtilsTest extends GroovyTestCase
{
    def static final String outputDirectory = "../testoutput/LogManagerUtilsTest"

    protected void setUp() {
        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
        System.setProperty("base.dir", outputDirectory)
        def outDirFile = new File(outputDirectory);
        FileUtils.deleteDirectory(outDirFile);
        outDirFile.mkdirs();
    }

    protected void tearDown() {
        super.tearDown();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testGetLogDirectories()
    {
        def settings = [:]
        def dirs = LogManagerUtils.getLogDirectories(settings);
        assertTrue(dirs.isEmpty());
        settings.logDirectories = ["logs1", "logs2"]
        settings.logDirectories.each{
            new File("${outputDirectory}/${it}").mkdirs();
        }
        settings.logDirectories.add("NONEXISTINGDIR");
        settings.logDirectories.add("invaliddir");
        new File("${outputDirectory}/invaliddir").setText(""); 
        dirs = LogManagerUtils.getLogDirectories(settings);
        assertEquals(2, dirs.size());
        def paths = dirs.canonicalPath;
        println paths
        assertTrue(paths.contains(new File("${outputDirectory}/logs1").canonicalPath));
        assertTrue(paths.contains(new File("${outputDirectory}/logs2").canonicalPath));
    }

    public void testRollbackLogFilters()
    {
        def settings = [:]
        def filter = LogManagerUtils.getRollbackFileFilters(settings);
        assertTrue(filter instanceof FalseFileFilter); 
        settings.rollbackFilePatterns = ["a.*", "b.*"];
        filter = LogManagerUtils.getRollbackFileFilters(settings);
        def file1 = new File("${outputDirectory}/afile1.log");
        def file2 = new File("${outputDirectory}/afile2.log");
        def file3 = new File("${outputDirectory}/bfile1.log");
        def file4 = new File("${outputDirectory}/cfile1.log");
        file1.setText("")
        file2.setText("")
        file3.setText("")
        file4.setText("")
        List files = FileUtils.listFiles(new File(outputDirectory), filter, new TrueFileFilter());
        assertEquals(3, files.size());
        def paths = files.canonicalPath;
        assertTrue(paths.contains(file1.canonicalPath));
        assertTrue(paths.contains(file2.canonicalPath));
        assertTrue(paths.contains(file3.canonicalPath));
    }

    public void testLogFilters()
    {
        def settings = [:]
        settings.validLogFilePatterns = ["a.*", "b.*"];
        settings.rollbackFilePatterns = ["aa.*"];
        def filter = LogManagerUtils.getLogFileFilters(settings);
        def file1 = new File("${outputDirectory}/afile1.log");
        def file2 = new File("${outputDirectory}/afile2.log");
        def file3 = new File("${outputDirectory}/bfile1.log");
        def file4 = new File("${outputDirectory}/cfile1.log");
        def file5 = new File("${outputDirectory}/aafile1.log");
        file1.setText("")
        file2.setText("")
        file3.setText("")
        file4.setText("")
        file5.setText("")
        List files = FileUtils.listFiles(new File(outputDirectory), filter, new TrueFileFilter());
        assertEquals(3, files.size());
        def paths = files.canonicalPath;
        assertTrue(paths.contains(file1.canonicalPath));
        assertTrue(paths.contains(file2.canonicalPath));
        assertTrue(paths.contains(file3.canonicalPath));
    }

    public void testLogFiltersThrowsExceptionIfNoLogFiltersSpecified()
    {
        try
        {
            LogManagerUtils.getLogFileFilters([:]);
            fail("Should throw exception since no log filepatterns specified");
        }
        catch(Exception e)
        {

        }
    }

    public void testIsValidLogFile()
    {
        def settings = [:]
        settings.logDirectories =  ["logs1", "logs2", "nonexistingdir"]
        settings.validLogFilePatterns = ["a.*", "b.*"];
        settings.rollbackFilePatterns = ["aa.*"];
        settings.logDirectories.each{new File("${outputDirectory}/${it}").mkdirs()}
        assertFalse(LogManagerUtils.isValidLogFile(settings, "a.log"))
        assertTrue(LogManagerUtils.isValidLogFile(settings, "logs1/a.log"))
        assertTrue(LogManagerUtils.isValidLogFile(settings, "logs1/anotherdir/a.log"))

    }
    public void testLogFilenames()
    {
        def settings = [:]
        settings.logDirectories =  ["logs1", "logs2", "nonexistingdir"]
        settings.validLogFilePatterns = ["a.*", "b.*"];
        settings.rollbackFilePatterns = ["aa.*"];

        LogManagerUtils.getLogDirectories(settings).each{File dir->
            dir.mkdirs();    
        }
        new File("${outputDirectory}/anotherDir").mkdirs();
        def file1 = new File("${outputDirectory}/${settings.logDirectories[0]}/afile1.log");
        def file2 = new File("${outputDirectory}/${settings.logDirectories[0]}/afile2.log");
        def file3 = new File("${outputDirectory}/${settings.logDirectories[1]}/bfile1.log");
        def file4 = new File("${outputDirectory}/${settings.logDirectories[1]}/cfile1.log");
        def file5 = new File("${outputDirectory}/${settings.logDirectories[1]}/aafile1.log");
        def file6 = new File("${outputDirectory}/anotherDir/aafile1.log");

        file1.setText("")
        file2.setText("")
        file3.setText("")
        file4.setText("")
        file5.setText("")
        file6.setText("")
        def logFileNames = LogManagerUtils.getLogFileNames(settings);
        assertEquals(3, logFileNames.size());
        println logFileNames
        assertTrue(logFileNames.contains("${settings.logDirectories[0]}/afile1.log".toString()));
        assertTrue(logFileNames.contains("${settings.logDirectories[0]}/afile2.log".toString()));
        assertTrue(logFileNames.contains("${settings.logDirectories[1]}/bfile1.log".toString())); 
    }
}
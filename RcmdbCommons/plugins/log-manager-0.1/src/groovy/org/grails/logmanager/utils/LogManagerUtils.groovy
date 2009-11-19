package org.grails.logmanager.utils

import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.*

/**
 * Created by IntelliJ IDEA.
 * User: mustafa seker
 * Date: Feb 22, 2009
 * Time: 9:31:49 PM
 * To change this template use File | Settings | File Templates.
 */

public class LogManagerUtils {

    def static getLogDirectories(settings)
    {
        def dirs = [];
        def dirNames = settings.logDirectories;
        if(dirNames != null)
        {
            dirNames.each{dirName->
                def dirFile = new File("${System.getProperty("base.dir")}/${dirName}");
                if(dirFile.exists() && dirFile.isDirectory())
                {
                    dirs.add(dirFile);
                }
            }
        }
        return dirs;
    }

    def static isValidLogFile(settings, String logFilePath)
    {
        File logFile = new File(new File(System.getProperty("base.dir")), logFilePath);
        def logDirs = getLogDirectories(settings);
        for(int i=0; i < logDirs.size(); i++)
        {
            File logDir = logDirs[i];
            if(logFile.canonicalPath.startsWith(logDir.canonicalPath))
            {
                return true;
            }
        }
        return false
    }

    def static getLogFileFilters(settings)
    {
        def filter = null;
        settings.validLogFilePatterns.each{
            if(filter == null)
            {
                filter = new RegexFileFilter(it);
            }
            else
            {
                filter = new OrFileFilter(filter, new RegexFileFilter(it));
            }
        }
        if(filter == null)
        {
            throw new Exception("No log file filters specified");
        }
        return new AndFileFilter(filter, new NotFileFilter(getRollbackFileFilters(settings)));
    }

    def static getRollbackFileFilters(settings)
    {
        def filter = null;
        settings.rollbackFilePatterns.each{
            if(filter == null)
            {
                filter = new RegexFileFilter(it);
            }
            else
            {
                filter = new OrFileFilter(filter, new RegexFileFilter(it));
            }
        }
        return filter == null?new FalseFileFilter():filter;
    }

    def static getLogFileNames(settings)
    {
        def logFiles = [];
        def dirs = getLogDirectories(settings);
        def baseDir = new File(System.getProperty("base.dir"));
        dirs.each{File dir->
            def listOfFiles = FileUtils.listFiles(dir, getLogFileFilters(settings), new TrueFileFilter());
            listOfFiles.findAll {!it.isDirectory()}.each{File logFile->
                def relativeLogFilePath = logFile.getCanonicalPath().substring(baseDir.getCanonicalPath().length()+1);
                relativeLogFilePath = relativeLogFilePath.replaceAll("\\\\", "/");
                logFiles.add(relativeLogFilePath);
            }
        }
        logFiles = logFiles.sort{it.toLowerCase()};
        return logFiles;
    }
}
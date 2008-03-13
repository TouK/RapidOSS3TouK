/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be 
 * noted in a separate copyright notice. All rights reserved.
 * This file is part of RapidCMDB.
 * 
 * RapidCMDB is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 */
/*
 * Created on Nov 14, 2007
 *
 */
package com.ifountain.comp.utils;

import java.io.IOException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class LoggerUtils
{

    public static final String DAILY_ROLLING_FILE_APPENDER = "DailyRollingFileAppender";

    public static void configureLogger(Logger aLogger, String fileName, Level level)
    {
        configureLogger(aLogger, fileName, level, "%d{yy/MM/dd HH:mm:ss.SSS} %p: %m%n");
    }
    public static void configureLogger(Logger aLogger, String fileName, Level level, String layoutPattern)
    {
        aLogger.setAdditivity(false);
        if (fileName != null) {
        	try {
                PatternLayout layout = new PatternLayout(layoutPattern);
                DailyRollingFileAppender fileappender = new DailyRollingFileAppender(layout, fileName, ".yyyy-MM-dd");
                fileappender.setAppend(true);
                fileappender.setName(DAILY_ROLLING_FILE_APPENDER);
                aLogger.removeAllAppenders();
                aLogger.addAppender(fileappender);
            } catch (IOException e) {
            	System.err.println(fileName + " log file could not be initialized.");
            	addConsoleAppender(aLogger, layoutPattern);
            }
    	}
        else {
        	System.err.println(fileName + " log file could not be initialized.");
        	addConsoleAppender(aLogger, layoutPattern);
        }
        if(level != null)
        {
            aLogger.setLevel(level);
        }
    }

    public static void addConsoleAppender(Logger aLogger, String layoutPattern)
    {
    	String layout_str = "";
    	PatternLayout layout = new PatternLayout( layout_str );
    	ConsoleAppender app = new ConsoleAppender( layout, "System.err" );
    	app.setTarget( "System.err" );
    	app.setName( "ConsoleAppender" );
    	aLogger.removeAllAppenders();
    	aLogger.addAppender( app );
    }

}

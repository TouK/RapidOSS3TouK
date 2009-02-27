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
 * Created on Jan 21, 2008
 *
 */
package com.ifountain.comp.test.util.logging;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.ifountain.comp.utils.LoggerUtils;

public class TestLogUtils
{
    public static Logger log = Logger.getLogger(TestLogUtils.class);
    public static void enableLogger()
    {
        enableLogger(TestLogUtils.log);
    }
    public static void enableLogger(Logger logger)
    {
        logger.removeAllAppenders();
        LoggerUtils.addConsoleAppender(logger, "%d{yy/MM/dd HH:mm:ss.SSS} %p: %m%n");
        logger.setLevel(Level.DEBUG);
    }
    public static void disableLogger(Logger logger)
    {
        logger.removeAllAppenders();        
    }
}

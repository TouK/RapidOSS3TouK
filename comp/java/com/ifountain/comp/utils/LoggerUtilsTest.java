/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
        assertTrue(logger.getAdditivity());


        LoggerUtils.configureLogger(logger,Level.INFO,logFile,false);
        assertEquals(logger.getLevel(),Level.INFO);
        assertFalse(logger.getAllAppenders().hasMoreElements());
        assertTrue(logger.getAdditivity());
        
        LoggerUtils.configureLogger(logger,Level.INFO,logFile,true);
        assertEquals(logger.getLevel(),Level.INFO);
        assertTrue(logger.getAllAppenders().hasMoreElements());
        assertFalse(logger.getAdditivity());
        
        assertEquals(logger.getAllAppenders().nextElement().getClass(),(new DailyRollingFileAppender()).getClass());

    }
    public void testDestroyLogger()
    {
        Logger logger=Logger.getLogger("testlogger");
        String logFile="testlogfile";

        LoggerUtils.configureLogger(logger,Level.INFO,logFile,true);

        assertFalse(logger.getAdditivity());
        assertTrue(logger.getAllAppenders().hasMoreElements());
        
        LoggerUtils.destroyLogger(logger);

        assertTrue(logger.getAdditivity());
        assertFalse(logger.getAllAppenders().hasMoreElements());

    }
    
    
}

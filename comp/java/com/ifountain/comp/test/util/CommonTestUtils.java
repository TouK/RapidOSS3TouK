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
 * Created on Aug 31, 2007
 *
 */
package com.ifountain.comp.test.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class CommonTestUtils
{

    public static void wait(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Properties testProperties;
    public static void initializeFromFile(String fileName){
        testProperties = new Properties();
        FileInputStream stream = null;
        try {
        	
        	stream = new FileInputStream(fileName);
            testProperties.load(stream);
            
        } catch (IOException e) {
        	
            e.printStackTrace();
            
        } finally {
        	try {
    			if (stream != null ) stream.close();
    		} catch (IOException e1) {
    
    		}
        }
    }
    public static String getTestProperty(String propertyName) {
        return testProperties.getProperty(String.valueOf(propertyName)).trim();
    }
    public static String getTestProperty(String propertyName, String defaultValue) {
        return testProperties.getProperty(String.valueOf(propertyName), defaultValue).trim();
    }
    
    public static void waitFor(com.ifountain.comp.utils.WaitAction waitAction) throws InterruptedException
    {
    	com.ifountain.comp.utils.SmartWait.waitFor(waitAction);
    }

    public static void waitFor(com.ifountain.comp.utils.WaitAction waitAction, int maxNumberOfIterations) throws InterruptedException
    {
    	com.ifountain.comp.utils.SmartWait.waitFor(waitAction, maxNumberOfIterations);
    }

}

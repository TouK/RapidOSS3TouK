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
 * Created on Feb 1, 2008
 *
 */
package com.ifountain.comp.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.ifountain.comp.test.util.testcase.RapidTestCase;

public class RapidConfigTest extends RapidTestCase
{
    public void testRapidConfig() throws Exception
    {
        String propertyName1 = "Prop1";
        RapidConfig rc = new MockRapidConfig();
        assertFalse(rc.containsKey(propertyName1));
        String propValue1 = "value1";
        String propertyName2 = "Prop2";
        String propValue2 = "value2";
        rc.addPropertyDirect(propertyName1, propValue1);
        rc.addPropertyDirect(propertyName2, propValue2);
        assertEquals(propValue1, rc.getProperty(propertyName1));
        assertEquals(propValue2, rc.getProperty(propertyName2));
        assertTrue(rc.containsKey(propertyName1));
        assertTrue(rc.containsKey(propertyName2));
        assertFalse(rc.containsKey("undefined"));
        
        List<String> keys =  new ArrayList<String>(Arrays.asList(new String[]{propertyName1, propertyName2}));
        Iterator<String> it = rc.getKeys();
        String key = it.next();
        keys.remove(key);
        key = it.next();
        keys.remove(key);
        assertTrue("getKeys does not return key names correct", keys.isEmpty());
        
        rc.clearProperty(propertyName1);
        assertFalse(rc.containsKey(propertyName1));
        rc.clearProperty(propertyName2);
        assertFalse(rc.containsKey(propertyName1));
        assertTrue(rc.isEmpty());
    }
    
    
    class MockRapidConfig extends RapidConfig
    {
        @Override
        public void load() throws Exception
        {
        }
    }
}

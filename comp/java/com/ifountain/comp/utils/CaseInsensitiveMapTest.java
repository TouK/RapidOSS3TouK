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
 * Created on Jul 18, 2007
 *
 */
package com.ifountain.comp.utils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.ifountain.comp.test.util.RCompTestCase;



public class CaseInsensitiveMapTest extends RCompTestCase
{
    public void testCaseInsensiteMap() throws Exception
    {
        String key = "KeY1";
        String value = "value1"; 
        CaseInsensitiveMap map = new CaseInsensitiveMap();
        assertTrue(map.isEmpty());
        
        assertEquals(null, map.get(null));
        String nullValue = "nullvalue";
        map.put(null, nullValue);
        assertEquals(nullValue, map.get(null));
        assertEquals(1, map.size());
        assertFalse(map.isEmpty());
        
        assertEquals(null, map.get(key));
        map.put(key, value);
        assertEquals(value, map.get(key));
        assertEquals(value, map.get("kEy1"));
        assertEquals(2, map.size());
        assertFalse(map.isEmpty());
        assertTrue(map.containsKey("KEY1"));
        assertTrue(map.containsValue(value));
        
        value = "newvalue";
        map.put("KEY1", value);
        assertEquals(2, map.size());
        assertEquals(value, map.get("kEy1"));
        
        assertEquals(value, map.remove("KEY1"));
        assertFalse(map.containsKey("KEY1"));
        assertEquals(null, map.get(key));
        assertEquals(1, map.size());
        assertFalse(map.containsValue(value));
    }
    
    public void testKeySet() throws Exception
    {
        String key1 = "KeY1";
        String value1 = "value1"; 
        String key2 = "KeY1";
        String value2 = "value1"; 
        CaseInsensitiveMap map = new CaseInsensitiveMap();
        map.put(key1, value1);
        map.put(key2, value2);
        
        Set keyset = map.keySet();
        for (Iterator iter = keyset.iterator(); iter.hasNext();)
        {
            assertTrue(iter.next() instanceof String);
        }
    }
    public void testEntrySet() throws Exception
    {
        String key1 = "KeY1";
        String value1 = "value1"; 
        String key2 = "KeY1";
        String value2 = "value1"; 
        CaseInsensitiveMap map = new CaseInsensitiveMap();
        map.put(key1, value1);
        map.put(key2, value2);
        
        Set entryset = map.entrySet();
        for (Iterator iter = entryset.iterator(); iter.hasNext();)
        {
            Entry entry = (Entry) iter.next();
            assertTrue(entry.getKey() instanceof String);
        }
    }
    
    public void testCaseinsensiteMapConstructor() throws Exception
    {
        Map map = new LinkedHashMap();
        map.put("keY1", "value1");
        map.put("keY2", "value2");
        map.put("KeY1", "value3");
        
        Map caseMap = new CaseInsensitiveMap(map);
        assertEquals(2, caseMap.size());
        assertTrue(caseMap.containsKey("key1"));
        assertTrue(caseMap.containsKey("key2"));
        assertEquals("value3", caseMap.get("key1"));
        
    }
    
//    public void testPerformance() throws Exception
//    {
//        Map normalMap = new HashMap();
//        long currTime = System.currentTimeMillis();
//        for (int i = 0; i < 100000; i++)
//        {
//            normalMap.put("keyhsadhasdhgsahdghsagdhgsahdga"+i, "valuedsfaskdjlasjd"+i);
//        }
//        
//        for (int i = 0; i < 1000; i++)
//        {
//            normalMap.entrySet();
//        }
//        Map anotherMap = new HashMap();
//        anotherMap.putAll(normalMap);
//        System.out.println(System.currentTimeMillis() - currTime);
//        
//        Map caseMap = new CaseInsensitiveMap();
//        currTime = System.currentTimeMillis();
//        for (int i = 0; i < 100000; i++)
//        {
//            caseMap.put("keyhsadhasdhgsahdghsagdhgsahdga"+i, "valuedsfaskdjlasjd"+i);
//        }
//        
//        for (int i = 0; i < 1000; i++)
//        {
//            caseMap.entrySet();
//        }
//        Map anotherMap2 = new CaseInsensitiveMap();
//        anotherMap2.putAll(caseMap);
//        System.out.println(System.currentTimeMillis() - currTime);
//    }
}

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created on Feb 22, 2008
 *
 * Author Sezgin Kucukkaraaslan
 */
package com.ifountain.smarts.datasource.queries;

import com.ifountain.comp.test.util.logging.TestLogUtils;
import com.ifountain.smarts.test.util.SmartsTestCase;
import com.ifountain.smarts.test.util.SmartsTestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstanceSetTest extends SmartsTestCase {

    public void testNext() throws Exception
    {
        SmartsTestUtils.deleteAllTopologyInstances("Switch", ".*");
        Map<String, String> atts = new HashMap<String, String>();
        atts.put("Model", "MyModel");
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Switch", "eraaswiad", atts, 1, 6);
        
        List<String> attributes = new ArrayList<String>();
        attributes.add("Model");
        attributes.add("Name");
        IQuery findInstancesQuery = new FindTopologyInstancesQuery(TestLogUtils.log, SmartsTestUtils.getTopologyAdapter(), 
                "Switch", ".*", attributes, 5 ,true);
        InstanceSet results = (InstanceSet) findInstancesQuery.execute();
        assertEquals(0, results.getRecords().size());
        
        List<Map<String, Object>> actualRecords = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < 5; i++)
        {
            assertTrue(results.hasNext());
            actualRecords.add(results.next());
            
            assertEquals(4 - i, results.getRecords().size());
        }
        assertTrue(results.hasNext());
        actualRecords.add(results.next());
        assertFalse(results.hasNext());
        assertEquals(0, results.getRecords().size());
        
        assertFalse(results.hasNext());
        
        List<String> actualNames = new ArrayList<String>();
        for (int i = 0; i < actualRecords.size(); i++)
        {
            Map<String, Object> record = actualRecords.get(i);
            String name = (String) record.get("Name");
            actualNames.add(name);
        }
        
        for (int i = 1; i < 7; i++)
        {
            assertTrue(actualNames.contains("eraaswiad" + i));
        }
    }
    
    public void testNextWithEmptyResultset() throws Exception
    {
        SmartsTestUtils.deleteAllTopologyInstances("Switch", ".*");
        List<String> attributes = new ArrayList<String>();
        attributes.add("Model");
        attributes.add("Name");
        
        IQuery findInstancesQuery = new FindTopologyInstancesQuery(TestLogUtils.log, SmartsTestUtils.getTopologyAdapter(),
                "Switch", ".*", attributes, 5 ,true);
        InstanceSet resultSet = (InstanceSet) findInstancesQuery.execute();
        assertFalse(resultSet.hasNext());
        
        findInstancesQuery =  new FindTopologyInstancesQuery(TestLogUtils.log, SmartsTestUtils.getTopologyAdapter(),
                "Switch", ".*", attributes, 5 ,true);
        resultSet = (InstanceSet)  findInstancesQuery.execute();
        assertFalse(resultSet.hasNext());
        
    }
}

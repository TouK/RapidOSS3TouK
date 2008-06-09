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
/**
 * Created on Feb 22, 2008
 *
 * Author Sezgin Kucukkaraaslan
 */
package com.ifountain.smarts.datasource.queries;

import com.ifountain.comp.test.util.logging.TestLogUtils;
import com.ifountain.smarts.datasource.BaseSmartsAdapter;
import com.ifountain.smarts.datasource.BaseTopologyAdapter;
import com.ifountain.smarts.test.util.SmartsTestCase;
import com.ifountain.smarts.test.util.SmartsTestUtils;
import org.apache.log4j.Logger;

import java.util.*;

public class FindTopologyInstancesQueryTest extends SmartsTestCase {
    
    public void testExecute() throws Exception
    {
        BaseTopologyAdapter topologyAdapter = SmartsTestUtils.getTopologyAdapter();
        String[] childClassesOfUnitaryComputerSystem = topologyAdapter.getChildren("UnitaryComputerSystem");
        for (int i = 0 ; i < childClassesOfUnitaryComputerSystem.length ; i++)
        {
            SmartsTestUtils.deleteAllTopologyInstances(childClassesOfUnitaryComputerSystem[i], "m.*");
            String[] childClassesOfThisClass = topologyAdapter.getChildren(childClassesOfUnitaryComputerSystem[i]);
            for (int j = 0 ; j < childClassesOfThisClass.length ; j++)
            {
                SmartsTestUtils.deleteAllTopologyInstances(childClassesOfThisClass[j], "m.*");
            }
        }
        Map<String, String> atts = new HashMap<String, String>();
        atts.put("Model", "models");
        atts.put("Location", "locations");
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Switch", "myswitch", atts, 0, 2);
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Switch", "mm", atts, 0, 2);
        atts = new HashMap<String, String>();
        atts.put("Model", "modelr");
        atts.put("Location", "locationr");
        SmartsTestUtils.createTopologyInstancesWithPrefixes("Router", "myrouter", atts, 0, 2);

        
        List<String> attributes = new ArrayList<String>();
        attributes.addAll(atts.keySet());
        FindTopologyInstancesQuery query = new FindTopologyInstancesQuery(TestLogUtils.log, topologyAdapter, 
                    "UnitaryComputerSystem", "m.*", attributes, 1, true);
        
        InstanceSet resultSet = (InstanceSet) query.execute();
        Map<String, LinkedList<String>> classInstanceMap = resultSet.getClassInstanceMap();
        assertEquals(2, classInstanceMap.size());
        assertTrue(classInstanceMap.containsKey("Switch"));
        assertTrue(classInstanceMap.containsKey("Router"));
        assertEquals(4, classInstanceMap.get("Switch").size());
        assertEquals(2, classInstanceMap.get("Router").size());
    }
    
    public void testExecuteDoesNotSearchChildClassesWithExpressionNotEnabled() throws Exception {
        SmartsTestUtils.deleteAllTopologyInstances("Router", ".*");
        SmartsTestUtils.createTopologyInstance("Router", "r1");
        
        List<String> atts = new ArrayList<String>();
        atts.add("CreationClassName");
        FindTopologyInstancesQueryMockWithCallCount query = new FindTopologyInstancesQueryMockWithCallCount(TestLogUtils.log, 
                                                                                SmartsTestUtils.getTopologyAdapter(), "Router", 
                                                                                "r1", atts, 10, false);
        query.execute();
        assertEquals("Query also searched for child classes", 1, query.populateInstancesCallCount);
    }

    class FindTopologyInstancesQueryMockWithCallCount extends FindTopologyInstancesQuery
    {
        int populateInstancesCallCount = 0;
        public FindTopologyInstancesQueryMockWithCallCount(Logger logger,
                BaseSmartsAdapter smartsAdapter, String creationClassName,
                String name, List<String> attributes, int fetchSize,
                boolean expressionsEnabled) {
            super(logger, smartsAdapter, creationClassName, name, attributes, fetchSize,
                    expressionsEnabled);
            populateInstancesCallCount = 0;
        }

        @Override
        protected void populateClassInstanceMap(
                Map<String, LinkedList<String>> classInstanceMap,
                String className) throws Exception {
            populateInstancesCallCount ++;
            super.populateClassInstanceMap(classInstanceMap, className);
        }
        
        
        
    }
}

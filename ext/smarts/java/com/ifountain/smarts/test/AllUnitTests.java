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
 * Created on Feb 19, 2008
 *
 * Author Sezgin
 */
package com.ifountain.smarts.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.ifountain.smarts.connection.SmartsConnectionTest;
import com.ifountain.smarts.datasource.BaseNotificationAdapterTest;
import com.ifountain.smarts.datasource.BaseSmartsAdapterTest;
import com.ifountain.smarts.datasource.BaseTopologyAdapterTest;
import com.ifountain.smarts.datasource.actions.CreateInstanceActionTest;
import com.ifountain.smarts.datasource.actions.DeleteInstanceActionTest;
import com.ifountain.smarts.datasource.actions.FindInstancesActionTest;
import com.ifountain.smarts.datasource.actions.GetActionTest;
import com.ifountain.smarts.datasource.actions.GetAllPropertiesActionTest;
import com.ifountain.smarts.datasource.actions.GetAttributeNamesActionTest;
import com.ifountain.smarts.datasource.actions.GetAttributeTypesActionTest;
import com.ifountain.smarts.datasource.actions.GetChildrenActionTest;
import com.ifountain.smarts.datasource.actions.GetInstancesActionTest;
import com.ifountain.smarts.datasource.actions.GetPropNamesActionTest;
import com.ifountain.smarts.datasource.actions.GetPropTypeActionTest;
import com.ifountain.smarts.datasource.actions.GetPropertiesActionTest;
import com.ifountain.smarts.datasource.actions.GetRelationNamesActionTest;
import com.ifountain.smarts.datasource.actions.GetRelationTypesActionTest;
import com.ifountain.smarts.datasource.actions.GetReverseRelationActionTest;
import com.ifountain.smarts.datasource.actions.InsertActionTest;
import com.ifountain.smarts.datasource.actions.InstanceExistsActionTest;
import com.ifountain.smarts.datasource.actions.InvokeOperationActionTest;
import com.ifountain.smarts.datasource.actions.PutActionTest;
import com.ifountain.smarts.datasource.actions.RemoveActionTest;
import com.ifountain.smarts.datasource.queries.FindTopologyInstancesQueryTest;
import com.ifountain.smarts.datasource.queries.InstanceSetTest;
import com.ifountain.smarts.test.util.SmartsTestCase;
import com.ifountain.smarts.util.SmartsHelperTest;
import com.ifountain.smarts.util.SmartsPropertyHelperTest;
import com.ifountain.smarts.util.property.MRArraySetToEntryTest;
import com.ifountain.smarts.util.property.MRArrayToEntryTest;
import com.ifountain.smarts.util.property.MRObjRefSetToEntryTest;
import com.ifountain.smarts.util.property.MRObjRefToEntryTest;
import com.ifountain.smarts.util.property.MRPrimitiveSetToEntryTest;

public class AllUnitTests extends SmartsTestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AllUnitTests.class);
    }
    
    public static Test suite()
    {
        TestSuite suite = new TestSuite(AllUnitTests.class.getName());
        suite.addTestSuite(SmartsConnectionTest.class);
        suite.addTestSuite(GetAttributeNamesActionTest.class);
        suite.addTestSuite(FindInstancesActionTest.class);
        suite.addTestSuite(GetRelationNamesActionTest.class);
        suite.addTestSuite(GetChildrenActionTest.class);
        suite.addTestSuite(GetPropTypeActionTest.class);
        suite.addTestSuite(CreateInstanceActionTest.class);
        suite.addTestSuite(DeleteInstanceActionTest.class);
        suite.addTestSuite(GetActionTest.class);
        suite.addTestSuite(GetAllPropertiesActionTest.class);
        suite.addTestSuite(GetAttributeTypesActionTest.class);
        suite.addTestSuite(GetInstancesActionTest.class);
        suite.addTestSuite(GetPropertiesActionTest.class);
        suite.addTestSuite(GetReverseRelationActionTest.class);
        suite.addTestSuite(InvokeOperationActionTest.class);
        suite.addTestSuite(PutActionTest.class);
        suite.addTestSuite(GetPropNamesActionTest.class);
        suite.addTestSuite(GetRelationTypesActionTest.class);
        suite.addTestSuite(InsertActionTest.class);
        suite.addTestSuite(InstanceExistsActionTest.class);
        suite.addTestSuite(RemoveActionTest.class);
        suite.addTestSuite(BaseNotificationAdapterTest.class);
        suite.addTestSuite(BaseSmartsAdapterTest.class);
        suite.addTestSuite(BaseTopologyAdapterTest.class);
        suite.addTestSuite(SmartsHelperTest.class);
        suite.addTestSuite(SmartsPropertyHelperTest.class);
        suite.addTestSuite(FindTopologyInstancesQueryTest.class);
        suite.addTestSuite(InstanceSetTest.class);
        suite.addTestSuite(MRArraySetToEntryTest.class);
        suite.addTestSuite(MRArrayToEntryTest.class);
        suite.addTestSuite(MRObjRefSetToEntryTest.class);
        suite.addTestSuite(MRObjRefToEntryTest.class);
        suite.addTestSuite(MRPrimitiveSetToEntryTest.class);
        return suite;
    }
}

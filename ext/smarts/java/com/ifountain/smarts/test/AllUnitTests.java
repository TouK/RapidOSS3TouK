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

import com.ifountain.smarts.connection.SmartsConnectionTest;
import com.ifountain.smarts.datasource.*;
import com.ifountain.smarts.datasource.actions.*;
import com.ifountain.smarts.datasource.queries.FindTopologyInstancesQueryTest;
import com.ifountain.smarts.datasource.queries.InstanceSetTest;
import com.ifountain.smarts.test.util.SmartsTestCase;
import com.ifountain.smarts.util.SmartsHelperTest;
import com.ifountain.smarts.util.SmartsPropertyHelperTest;
import com.ifountain.smarts.util.property.*;
import junit.framework.Test;
import junit.framework.TestSuite;

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
        suite.addTestSuite(InvokeOperationWithNativeParamsActionTest.class);
        suite.addTestSuite(PutActionTest.class);
        suite.addTestSuite(GetPropNamesActionTest.class);
        suite.addTestSuite(GetRelationTypesActionTest.class);
        suite.addTestSuite(InsertActionTest.class);
        suite.addTestSuite(InstanceExistsActionTest.class);
        suite.addTestSuite(InvokeOperationActionTest.class);
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
        suite.addTestSuite(StagingAreaTest.class);
        suite.addTestSuite(BaseSmartsListeningAdapterTest.class);
        suite.addTestSuite(SmartsTopologyListeningAdapterTest.class);
        suite.addTestSuite(SmartsNotificationListeningAdapterTest.class);
        return suite;
    }
}

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
 * Created on Mar 8, 2007
 *
 * Author Sezgin kucukkaraaslan
 */
package com.ifountain.core.test;
import junit.framework.Test;
import junit.framework.TestSuite;

import com.ifountain.core.test.util.RapidCoreTestCase;

public class AllTests extends RapidCoreTestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AllTests.class);
    }


    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public static Test suite()
    {
        TestSuite suite = new TestSuite(AllTests.class.getName());
        suite.addTestSuite(TestInclusionTest.class);
        suite.addTest(AllUnitTests.suite());
        return suite;
    }
}

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created on Feb 19, 2008
 *
 * Author Sezgin
 */
package com.ifountain.smarts.test;

import com.ifountain.smarts.test.util.SmartsTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests extends SmartsTestCase {

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

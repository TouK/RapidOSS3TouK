/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created on Feb 19, 2008
 *
 * Author Sezgin
 */
package com.ifountain.smarts.test;

import com.ifountain.comp.test.util.testcase.AbstractTestInclusionTestCase;

public class TestInclusionTest extends AbstractTestInclusionTestCase {

    @Override
    public Class[] getAllTestSuiteClasses() {
        return new Class[]{AllTests.class};
    }

    @Override
    public String getRootPath() {
        return "ext/smarts/java/com/ifountain/smarts";
    }


}

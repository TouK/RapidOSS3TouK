package com.ifountain.rcmdb.test.util

import com.ifountain.comp.test.util.testcase.RapidTestCase

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Apr 18, 2008
* Time: 2:26:55 PM
* To change this template use File | Settings | File Templates.
*/
class RapidCmdbTestCase extends RapidTestCase{
    def defaultBaseDir;
    protected void setUp() {
        defaultBaseDir = System.getProperty("base.dir", ".");
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        System.setProperty("base.dir", defaultBaseDir);
    }

}
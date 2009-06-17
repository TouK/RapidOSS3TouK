package com.ifountain.rcmdb.test.util

import com.thoughtworks.selenium.SeleneseTestCase
import com.thoughtworks.selenium.DefaultSelenium
import com.ifountain.comp.test.util.CommonTestUtils


/**
* Created by IntelliJ IDEA.
* User: fadime
* Date: Jun 10, 2009
* Time: 1:13:32 AM
* To change this template use File | Settings | File Templates.
*/
class SeleniumTestCase extends SeleneseTestCase {

    public static DefaultSelenium selenium;
    private static boolean start = true;
    static{
        CommonTestUtils.initializeFromFile("RCMDBTest.properties");
    }

    public static void suiteSetUp(url, browser) {

       // selenium = new DefaultSelenium(SeleniumTestUtils.getSeleniumServerHost(),
        selenium = new DefaultSelenium("192.168.1.111",
                Integer.parseInt(SeleniumTestUtils.getSeleniumServerPort()), browser, url);
        selenium.start();
        selenium.setTimeout("30000");

        System.addShutdownHook {
            selenium.stop();
        }
    }


    void setUp(String url, String browser) throws Exception {

        CommonTestUtils.initializeFromFile("RCMDBTest.properties");
        if (start) {
            start = false;
            suiteSetUp(url, browser);
        }
        selenium.open(url);
        selenium.waitForPageToLoad("30000");
    }

}
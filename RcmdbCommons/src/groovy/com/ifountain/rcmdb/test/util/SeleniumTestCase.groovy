package com.ifountain.rcmdb.test.util

import com.thoughtworks.selenium.SeleneseTestCase
import com.thoughtworks.selenium.DefaultSelenium
import com.thoughtworks.selenium.SeleniumException
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

    public static void suiteSetUp(browserString, url) {
        CommonTestUtils.initializeFromFile("RCMDBTest.properties");
        selenium = new DefaultSelenium(SeleniumTestUtils.getSeleniumServerHost(), SeleniumTestUtils.getSeleniumServerPort(), browserString, url);
        selenium.start();
        selenium.setTimeout("30000");

        System.addShutdownHook {
            selenium.stop();
        }
    }


     void setUp(String url, String browserString) throws Exception {

        if (start) {
            start = false;
            suiteSetUp(browserString, url);
        }
                selenium.open(url);
                selenium.waitForPageToLoad("30000");
    }

}
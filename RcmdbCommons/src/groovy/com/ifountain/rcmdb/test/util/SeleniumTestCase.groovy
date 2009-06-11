package com.ifountain.rcmdb.test.util

import com.thoughtworks.selenium.SeleneseTestCase
import com.thoughtworks.selenium.DefaultSelenium

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
        selenium = new DefaultSelenium("localhost", 4444, browserString, url);
        selenium.start();
        selenium.setTimeout("300000");
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
        selenium.waitForPageToLoad("300000");
    }

}
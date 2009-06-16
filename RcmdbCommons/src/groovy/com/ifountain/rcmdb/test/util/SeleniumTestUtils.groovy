package com.ifountain.rcmdb.test.util

import com.ifountain.comp.test.util.CommonTestUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jun 16, 2009
* Time: 11:48:31 AM
* To change this template use File | Settings | File Templates.
*/
class SeleniumTestUtils {
    public static String getSeleniumServerHost()
    {
        return CommonTestUtils.getTestProperty("Selenium.ServerHost");
    }

    public static String getSeleniumServerPort()
    {
        return CommonTestUtils.getTestProperty("Selenium.ServerPort");
    }
}
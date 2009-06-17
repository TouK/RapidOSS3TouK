package com.ifountain.rcmdb.test.util

import com.ifountain.comp.test.util.CommonTestUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jun 16, 2009
* Time: 11:48:31 AM
* To change this template use File | Settings | File Templates.
*/
class SeleniumTestUtils  {



    public static String getRsHome()
    {
        return CommonTestUtils.getTestProperty("Selenium.RS_HOME");
    }

    public static String getSeleniumServerHost()
    {
        return CommonTestUtils.getTestProperty("Selenium.ServerHost");
    }

    public static String getSeleniumServerPort()
    {
        return CommonTestUtils.getTestProperty("Selenium.ServerPort");
    }
     public static String getRIHost()
    {
        return CommonTestUtils.getTestProperty("Selenium.RIHost");
    }
     public static String getRIPort()
    {
        return CommonTestUtils.getTestProperty("Selenium.RIPort");
    }
      public static String  getSeleniumBrowser()
    {
        return CommonTestUtils.getTestProperty("Selenium.browser");
    }
     public static void createScript(String path,String scriptContent)
     {
          File file = new File(path)
          file.setText(scriptContent)
     }

}
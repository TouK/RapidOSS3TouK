package com.ifountain.rcmdb.test.util

import com.ifountain.comp.test.util.CommonTestUtils
import com.thoughtworks.selenium.DefaultSelenium
import com.thoughtworks.selenium.SeleneseTestCase
import com.thoughtworks.selenium.Selenium
import org.codehaus.groovy.runtime.InvokerHelper

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
        registerDynamicMethodsToSelenium();
    }

    public static void registerDynamicMethodsToSelenium()
    {
        def utilityClassNamesToBeTried = ["utils.UserGroupUiTestUtilities", "utils.ScriptUiUtilities"]
        def utilityClassesToBeTried = []
        utilityClassNamesToBeTried.each{
            try{
                utilityClassesToBeTried << SeleniumTestCase.class.classLoader.loadClass(it);
            }catch(Throwable e)
            {
            }
        }
        DefaultSelenium.metaClass.clickAndWait = {String url->
            delegate.clickAndWait(url, "30000");
        }
        DefaultSelenium.metaClass.clickAndWait = {String url, String time->
            delegate.click(url);
            delegate.waitForPageToLoad(time);
        }
        DefaultSelenium.metaClass.openAndWait = {String url->
            delegate.openAndWait(url, "30000");
        }
        DefaultSelenium.metaClass.getPageText = {->
            return selenium.getEval("new XMLSerializer().serializeToString(this.browserbot.getCurrentWindow().document)")
        }
        DefaultSelenium.metaClass.openAndWait = {String url, String time->
            delegate.setTimeout (time);
            try{
                delegate.open(url);
            }finally{
                delegate.setTimeout ("30000");
            }
        }

        DefaultSelenium.metaClass.methodMissing = {String methodName, params ->
            def newParams = new ArrayList(InvokerHelper.asList(params));
            newParams.add(0, delegate);
            for(int i=0; i < utilityClassesToBeTried.size(); i++){
                Class utilityClass = utilityClassesToBeTried[i];
                try{
                    def res = utilityClass.metaClass.invokeStaticMethod(utilityClass, methodName, newParams as Object[]);
                    return res;
                }
                catch(MissingMethodException ex)
                {
                    if(ex.getMethod() != methodName || ex.getType().name != utilityClass.name)
                    {
                        throw ex;
                    }
                }
            }
            throw new MissingMethodException(methodName, Selenium, params);
        }
    }

    public static void suiteSetUp(url, browser) {

        selenium = new DefaultSelenium(SeleniumTestUtils.getSeleniumServerHost(),
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
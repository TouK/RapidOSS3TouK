package com.ifountain.rcmdb.test.util

import com.thoughtworks.selenium.SeleneseTestCase
import com.thoughtworks.selenium.DefaultSelenium
import com.thoughtworks.selenium.SeleniumException
import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;



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
        selenium.setTimeout("30000");

        System.addShutdownHook {
            selenium.stop();
        }
    }

       public void timerThreadsWontBeTerminated() throws Exception {

             RemoteControlConfiguration conf = new RemoteControlConfiguration();
             conf.setPort( 4444 );
             conf.setRetryTimeoutInSeconds(5);
             conf.setReuseBrowserSessions( false );
             conf.setTrustAllSSLCertificates( true );
             conf.setTimeoutInSeconds( 20 );
             
                server = new SeleniumServer( conf );
                server.start();


     }

     void setUp(String url, String browserString) throws Exception {

        if (start) {
            start = false;
            suiteSetUp(browserString, url);
        }
        for(int i=0; i < 10; i++)
        {
            try{
                selenium.open(url);
                selenium.waitForPageToLoad("300000");
                break;
            }
            catch(SeleniumException e)
            {
                if(i == 9)
                {
                    throw e;
                }
            }
        }
    }

}
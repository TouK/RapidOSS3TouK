package connection

import com.ifountain.core.test.util.RapidCoreTestCase
import datasource.DoRequestAction
import com.ifountain.comp.test.util.logging.TestLogUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Dec 29, 2008
* Time: 4:07:08 PM
* To change this template use File | Settings | File Templates.
*/
class DoRequestActionTest extends RapidCoreTestCase{


      void testGetCompleteUrlManagesSlashes(){
         def url = "RapidSuite/script/list"
         DoRequestAction action = new DoRequestAction(TestLogUtils.log, url, [:], DoRequestAction.GET);
         def baseUrl = "http://localhost:12222"

         def completeUrl = action.getCompleteUrl(baseUrl);
         assertEquals("http://localhost:12222/RapidSuite/script/list", completeUrl);

         baseUrl = "http://localhost:12222/"
         completeUrl = action.getCompleteUrl(baseUrl);
         assertEquals("http://localhost:12222/RapidSuite/script/list", completeUrl);

         url = "/RapidSuite/script/list"
         action = new DoRequestAction(TestLogUtils.log, url, [:], DoRequestAction.GET);
         baseUrl = "http://localhost:12222/"

         completeUrl = action.getCompleteUrl(baseUrl);
         assertEquals("http://localhost:12222/RapidSuite/script/list", completeUrl);

         baseUrl = "http://localhost:12222"

         completeUrl = action.getCompleteUrl(baseUrl);
         assertEquals("http://localhost:12222/RapidSuite/script/list", completeUrl);
      }

}
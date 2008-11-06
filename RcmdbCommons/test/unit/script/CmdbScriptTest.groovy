package script
import com.ifountain.core.test.util.RapidCoreTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import org.apache.log4j.Level;
/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Oct 27, 2008
 * Time: 1:25:00 PM
 * To change this template use File | Settings | File Templates.
 */
class CmdbScriptTest extends RapidCoreTestCase{


     void testCreateStaticParams(){

         CompassForTests.addOperationSupport (CmdbScript, CmdbScriptOperations);

         CmdbScript script=new CmdbScript(staticParam:"x:5,y:6");
         def mapParams=CmdbScript.getStaticParamMap(script);
         assertEquals(mapParams.x,"5");
         assertEquals(mapParams.y,"6");
         assertEquals(mapParams.size(),2);

         CmdbScript scriptWithNoStaticParam=new CmdbScript();
         def mapParams2=CmdbScript.getStaticParamMap(scriptWithNoStaticParam);
         assertEquals(mapParams2.size(),0);

         CmdbScript scriptWithCustomParam=new CmdbScript(staticParam:"xyz-dsfdfdf");
         def mapParams3=CmdbScript.getStaticParamMap(scriptWithCustomParam);
         assertEquals(mapParams3.size(),0);
                  

     }

     void testConfigureScriptLogger()
     {
          CompassForTests.addOperationSupport (CmdbScript, CmdbScriptOperations);

          def logLevel=Level.DEBUG;
          def scriptParams=[:]
          scriptParams["name"]="testscript";
          scriptParams["logFile"]="testscript";
          scriptParams["logLevel"]=logLevel.toString();
          scriptParams["logFileOwn"]=false;
          
          CmdbScript script=new CmdbScript(name:scriptParams.name,logFile:scriptParams.logFile,logFileOwn:scriptParams.logFileOwn,logLevel:scriptParams.logLevel);

          def logger=null;
          
          CmdbScript.configureScriptLogger(script);
          logger=CmdbScript.getScriptLogger(script);
          assertEquals(logger.getLevel(),logLevel);
          assertFalse(logger.getAllAppenders().hasMoreElements());

          script.logFileOwn=true;          
          script.logLevel=Level.INFO.toString();
          CmdbScript.configureScriptLogger(script);
          logger=CmdbScript.getScriptLogger(script);
          assertEquals(logger.getLevel(),Level.INFO);
          assertTrue(logger.getAllAppenders().hasMoreElements());

          
          
     }
     
}
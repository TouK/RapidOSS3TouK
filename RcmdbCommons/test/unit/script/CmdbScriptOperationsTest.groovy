/*
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
package script
import com.ifountain.core.test.util.RapidCoreTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import org.apache.log4j.Level;
import com.ifountain.rcmdb.datasource.ListeningAdapterManager;
import datasource.BaseListeningDatasource
import com.ifountain.rcmdb.datasource.BaseListeningDatasourceMock;


/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Oct 27, 2008
 * Time: 1:25:00 PM
 * To change this template use File | Settings | File Templates.
 */
class CmdbScriptOperationsTest extends RapidCoreTestCase{

    protected void setUp() {
         super.setUp()

     }
     protected void tearDown() {
        super.tearDown()
        GroovySystem.metaClassRegistry.removeMetaClass(ListeningAdapterManager)
        ListeningAdapterManager.destroyInstance();
     }

     void testBeforeDelete(){
        CompassForTests.initialize([CmdbScript]);
        CompassForTests.addOperationSupport (CmdbScript, CmdbScriptOperations);


        def ds=new BaseListeningDatasourceMock(name:"myds");
        CmdbScript script=new CmdbScript(name:"testscript",type:CmdbScript.LISTENING,listeningDatasource:ds);

        CompassForTests.getOperationData.setObjectsWillBeReturned([script]);

        def stoppedDatasource=null;
        ListeningAdapterManager.metaClass.stopAdapter= { BaseListeningDatasource listeningDatasource ->
            stoppedDatasource = listeningDatasource;
        }
        assertNull(stoppedDatasource);
        script.beforeDelete();
        assertEquals(stoppedDatasource,ds);

     }
     void testBeforeUpdate(){
        CompassForTests.initialize([CmdbScript]);
        CompassForTests.addOperationSupport (CmdbScript, CmdbScriptOperations);


        def ds=new BaseListeningDatasourceMock(name:"myds");
        CmdbScript script=new CmdbScript(name:"testscript",type:CmdbScript.LISTENING,listeningDatasource:ds);

        CompassForTests.getOperationData.setObjectsWillBeReturned([script]);

        def stoppedDatasource=null;
        ListeningAdapterManager.metaClass.stopAdapter= { BaseListeningDatasource listeningDatasource ->
            stoppedDatasource = listeningDatasource;
        }
        assertNull(stoppedDatasource);
        script.beforeUpdate();
        assertEquals(stoppedDatasource,ds);

     }




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


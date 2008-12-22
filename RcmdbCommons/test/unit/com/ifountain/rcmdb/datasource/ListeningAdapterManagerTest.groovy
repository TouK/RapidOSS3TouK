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
package com.ifountain.rcmdb.datasource
import datasource.*;
import script.*;
import org.apache.log4j.Level;
import com.ifountain.rcmdb.test.util.CompassForTests;
import com.ifountain.rcmdb.scripting.ScriptManager;
import com.ifountain.core.datasource.BaseListeningAdapter;

/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Oct 23, 2008
 * Time: 4:03:10 PM
 * To change this template use File | Settings | File Templates.
 */
class ListeningAdapterManagerTest extends GroovyTestCase{

    static def scriptMap=[:]
    void testStartAdapterThrowsExceptionWhenNoListeningScriptIsDefined(){
        def ds=new BaseListeningDatasource();
        try {

            ListeningAdapterManager.getInstance().startAdapter(ds);
            fail("Should throw exception");
        }
        catch(e)
        {
            println e;
        }

        ds.listeningScript=new CmdbScript(name:"dummysc",type:CmdbScript.ONDEMAND);
        try {

            ListeningAdapterManager.getInstance().startAdapter(ds);
            fail("Should throw exception");
        }
        catch(e)
        {
            println e;
        }
    }
    void testAdapterCreatesScriptLogger()
    {
        def logFile="ListeningAdapterManagerTestScript";
        def logLevel=Level.DEBUG;

        ScriptManager.getInstance().initialize(this.class.getClassLoader(), System.getProperty("base.dir"), []);
        ListeningAdapterManager.getInstance().initialize();
        CompassForTests.addOperationSupport (CmdbScript, CmdbScriptOperations);

        CompassForTests.initialize([CmdbScript]);
        CompassForTests.addOperationData.setObjectsWillBeReturned([new CmdbScript(name:"dummysc",type:CmdbScript.LISTENING,scriptFile:"ListeningAdapterManagerTestScript",logFile:logFile,logLevel:logLevel.toString())]);
        def script=CmdbScript.addScript(name:"dummysc",type:CmdbScript.LISTENING,scriptFile:"ListeningAdapterManagerTestScript");
        
        def ds=new BaseListeningDatasourceMock();
        ds.listeningScript=script;

        ListeningAdapterManager.getInstance().startAdapter(ds);

        assertEquals(scriptMap.logger.getLevel(),Level.DEBUG)

    }
    void testAdapterRunsTheScript()
    {
        ScriptManager.getInstance().initialize(this.class.getClassLoader(), System.getProperty("base.dir"), []);
        ListeningAdapterManager.getInstance().initialize();
        CompassForTests.addOperationSupport (CmdbScript, CmdbScriptOperations);
        CompassForTests.addOperationSupport (BaseListeningDatasource, BaseListeningDatasourceOperations);
        
        
        CompassForTests.initialize([CmdbScript]);
        CompassForTests.addOperationData.setObjectsWillBeReturned([new CmdbScript(name:"dummysc",type:CmdbScript.LISTENING,scriptFile:"ListeningAdapterManagerTestScript",staticParam:"x:5" )]);
        def script=CmdbScript.addScript(name:"dummysc",type:CmdbScript.LISTENING,scriptFile:"ListeningAdapterManagerTestScript");

        def ds=new BaseListeningDatasourceMock();
        ds.listeningScript=script;

        ListeningAdapterManager.getInstance().startAdapter(ds);



        assertEquals(scriptMap.logger,CmdbScript.getScriptLogger(script));
        assertEquals(scriptMap.datasource,ds);
        assertEquals(scriptMap.staticParam,script.staticParam);
        assertEquals(scriptMap.staticParamMap.x,CmdbScript.getStaticParamMap(script).x);
        

        assertEquals(scriptMap.scriptRunStarted,true);
        assertEquals(scriptMap.scriptRunEnded,true);

        assertEquals(scriptMap.scriptInitInvoked,true);

        assertNotNull(ds.listeningAdapter);
        assertNotNull(ds.listeningAdapter.listeningAdapterObserver);
        assertEquals(ds.listeningAdapter.subscribeCalled,true);


        assertNotNull(ds.adapterParams)
        assertEquals(ds.adapterParams.returnparam1,"param1")
        assertEquals(ds.adapterParams.logger,CmdbScript.getScriptLogger(script))

        
    }

    void testStopAdapter()
    {
        ScriptManager.getInstance().initialize(this.class.getClassLoader(), System.getProperty("base.dir"), []);
        ListeningAdapterManager.getInstance().initialize();
        CompassForTests.addOperationSupport (CmdbScript, CmdbScriptOperations);
        CompassForTests.addOperationSupport (BaseListeningDatasource, BaseListeningDatasourceOperations);


        CompassForTests.initialize([CmdbScript]);
        CompassForTests.addOperationData.setObjectsWillBeReturned([new CmdbScript(name:"dummysc",type:CmdbScript.LISTENING,scriptFile:"ListeningAdapterManagerTestScript",logFileOwn:true)]);
        def script=CmdbScript.addScript(name:"dummysc",type:CmdbScript.LISTENING,scriptFile:"ListeningAdapterManagerTestScript",logFileOwn:true);

        def ds=new BaseListeningDatasourceMock();
        ds.listeningScript=script;

        ListeningAdapterManager.getInstance().stopAdapter(ds);
        
        ListeningAdapterManager.getInstance().startAdapter(ds);
        ListeningAdapterManager.getInstance().stopAdapter(ds);

        assertEquals(ds.listeningAdapter.unsubscribeCalled,true);
        assertEquals(ds.listeningAdapter.countObservers(),0);

        assertEquals(scriptMap.cleanUpInvoked,true);

        //assertFalse(CmdbScript.getScriptLogger(script).getAllAppenders().hasMoreElements());
    }
}


class BaseListeningDatasourceMock extends BaseListeningDatasource
{
    
    BaseListeningAdapterMock listeningAdapter=null;
    Map adapterParams=null;        
     def getListeningAdapter(Map params){
         adapterParams=params;
         listeningAdapter=new BaseListeningAdapterMock();
         return listeningAdapter;
    }
}

class BaseListeningAdapterMock extends BaseListeningAdapter
{
    ListeningAdapterObserver listeningAdapterObserver=null;
    def subscribeCalled=false;
    def unsubscribeCalled=false;
    
    public BaseListeningAdapterMock()
    {
       super(null, 0, null);
    }
    public Object _update(Observable o, Object arg)
    {
      return null;
    }
    protected void _subscribe() throws Exception
    {

    }
    public void subscribe() throws Exception
    {
        subscribeCalled=true;
    }
    public void unsubscribe() throws Exception
    {
        unsubscribeCalled=true;
    }
    protected void _unsubscribe()
    {

    }

    public synchronized void addObserver(ListeningAdapterObserver observer)
    {
        this.listeningAdapterObserver=observer;
        super.addObserver(observer);
    }

    
}


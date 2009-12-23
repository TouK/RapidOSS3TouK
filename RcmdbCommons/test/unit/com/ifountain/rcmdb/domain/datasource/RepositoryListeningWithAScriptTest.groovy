package com.ifountain.rcmdb.domain.datasource

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.datasource.ListeningAdapterManager
import com.ifountain.core.connection.ConnectionManager
import com.ifountain.comp.test.util.logging.TestLogUtils
import com.ifountain.core.test.util.DatasourceTestUtils
import com.ifountain.core.connection.ConnectionParam
import connection.RepositoryConnection
import script.CmdbScript
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.rcmdb.util.ClosureWaitAction
import com.ifountain.core.datasource.AdapterStateProvider
import com.ifountain.rcmdb.scripting.ScriptManager
import com.ifountain.rcmdb.domain.connection.RepositoryConnectionImpl
import com.ifountain.rcmdb.test.util.RsApplicationTestUtils
import application.RsApplication
import datasource.RepositoryDatasource
import datasource.BaseListeningDatasource
import com.ifountain.rcmdb.test.util.CompassForTests
import script.CmdbScriptOperations
import datasource.RepositoryDatasourceOperations
import datasource.BaseListeningDatasourceOperations
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.util.DataStore
import com.ifountain.comp.utils.SmartWait
import com.ifountain.rcmdb.domain.method.EventTriggeringUtils
import com.ifountain.rcmdb.domain.ObjectProcessor
import com.ifountain.rcmdb.datasource.RunnerObject
import com.ifountain.rcmdb.datasource.ListeningAdapterRunner
import com.ifountain.core.datasource.AdapterStateProvider

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 10, 2009
* Time: 3:23:22 PM
* To change this template use File | Settings | File Templates.
*/
class RepositoryListeningWithAScriptTest extends RapidCmdbWithCompassTestCase {

    def repoScriptClass;
    def modelClass1;
    def modelClass2;
    public void setUp() {
        super.setUp();
        clearMetaClasses();

//        TestLogUtils.enableLogger ();

        ScriptManager.metaClass.checkScript= { String scriptPath ->
        }

        ScriptManager.metaClass.addScript= { String scriptPath ->
        }

        ListeningAdapterManager.getInstance().initialize();
        ConnectionManager.initialize(TestLogUtils.log, DatasourceTestUtils.getParamSupplier(), Thread.currentThread().getContextClassLoader(), 1000);

        initializeModels();

        initializeScripts();


    }

    public void tearDown() {
        clearMetaClasses();
        super.tearDown();

    }
    public void clearMetaClasses()
    {
        ListeningAdapterManager.destroyInstance();
        if(ConnectionManager.isInitialized())
        {
            ConnectionManager.destroy();
        }
        ScriptManager.destroyInstance();

        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(CmdbScript);
        GroovySystem.metaClassRegistry.removeMetaClass(ScriptManager);
        ExpandoMetaClass.enableGlobally();
    }

    public void initializeModels()
    {

        def model1Name = "Model1";
        def model1datasource = [keys:[[propertyName:"prop1"]]]
        def model1prop1 = [name: "prop1", type: ModelGenerator.STRING_TYPE];
        def model1prop2 = [name: "prop2", type: ModelGenerator.NUMBER_TYPE];
        def model1prop3 = [name: "prop3", type: ModelGenerator.BOOLEAN_TYPE];
        def model1MetaProps = [name: model1Name]
        def model1Props = [model1prop1, model1prop2,model1prop3];
        def model1keyPropList = [model1prop1];


        def model1Text = ModelGenerationTestUtils.getModelText(model1MetaProps, [model1datasource], model1Props, model1keyPropList, []);
        modelClass1 = gcl.parseClass (model1Text);

        def model2Name = "Model2";
        def model2datasource = [:]
        def model2prop1 = [name: "propa", type: ModelGenerator.STRING_TYPE];
        def model2prop2 = [name: "propb", type: ModelGenerator.NUMBER_TYPE];
        def model2prop3 = [name: "propc", type: ModelGenerator.BOOLEAN_TYPE];
        def model2MetaProps = [name: model2Name]
        def model2Props = [model2prop1, model2prop2,model2prop3];
        def model2keyPropList = [];


        def model2Text = ModelGenerationTestUtils.getModelText(model2MetaProps, [model2datasource], model2Props, model2keyPropList, []);
        modelClass2 = gcl.parseClass (model2Text);

        initialize([modelClass1,modelClass2,CmdbScript, RepositoryConnection, RepositoryDatasource, BaseListeningDatasource, relation.Relation], []);
        CompassForTests.addOperationSupport(CmdbScript, CmdbScriptOperations);
        CompassForTests.addOperationSupport(RepositoryDatasource, RepositoryDatasourceOperations);
        CompassForTests.addOperationSupport(BaseListeningDatasource, BaseListeningDatasourceOperations);

        RsApplicationTestUtils.initializeRsApplicationOperations(RsApplication);


    }



    public void testRepositoryListeningScript()
    {
        def connectionName = "repoConn"

        DatasourceTestUtils.getParamSupplier().setParam(new ConnectionParam("RepositoryConnection", connectionName, RepositoryConnectionImpl.class.getName(), [:], 10, 1000, 0));


        def conn = RepositoryConnection.add(name: RepositoryConnection.RCMDB_REPOSITORY);
        def listeningScript = CmdbScript.addScript([name: "repoListeningScript", type: CmdbScript.LISTENING, listenToRepository: true],true)
        assertFalse(conn.hasErrors())
        assertFalse(listeningScript.hasErrors())

        def listeningHistory=[];
        DataStore.put("listeningHistory",listeningHistory);

        assertFalse(listeningScript.listeningDatasource.getListeningAdapter([:],TestLogUtils.log).isConversionEnabledForUpdate());

        //        DataStore.get("listeningHistory").each{
        //            println it;
        //        };

        try{


            CmdbScript.startListening(listeningScript);
            SmartWait.waitFor(new ClosureWaitAction({
                assertEquals(AdapterStateProvider.STARTED, ListeningAdapterManager.getInstance().getState(listeningScript.listeningDatasource));
            }))

            //test creates
            def model1ins1=modelClass1.add(prop1:"ev1")
            def model1ins2=modelClass1.add(prop1:"ev2",prop2:55,prop3:true)
            def model1ins3=modelClass1.add(prop1:"ev3",prop2:null,prop3:false)

            SmartWait.waitFor(new ClosureWaitAction({
                assertEquals(3,listeningHistory.size());
            }))

            def historyIndex=0;
            [model1ins1,model1ins2,model1ins3].each{ instance ->

                assertEquals(EventTriggeringUtils.AFTER_INSERT_EVENT,listeningHistory[historyIndex][ObjectProcessor.EVENT_NAME])
                assertEquals(instance.class,listeningHistory[historyIndex][ObjectProcessor.DOMAIN_OBJECT].class)
                assertEquals(instance.prop1,listeningHistory[historyIndex][ObjectProcessor.DOMAIN_OBJECT].prop1)
                assertEquals(instance.prop2,listeningHistory[historyIndex][ObjectProcessor.DOMAIN_OBJECT].prop2)
                assertEquals(instance.prop3,listeningHistory[historyIndex][ObjectProcessor.DOMAIN_OBJECT].prop3)
                assertEquals(2,listeningHistory[historyIndex].size());

                historyIndex++;
            }

            //test updates
            model1ins2.update(prop2:45,prop3:false);
            //in this case no update history will be added since update is ignored
            model1ins2.update(prop2:45,prop3:false);
            model1ins2.update(prop2:65,prop3:false);

            SmartWait.waitFor(new ClosureWaitAction({
                assertEquals(5,listeningHistory.size());
            }))


            assertEquals(3,listeningHistory[3].size());
            assertEquals(EventTriggeringUtils.AFTER_UPDATE_EVENT,listeningHistory[3][ObjectProcessor.EVENT_NAME]);
            assertEquals(modelClass1,listeningHistory[3][ObjectProcessor.DOMAIN_OBJECT].class);
            assertEquals(model1ins2.id,listeningHistory[3][ObjectProcessor.DOMAIN_OBJECT].id);
            assertEquals(2,listeningHistory[3][ObjectProcessor.UPDATED_PROPERTIES].size());
            assertEquals(55,listeningHistory[3][ObjectProcessor.UPDATED_PROPERTIES]["prop2"]);
            assertEquals(true,listeningHistory[3][ObjectProcessor.UPDATED_PROPERTIES]["prop3"]);

            assertEquals(3,listeningHistory[4].size());
            assertEquals(EventTriggeringUtils.AFTER_UPDATE_EVENT,listeningHistory[4][ObjectProcessor.EVENT_NAME]);
            assertEquals(modelClass1,listeningHistory[4][ObjectProcessor.DOMAIN_OBJECT].class);
            assertEquals(model1ins2.id,listeningHistory[4][ObjectProcessor.DOMAIN_OBJECT].id);
            assertEquals(1,listeningHistory[4][ObjectProcessor.UPDATED_PROPERTIES].size());
            assertEquals(45,listeningHistory[4][ObjectProcessor.UPDATED_PROPERTIES]["prop2"]);

            //test removes
            model1ins3.remove();
            model1ins2.remove();

            SmartWait.waitFor(new ClosureWaitAction({
                assertEquals(7,listeningHistory.size());
            }))

            assertEquals(2,listeningHistory[5].size());
            assertEquals(EventTriggeringUtils.AFTER_DELETE_EVENT,listeningHistory[5][ObjectProcessor.EVENT_NAME]);
            assertEquals(modelClass1,listeningHistory[5][ObjectProcessor.DOMAIN_OBJECT].class);
            assertEquals(model1ins3.id,listeningHistory[5][ObjectProcessor.DOMAIN_OBJECT].id);

            assertEquals(2,listeningHistory[6].size());
            assertEquals(EventTriggeringUtils.AFTER_DELETE_EVENT,listeningHistory[6][ObjectProcessor.EVENT_NAME]);
            assertEquals(modelClass1,listeningHistory[6][ObjectProcessor.DOMAIN_OBJECT].class);
            assertEquals(model1ins2.id,listeningHistory[6][ObjectProcessor.DOMAIN_OBJECT].id);


            //test create and update is ignored for modelClass2 instance
            def model2ins1=modelClass2.add(propa:"ev1",propb:45,propc:false)
            assertFalse(model2ins1.hasErrors())

            model2ins1.update(propb:56,propc:true);
            assertFalse(model2ins1.hasErrors())

            model2ins1.remove();

            SmartWait.waitFor(new ClosureWaitAction({
                assertEquals(8,listeningHistory.size());
            }))

            assertEquals(2,listeningHistory[7].size());
            assertEquals(EventTriggeringUtils.AFTER_DELETE_EVENT,listeningHistory[7][ObjectProcessor.EVENT_NAME]);
            assertEquals(modelClass2,listeningHistory[7][ObjectProcessor.DOMAIN_OBJECT].class);
            assertEquals(model2ins1.id,listeningHistory[7][ObjectProcessor.DOMAIN_OBJECT].id);
        }
        catch(e)
        {
            e.printStackTrace();
            throw e;
        }
        finally
        {
            CmdbScript.stopListening(listeningScript);
        }


        SmartWait.waitFor(new ClosureWaitAction({
            assertEquals(AdapterStateProvider.STOPPED, ListeningAdapterManager.getInstance().getState(listeningScript.listeningDatasource));
        }))

        assertEquals(8,listeningHistory.size());


    }

    private void initializeScripts()
    {
         CmdbScript.metaClass.'static'.getScriptObject={ CmdbScript script, Map params ->
            def scriptObject=repoScriptClass.newInstance();
            setScriptObjectParameters(scriptObject,params);
            return scriptObject;
         }
         loadRepoListeningScript();

    }
    private void setScriptObjectParameters(scriptObject,params)
    {
        scriptObject.setProperty("logger",TestLogUtils.log);
        scriptObject.setProperty("model1Class",modelClass1);
        scriptObject.setProperty("model2Class",modelClass2);
        params.each{propName, propValue ->
            scriptObject.setProperty(propName, propValue);
        }
    }
    private void loadRepoListeningScript()
    {
        def scriptContent='''
        import com.ifountain.rcmdb.util.DataStore;

        def getParameters() {
            def classMap=[:];
            classMap[model1Class.name]=[];
            classMap[model2Class.name]=["afterDelete"];

            return [Classes: classMap]
        }

        def init() {}
        def cleanUp() {}

        def update(changeEvent) {
            DataStore.get("listeningHistory").add(changeEvent);
        }

        ''';

        repoScriptClass = gcl.parseClass(scriptContent.toString());

    }



}
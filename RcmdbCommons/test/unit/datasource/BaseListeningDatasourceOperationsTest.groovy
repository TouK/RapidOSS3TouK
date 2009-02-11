package datasource
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 11, 2009
 * Time: 9:15:48 AM
 * To change this template use File | Settings | File Templates.
 */
import script.CmdbScript
import script.CmdbScriptOperations

import datasource.BaseListeningDatasource
import datasource.BaseListeningDatasourceOperations

import com.ifountain.rcmdb.datasource.ListeningAdapterManager
import com.ifountain.rcmdb.scripting.ScriptManager
import com.ifountain.rcmdb.scripting.ScriptScheduler
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase

import org.apache.commons.io.FileUtils
import org.apache.log4j.Level


class BaseListeningDatasourceOperationsTest extends RapidCmdbWithCompassTestCase{
     def static base_directory = "../testoutput/";
     
     public void setUp() {
        super.setUp();
        clearMetaClasses();
        initialize();
    }

    public void tearDown() {
        super.tearDown();
    }
    private void clearMetaClasses()
    {
        ListeningAdapterManager.destroyInstance();
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(ScriptScheduler)
        ExpandoMetaClass.enableGlobally();
    }
    void initialize(){
        ScriptManager manager = ScriptManager.getInstance();
        if(new File(base_directory).exists())
        {
            FileUtils.deleteDirectory (new File(base_directory));
        }
        manager.initialize(this.class.getClassLoader(), base_directory, [], [:]);
        new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY").mkdirs();
        
        initialize([CmdbScript,BaseListeningDatasource], []);
        CompassForTests.addOperationSupport (CmdbScript, CmdbScriptOperations);
        CompassForTests.addOperationSupport (BaseListeningDatasource, BaseListeningDatasourceOperations);
     }
     void testBeforeDelete(){        


        def scriptFile="baselistening_test.groovy";
        createScript(scriptFile,"return 1");
        
        CmdbScript script=CmdbScript.add(name:"testscript",type:CmdbScript.LISTENING,scriptFile:scriptFile)
        assertFalse(script.hasErrors())
        
        BaseListeningDatasource ds=BaseListeningDatasource.add(name:"myds",listeningScript:script)
        assertFalse(ds.hasErrors())
        assertEquals(ds.listeningScript.name,script.name);


        assertEquals(script.listeningDatasource.id,ds.id)


        def stoppedDatasource=null;
        ListeningAdapterManager.metaClass.stopAdapter= { BaseListeningDatasource listeningDatasource ->
            println "stopAdapter in beforedelete";
            stoppedDatasource = listeningDatasource;
        }
        assertNull(stoppedDatasource);
        ds.beforeDelete();
        assertEquals(stoppedDatasource.id,ds.id);
        assertEquals(stoppedDatasource.name,ds.name);

     }
     void testGetListeningAdapter()
     {
        BaseListeningDatasource ds=BaseListeningDatasource.add(name:"myds")
        assertFalse(ds.hasErrors())
        assertNull(ds.getListeningAdapter(null,null));
     }
     
    def createScript(scriptName,scriptContent)
    {
        def scriptFile = new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write (scriptContent);
    }
}
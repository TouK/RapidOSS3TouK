package scriptTests


import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.scripting.ScriptManager
import script.CmdbScript
import script.CmdbScriptOperations
import com.ifountain.rcmdb.test.util.CompassForTests
import org.apache.commons.io.FileUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 19, 2009
* Time: 9:06:30 AM
* To change this template use File | Settings | File Templates.
*/
class AutocompleScriptTests  extends RapidCmdbWithCompassTestCase {

    def static script_base_directory = "../testoutput/";

    def RsComputerSystem;
    def RsTopologyObject;
    
    public void setUp() {
        super.setUp();

        ["RsComputerSystem","RsTopologyObject"].each{ className ->
            setProperty(className,gcl.loadClass(className));
        }

        initialize([CmdbScript,RsComputerSystem,RsTopologyObject], []);
        CompassForTests.addOperationSupport (CmdbScript,CmdbScriptOperations);
        initializeScriptManager();
    }

    public void tearDown() {
        super.tearDown();
    }
    void initializeScriptManager()
    {             
        def base_directory = getWorkspacePath()+"/RapidModules/RapidInsight"
        println "base path is :"+new File(base_directory).getCanonicalPath();

        if (new File(script_base_directory).exists())
        {
            FileUtils.deleteDirectory(new File(script_base_directory));
        }
        new File("$script_base_directory/$ScriptManager.SCRIPT_DIRECTORY").mkdirs();

        ScriptManager manager = ScriptManager.getInstance();
        manager.initialize(this.class.getClassLoader(), script_base_directory, [], [:]);


        FileUtils.copyFileToDirectory (new File("${base_directory}/scripts/autocomplete.groovy"),new File("$script_base_directory/$ScriptManager.SCRIPT_DIRECTORY"));


    }
    public void testAutoCompleteReturnsRsComputerSystemWithMaxAndSort()
    {
        def script=CmdbScript.addScript([name:"autocomplete",scriptFile:"autocomplete",type: CmdbScript.ONDEMAND])
        assertFalse(script.hasErrors());

        RsTopologyObject.add(name:"obj1");
        assertEquals(1,RsTopologyObject.countHits("alias:*"))

        RsComputerSystem.add(name:"aad1");
        RsComputerSystem.add(name:"aab1");
        RsComputerSystem.add(name:"aac1");
        RsComputerSystem.add(name:"aa");
        RsComputerSystem.add(name:"ab");
        RsComputerSystem.add(name:"ac");
        RsComputerSystem.add(name:"ad");
        assertEquals(7,RsComputerSystem.countHits("alias:*"))

        def results=getAutoCompleteData("a");
        assertEquals(7,results.size());
        assertEquals(["aa","aab1","aac1","aad1","ab","ac","ad"],results);

        results=getAutoCompleteData("aa");
        assertEquals(4,results.size());
        assertEquals(["aa","aab1","aac1","aad1"],results);

        //test max 20 items are listed
        RsComputerSystem.removeAll();
        assertEquals(0,RsComputerSystem.countHits("alias:*"))

        22.times{ count ->
            RsComputerSystem.add(name:"b${count}");
        }
        assertEquals(22,RsComputerSystem.countHits("alias:*"))

        results=getAutoCompleteData("b");
        assertEquals(20,results.size());


    }
    public void testAutoCompleteIgnoresLeftRightSpaceInQuery()
    {
         def script=CmdbScript.addScript([name:"autocomplete",scriptFile:"autocomplete",type: CmdbScript.ONDEMAND])
        assertFalse(script.hasErrors());


        RsComputerSystem.add(name:"a 1");
        RsComputerSystem.add(name:"a 2");
        RsComputerSystem.add(name:"a3");
        RsComputerSystem.add(name:"b1");
        RsComputerSystem.add(name:"aa1");

        assertEquals(5,RsComputerSystem.countHits("alias:*"));

        def spacedResults=getAutoCompleteData("a ");
        def nospaceResults=getAutoCompleteData("a");

        assertEquals(spacedResults,nospaceResults);
        assertEquals(["a 1","a 2","a3","aa1"],spacedResults);



    }
     def getAutoCompleteData(query){
        def script=CmdbScript.get(name:"autocomplete")

        def result=CmdbScript.runScript(script,["params":["query":query]]);

        def resultXml = new XmlSlurper().parseText(result);

        def suggestions=resultXml.Suggestion;

        def results=[]
        suggestions.each {
            results.add(it.@name.toString());
        }
        println "result parsed from xml ${results}"
        return results;
    }


}
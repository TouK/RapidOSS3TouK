

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.scripting.ScriptManager
import script.CmdbScript
import script.CmdbScriptOperations
import com.ifountain.rcmdb.test.util.CompassForTests

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 19, 2009
* Time: 9:06:30 AM
* To change this template use File | Settings | File Templates.
*/
class AutocompleScriptTests  extends RapidCmdbWithCompassTestCase {

    
    public void setUp() {
        super.setUp();
        initialize([CmdbScript,RsComputerSystem,RsTopologyObject], []);
        CompassForTests.addOperationSupport (CmdbScript,CmdbScriptOperations);
        initializeScriptManager();
    }

    public void tearDown() {

        super.tearDown();
    }
    void initializeScriptManager()
    {             
          //to run in Hudson
        def base_directory = "../RapidSuite";
        //def canonicalPath=new File(System.getProperty("base.dir", ".")).getCanonicalPath();
        def canonicalPath=new File(".").getCanonicalPath();
        //to run in developer pc
        if(canonicalPath.endsWith("RapidModules"))
        {
            base_directory = "RapidInsight"
        }
        println "base path is :"+new File(base_directory).getCanonicalPath();

        ScriptManager manager = ScriptManager.getInstance();        
        manager.initialize(this.class.getClassLoader(), base_directory, [], [:]);
        //new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY").mkdirs();
        
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

        RsComputerSystem.removeAll();
        assertEquals(0,RsComputerSystem.countHits("alias:*"))

        30.times{ count ->
            RsComputerSystem.add(name:"b${count}");    
        }
        assertEquals(30,RsComputerSystem.countHits("alias:*"))

        results=getAutoCompleteData("b");
        assertEquals(20,results.size());

        
    }   
     def getAutoCompleteData(query){
        def script=CmdbScript.get(name:"autocomplete")
        
        def result=CmdbScript.runScript(script,["params":["query":query]]);
        println result
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
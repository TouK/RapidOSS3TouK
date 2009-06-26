package scriptTests


import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.scripting.ScriptManagerForTest

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 19, 2009
* Time: 9:06:30 AM
* To change this template use File | Settings | File Templates.
*/
class AutocompleScriptTests  extends RapidCmdbWithCompassTestCase {



    def RsComputerSystem;
    def RsTopologyObject;

    public void setUp() {
        super.setUp();

        ["RsComputerSystem","RsTopologyObject"].each{ className ->
            setProperty(className,gcl.loadClass(className));
        }

        initialize([RsComputerSystem,RsTopologyObject], []);
        initializeScriptManager();
    }

    public void tearDown() {
        super.tearDown();
    }
    void initializeScriptManager()
    {
        def base_directory = getWorkspacePath()+"/RapidModules/RapidInsight/scripts"
        println "base path is :"+new File(base_directory).getCanonicalPath();
        ScriptManagerForTest.initialize(gcl,base_directory);

    }
    public void testAutoCompleteReturnsRsComputerSystemWithMaxAndSort()
    {

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
        def result=ScriptManagerForTest.runScript("autocomplete",["params":["query":query]]);

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
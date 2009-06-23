package scripts

import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import script.CmdbScript

import script.ScriptController

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 13, 2009
 * Time: 3:12:46 PM
 * To change this template use File | Settings | File Templates.
 */



// Warning :  this test copies getSummaryData script from parent - svn RapidInsight directory to the scripts directory
class GetSummaryDataScriptIntegrationTests extends RapidCmdbIntegrationTestCase {
    static transactional = false;
    def classes=[:]

     static String CRITICAL = "Critical";
     static String MAJOR = "Major";
     static String MINOR = "Minor";
     static String WARNING = "Warning";
     static String INDETERMINATE = "Indeterminate";
     static String NORMAL = "Normal";
     static String INVALID = "invalid";


     static severityMap=[:];

    void setUp() throws Exception {
        super.setUp();
        loadClasses(["RsEvent"])
        clearAll();

        severityMap[CRITICAL]=5;
        severityMap[MAJOR]=4;
        severityMap[MINOR]=3;
        severityMap[WARNING]=2;
        severityMap[INDETERMINATE]=1;
        severityMap[NORMAL]=0;
        severityMap[INVALID]=50;
        copyScript("getSummaryData");
    }

    void tearDown() throws Exception {
        super.tearDown();
    }


    void clearAll()
    {

        classes.RsEvent.removeAll();

        CmdbScript.list().each{
            CmdbScript.deleteScript(it);
        }
    }
     void loadClasses(classList)
    {
        classList.each{
            def loadedClass=this.class.classLoader.loadClass(it)
            classes[loadedClass.getSimpleName()]=loadedClass
        }
    }
    void copyScript(scriptName)
    {

        def scriptPath= "../../../RapidModules/RapidInsight/scripts/${scriptName}.groovy";

        def ant=new AntBuilder();

        ant.copy(file: scriptPath, toDir: "scripts",overwrite:true);

    }

    void testSummaryDataWithNoEvent(){
        def summaryScript=CmdbScript.addScript([name:"getSummaryData"])
        assertFalse(summaryScript.hasErrors());

        assertEquals(classes.RsEvent.count(),0)

        def params=[name:"TESTDS",nodeType:"Container"]
        def setMap=getSummaryDataMap(params);


        assertEquals(setMap.size(),6);
        assertEquals(setMap[NORMAL],1);
        setMap.each{ key , count ->
        	if(key != NORMAL )
        	 	assertEquals(count,0)
        }

    }
    void testSummaryDataWithInvalidSeverityEvents(){
        def summaryScript=CmdbScript.addScript([name:"getSummaryData"])
        assertFalse(summaryScript.hasErrors());

        assertEquals(classes.RsEvent.count(),0)


        def eventCounts=[:]

        eventCounts[CRITICAL]=5
        eventCounts[MAJOR]=4
        eventCounts[MINOR]=1
        eventCounts[WARNING]=3
        eventCounts[INDETERMINATE]=7
        eventCounts[NORMAL]=2
        eventCounts[INVALID]=10

        eventCounts.each { severity , count ->
            def severityVal=severityMap[severity];

            count.times{
                classes.RsEvent.add(name:"event${severity}${it}",rsDatasource:"TESTDS",severity:severityVal)
            }

        }
        def totalEventCount=classes.RsEvent.count();
        assertEquals(eventCounts.values().sum(),totalEventCount)


        def params=[name:"TESTDS",nodeType:"Container"]
        def setMap=getSummaryDataMap(params);



        assertEquals(setMap.size(),6);
        def totalEventCountFromSummary=0;
        assertEquals(setMap[NORMAL],eventCounts[NORMAL]+eventCounts[INVALID])

        eventCounts.each { severity , count ->
            if(severity!=NORMAL && severity!=INVALID)
            {
        	    assertEquals(setMap[severity],count);
            }
        	totalEventCountFromSummary+=count;
        }


        assertEquals(totalEventCount,totalEventCountFromSummary);


    }
     void testSummaryDataWithEvents(){
        def summaryScript=CmdbScript.addScript([name:"getSummaryData"])
        assertFalse(summaryScript.hasErrors());

        assertEquals(classes.RsEvent.count(),0)


        def eventCounts=[:]

        eventCounts[CRITICAL]=5
        eventCounts[MAJOR]=4
        eventCounts[MINOR]=1
        eventCounts[WARNING]=3
        eventCounts[INDETERMINATE]=7
        eventCounts[NORMAL]=2

        eventCounts.each { severity , count ->
            def severityVal=severityMap[severity];

            count.times{
                classes.RsEvent.add(name:"event${severity}${it}",rsDatasource:"TESTDS",severity:severityVal)
            }

        }
        def totalEventCount=classes.RsEvent.count();
        assertEquals(eventCounts.values().sum(),totalEventCount)


        def params=[name:"TESTDS",nodeType:"Container"]
        def setMap=getSummaryDataMap(params);



        assertEquals(setMap.size(),6);
        def totalEventCountFromSummary=0;
        eventCounts.each { severity , count ->
        	assertEquals(setMap[severity],count);
        	totalEventCountFromSummary+=count;
        }

        assertEquals(totalEventCount,totalEventCountFromSummary);


    }
    def getSummaryDataMap(params){
        params.id="getSummaryData";
        def controller=runScriptViaController(params);

        def resultXml = new XmlSlurper().parseText(controller.response.contentAsString);
        def sets=resultXml.set;
        assertEquals(sets.size(),6);

        def setMap=[:]
        sets.each {
            setMap[it.@label.toString()]=it.@value.toInteger();
        }
        println "map result parsed from xml ${setMap}"
        return setMap;
    }

    def runScriptViaController(externalParams){
        def params=[:]

        params.putAll(externalParams);


        def controller = new ScriptController();
        params.each{ key , val ->
            controller.params[key] = val;
        }
        controller.run();
        return controller;
    }


}
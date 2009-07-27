package scriptTests

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.scripting.ScriptManagerForTest


/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 27, 2009
* Time: 3:06:44 PM
* To change this template use File | Settings | File Templates.
*/
class GetSummaryDataScriptTest extends RapidCmdbWithCompassTestCase {

     def classes=[:]

     static String CRITICAL = "Critical";
     static String MAJOR = "Major";
     static String MINOR = "Minor";
     static String WARNING = "Warning";
     static String INDETERMINATE = "Indeterminate";
     static String NORMAL = "Normal";
     static String INVALID = "invalid";


     static severityMap=[:];

    public void setUp() throws Exception {
        super.setUp();
        loadClasses(["RsEvent"])
        initialize([classes.RsEvent], []);

        severityMap[CRITICAL]=5;
        severityMap[MAJOR]=4;
        severityMap[MINOR]=3;
        severityMap[WARNING]=2;
        severityMap[INDETERMINATE]=1;
        severityMap[NORMAL]=0;
        severityMap[INVALID]=50;
        initializeScriptManager();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    void initializeScriptManager()
    {
        def base_directory = getWorkspacePath()+"/RapidModules/RapidInsight/scripts"
        println "base path is :"+new File(base_directory).getCanonicalPath();

        ScriptManagerForTest.initialize (gcl,base_directory);
        ScriptManagerForTest.addScript('getSummaryData');

    }

     void loadClasses(classList)
    {
        classList.each{
            def loadedClass=this.class.classLoader.loadClass(it)
            classes[loadedClass.getSimpleName()]=loadedClass
        }
    }

    void testSummaryDataWithNoEvent(){
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
        def scriptResult=ScriptManagerForTest.runScript("getSummaryData",["params":params]);        

        def resultXml = new XmlSlurper().parseText(scriptResult);
        def sets=resultXml.set;
        assertEquals(sets.size(),6);

        def setMap=[:]
        sets.each {
            setMap[it.@label.toString()]=it.@value.toInteger();
        }
        println "map result parsed from xml ${setMap}"
        return setMap;
    }



}
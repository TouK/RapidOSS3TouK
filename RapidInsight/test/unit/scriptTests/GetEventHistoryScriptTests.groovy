package scriptTests

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.scripting.ScriptManagerForTest
import java.text.SimpleDateFormat
import java.sql.Timestamp

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 18, 2010
* Time: 5:30:28 PM
* To change this template use File | Settings | File Templates.
*/
class GetEventHistoryScriptTests extends RapidCmdbWithCompassTestCase {

    def RsHistoricalEvent;
    def formatter ;

    public void setUp() {
        super.setUp();

        ["RsHistoricalEvent"].each{ className ->
            setProperty(className,gcl.loadClass(className));
        }

        initialize([RsHistoricalEvent], []);
        initializeScriptManager();

        formatter = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
    }

    public void tearDown() {
        super.tearDown();
    }

    void initializeScriptManager()
    {
        def base_directory = getWorkspacePath()+"/RapidModules/RapidInsight/scripts"
        println "base path is :"+new File(base_directory).getCanonicalPath();
        ScriptManagerForTest.initialize(gcl,base_directory);
        ScriptManagerForTest.addScript('getEventHistory');
    }

    public void testGetEventHistoryForContainer()
    {
        def now=Date.now();
        def ev1=RsHistoricalEvent.add(name:"ev1",elementName:"el1",createdAt:now,clearedAt:now+1000,rsDatasource:"rs1");
        def ev2=RsHistoricalEvent.add(name:"ev2",elementName:"el2",createdAt:now+1000,clearedAt:now,rsDatasource:"rs1");
        def ev3=RsHistoricalEvent.add(name:"ev3",elementName:"el3",createdAt:now,clearedAt:now+1000,rsDatasource:"rs2");
        
        assertEquals(3,RsHistoricalEvent.count());
        def data=getScriptData([nodeType:"Container",name:"rs1"]);
        assertEquals(2,data.size());

        //ev1 is first because of clearedAt is latest
        assertEquals("el1 ev1",data[0].title);
        assertEquals("true",data[0].isDuration);
        assertEquals("ev1",data[0].body);
        assertEquals(formatter.format(new Timestamp(ev1.createdAt))+ " GMT",data[0].start);
        assertEquals(formatter.format(new Timestamp(ev1.clearedAt))+ " GMT",data[0].end);
        
        assertEquals("el2 ev2",data[1].title);
        assertEquals("true",data[1].isDuration);
        assertEquals("ev2",data[1].body);
        //because clearedAt is smaller then created end is same as createdAt
        assertEquals(formatter.format(new Timestamp(ev2.createdAt))+ " GMT",data[1].start);
        assertEquals(formatter.format(new Timestamp(ev2.createdAt))+ " GMT",data[1].end);
    }
    public void testGetEventHistoryForObject()
    {
        def now=Date.now();
        def ev1=RsHistoricalEvent.add(name:"ev1",elementName:"el1",createdAt:now,clearedAt:now+1000,rsDatasource:"rs1");
        def ev2=RsHistoricalEvent.add(name:"ev2",elementName:"el1",createdAt:now+1000,clearedAt:now,rsDatasource:"rs1");
        def ev3=RsHistoricalEvent.add(name:"ev3",elementName:"el3",createdAt:now,clearedAt:now+1000,rsDatasource:"rs1");

        assertEquals(3,RsHistoricalEvent.count());
        def data=getScriptData([name:"el1"]);
        assertEquals(2,data.size());

        //ev1 is first because of clearedAt is latest
        assertEquals("el1 ev1",data[0].title);
        assertEquals("true",data[0].isDuration);
        assertEquals("ev1",data[0].body);
        assertEquals(formatter.format(new Timestamp(ev1.createdAt))+ " GMT",data[0].start);
        assertEquals(formatter.format(new Timestamp(ev1.clearedAt))+ " GMT",data[0].end);

        assertEquals("el1 ev2",data[1].title);
        assertEquals("true",data[1].isDuration);
        assertEquals("ev2",data[1].body);
        //because clearedAt is smaller then created end is same as createdAt
        assertEquals(formatter.format(new Timestamp(ev2.createdAt))+ " GMT",data[1].start);
        assertEquals(formatter.format(new Timestamp(ev2.createdAt))+ " GMT",data[1].end);
    }

    def getScriptData(params){
        def result=ScriptManagerForTest.runScript("getEventHistory",["params":params]);

        def resultXml = new XmlSlurper().parseText(result);
        def results=[]
        resultXml.event.each{
            def props=it.attributes();
            props.body=it.text();
            results.add(props);
        }
        println "xml result : ${result}"
        println "result parsed from xml ${results}"
        return results;
    }
}
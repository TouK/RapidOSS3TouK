package solutionTests

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.test.util.scripting.ScriptManagerForTest

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jun 26, 2009
* Time: 1:22:07 PM
* To change this template use File | Settings | File Templates.
*/
class PutInMaintenanceScriptTest extends RapidCmdbWithCompassTestCase {

    def RsInMaintenance;
    def RsInMaintenanceSchedule;
    def RsEvent;


    def RsInMaintenanceOperations;
    def RsInMaintenanceScheduleOperations;

    public void setUp() {
        super.setUp();
        GroovyClassLoader loader = new GroovyClassLoader();
        def op_base_directory = getWorkspacePath()+"/RapidModules/RapidInsight/solutions/inMaintenance/operations"

        ["RsInMaintenance","RsInMaintenanceSchedule","RsEvent"].each{ className ->
            setProperty(className,gcl.loadClass(className));
        }
        
        ["RsInMaintenanceOperations","RsInMaintenanceScheduleOperations"].each{ className ->
            setProperty(className,loader.parseClass(new File("${op_base_directory}/${className}.groovy")));
        }

        initializeScriptManager();

        initialize([RsInMaintenance, RsInMaintenanceSchedule,RsEvent], []);
        CompassForTests.addOperationSupport (RsInMaintenance,RsInMaintenanceOperations);
        CompassForTests.addOperationSupport (RsInMaintenanceSchedule,RsInMaintenanceScheduleOperations);
    }

    public void tearDown() {
        super.tearDown();
    }

    void initializeScriptManager()
    {
        def base_directory = getWorkspacePath()+"/RapidModules/RapidInsight/solutions/inMaintenance/scripts"
        println "base path is :"+new File(base_directory).getCanonicalPath();
        ScriptManagerForTest.initialize(gcl,base_directory);
        ScriptManagerForTest.addScript("putInMaintenance");

    }

    public void testPutInMaintenanceWithoutMinutesAndThenTakeObjectOutOfMaintenance()
    {
        assertEquals(0,RsInMaintenance.count());
        def params=[model:"maintenance",objectName:"testobj",inMaintenance:"true",info:"manualMaintenance"];
        def webParams=[session:[username:"testuser"]];

        def startTimeForMaintenance=System.currentTimeMillis();

        runScript(params,webParams);
        assertEquals(1,RsInMaintenance.count());
        def maintObject=RsInMaintenance.list()[0];

        assertEquals(params.objectName,maintObject.objectName);
        assertEquals(params.info,maintObject.info);
        assertEquals("User ${webParams.session.username}",maintObject.source);
        assertEquals(new Date(0),maintObject.ending);
        assertTrue(maintObject.starting.getTime()>=startTimeForMaintenance);


        //we remove inMaintenance key, and it will be taken out of maintenance
        params=[model:"maintenance",objectName:"testobj",info:"manualMaintenance"];
        runScript(params,webParams);
        assertEquals(0,RsInMaintenance.count());
    }
    public void testPutInMaintenanceWithMinutes()
    {
        assertEquals(0,RsInMaintenance.count());
        def params=[model:"maintenance",objectName:"testobj",inMaintenance:"true",info:"manualMaintenance",minutes:"10"];
        def webParams=[session:[username:"testuser"]];

        def startTimeForMaintenance=System.currentTimeMillis();

        runScript(params,webParams);
        assertEquals(1,RsInMaintenance.count());
        def maintObject=RsInMaintenance.list()[0];


        assertEquals(params.objectName,maintObject.objectName);
        assertEquals(params.info,maintObject.info);
        assertEquals("User ${webParams.session.username}",maintObject.source);
        //10 minutes make 600 seconds
        def timeDiff=(maintObject.ending.getTime()-Date.now())/1000;
        assertTrue(timeDiff>599);
        assertTrue(timeDiff<601);
        assertTrue(maintObject.starting.getTime()>=startTimeForMaintenance);
    }
    public void testPutInMaintenanceWithMinutesThrowsExceptionIfMinutesIsNotAValidInteger()
    {

        def params=[model:"maintenance",objectName:"testobj",inMaintenance:"true",info:"manualMaintenance",minutes:"sdsadasdasd"];
        def webParams=[session:[username:"testuser"]];

        try{
            runScript(params,webParams);
            fail("should throw : Minutes should be an integer")
        }
        catch(e)
        {
            assertEquals("Wrong Exception ${e}",e.getMessage().toString(),"Minutes should be an integer")
        }

    }

    public void testAddObjectScheduleAndRemoveSchedule()
    {
        assertEquals(0,RsInMaintenanceSchedule.count());

        def nextYear=(Calendar.getInstance().get(Calendar.YEAR)+1).toString();

        def params=[model:"schedule",mode:"create",objectName:"testObject",info:"testschedule1",
                startTime_year:nextYear,startTime_month:"1",startTime_day:"15",startTime_hour:"10",startTime_minute:"00",
                endTime_year:nextYear,endTime_month:"2",endTime_day:"25",endTime_hour:"11",endTime_minute:"00"];

        runScript(params,[:]);
        
        assertEquals(1,RsInMaintenanceSchedule.count());

        def scheduleObject=RsInMaintenanceSchedule.searchEvery("objectName:testObject",[sort:"id",order:"desc",max:1])[0];

        assertEquals(params.objectName,scheduleObject.objectName);
        assertEquals(params.info,scheduleObject.info);

        checkTimes(params,scheduleObject);

        //adding another schedule
        def params2=[model:"schedule",mode:"create",objectName:"testObject",info:"testschedule1",
                startTime_year:nextYear+1,startTime_month:"3",startTime_day:"15",startTime_hour:"10",startTime_minute:"00",
                endTime_year:nextYear+1,endTime_month:"4",endTime_day:"25",endTime_hour:"11",endTime_minute:"00"];

        runScript(params2,[:]);

        assertEquals(2,RsInMaintenanceSchedule.count());

        def scheduleObject2=RsInMaintenanceSchedule.searchEvery("objectName:testObject",[sort:"id",order:"desc",max:1])[0];

        assertEquals(params2.objectName,scheduleObject2.objectName);
        assertEquals(params2.info,scheduleObject2.info);

        checkTimes(params2,scheduleObject2);

        //remove one of schedules
        def removeParams=[model:"schedule",mode:"delete",scheduleid:scheduleObject2.id.toString()];
        runScript(removeParams,[:]);
        assertEquals(1,RsInMaintenanceSchedule.count());
        assertEquals(1,RsInMaintenanceSchedule.countHits("id:${scheduleObject.id}"));
        assertEquals(0,RsInMaintenanceSchedule.countHits("id:${scheduleObject2.id}"));
        
        //remove the other
        def removeParams2=[model:"schedule",mode:"delete",scheduleid:scheduleObject.id.toString()];
        runScript(removeParams2,[:]);
        assertEquals(0,RsInMaintenanceSchedule.count());

        

    }
    private def getScheduleParamsForExceptionTests()
    {
        def nextYear=(Calendar.getInstance().get(Calendar.YEAR)+1).toString();

        return   [model:"schedule",mode:"create",objectName:"testObject",info:"testschedule1",
                startTime_year:nextYear,startTime_month:"1",startTime_day:"15",startTime_hour:"10",startTime_minute:"00",
                endTime_year:nextYear,endTime_month:"2",endTime_day:"25",endTime_hour:"11",endTime_minute:"00"];
    }
    public void testAddObjectScheduleThrowsExceptionIfDateFieldsAreMissingOrNotInteger()
    {
       assertEquals(0,RsInMaintenanceSchedule.count());

        //startTime_month:"1" is missing
        def params=getScheduleParamsForExceptionTests();
        params.remove("startTime_month");
        
        try{
            runScript(params,[:]);
            fail("should throw exception");
        }
        catch(e)
        {
            assertEquals("wrong exception ${e}",e.getMessage().toString(),"month of startTime is missing or not an integer")
        }
        assertEquals(0,RsInMaintenanceSchedule.count());

        //endTime_minute:"00" is missing
        def params2=getScheduleParamsForExceptionTests();
        params2.remove("endTime_minute");

        try{
            runScript(params2,[:]);
            fail("should throw exception");
        }
        catch(e)
        {
            assertEquals("wrong exception ${e}",e.getMessage().toString(),"minute of endTime is missing or not an integer")
        }
        assertEquals(0,RsInMaintenanceSchedule.count());

        //startTime_year is not an integer
        def params3=getScheduleParamsForExceptionTests();
        params3.startTime_year="abcd";

        try{
            runScript(params3,[:]);
            fail("should throw exception");
        }
        catch(e)
        {
            assertEquals("wrong exception ${e}",e.getMessage().toString(),"year of startTime is missing or not an integer")
        }
        assertEquals(0,RsInMaintenanceSchedule.count());


    }
    public void testAddObjectScheduleThrowsExceptionIfDateIsWrong()
    {
        def nextYear=(Calendar.getInstance().get(Calendar.YEAR)+1).toString();

        //startTime_day is not a valid day 35
        def params=getScheduleParamsForExceptionTests();
        params.startTime_day="35";

        try{
            runScript(params,[:]);
            fail("should throw exception");
        }
        catch(IllegalArgumentException e)
        {
            assertTrue("wrong exception ${e}",e.getMessage().toString().indexOf("DAY_OF_MONTH")>=0)
        }

        //endTime_month is not a valid month 17
        def params2=getScheduleParamsForExceptionTests();
        params2.endTime_month="17";

        try{
            runScript(params2,[:]);
            fail("should throw exception");
        }
        catch(IllegalArgumentException e)
        {
            assertTrue("wrong exception ${e}",e.getMessage().toString().indexOf("MONTH")>=0)
        }
        
        assertEquals(0,RsInMaintenanceSchedule.count());
    }
    private void checkTimes(params,scheduleObject)
    {
        Calendar startTime=Calendar.getInstance();
        startTime.setTimeInMillis(scheduleObject.starting.getTime())

        assertEquals(params.startTime_year.toInteger(),startTime.get(Calendar.YEAR));
        //Calendar month starts from 0 not from 1 , script puts -1 to month while adding
        assertEquals(params.startTime_month.toInteger(),startTime.get(Calendar.MONTH)+1);
        assertEquals(params.startTime_day.toInteger(),startTime.get(Calendar.DATE));
        assertEquals(params.startTime_hour.toInteger(),startTime.get(Calendar.HOUR));
        assertEquals(params.startTime_minute.toInteger(),startTime.get(Calendar.MINUTE));

        Calendar endTime=Calendar.getInstance();
        endTime.setTimeInMillis(scheduleObject.ending.getTime())


        assertEquals(params.endTime_year.toInteger(),endTime.get(Calendar.YEAR));
        //Calendar month starts from 0 not from 1 , script puts -1 to month while adding
        assertEquals(params.endTime_month.toInteger(),endTime.get(Calendar.MONTH)+1);
        assertEquals(params.endTime_day.toInteger(),endTime.get(Calendar.DATE));
        assertEquals(params.endTime_hour.toInteger(),endTime.get(Calendar.HOUR));
        assertEquals(params.endTime_minute.toInteger(),endTime.get(Calendar.MINUTE));

        assertFalse(startTime.getTimeInMillis() == endTime.getTimeInMillis());
    }



    private void runScript(params,webParams)
    {
        ScriptManagerForTest.runScript ("putInMaintenance",["params":params,"web":webParams])
    }



}
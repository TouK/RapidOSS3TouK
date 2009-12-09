package com.ifountain.rcmdb.rrd
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream
import com.ifountain.rcmdb.util.ExecutionContextManagerUtils
import javax.imageio.ImageIO
import com.ifountain.rcmdb.domain.util.ControllerUtils

/**
* Created by IntelliJ IDEA.
* User: ifountain
* Date: Jun 17, 2009
* Time: 9:38:15 AM
* To change this template use File | Settings | File Templates.
*/
class RrdUtilsTests extends RapidCmdbWithCompassTestCase {

    String fileDirectory = "rrdFiles"
    String rrdFileName = "rrdUtilsTest.rrd";
    String rrdFileNameExt = "rrdUtilsTestExt.rrd";
    String testImageFile = "rrdUtilsTestImage.png"

    public void setUp() {
        super.setUp();
        clearMetaClasses();

        new File(fileDirectory).mkdirs()
        new File(fileDirectory + "/" + rrdFileName).delete();
        new File(fileDirectory + "/" + rrdFileNameExt).delete();
        new File(fileDirectory + "/" + testImageFile).delete();
    }

    public void tearDown() {
        new File(fileDirectory + "/" + rrdFileName).delete();
        new File(fileDirectory + "/" + rrdFileNameExt).delete();
        new File(fileDirectory + "/" + testImageFile).delete();
        deleteDirectory(new File(fileDirectory))
        clearMetaClasses();
        super.tearDown();
    }

    private void deleteDirectory(File directory){
        if( directory.exists() ) {
            File[] files = directory.listFiles()
            files.each{
                if(it.isDirectory())
                    deleteDirectory(it)
                else
                    it.delete()
            }
         }
         directory.delete()
    }

    private void clearMetaClasses(){
         ExpandoMetaClass.disableGlobally();
         GroovySystem.metaClassRegistry.removeMetaClass(Grapher);
         GroovySystem.metaClassRegistry.removeMetaClass(ControllerUtils);
         ExpandoMetaClass.enableGlobally();
    }

    public void testGraphThrowsExceptionIfNoWebResponseIsDefined() {
        def graphCallConfig=null;
        Grapher.metaClass.'static'.graph={ Map config ->
             graphCallConfig=config;
        }

        def graphConfig=[destination:'web'];
        graphConfig[RrdUtils.DATASOURCE]=[];

        try{
            RrdUtils.graph(graphConfig);
            fail("should throw Exception");
        }
        catch(e)
        {
            assertTrue("wrong exception ${e}",e.getMessage().indexOf("Web response is not avaliable")>=0)
        }
        assertEquals(graphConfig,graphCallConfig);
        assertNotSame(graphConfig,graphCallConfig);
    }

    public void testGraphThrowsExceptionIfDatasourceIsDefined() {
        def graphCallConfig=null;
        Grapher.metaClass.'static'.graph={ Map config ->
             graphCallConfig=config;
        }

        def graphConfig=[:];

        try{
            RrdUtils.graph(graphConfig);
            fail("should throw Exception");
        }
        catch(e)
        {
            assertEquals("no datasource specified.", e.getMessage())
        }
        assertNull(graphCallConfig);
    }

    public void testGraphWithoutDestination()
    {
        def graphCallConfig=null;
        def bytesToBeReturned = "this is a string".getBytes()
        Grapher.metaClass.'static'.graph={ Map config ->
             graphCallConfig=config;
             return bytesToBeReturned;
        }

        def graphConfig=[:];
        def ds1 = [:]
        def ds2 = [:]
        ds1[RrdUtils.DATABASE_NAME] = "db1";
        ds2[RrdUtils.DATABASE_NAME] = "db2";
        graphConfig[RrdUtils.DATASOURCE]=[ds1,ds2];
        def returnedBytes = RrdUtils.graph(graphConfig);
        assertEquals (bytesToBeReturned, returnedBytes);
        assertNotSame(graphConfig,graphCallConfig);
        def constructedDses = graphCallConfig[RrdUtils.DATASOURCE]
        assertNotNull(constructedDses.find {it[RrdUtils.DATABASE_NAME] == "${RrdUtils.RRD_FOLDER}db1"});
        assertNotNull(constructedDses.find {it[RrdUtils.DATABASE_NAME] == "${RrdUtils.RRD_FOLDER}db2"});

    }


    
    public void testGraphWithWebDestination()
    {
        def graphCallConfig=null;
        def bytesToBeReturned = getImageBytes("${getWorkspaceDirectory()}/RapidModules/RapidUiPlugin/web-app/images/network-offline.png")
        Grapher.metaClass.'static'.graph={ Map config ->
             graphCallConfig=config;
             return bytesToBeReturned;
        }
        def contextParams = [:];
        contextParams[RapidCMDBConstants.WEB_RESPONSE] = [outputStream:new ByteOutputStream()]
        ExecutionContextManagerUtils.executeInContext (contextParams){        
            def graphConfig=[destination:"web"];
            def ds1 = [:]
            ds1[RrdUtils.DATABASE_NAME] = "db1";
            graphConfig[RrdUtils.DATASOURCE]=[ds1];

            def returnedBytes = RrdUtils.graph(graphConfig);

            assertEquals (bytesToBeReturned, returnedBytes);
            assertNotSame(graphConfig,graphCallConfig);
            def constructedDses = graphCallConfig[RrdUtils.DATASOURCE]
            assertNotNull(constructedDses.find {it[RrdUtils.DATABASE_NAME] == "${RrdUtils.RRD_FOLDER}db1"});
            assertTrue(Arrays.equals(bytesToBeReturned, contextParams[RapidCMDBConstants.WEB_RESPONSE].outputStream.getBytes()));
        }
    }
    public void testGraphWithFileDestination()
    {
        def destinationFile = new File("../testOutput/dest.png");
        destinationFile.parentFile.mkdirs();
        destinationFile.delete();
        
        def graphCallConfig=null;
        def bytesToBeReturned = getImageBytes("${getWorkspaceDirectory()}/RapidModules/RapidUiPlugin/web-app/images/network-offline.png")
        Grapher.metaClass.'static'.graph={ Map config ->
             graphCallConfig=config;
             return bytesToBeReturned;
        }
        def contextParams = [:];
        def graphConfig=[destination:destinationFile.path];
        def ds1 = [:]
        ds1[RrdUtils.DATABASE_NAME] = "db1";
        graphConfig[RrdUtils.DATASOURCE]=[ds1];

        def returnedBytes = RrdUtils.graph(graphConfig);

        assertEquals (bytesToBeReturned, returnedBytes);
        assertNotSame(graphConfig,graphCallConfig);
        def constructedDses = graphCallConfig[RrdUtils.DATASOURCE]
        assertNotNull(constructedDses.find {it[RrdUtils.DATABASE_NAME] == "${RrdUtils.RRD_FOLDER}db1"});
        def writtenImage = getImageBytes(destinationFile.path)
        assertTrue(Arrays.equals(bytesToBeReturned, writtenImage));
    }



    public void testFetchDataInfoMap() {
        createDatabase()
        def info = RrdUtils.fetchDataInfoMap(rrdFileName)
        assertEquals('fetchDataInfoMap returns wrong function value','AVERAGE', info['function'])
        assertEquals('fetchDataInfoMap returns wrong datasource value', ['firstDatasource', 'secondDatasource'], info['datasources'])
    }

    public void testFetchAllDataAsMapWithDBNameOnly() {
        createDatabase()
        def result = RrdUtils.fetchAllDataAsMap(rrdFileName)
        assertTrue('result should have firstDatasource map', result.containsKey('firstDatasource'))
        assertTrue('result should have secondDatasource map', result.containsKey('secondDatasource'))
        assertEquals('result has wrong values', 1.0D, result['firstDatasource']['30030'])
        assertEquals('result has wrong values', 2.0D, result['firstDatasource']['30060'])
        assertEquals('result has wrong values', 3.0D, result['firstDatasource']['30090'])
        assertEquals('result has wrong values', 0.1D, result['secondDatasource']['30060'])
        assertEquals('result has wrong values', 0.1D, result['secondDatasource']['30090'])
        assertEquals('result has wrong values', 0.1D, result['secondDatasource']['30120'])
    }

    public void testFetchAllDataAsMapWithSingleSource() {
        createDatabase()
        def result = RrdUtils.fetchAllDataAsMap(rrdFileName, 'firstDatasource')
        assertTrue('result should have firstDatasource map', result.containsKey('firstDatasource'))
        assertEquals('result has wrong values', 1.0D, result['firstDatasource']['30030'])
        assertEquals('result has wrong values', 2.0D, result['firstDatasource']['30060'])
        assertEquals('result has wrong values', 3.0D, result['firstDatasource']['30090'])
    }
    
    public void testFetchDataWithTimestamps() {
        createDatabase()
        def result = RrdUtils.fetchDataAsMap(rrdFileName, ['firstDatasource', 'secondDatasource'], 'AVERAGE', 30060000, 30120000)
        assertTrue('result should have firstDatasource map', result.containsKey('firstDatasource'))
        assertTrue('result should have secondDatasource map', result.containsKey('secondDatasource'))
        assertEquals('result has wrong values', 2.0D, result['firstDatasource']['30060'])
        assertEquals('result has wrong values', 3.0D, result['firstDatasource']['30090'])
        assertEquals('result has wrong values', 4.0D, result['firstDatasource']['30120'])
        assertEquals('result has wrong values', 0.1D, result['secondDatasource']['30060'])
        assertEquals('result has wrong values', 0.1D, result['secondDatasource']['30090'])
        assertEquals('result has wrong values', 0.1D, result['secondDatasource']['30120'])
    }

    private void createMultipleDatabase() {
        def dbConfig = [:]
        dbConfig[DbUtils.DATABASE_NAME] = rrdFileName
        dbConfig[DbUtils.START_TIME] =  30000000L
        dbConfig[DbUtils.STEP] = 30

        def datapointList = []
        def datapoint1 = [:]
        datapoint1[DbUtils.NAME] = 'firstDBSource'
        datapoint1[DbUtils.TYPE] = 'GAUGE'
        datapoint1[DbUtils.HEARTBEAT] = 60
        datapointList.add(datapoint1)
        dbConfig[DbUtils.DATASOURCE] = datapointList

        def archive = [:]
        archive[DbUtils.FUNCTION] = 'AVERAGE'
        archive[DbUtils.XFF] = 0.5
        archive[DbUtils.STEPS] = 1
        archive[DbUtils.ROWS] = 10
        dbConfig[DbUtils.ARCHIVE] = [archive]

        RrdUtils.createDatabase(dbConfig)
        RrdUtils.updateData(rrdFileName, ["30030000:1", "30060000:2", "30090000:3", "30120000:4", "30150000:5", "30180000:6"])

        dbConfig[DbUtils.DATABASE_NAME] = rrdFileNameExt
        dbConfig[DbUtils.START_TIME] =  30000000L
        dbConfig[DbUtils.STEP] = 30

        datapointList.clear()
        def datapoint2 = [:]
        datapoint2[DbUtils.NAME] = 'secondDBSource'
        datapoint2[DbUtils.TYPE] = 'COUNTER'
        datapoint2[DbUtils.HEARTBEAT] = 60
        datapointList.add(datapoint2)
        dbConfig[DbUtils.DATASOURCE] = datapointList
        dbConfig[DbUtils.ARCHIVE] = [archive]

        RrdUtils.createDatabase(dbConfig)
        RrdUtils.updateData(rrdFileNameExt, ["30030000:3", "30060000:6", "30090000:9", "30120000:12", "30150000:15", "30180000:18"])
    }


    private getImageBytes(String imageFile)
    {
        def testimage = ImageIO.read(new File(imageFile));
        def imgeBytes = new ByteOutputStream();
        ImageIO.write(testimage, "png", imgeBytes)
        return imgeBytes.getBytes();
    }

    private void createDatabase() {
        def dbConfig = [:]
        dbConfig[DbUtils.DATABASE_NAME] = rrdFileName
        dbConfig[DbUtils.START_TIME] =  30000000L
        dbConfig[DbUtils.STEP] = 30

        def datapointList = []
        def datapoint1 = [:]
        datapoint1[DbUtils.NAME] = 'firstDatasource'
        datapoint1[DbUtils.TYPE] = 'GAUGE'
        datapoint1[DbUtils.HEARTBEAT] = 60

        def datapoint2 = [:]
        datapoint2[DbUtils.NAME] = 'secondDatasource'
        datapoint2[DbUtils.TYPE] = 'COUNTER'
        datapoint2[DbUtils.HEARTBEAT] = 60

        datapointList.add(datapoint1)
        datapointList.add(datapoint2)

        dbConfig[DbUtils.DATASOURCE] = datapointList

        def archive = [:]
        archive[DbUtils.FUNCTION] = 'AVERAGE'
        archive[DbUtils.XFF] = 0.5
        archive[DbUtils.STEPS] = 1
        archive[DbUtils.ROWS] = 10
        dbConfig[DbUtils.ARCHIVE] = [archive]

        RrdUtils.createDatabase(dbConfig)
        RrdUtils.updateData(rrdFileName, ["30030000:1:3", "30060000:2:6", "30090000:3:9", "30120000:4:12", "30150000:5:15", "30180000:6:18"])
    }

}
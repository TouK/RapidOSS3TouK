package com.ifountain.rcmdb.rrd
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase

/**
* Created by IntelliJ IDEA.
* User: ifountain
* Date: Jun 17, 2009
* Time: 9:38:15 AM
* To change this template use File | Settings | File Templates.
*/
class RrdUtilsTests extends RapidCmdbWithCompassTestCase {

    String fileDirectory = "rrdFiles"
    String rrdFileName = "testRrd.rrd";
    String testImageFile = "testImage.png"

    public void setUp() {
        super.setUp();
        clearMetaClasses();

        new File(fileDirectory).mkdirs()
        new File(fileDirectory + "/" + rrdFileName).delete();
        new File(fileDirectory + "/" + testImageFile).delete();
    }

    public void tearDown() {
        new File(fileDirectory + "/" + rrdFileName).delete();
        new File(fileDirectory + "/" + testImageFile).delete();
//        deleteDirectory(new File(fileDirectory))
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
         ExpandoMetaClass.enableGlobally();
    }


    public void testGraphThrowsExceptionIfNoWebResponseIsDefined(){
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

        assertSame(graphConfig,graphCallConfig);

    }

}
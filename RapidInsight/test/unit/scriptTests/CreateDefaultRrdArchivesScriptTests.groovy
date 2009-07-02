package scriptTests
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.scripting.ScriptManagerForTest
/**
 * Created by IntelliJ IDEA.
 * User: ifountain
 * Date: Jul 2, 2009
 * Time: 10:50:58 AM
 * To change this template use File | Settings | File Templates.
 */
class CreateDefaultRrdArchivesScriptTests  extends RapidCmdbWithCompassTestCase  {
    def rrdArchive;
   public void setUp() {
        super.setUp();
        rrdArchive = this.class.classLoader.loadClass("RrdArchive");
       initialize([rrdArchive], []);
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
        ScriptManagerForTest.addScript('createDefaultRrdArchives');

    }
    public void testArchivesCreated(){
        def result=ScriptManagerForTest.runScript("createDefaultRrdArchives",[:]);

        //assertEquals(4,rrdArchive.count());
        
        def arc1 = rrdArchive.get(name:"1hour");
        def arc2 = rrdArchive.get(name:"6hours");
        def arc3 = rrdArchive.get(name:"12hours");
        def arc4 = rrdArchive.get(name:"24hours");

        assertTrue("Archive for 1 hour is not created.",arc1!=null);
        assertTrue("Archive for 6 hours is not created.",arc2!=null);
        assertTrue("Archive for 12 hours is not created.",arc3!=null);
        assertTrue("Archive for 24 hours is not created.",arc4!=null);
    }
}
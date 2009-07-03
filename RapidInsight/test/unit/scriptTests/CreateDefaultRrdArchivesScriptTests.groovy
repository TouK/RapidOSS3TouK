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
    
    void initializeScriptManager() {
        def base_directory = getWorkspacePath()+"/RapidModules/RapidInsight/scripts"
        println "base path is :"+new File(base_directory).getCanonicalPath();
        ScriptManagerForTest.initialize(gcl,base_directory);
        ScriptManagerForTest.addScript('createDefaultRrdArchives');
    }

    public void testArchivesCreatedSuccessfully(){
        def result=ScriptManagerForTest.runScript("createDefaultRrdArchives",[:]);

        def arc1 = rrdArchive.get(name:"1hour");
        def arc2 = rrdArchive.get(name:"6hours");
        def arc3 = rrdArchive.get(name:"12hours");
        def arc4 = rrdArchive.get(name:"1day");
        def arc5 = rrdArchive.get(name:"1week");
        def arc6 = rrdArchive.get(name:"2weeks");
        def arc7 = rrdArchive.get(name:"1month");
        def arc8 = rrdArchive.get(name:"3months");
        def arc9 = rrdArchive.get(name:"6months");
        def arc10 = rrdArchive.get(name:"1year");

        assertTrue("Archive for 1 hour is not created.",arc1!=null);
        assertTrue("Archive for 6 hours is not created.",arc2!=null);
        assertTrue("Archive for 12 hours is not created.",arc3!=null);
        assertTrue("Archive for 1 day is not created.",arc4!=null);
        assertTrue("Archive for 1 week is not created.",arc5!=null);
        assertTrue("Archive for 2 weeks is not created.",arc6!=null);
        assertTrue("Archive for 1 month is not created.",arc7!=null);
        assertTrue("Archive for 3 months is not created.",arc8!=null);
        assertTrue("Archive for 6 months is not created.",arc9!=null);
        assertTrue("Archive for 1 year is not created.",arc10!=null);
    }
}
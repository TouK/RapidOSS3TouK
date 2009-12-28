import com.ifountain.rcmdb.test.util.AllTestTestUtils
import junit.framework.TestCase

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Dec 24, 2009
 * Time: 10:11:44 AM
 * To change this template use File | Settings | File Templates.
 */
class HudsonTestOrderAllTests {
    public static junit.framework.TestSuite suite()
    {
        //System.setProperty("base.dir","D:\\Ideaworkspace\\RapidModules\\RcmdbCommons\\");
        return AllTestTestUtils.loadTestsInHudsonOrder(RcmdbCommonsAllTests,"http://192.168.1.134:8080/job/RapidCMDBTests/2266/consoleFull");
    }
}
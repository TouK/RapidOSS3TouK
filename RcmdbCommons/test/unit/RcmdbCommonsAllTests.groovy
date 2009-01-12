import com.ifountain.rcmdb.test.util.AllTestTestUtils
import junit.framework.TestCase

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 12, 2009
* Time: 10:59:10 AM
* To change this template use File | Settings | File Templates.
*/
class RcmdbCommonsAllTests extends TestCase
{
    public static junit.framework.TestSuite suite()
    {
        return AllTestTestUtils.loadTests(RcmdbCommonsAllTests, "RcmdbCommons/test/unit")
    }
}
import junit.framework.TestCase
import com.ifountain.rcmdb.test.util.AllTestTestUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 12, 2009
* Time: 1:34:18 PM
* To change this template use File | Settings | File Templates.
*/
class RcmdbAllTests extends TestCase
{
    public static junit.framework.TestSuite suite()
    {
        return AllTestTestUtils.loadTests(RcmdbAllTests, "RapidCmdb/test/unit")
    }
}
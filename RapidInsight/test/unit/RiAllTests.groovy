import junit.framework.TestCase
import com.ifountain.rcmdb.test.util.AllTestTestUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 12, 2009
* Time: 1:32:49 PM
* To change this template use File | Settings | File Templates.
*/
class RiAllTests extends TestCase
{
    public static junit.framework.TestSuite suite()
    {
        return AllTestTestUtils.loadTests(RiAllTests, "RapidInsight/test/unit")
    }
}
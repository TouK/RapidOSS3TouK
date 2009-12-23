import com.ifountain.rcmdb.test.util.AllTestTestUtils
import junit.framework.TestCase

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Dec 23, 2009
* Time: 11:15:26 AM
* To change this template use File | Settings | File Templates.
*/
class RapidInsightAllTests extends TestCase {
    
    public static junit.framework.TestSuite suite()
    {
        return AllTestTestUtils.loadTests(RapidInsightAllTests, "RapidInsight/test/unit")
    }
}
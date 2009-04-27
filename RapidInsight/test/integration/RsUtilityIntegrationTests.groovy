import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import script.CmdbScript
import org.apache.commons.io.FileUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 24, 2009
* Time: 6:01:51 PM
* To change this template use File | Settings | File Templates.
*/
class RsUtilityIntegrationTests extends RapidCmdbIntegrationTestCase{
    static transactional = false;
    void setUp() throws Exception {
        super.setUp();
        CmdbScript.list().each {
            CmdbScript.deleteScript(it);
        }
    }
    void tearDown() throws Exception {
        super.tearDown();
    }
    public void testGetUtility()
    {
       def utilityName="UtilForTest";
       def utilityContent="""
            class UtilForTest {
                static int x=5;
                static String method1()
                {
                    return "method1result";
                }
            }
        """;

        deleteUtility(utilityName);
        RsUtility.reloadOperations();

        createUtility(utilityName,utilityContent);
        assertEquals(5,RsUtility.getUtility("UtilForTest").x);
        assertEquals("method1result",RsUtility.getUtility("UtilForTest").method1());
        assertSame(RsUtility.getUtility("UtilForTest"),RsUtility.getUtility("UtilForTest"));


    }
    public void testGetUtilityThrowsExceptionIfUtilityDoesNotExist()
    {
        try{
            RsUtility.getUtility("nonexistingutility");
            fail("Should throw ClassNotFoundException e");
        }
        catch(ClassNotFoundException e)
        {

        }
    }
    public void testReloadingRsUtilityReloadsUtilities()
    {
       def utilityName="UtilForTest";
       def utilityContent="""
            class UtilForTest {
                static int x=5;
                static String method1()
                {
                    return "method1result";
                }
            }
        """;

        deleteUtility(utilityName);
        RsUtility.reloadOperations();

        createUtility(utilityName,utilityContent);
        assertEquals(5,RsUtility.getUtility("UtilForTest").x);
        assertEquals("method1result",RsUtility.getUtility("UtilForTest").method1());
        
        //change the static variable
        RsUtility.getUtility("UtilForTest").x=15;
        assertEquals(15,RsUtility.getUtility("UtilForTest").x);

        //reload and see that original variable come
        RsUtility.reloadOperations();
        assertEquals(5,RsUtility.getUtility("UtilForTest").x);

        //change the utility see that before reload original remains
        utilityContent="""
            class UtilForTest {
                static int x=45;
                static String method1()
                {
                    return "newresult";
                }
            }
        """;

        createUtility(utilityName,utilityContent);
        assertEquals(5,RsUtility.getUtility("UtilForTest").x);
        assertEquals("method1result",RsUtility.getUtility("UtilForTest").method1());

        RsUtility.reloadOperations();
        assertEquals(45,RsUtility.getUtility("UtilForTest").x);
        assertEquals("newresult",RsUtility.getUtility("UtilForTest").method1());

    }

    def createUtility(name, content)
    {
        def path="operations/${name}.groovy";
        def file = new File(path);
        file.write(content);
    }
    def deleteUtility(name)
    {
        def path="operations/${name}.groovy";
         if(new File(path).exists())
        {
            FileUtils.deleteQuietly (new File(path))
        }
    }
}
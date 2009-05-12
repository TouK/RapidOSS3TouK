package application


import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import script.CmdbScript
import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 24, 2009
* Time: 6:01:51 PM
* To change this template use File | Settings | File Templates.
*/
class RsApplicationIntegrationTests extends RapidCmdbIntegrationTestCase{
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
        ApplicationHolder.application.classLoader.loadClass("application.RsApplication").reloadOperations();

        createUtility(utilityName,utilityContent);
        assertEquals(5,RsApplication.getUtility("UtilForTest").x);
        assertEquals("method1result",RsApplication.getUtility("UtilForTest").method1());
        assertSame(RsApplication.getUtility("UtilForTest"),RsApplication.getUtility("UtilForTest"));


    }
    public void testGetUtilityThrowsExceptionIfUtilityDoesNotExist()
    {
        try{
            RsApplication.getUtility("nonexistingutility");
            fail("Should throw ClassNotFoundException e");
        }
        catch(ClassNotFoundException e)
        {

        }
    }
    public void testReloadingRsApplicationReloadsUtilities()
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
        ApplicationHolder.application.classLoader.loadClass("application.RsApplication").reloadOperations();

        createUtility(utilityName,utilityContent);
        assertEquals(5,RsApplication.getUtility("UtilForTest").x);
        assertEquals("method1result",RsApplication.getUtility("UtilForTest").method1());

        //change the static variable
        RsApplication.getUtility("UtilForTest").x=15;
        assertEquals(15,RsApplication.getUtility("UtilForTest").x);

        //reload and see that original variable come
        ApplicationHolder.application.classLoader.loadClass("application.RsApplication").reloadOperations();
        assertEquals(5,RsApplication.getUtility("UtilForTest").x);

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
        assertEquals(5,RsApplication.getUtility("UtilForTest").x);
        assertEquals("method1result",RsApplication.getUtility("UtilForTest").method1());

        ApplicationHolder.application.classLoader.loadClass("application.RsApplication").reloadOperations();
        assertEquals(45,RsApplication.getUtility("UtilForTest").x);
        assertEquals("newresult",RsApplication.getUtility("UtilForTest").method1());

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
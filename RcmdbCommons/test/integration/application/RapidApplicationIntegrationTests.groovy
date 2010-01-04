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
class RapidApplicationIntegrationTests extends RapidCmdbIntegrationTestCase{
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
            public class ${utilityName} {
                static int x=5;
                static String method1()
                {
                    return "method1result";
                }
            }
        """;

        deleteUtility(utilityName);
        ApplicationHolder.application.classLoader.loadClass("application.RapidApplication").reloadOperations();

        createUtility(utilityName,utilityContent);
        assertEquals(5,RapidApplication.getUtility(utilityName).x);
        assertEquals("method1result",RapidApplication.getUtility(utilityName).method1());
        assertNotSame(RapidApplication.getUtility(utilityName),RapidApplication.getUtility(utilityName));


    }
    public void testGetUtilityThrowsExceptionIfUtilityDoesNotExist()
    {
        try{
            RapidApplication.getUtility("nonexistingutility");
            fail("Should throw ClassNotFoundException e");
        }
        catch(ClassNotFoundException e)
        {

        }
    }
    public void testReloadingRapidApplicationReloadsUtilities()
    {
       def utilityName="UtilForTest";
       def utilityContent="""
            public class ${utilityName} {
                static int x=5;
                static String method1()
                {
                    return "method1result";
                }
            }
        """;

        deleteUtility(utilityName);
        ApplicationHolder.application.classLoader.loadClass("application.RapidApplication").reloadOperations();

        createUtility(utilityName,utilityContent);
        assertEquals(5,RapidApplication.getUtility(utilityName).x);
        assertEquals("method1result",RapidApplication.getUtility(utilityName).method1());

        //change the static variable
        RapidApplication.getUtility(utilityName).x=15;
        assertEquals(15,RapidApplication.getUtility(utilityName).x);

        //reload and see that original variable come
        ApplicationHolder.application.classLoader.loadClass("application.RapidApplication").reloadOperations();
        assertEquals(5,RapidApplication.getUtility(utilityName).x);

        //change the utility see that before reload original remains
        utilityContent="""
            public class ${utilityName} {
                static int x=45;
                static String method1()
                {
                    return "newresult";
                }
            }
        """;

        createUtility(utilityName,utilityContent);
        assertEquals(5,RapidApplication.getUtility(utilityName).x);
        assertEquals("method1result",RapidApplication.getUtility(utilityName).method1());

        ApplicationHolder.application.classLoader.loadClass("application.RapidApplication").reloadOperations();
        assertEquals(45,RapidApplication.getUtility(utilityName).x);
        assertEquals("newresult",RapidApplication.getUtility(utilityName).method1());

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
package auth

import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import org.springframework.mock.web.HeaderValueHolder
import com.ifountain.rcmdb.test.util.IntegrationTestUtils
import com.ifountain.rcmdb.domain.util.ControllerUtils
import org.jsecurity.authc.UsernamePasswordTokenWithParams
import org.jsecurity.authc.AuthenticationException

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Oct 16, 2009
* Time: 11:48:18 AM
* To change this template use File | Settings | File Templates.
*/
class AuthControllerIntegrationTests extends RapidCmdbIntegrationTestCase {
    static transactional = false;


    public void setUp() {
        super.setUp();
    }

    public void tearDown() {
        super.tearDown();
    }

    public void testLoginAndIndex()
    {
        //index redirects to ligon
        def controller=new AuthController();
        controller.index();
        assertEquals('/auth/login',controller.response.redirectedUrl);        
        
        IntegrationTestUtils.resetController (controller);
        controller.params.login="testuser";
        controller.params.targetUri="testPage";

        def result=controller.login();

        assertEquals("testuser",result.username);
        assertEquals("testPage",result.targetUri);
        assertEquals(false,result.rememberMe);


        //request login with format xml                
        IntegrationTestUtils.resetController (controller);
        controller.params.targetUri="testPage";
        controller.params.format="xml";

        controller.login();
        assertEquals("<Authenticate><Url>testPage</Url></Authenticate>",controller.response.getContentAsString());
        

        //request with mobile
        IntegrationTestUtils.resetController (controller);
        controller.request.addHeader("user-agent","mobile");
        controller.params.targetUri="testPage";

        controller.login();

        assertEquals('/auth/mobilelogin?targetUri=testPage',controller.response.redirectedUrl);


    }


    public void testUnauthorized()
    {
        def controller=new AuthController();
        controller.unauthorized();
        assertEquals("You do not have permission to access this page.",controller.response.getContentAsString());

    }

    public void testUnauthorizedWithFormatXml()
    {
        def controller=new AuthController();

        controller.params.format="xml";
        controller.unauthorized();
        assertEquals(ControllerUtils.convertErrorToXml("You do not have permission to access this url"),controller.response.getContentAsString())
        println controller.response.getContentAsString();
    }

    public void testSignInSuccessfully()
    {
        def loginMock=[:];        
        loginMock.login={ UsernamePasswordTokenWithParams authToken ->
            assertEquals("testuser",authToken.username)
            assertEquals("123",new String(authToken.password))
            assertEquals(true,authToken.isRememberMe())
            return [principal:authToken.username]
        }
        def controller=new AuthController();
        controller.params.login="testuser";
        controller.params.password="123";
        controller.params.targetUri="test.gsp";
        controller.params.rememberMe="1";
        controller.jsecSecurityManager=loginMock;
        
        controller.signIn();

        assertEquals("test.gsp",controller.response.redirectedUrl);


        //login without targetUri
        IntegrationTestUtils.resetController(controller);
        controller.params.login="testuser";
        controller.params.password="123";
        controller.params.rememberMe="1";
        controller.jsecSecurityManager=loginMock;

        controller.signIn();
        assertEquals("/",controller.response.redirectedUrl);

    }
    public void testSignInSuccessfullyWithFormatXml()
    {

        def loginMock=[:];        
        loginMock.login={UsernamePasswordTokenWithParams authToken ->
            assertEquals("testuser",authToken.username)
            assertEquals("123",new String(authToken.password))
            assertEquals(false,authToken.isRememberMe())
            return [principal:authToken.username]
        }
        def controller=new AuthController();
        controller.params.format="xml";
        controller.params.login="testuser";
        controller.params.password="123";
        controller.params.targetUri="test.gsp";
        controller.jsecSecurityManager=loginMock;

        controller.signIn();

        assertEquals(ControllerUtils.convertSuccessToXml("Successfully logged in."),controller.response.getContentAsString());
        println controller.response.redirectedUrl
        println controller.response.getContentAsString();
    }

    public void testSignInFailure()
    {

       def loginMock=[:];
       loginMock.login={UsernamePasswordTokenWithParams authToken ->
            assertEquals("testuser",authToken.username)
            assertEquals("123",new String(authToken.password))
            assertEquals(true,authToken.isRememberMe())
            throw new AuthenticationException("testLoginFailue");
       }
        def controller=new AuthController();
        controller.params.login="testuser";
        controller.params.password="123";
        controller.params.targetUri="test.gsp";
        controller.params.rememberMe="2";
        controller.jsecSecurityManager=loginMock;

        controller.signIn();

        println controller.response.redirectedUrl
        assertTrue(controller.response.redirectedUrl.indexOf("/auth/login?login=testuser&rememberMe=true&targetUri=test.gsp&flash=Error%3A+Login+Failed")>=0)
        
    }
    public void testSignInFailureWithFormatXml()
    {

       def loginMock=[:];
       loginMock.login={UsernamePasswordTokenWithParams authToken ->
            assertEquals("testuser",authToken.username)
            assertEquals("123",new String(authToken.password))
            assertEquals(true,authToken.isRememberMe())
            throw new AuthenticationException("testLoginFailue");
       }
        def controller=new AuthController();
        controller.params.format="xml";
        controller.params.login="testuser";
        controller.params.password="123";
        controller.params.targetUri="test.gsp";
        controller.params.rememberMe="2";
        controller.jsecSecurityManager=loginMock;

        controller.signIn();

        assertTrue(controller.response.getContentAsString().indexOf("Login Failed")>=0);
        assertTrue(controller.response.getContentAsString().indexOf("<Errors>")>=0);

    }

    /*
    //Commented out becauses logs off current user 
    public void testLogout()
    {
        def controller=new AuthController();
        controller.params.targetUri="test.gsp";
        controller.logout();

        assertEquals("test.gsp",controller.response.redirectedUrl);
    }
    */

}

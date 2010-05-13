package email.datasource
import datasource.EmailAdapter

import com.ifountain.core.test.util.RapidCoreTestCase
import com.ifountain.core.datasource.Action
import org.apache.log4j.Logger
import com.ifountain.rcmdb.test.util.EmailConnectionImplTestUtils;
/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 19, 2009
* Time: 9:37:44 AM
* To change this template use File | Settings | File Templates.
*/
class EmailAdapterTest extends RapidCoreTestCase{
    protected void setUp() throws Exception {
        super.setUp();
        clearMetaClasses();
    }

    protected void tearDown() throws Exception {
        clearMetaClasses();
        super.tearDown();
    }
     public void clearMetaClasses()
    {
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(EmailAdapter)
        ExpandoMetaClass.enableGlobally();
    }
    public void testSendEmailPassesParamsAndLoggerToAction(){        
        def params= EmailConnectionImplTestUtils.getSendEmailParams("a@b", "b@c", "testsbj", "testbdy")
        def adapter=new EmailAdapterMock();
        adapter.sendEmail(params)
        def action=adapter.callParams.action;
        assertNotNull(action)
        assertEquals(params.size(),action.getParams().size())
        assertEquals(params,action.getParams())
        assertEquals(adapter.testLogger,action.getLogger())
    }
}

class EmailAdapterMock extends EmailAdapter
{
    public callParams=null;
    public static testLogger=Logger.getRootLogger()
    
    public EmailAdapterMock()
    {
        super("xxx",0,testLogger)
    }

   public void executeAction(Action action) throws Exception
   {
       callParams=[:]
       callParams.action=action;
   }
}


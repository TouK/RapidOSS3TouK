package email.datasource

import com.ifountain.core.test.util.RapidCoreTestCase
import datasource.EmailAdapter
import datasource.EmailDatasourceOperations
import org.apache.log4j.Logger
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import connection.EmailConnection
import com.ifountain.rcmdb.test.util.CompassForTests
import datasource.EmailDatasource;

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 19, 2009
* Time: 8:59:39 AM
* To change this template use File | Settings | File Templates.
*/
class EmailDatasourceOperationsTest extends RapidCmdbWithCompassTestCase{
    public void setUp() throws Exception {
        super.setUp();
        clearMetaClasses();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }
    public void clearMetaClasses()
    {           
        ExpandoMetaClass.disableGlobally();        
        GroovySystem.metaClassRegistry.removeMetaClass(EmailDatasourceOperations)
        ExpandoMetaClass.enableGlobally();
    }
    //renderTemplate test is implemented as integration test
    
    public void testSendEmailPassesParamsToEmailAdapter()
    {
        def params=[:]

        params.from="a@b"
        params.to="b@c"
        params.subject="testsbj"
        params.body="testbdy"
        params.contentType="text/plain"

        def adapter=new EmailAdapterMockForOperations()
        def oper=new EmailDatasourceOperations();
        oper.setAdapter(adapter)
        oper.sendEmail(params);

        
        assertEquals(params.size(),adapter.callParams.size())
        assertEquals(params,adapter.callParams)
    }
    public void testOnLoadDoesNotThrowExceptionWhenDatasourceDoesNotHaveConnection()
    {
         initialize([EmailDatasource,EmailConnection],[]);
         CompassForTests.addOperationSupport (EmailDatasource,EmailDatasourceOperations);


         def con=EmailConnection.add(name:"testcon",smtpHost:"u",smtpHost:50,protocol:EmailConnection.SMTP);
         assertFalse(con.errors.toString(),con.hasErrors());

         def newDs=EmailDatasource.add(name:"testds",connection:con);
         assertFalse(newDs.hasErrors());
         assertNotNull(newDs.adapter);

         newDs.removeRelation(connection:con);
         assertFalse(newDs.hasErrors());
         assertNull(newDs.connection);

         try{
            def dsFromRepo=EmailDatasource.get(name:newDs.name);
            assertNull(dsFromRepo.adapter);

         }
         catch(e)
         {
             e.printStackTrace();
             fail("Should not throw exception. Exception thrown is ${e}");
         }


    }
}

class EmailAdapterMockForOperations extends EmailAdapter
{
    public callParams=null;

    public EmailAdapterMockForOperations()
    {
        super("xxx",0,Logger.getRootLogger())
    }

    public void sendEmail(Map params) throws Exception{
        callParams=params
    }
}

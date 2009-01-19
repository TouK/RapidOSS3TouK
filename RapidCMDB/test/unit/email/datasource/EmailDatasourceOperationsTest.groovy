package email.datasource

import com.ifountain.core.test.util.RapidCoreTestCase
import datasource.EmailAdapter
import datasource.EmailDatasourceOperations
import org.apache.log4j.Logger;

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 19, 2009
* Time: 8:59:39 AM
* To change this template use File | Settings | File Templates.
*/
class EmailDatasourceOperationsTest extends RapidCoreTestCase{
    protected void setUp() throws Exception {
        super.setUp();
        clearMetaClasses();
    }

    protected void tearDown() throws Exception {
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
    public void testSendEmailCallsRenderTemplateAndGeneratesBodyParamIfTemplateParamIsSupplied(){
        def renderCallParams=[:];
        EmailDatasourceOperations.metaClass.renderTemplate = { templatePath,parameters ->
            renderCallParams.templatePath=templatePath
            renderCallParams.parameters=parameters
            return "testrenderresult"
        }

        def params=[:]

        params.from="a@b"
        params.to="b@c"
        params.subject="testsbj"        
        params.contentType="text/plain"
        params.template="testtemplate"
        params.templateParams=["x":5,"y":6]

        def adapter=new EmailAdapterMockForOperations()
        def oper=new EmailDatasourceOperations();
        oper.setAdapter(adapter)
        oper.sendEmail(params);

        assertEquals(params.size()+1,adapter.callParams.size())
        params.each{ key, val ->
            assertEquals(val,adapter.callParams[key])
        }
        assertEquals(adapter.callParams["body"],"testrenderresult")
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

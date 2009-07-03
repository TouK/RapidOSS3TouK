package datasource
import org.apache.log4j.Logger  
import org.codehaus.groovy.grails.web.pages.GroovyPagesTemplateEngine
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.support.*
import org.codehaus.groovy.grails.web.context.*
import com.ifountain.core.datasource.BaseAdapter

/**
* Created by IntelliJ IDEA.
* User: iFountain
* Date: Dec 23, 2008
* Time: 4:49:02 PM
* To change this template use File | Settings | File Templates.
*/
class EmailDatasourceOperations extends BaseDatasourceOperations{
    EmailAdapter adapter;
    def onLoad(){
       def ownConnection=getProperty("connection")
       if(ownConnection != null)
       {
            this.adapter = new EmailAdapter(ownConnection.name, reconnectInterval*1000, getLogger());
       }
    }

    def getAdapters()
    {
        return [adapter];
    }

    public void sendEmail(params)
    {
       def emailParams=[:]
       emailParams.putAll(params);
       if(params.containsKey("template"))
       {
           def body=renderTemplate(emailParams["template"],emailParams["templateParams"])
           emailParams["body"]=body;
       }
       this.adapter.sendEmail(emailParams);
    }

    public EmailAdapter getAdapter()
    {
        return adapter;
    }
    public void setAdapter(EmailAdapter adapter){
        this.adapter=adapter;        
    }

    public static String renderTemplate(templatePath,parameters){
        def engine=new GroovyPagesTemplateEngine()
        def template= engine.createTemplate(new File(templatePath));

        def requestAttributes = RequestContextHolder.getRequestAttributes()

        boolean unbindRequest = false

        // outside of an executing request, establish a mock version
        if(!requestAttributes) {            
            def servletContext  = ServletContextHolder.getServletContext()
            def applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext)
            requestAttributes = grails.util.GrailsWebUtil.bindMockWebRequest(applicationContext)
            unbindRequest = true
        }

        def out = new StringWriter();
        def originalOut = requestAttributes.getOut()
        requestAttributes.setOut(out)
        try {

            template.make( parameters ).writeTo(out)

        }
        finally {
            requestAttributes.setOut(originalOut)
            if(unbindRequest) {
                RequestContextHolder.setRequestAttributes(null)
            }
        }

        return out.toString();
                
    }
}
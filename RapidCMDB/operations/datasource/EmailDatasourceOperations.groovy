package datasource
import org.apache.log4j.Logger  
import org.codehaus.groovy.grails.web.pages.GroovyPagesTemplateEngine
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.support.*
import org.codehaus.groovy.grails.web.context.*
import com.ifountain.core.datasource.BaseAdapter
import com.ifountain.annotations.HideProperty

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

    @HideProperty def getAdapters()
    {
        return [adapter];
    }

    public void sendEmail(params)
    {
       def emailParams=[:]
       emailParams.putAll(params);
       this.adapter.sendEmail(emailParams);
    }

    @HideProperty public EmailAdapter getAdapter()
    {
        return adapter;
    }
    public void setAdapter(EmailAdapter adapter){
        this.adapter=adapter;        
    }
}
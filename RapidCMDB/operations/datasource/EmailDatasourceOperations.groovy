package datasource
import org.apache.log4j.Logger
/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 23, 2008
 * Time: 4:49:02 PM
 * To change this template use File | Settings | File Templates.
 */
class EmailDatasourceOperations extends BaseDatasourceOperations{
    def adapter;
    def onLoad(){
       this.adapter = new EmailAdapter(getProperty("connection").name, reconnectInterval*1000, Logger.getRootLogger());
    }

    def sendEmail(String from,String to,String subject,String body)
    {
        
    }

    def getAdapter()
    {
        return adapter;
    }
}
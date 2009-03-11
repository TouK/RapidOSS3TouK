/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 23, 2008
 * Time: 4:45:35 PM
 * To change this template use File | Settings | File Templates.
 */
package datasource

import com.ifountain.core.datasource.BaseAdapter

import org.apache.log4j.Logger
import org.apache.commons.lang.exception.ExceptionUtils


public class EmailAdapter extends BaseAdapter{

    public static final PLAIN="text/plain";
    public static final HTML="text/html";
    
    public EmailAdapter(connectionName, reconnectInterval, logger){
            super(connectionName, reconnectInterval, logger);
    }
    
     public Map<String, Object> getObject(Map<String, String> ids, List<String> fieldsToBeRetrieved) {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    protected boolean isConnectionException(Throwable t) {
        return false;/*ExceptionUtils.indexOfThrowable(t, javax.mail.MessagingException.class) > -1 ||
                ExceptionUtils.indexOfThrowable(t,javax.mail.SendFailedException.class) > -1  */
    }

     public void sendEmail(Map params) throws Exception{
        SendEmailAction action = new SendEmailAction(logger,params);
        executeAction(action);        
    }
}
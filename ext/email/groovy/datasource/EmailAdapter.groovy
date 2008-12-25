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


public class EmailAdapter extends BaseAdapter{

    public EmailAdapter(connectionName, reconnectInterval, logger){
            super(connectionName, reconnectInterval, logger);
    }
    
     public Map<String, Object> getObject(Map<String, String> ids, List<String> fieldsToBeRetrieved) {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

     public String sendMail(Map params) throws Exception{
        SendEmailAction action = new SendEmailAction(logger,params);
        executeAction(action);
        return "";
    }

}
package datasources;

import com.ifountain.smarts.datasource.BaseNotificationAdapter;
import org.apache.log4j.Logger;
import api.RS;

public class NotificationAdapter extends BaseNotificationAdapter {
    
    public NotificationAdapter(){
    	super();
    }
    
    public NotificationAdapter(String datasourceName, long reconnectInterval, Logger logger) {
        super(datasourceName, reconnectInterval, logger);
    }    
    
    public static getInstance(String datasourceName, long reconnectInterval){
    	return new NotificationAdapter(datasourceName, reconnectInterval, RS.getSession().logger);
    }

    public static getInstance(datasourceName){
    	return new NotificationAdapter(datasourceName, 0, RS.getSession().logger);
    }   
    
}
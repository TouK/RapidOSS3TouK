package datasources;

import com.ifountain.smarts.datasource.BaseNotificationAdapter;
import org.apache.log4j.Logger;

public class NotificationAdapter extends BaseNotificationAdapter {

    public NotificationAdapter() {
        super();
    }

    public NotificationAdapter(String datasourceName, long reconnectInterval, Logger logger) {
        super(datasourceName, reconnectInterval, logger); 
    }
    

    
    public Map<String, Object> getNotification(String className, String instanceName, String eventName, List<String> attributes) {
        Map<String, Object> result = super.getNotification(className, instanceName, eventName, attributes); //To change body of overridden methods use File | Settings | File Templates.
        if(!result)
        {
            result = [:];
        }
        return result;
    }
    
}
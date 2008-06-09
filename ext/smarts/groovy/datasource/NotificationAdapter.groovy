package datasource

import com.ifountain.smarts.datasource.BaseNotificationAdapter
import org.apache.log4j.Logger;

public class NotificationAdapter extends BaseNotificationAdapter {

    public NotificationAdapter() {
        super();
    }

    public NotificationAdapter(String datasourceName, long reconnectInterval, Logger logger) {
        super(datasourceName, reconnectInterval, logger); 
    }
}
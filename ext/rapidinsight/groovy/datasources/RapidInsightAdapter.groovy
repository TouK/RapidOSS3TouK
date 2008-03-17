package datasources;

import java.util.Map;
import org.apache.log4j.Logger;
import com.ifountain.core.datasource.BaseAdapter;

public class RapidInsightAdapter extends HttpAdapter {
	
	public RapidInsightAdapter(Logger logger) {
        super();
        setLogger(logger);
    }
	
    public RapidInsightAdapter(String connectionName, long reconnectInterval, Logger logger) {
        super(connectionName, reconnectInterval, logger);
    }
    
	public static getInstance(){
	    return new RapidInsightAdapter(Logger.getRootLogger());
	}
	public static getInstance(connectionName){
	    return new RapidInsightAdapter(connectionName, 0, Logger.getRootLogger());
	}    

}
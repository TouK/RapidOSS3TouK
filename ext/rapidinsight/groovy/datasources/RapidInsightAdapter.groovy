package datasources;

import java.util.Map;
import org.apache.log4j.Logger;
import com.ifountain.core.datasource.BaseAdapter;
import api.RS;

public class RapidInsightAdapter extends HttpAdapter {
	
	public RapidInsightAdapter(Logger logger) {
        super();
        setLogger(logger);
    }
	
    public RapidInsightAdapter(String connectionName, long reconnectInterval, Logger logger) {
        super(connectionName, reconnectInterval, logger);
    }
    
	public static getInstance(){
	    return new RapidInsightAdapter(RS.getSession().logger);
	}
	public static getInstance(connectionName){
	    return new RapidInsightAdapter(connectionName, 0, RS.getSession().logger);
	}    

}
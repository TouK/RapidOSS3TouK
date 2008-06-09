package datasource

import com.ifountain.smarts.datasource.BaseTopologyAdapter
import org.apache.log4j.Logger;

public class TopologyAdapter extends BaseTopologyAdapter {
    
    public TopologyAdapter(){
    	super();
    }
    
    public TopologyAdapter(String datasourceName, long reconnectInterval, Logger logger) {
        super(datasourceName, reconnectInterval, logger);
    }    
    
}
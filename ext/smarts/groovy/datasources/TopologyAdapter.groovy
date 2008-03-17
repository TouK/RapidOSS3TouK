package datasources;

import com.ifountain.smarts.datasource.BaseTopologyAdapter;
import org.apache.log4j.Logger;

public class TopologyAdapter extends BaseTopologyAdapter {
    
    public TopologyAdapter(){
    	super();
    }
    
    public TopologyAdapter(String datasourceName, long reconnectInterval, Logger logger) {
        super(datasourceName, reconnectInterval, logger);
    }    
    
    def static getInstance(String datasourceName, long reconnectInterval){
    	return new TopologyAdapter(datasourceName, reconnectInterval,  Logger.getRootLogger());
    }

    def static getInstance(datasourceName){
    	return new TopologyAdapter(datasourceName, 0,  Logger.getRootLogger());
    }   
    
    def addObject(className, instanceName, params){
    	createTopologyInstanceWithProperties(className, instanceName, params);
    }
    
    def addObject(Map params){
    	def className = params.ClassName;
    	def instanceName = params.InstanceName;
    	params.remove(params.ClassName);
    	params.remove(params.InstanceName);
   		createTopologyInstanceWithProperties(className, instanceName, params);
    }
    
    def setTest(String newTest){
    	this.testString = newTest;
    }
}
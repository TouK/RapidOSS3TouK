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
    
    def addObject(className, instanceName, params){
    	createTopologyInstanceWithProperties(className, instanceName, params);
    }

    public Map<String, Object> getObject(String className, String instanceName, List<String> atts) {
        Map<String, Object> result = super.getObject(className, instanceName, atts); //To change body of overridden methods use File | Settings | File Templates.
        if(!result)
        {
            result = [:];
        }
        return result;
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
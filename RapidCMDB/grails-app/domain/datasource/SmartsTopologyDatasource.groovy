package datasource;
import datasource.TopologyAdapter
import org.apache.log4j.Logger
import connection.SmartsConnection

class SmartsTopologyDatasource extends BaseDatasource{
    SmartsConnection connection;
    def adapter;
    static transients =  ['adapter']
    static mapping = {
        tablePerHierarchy false
    }

    def onLoad = {
        this.adapter = new TopologyAdapter(connection.name, 0, Logger.getRootLogger());
    }

    def getProperty(Map keys, String propName)
     {
         def prop = this.adapter.getObject(keys.CreationClassName, keys.Name, [propName]);
         if(prop)
         {
             return prop[propName];
         }
         return "";
     }

    def getProperties(Map keys, List properties){
        return this.adapter.getObject(keys.CreationClassName, keys.Name, properties);
    }

    def addObject(Map params){
        def tempParams = [:];
        tempParams.putAll(params);
        def className = tempParams.CreationClassName;
    	def instanceName = tempParams.Name;
    	tempParams.remove("CreationClassName");
    	tempParams.remove("Name");
   		this.adapter.createTopologyInstanceWithProperties(className, instanceName, tempParams);
    }

    def getObject(Map keys){
        return this.adapter.getObject(keys.CreationClassName, keys.Name);
    }

    def getObject(Map keys, List properties){
        Map<String, Object> result = this.adapter.getObject(keys.CreationClassName, keys.Name, properties);
        if (!result){
            result = [:];
        }
        return result;
    }

    def getObjects(Map keys){
        return this.adapter.getObjects(keys.CreationClassName, keys.Name);
    }

    def getObjects(Map keys, boolean expEnabled){
        return this.adapter.getObjects(keys.CreationClassName, keys.Name, expEnabled);
    }

    def getObjects(Map keys, List properties, boolean expEnabled){
        return this.adapter.getObjects(keys.CreationClassName, keys.Name, properties, expEnabled);
    }

    def removeObject(Map keys){
        this.adapter.deleteTopologyInstance(keys.CreationClassName, keys.Name);
    }

    def updateObject(Map updateParams){
        def tempParams = [:];
        tempParams.putAll(updateParams);
        def className = tempParams.CreationClassName;
    	def instanceName = tempParams.Name;
    	tempParams.remove("CreationClassName");
    	tempParams.remove("Name");
        this.adapter.updateTopologyInstanceWithProperties(className, instanceName, tempParams);
    }

    def addTopologyRelation(firstClassName, firstInstanceName, secondClassName, secondInstanceName, relationName){
        this.adapter.addRelationshipBetweenTopologyObjects(firstClassName, firstInstanceName, secondClassName, secondInstanceName, relationName);
    }

    def removeTopologyRelation(firstClassName, firstInstanceName, secondClassName, secondInstanceName, relationName){
        this.adapter.removeTopologyRelationship(firstClassName, firstInstanceName, secondClassName, secondInstanceName, relationName);
    }

}

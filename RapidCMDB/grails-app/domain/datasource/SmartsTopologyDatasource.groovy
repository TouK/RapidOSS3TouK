package datasource

import connection.SmartsConnection
import datasource.TopologyAdapter
import org.apache.log4j.Logger;
class SmartsTopologyDatasource extends BaseDatasource{
    static searchable = {
        except = [];
    };
    static datasources = [:]

    
    SmartsConnection connection ;
    

    static hasMany = [:]
    
    static constraints={
    connection(nullable:true)
        
     
    }

    static mappedBy=["connection":"smartsTopologyDatasources"]
    static belongsTo = []
    def adapter;
    static transients =  ['adapter']

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

    def invokeOperation(className, instanceName, opName, opParams){
        this.adapter.invokeOperation(className, instanceName, opName, opParams);
    }
}

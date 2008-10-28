package datasource

import com.ifountain.smarts.datasource.BaseTopologyAdapter
import com.ifountain.smarts.datasource.SmartsTopologyListeningAdapter
import com.ifountain.smarts.util.params.SmartsSubscribeParameters
import org.apache.log4j.Logger

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 11, 2008
 * Time: 6:04:20 PM
 * To change this template use File | Settings | File Templates.
 */
class SmartsTopologyDatasourceOperations extends BaseListeningDatasourceOperations{

    def adapter;
    def onLoad(){
        this.adapter = new BaseTopologyAdapter(connection.name, reconnectInterval*1000, Logger.getRootLogger());
    }

    def getProperty(Map keys, String propName)
     {
         checkParams(keys, ["CreationClassName", "Name"]);
         def prop = this.adapter.getObject(keys.CreationClassName, keys.Name, [propName]);
         if(prop)
         {
             return prop[propName];
         }
         return "";
     }

    def getProperties(Map keys, List properties){
        checkParams(keys, ["CreationClassName", "Name"]);
        return this.adapter.getObject(keys.CreationClassName, keys.Name, properties);
    }

    def getListeningAdapter(Map params){
        def paramsList = params.subscribeParameters;
        def smartsSubscribeParamsList = [];
        paramsList.each{
            smartsSubscribeParamsList.add(new SmartsSubscribeParameters(it.CreationClassName, it.Name, it.Attributes as String[]));
        }
        return new SmartsTopologyListeningAdapter(connection.name, reconnectInterval*1000, params.logger, smartsSubscribeParamsList as SmartsSubscribeParameters[]);
    }

    def addObject(Map params){
        checkParams(params, ["CreationClassName", "Name"]);
        def tempParams = [:];
        tempParams.putAll(params);
        def className = tempParams.CreationClassName;
    	def instanceName = tempParams.Name;
    	tempParams.remove("CreationClassName");
    	tempParams.remove("Name");
   		this.adapter.createTopologyInstanceWithProperties(className, instanceName, tempParams);
    }

    def getObject(Map keys){
        checkParams(keys, ["CreationClassName", "Name"]);
        return this.adapter.getObject(keys.CreationClassName, keys.Name);
    }

    def getObject(Map keys, List properties){
        checkParams(keys, ["CreationClassName", "Name"]);
        Map<String, Object> result = this.adapter.getObject(keys.CreationClassName, keys.Name, properties);
        if (!result){
            result = [:];
        }
        return result;
    }

    def getObjects(Map keys){
        checkParams(keys, ["CreationClassName", "Name"]);
        return this.adapter.getObjects(keys.CreationClassName, keys.Name);
    }

    def getObjects(Map keys, boolean expEnabled){
        checkParams(keys, ["CreationClassName", "Name"]);
        return this.adapter.getObjects(keys.CreationClassName, keys.Name, expEnabled);
    }

    def getObjects(Map keys, List properties, boolean expEnabled){
        checkParams(keys, ["CreationClassName", "Name"]);
        return this.adapter.getObjects(keys.CreationClassName, keys.Name, properties, expEnabled);
    }

    def removeObject(Map keys){
        checkParams(keys, ["CreationClassName", "Name"]);
        this.adapter.deleteTopologyInstance(keys.CreationClassName, keys.Name);
    }

    def updateObject(Map updateParams){
        checkParams(updateParams, ["CreationClassName", "Name"]);
        def tempParams = [:];
        tempParams.putAll(updateParams);
        def className = tempParams.CreationClassName;
    	def instanceName = tempParams.Name;
    	tempParams.remove("CreationClassName");
    	tempParams.remove("Name");
        this.adapter.updateTopologyInstanceWithProperties(className, instanceName, tempParams);
    }

    def addTopologyRelation(firstClassName, firstInstanceName, secondClassName, secondInstanceName, relationName){
        checkParams([firstClassName:firstClassName, firstInstanceName:firstInstanceName, secondClassName:secondClassName, secondInstanceName:secondInstanceName, relationName:relationName], ["relationName", "secondInstanceName", "secondClassName","firstInstanceName","firstClassName"]);
        this.adapter.addRelationshipBetweenTopologyObjects(firstClassName, firstInstanceName, secondClassName, secondInstanceName, relationName);
    }

    def removeTopologyRelation(firstClassName, firstInstanceName, secondClassName, secondInstanceName, relationName){
        checkParams([firstClassName:firstClassName, firstInstanceName:firstInstanceName, secondClassName:secondClassName, secondInstanceName:secondInstanceName, relationName:relationName], ["relationName", "secondInstanceName", "secondClassName","firstInstanceName","firstClassName"]);
        this.adapter.removeTopologyRelationship(firstClassName, firstInstanceName, secondClassName, secondInstanceName, relationName);
    }

    def invokeOperation(className, instanceName, opName, opParams){
        checkParams([ClassName:className, InstanceName:instanceName, OperationName:opName], ["ClassName", "InstanceName", "OperationName"]);
        opParams.each{
            if(it == null)
            {
                throw new Exception("Operation parameters cannot be null.");
            }    
        }
        this.adapter.invokeOperation(className, instanceName, opName, opParams);
    }

    def checkParams(Map params, List requiredParams)
    {
        requiredParams.each{paramName->
            if(params[paramName] == null)
            {
                throw new Exception("Mandatory parameter ${paramName} can not be null.");
            }
        }
        params.each{paramName, paramsValue->
            if(paramsValue == null)
            {
                throw new Exception("Parameter ${paramName} can not be null.");
            }
        }
    }
}
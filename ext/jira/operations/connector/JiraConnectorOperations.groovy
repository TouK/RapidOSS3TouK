package connector

import connection.JiraConnection
import datasource.JiraDatasource

class JiraConnectorOperations  extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{
	static def getConnectionName(connectorName){
        return "${connectorName}Conn";
    }
    static def getDatasourceName(connectorName){
        return "${connectorName}Ds";
    }
    public static Map updateConnector(JiraConnector jiraConnector, Map connectorParams, Map datasourceParams, Map connectionParams)    {
        def oldConnName =  JiraConnector.getConnectionName(jiraConnector.name).toString();
        def jiraConnection = JiraConnection.get(name: oldConnName);

        def oldDsName =  JiraConnector.getDatasourceName(jiraConnector.name).toString();
        def jiraDatasource = JiraDatasource.get(name: oldDsName);
        
        def updatedObjects = [:];
        def oldConnectorProps = [:]
        def oldConnectionProps = [:]
        def oldDatasourceProps = [:]
        connectorParams.each{String propName, Object value->
            oldConnectorProps[propName] = jiraConnector.getProperty (propName);
        }
        connectionParams.each{String propName, Object value->
            oldConnectionProps[propName] = jiraConnection.getProperty (propName);
        }

        datasourceParams.each{String propName, Object value->
	        oldDatasourceProps[propName] = jiraDatasource.getProperty (propName);
	    }
        
        def newConnName =  JiraConnector.getConnectionName(connectorParams.name).toString();
        connectionParams.name = newConnName;
        def newDsName =  JiraConnector.getDatasourceName(connectorParams.name).toString();
        datasourceParams.name = newDsName;
        jiraConnector.update(connectorParams);
        jiraConnection.update(connectionParams);
        jiraDatasource.update(datasourceParams);
        updatedObjects["jiraConnector"] = jiraConnector;
        updatedObjects["jiraConnection"] = jiraConnection;
        updatedObjects["jiraDatasource"] = jiraDatasource;
        if(jiraConnection.hasErrors() || jiraConnector.hasErrors() || jiraDatasource.hasErrors()) {
            JiraConnector.get(id:jiraConnector.id).update(oldConnectorProps);
            JiraConnection.get(id:jiraConnection.id).update(oldConnectionProps);
            JiraDatasource.get(id:jiraDatasource.id).update(oldDatasourceProps);
        }
        else  {
            boolean isConnectorNameChanged = oldConnectorProps.name != connectorParams.name;
            if(!oldConnectionProps.equals(connectionParams) || isConnectorNameChanged) {
                if(isConnectorNameChanged){
                    jiraConnector.ds.update(name:JiraConnector.getDatasourceName(jiraConnector.name));
                    jiraConnector.ds.connection.update(name:JiraConnector.getDatasourceName(jiraConnector.name));
                }
            }
        }
        return updatedObjects;
    }
    public static Map addConnector(Map connectorParams, Map datasourceParams, Map connectionParams){
        def createdObjects = [:];
        try{
            def jiraConnector = JiraConnector.add(connectorParams)
            createdObjects["jiraConnector"] = jiraConnector;
            if(!jiraConnector.hasErrors()){
                def connectionName = getConnectionName(jiraConnector.name);
                connectionParams.name = connectionName;
                def jiraConnection = JiraConnection.add(connectionParams)
                createdObjects["jiraConnection"] = jiraConnection;
                if(!jiraConnection.hasErrors()){
                    def datasourceName = JiraConnector.getDatasourceName(jiraConnector.name)
                    def reconnectInterval = datasourceParams.reconnectInterval; 
                    def datasource = datasource.JiraDatasource.add(name: datasourceName, reconnectInterval:reconnectInterval, connection:jiraConnection);
                    createdObjects["datasource"] = datasource;
                    if(!datasource.hasErrors()){
                        datasource.addRelation(connection:jiraConnection);
                        jiraConnector.addRelation(ds:datasource);
                    }
                }
            }
        }
        finally{
            def objectsWithoutError = createdObjects.values().findAll {!it.hasErrors()}
            if(objectsWithoutError.size() != createdObjects.size())
            {
                objectsWithoutError*.remove();
            }
        }
        return createdObjects;
    }
}
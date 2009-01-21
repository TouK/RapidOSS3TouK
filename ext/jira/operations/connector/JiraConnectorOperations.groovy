package connector

import connection.JiraConnection

class JiraConnectorOperations  extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{
	static def getConnectionName(connectorName){
        return "${connectorName}Conn";
    }
    static def getDatasourceName(connectorName){
        return "${connectorName}Ds";
    }
    public static Map updateConnector(JiraConnector jiraConnector, Map connectorParams, Map connectionParams)    {
        def oldConnName =  JiraConnector.getConnectionName(jiraConnector.name).toString();
        def jiraConnection = JiraConnection.get(name: oldConnName);

        def updatedObjects = [:];
        def oldConnectorProps = [:]
        def oldConnectionProps = [:]
        connectorParams.each{String propName, Object value->
            oldConnectorProps[propName] = jiraConnector.getProperty (propName);
        }
        connectionParams.each{String propName, Object value->
            oldConnectionProps[propName] = jiraConnection.getProperty (propName);
        }

        def newConnName =  JiraConnector.getConnectionName(jiraConnector.name).toString();
        connectionParams.name = newConnName;
        jiraConnector.update(connectorParams);
        jiraConnection.update(connectionParams);
        updatedObjects["jiraConnector"] = jiraConnector;
        updatedObjects["jiraConnection"] = jiraConnection;
        if(jiraConnection.hasErrors() || jiraConnector.hasErrors()) {
            JiraConnector.get(id:jiraConnector.id).update(oldConnectorProps);
            JiraConnection.get(id:jiraConnection.id).update(oldConnectionProps);
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
    public static Map addConnector(Map connectorParams, Map connectionParams){
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
                    def datasource = datasource.JiraDatasource.add(name: datasourceName, connection:jiraConnection);
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
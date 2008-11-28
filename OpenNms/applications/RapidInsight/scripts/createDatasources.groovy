import connection.DatabaseConnection
import datasource.SingleTableDatabaseDatasource

import connection.HttpConnection
import datasource.HttpDatasource




def dbDatasourceParams=[]
dbDatasourceParams.add([name:"openNmsAlarmsDs",tableName:"alarms",tableKeys:"alarmid"]);
dbDatasourceParams.add([name:"openNmsNodesDs",tableName:"node",tableKeys:"nodeid"]);
dbDatasourceParams.add([name:"openNmsInterfacesDs",tableName:"ipinterface",tableKeys:"id"]);
dbDatasourceParams.add([name:"openNmsSnmpInterfacesDs",tableName:"snmpinterface",tableKeys:"id"]);
dbDatasourceParams.add([name:"openNmsServicesDs",tableName:"ifservices",tableKeys:"id"]);
dbDatasourceParams.add([name:"openNmsServiceMapDs",tableName:"servicemap",tableKeys:"ipaddr,servicemapname"]);

def dbConnection=DatabaseConnection.add(driver:"org.postgresql.Driver",url:"jdbc:postgresql://localhost:5432/opennms",username:"opennms",userPassword:"opennms");
if(dbConnection.hasErrors())
{
    logger.warn("Can not create dbConnection. Reason: ${dbConnection.errors}");
}
else
{
    logger.warn("Created dbConnection");
    for(dsParams in dbDatasourceParams)
    {
        dsParams.connection=dbConnection;
        def dbDatasource=SingleTableDatabaseDatasource.add(dsParams);
        if(dbDatasource.hasErros())
        {
            logger.warn("Can not create dbDatasource ${dsParams.name}. Reason: ${dbDatasource.errors}");
        }
        else
        {
            logger.warn("Created dbDatasource ${dsParams.name} ");        
        }
        
                
    }
}


def httpConnection=HttpConnection.add(baseUrl:"http://localhost:8980/opennms/");
if(httpConnection.hasErrors())
{
    logger.warn("Can not create httpConnection. Reason: ${httpConnection.errors}");
}
else
{
    logger.warn("Created httpConnection");
    def httpDatasource=HttpDatasource.add(connection:httpConnection);
    if(httpDatasource.hasErrors())
    {
        logger.warn("Can not create httpDatasource. Reason: ${httpDatasource.errors}");
    }
    else
    {
        logger.warn("Created httpDatasource");
    }        
}
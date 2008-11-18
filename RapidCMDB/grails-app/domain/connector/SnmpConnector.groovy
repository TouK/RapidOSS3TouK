package connector

import script.CmdbScript
import connection.SnmpConnection

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Nov 18, 2008
* Time: 2:16:23 PM
*/
class SnmpConnector {
   static searchable = {
        except = [];
    };
    static datasources = ["RCMDB":["keys":["name":["nameInDs":"name"]]]]
    String name ="";
    String rsOwner = "p";
    CmdbScript script;
    SnmpConnection connection;


    static relations  =[
        script:[type:CmdbScript, isMany:false],
        connection:[type:SnmpConnection, isMany:false]
    ]
    static constraints={
      name(blank:false,nullable:false,key:[])
      script(nullable:true)
      connection(nullable:true)
    }
    static transients = [];

    public String toString()
    {
    	return name;
    }

    static def getConnectionName(connectorName){
        return "${connectorName}Conn";
    }

    static def getDatasourceName(connectorName){
        return "${connectorName}Ds";
    }
}
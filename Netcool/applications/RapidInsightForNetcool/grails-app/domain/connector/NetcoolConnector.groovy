package connector

import org.apache.log4j.Level

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Aug 13, 2008
* Time: 11:19:01 AM
*/

class NetcoolConnector
{
    static searchable = {
        except = [];
    };
    static datasources = ["RCMDB":["keys":["name":["nameInDs":"name"]]]]

    String name ="ncoms";
    String rsOwner = "p"
    String logLevel = Level.WARN.toString();
    static relations = [:]
    static constraints={
      name(blank:false,nullable:false,key:[])
      logLevel(inList:[Level.ALL.toString(),Level.DEBUG.toString(),Level.INFO.toString(),
              Level.WARN.toString(), Level.ERROR.toString(), Level.FATAL.toString(), Level.OFF.toString()])
    }
    static propertyConfiguration= [:]
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
    static def getScriptName(connectorName){
        return "${connectorName}Connector";
    }
}
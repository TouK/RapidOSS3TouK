package connector
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 29, 2008
 * Time: 8:42:36 AM
 * To change this template use File | Settings | File Templates.
 */
class SmartsListeningTopologyConnector extends SmartsConnector{
    static searchable = {
        except = [];
    };
    static datasources = [:]

    static relations = [:]

    static constraints={
    }
    static propertyConfiguration= [:]
    static transients = [];

    public String toString()
    {
    	return name;
    }

    static def getConnectionName(connectorName){
        return "${connectorName}TopologyConn";
    }

    static def getDatasourceName(connectorName){
        return "${connectorName}TopologyDs";
    }
    static def getScriptName(connectorName){
        return "${connectorName}TopologyListeningScript";
    }
}
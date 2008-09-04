package connector
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 29, 2008
 * Time: 8:43:17 AM
 * To change this template use File | Settings | File Templates.
 */
class SmartsListeningNotificationConnector extends SmartsConnector{
    String notificationList;
    boolean tailMode;
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
        return "${connectorName}NotificationConn";
    }

    static def getDatasourceName(connectorName){
        return "${connectorName}NotificationDs";
    }
    static def getScriptName(connectorName){
        return "${connectorName}NotificationListeningScript";
    }
}
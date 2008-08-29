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

    static hasMany = [:]

    static constraints={
    }
    static mappedBy=[:]
    static belongsTo = []
    static propertyConfiguration= [:]
    static transients = [];

    public String toString()
    {
    	return name;
    }
}
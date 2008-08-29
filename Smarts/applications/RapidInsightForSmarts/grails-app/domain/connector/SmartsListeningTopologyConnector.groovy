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
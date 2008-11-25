package datasource

import connection.OpenNmsHttpConnection
import connection.OpenNmsHttpConnection

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Nov 21, 2008
* Time: 9:35:53 AM
*/
class OpenNmsHttpDatasource extends BaseDatasource{
    static searchable = {
        except = ["connection"];
    };
    static datasources = [:]

    OpenNmsHttpConnection connection;
    Long reconnectInterval = 0;

    static relations = [
            connection: [isMany: false, reverseName: "openNMSHttpDatasources", type: OpenNmsHttpConnection]
    ]
    static constraints = {
        connection(nullable: false)
    }
    static transients = [];
}
package datasource

import connection.OpenNMSHttpConnection

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Nov 21, 2008
* Time: 9:35:53 AM
*/
class OpenNMSHttpDatasource extends BaseDatasource{
    static searchable = {
        except = ["connection"];
    };
    static datasources = [:]

    OpenNMSHttpConnection connection;
    Long reconnectInterval = 0;

    static relations = [
            connection: [isMany: false, reverseName: "openNMSHttpDatasources", type: OpenNMSHttpConnection]
    ]
    static constraints = {
        connection(nullable: false)
    }
    static transients = [];
}
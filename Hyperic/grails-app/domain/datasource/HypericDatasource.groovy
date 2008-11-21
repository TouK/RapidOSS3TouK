package datasource

import connection.HypericConnection

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Nov 21, 2008
* Time: 9:35:53 AM
*/
class HypericDatasource extends BaseDatasource{
    static searchable = {
        except = ["connection"];
    };
    static datasources = [:]

    HypericConnection connection;
    Long reconnectInterval = 0;

    static relations = [
            connection: [isMany: false, reverseName: "hypericDatasources", type: HypericConnection]
    ]
    static constraints = {
        connection(nullable: false)
    }
    static transients = [];
}
package datasource

import connection.TcpListeningConnection

/**
* Created by Sezgin Kucukkaraaslan
* Date: Oct 28, 2010
* Time: 3:34:03 PM
*/
class TcpListeningDatasource extends BaseListeningDatasource {
    static searchable = {
        except = ["connection"];
    };
    static datasources = [:]

    TcpListeningConnection connection;
    org.springframework.validation.Errors errors;

    static relations = [
            connection: [isMany: false, reverseName: "tcpListeningDatasources", type: TcpListeningConnection]
    ]
    static constraints = {
        connection(nullable: false)
    }
}
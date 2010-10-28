package connection

import com.ifountain.rcmdb.tcp.connection.TcpListeningConnectionImpl
import datasource.TcpListeningDatasource

/**
* Created by Sezgin Kucukkaraaslan
* Date: Oct 28, 2010
* Time: 3:31:58 PM
*/
class TcpListeningConnection extends Connection {
    static searchable = {
        except = ["tcpListeningDatasources"];
    };
    static cascaded = ["tcpListeningDatasources": true]
    static datasources = [:]

    String connectionClass = TcpListeningConnectionImpl.class.name;
    String host = "localhost";
    Long port = 9999;
    Long maxNumberOfConnections = 1;
    List tcpListeningDatasources = [];
    org.springframework.validation.Errors errors;
    static relations = [
            tcpListeningDatasources: [isMany: true, reverseName: "connection", type: TcpListeningDatasource]
    ]
    static constraints = {
        port(nullable: false)
        host(blank: false, nullable: false)
    }

    static transients = [];
}
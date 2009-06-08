package connection

import com.ifountain.rcmdb.sms.connection.SmsConnectionImpl
import datasource.SmsDatasource

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 5, 2009
* Time: 2:29:03 PM
*/
class SmsConnection extends Connection {
    static searchable = {
        except = ["smsDatasources"];
    };
    static cascaded = ["smsDatasources": true]
    static datasources = [:]

    String connectionClass = SmsConnectionImpl.class.name;
    String host = "";
    String username = "";
    String userPassword = "";
    Long port = 2775;
    Long maxNumberOfConnections = 1;
    Long minTimeout = 20;
    List smsDatasources = [];
    org.springframework.validation.Errors errors;
    static relations = [
            smsDatasources: [isMany: true, reverseName: "connection", type: SmsDatasource]
    ]
    static constraints = {
        userPassword(blank: true, nullable: true)
        port(nullable: false)
        username(blank: false)
        host(blank: false, nullable: false)
    }

    static transients = [];
}
package connection

import com.ifountain.rcmdb.jabber.connection.JabberConnectionImpl
import datasource.JabberDatasource

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 3, 2009
* Time: 11:42:21 AM
*/
class JabberConnection extends Connection {
    static searchable = {
        except = ["jabberDatasources"];
    };
    static cascaded = ["jabberDatasources": true]
    static datasources = [:]

    String connectionClass = JabberConnectionImpl.class.name;
    String host = "talk.google.com";
    Long port= 5222;
    String username = "";
    String userPassword = "";
    String serviceName = "gmail.com";
    Long maxNumberOfConnections = 1;
    Long minTimeout = 20;
    List jabberDatasources = [];
    org.springframework.validation.Errors errors;
    static relations = [
            jabberDatasources: [isMany: true, reverseName: "connection", type: JabberDatasource]
    ]
    static constraints = {
        userPassword(blank: false, nullable: false)
        serviceName(blank: false, nullable: false)
        username(blank: false)
        host(blank: false, nullable: false)
    }

    static transients = [];
}
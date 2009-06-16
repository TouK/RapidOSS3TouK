package connection

import com.ifountain.rcmdb.aol.connection.AolConnectionImpl
import datasource.AolDatasource

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 9, 2009
* Time: 4:40:19 PM
*/
class AolConnection extends Connection{
   static searchable = {
        except = ["aolDatasources"];
    };
    static cascaded = ["aolDatasources": true]
    static datasources = [:]

    String connectionClass = AolConnectionImpl.class.name;
    String host = "login.oscar.aol.com";
    String username = "";
    String userPassword = "";
    Long port = 5190;
    Long maxNumberOfConnections = 1;
    Long minTimeout = 20;
    List aolDatasources = [];
    org.springframework.validation.Errors errors;
    static relations = [
            aolDatasources: [isMany: true, reverseName: "connection", type: AolDatasource]
    ]
    static constraints = {
        userPassword(blank: true, nullable: true)
        port(nullable: false)
        username(blank: false)
        host(blank: false, nullable: false)
    }

    static transients = [];
}
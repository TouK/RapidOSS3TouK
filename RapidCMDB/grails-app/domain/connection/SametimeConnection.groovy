package connection

import com.ifountain.rcmdb.sametime.connection.SametimeConnectionImpl
import datasource.SametimeDatasource

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: May 21, 2009
* Time: 1:45:35 PM
*/
class SametimeConnection extends Connection {
    static searchable = {
        except = ["sametimeDatasources"];
    };
    static cascaded = ["sametimeDatasources": true]
    static datasources = [:]

    String connectionClass = SametimeConnectionImpl.class.name;
    String host = "stdemo3.dfw.ibm.com";
    String username = "";
    String userPassword = "";
    String community = "";
    Long maxNumberOfConnections = 1;
    Long minTimeout = 20;
    List sametimeDatasources = [];
    org.springframework.validation.Errors errors;
    static relations = [
            sametimeDatasources: [isMany: true, reverseName: "connection", type: SametimeDatasource]
    ]
    static constraints = {
        userPassword(blank: true, nullable: true)
        community(blank: true, nullable: true)
        username(blank: false)
        host(blank: false, nullable: false)
    }

    static transients = [];
}
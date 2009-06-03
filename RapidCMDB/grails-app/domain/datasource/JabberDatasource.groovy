package datasource

import connection.JabberConnection

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 3, 2009
* Time: 11:44:57 AM
*/
class JabberDatasource extends BaseDatasource {
    static searchable = {
        except = ["connection"];
    };
    static datasources = [:]

    JabberConnection connection;
    Long reconnectInterval = 0;
    org.springframework.validation.Errors errors;

    static relations = [
            connection: [isMany: false, reverseName: "jabberDatasources", type: JabberConnection]
    ]
    static constraints = {
        connection(nullable: false)
    }
    static transients = []
}
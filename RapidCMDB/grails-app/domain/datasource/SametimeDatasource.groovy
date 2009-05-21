package datasource

import connection.SametimeConnection

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: May 21, 2009
* Time: 1:49:19 PM
*/
class SametimeDatasource extends BaseDatasource {
    static searchable = {
        except = ["connection"];
    };
    static datasources = [:]

    SametimeConnection connection;
    Long reconnectInterval = 0;
    org.springframework.validation.Errors errors;

    static relations = [
            connection: [isMany: false, reverseName: "sametimeDatasources", type: SametimeConnection]
    ]
    static constraints = {
        connection(nullable: false)
    }


    static transients = []
}
package datasource

import connection.AolConnection

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 9, 2009
* Time: 4:41:17 PM
*/
class AolDatasource extends BaseDatasource {
    static searchable = {
        except = ["connection"];
    };
    static datasources = [:]

    AolConnection connection;
    Long reconnectInterval = 0;
    org.springframework.validation.Errors errors;

    static relations = [
            connection: [isMany: false, reverseName: "aolDatasources", type: AolConnection]
    ]
    static constraints = {
        connection(nullable: false)
    }
    static transients = []
}
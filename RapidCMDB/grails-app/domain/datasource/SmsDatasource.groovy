package datasource

import connection.SmsConnection

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 5, 2009
* Time: 2:32:24 PM
*/
class SmsDatasource extends BaseDatasource{
   static searchable = {
        except = ["connection"];
    };
    static datasources = [:]

    SmsConnection connection;
    Long reconnectInterval = 0;
    org.springframework.validation.Errors errors;

    static relations = [
            connection: [isMany: false, reverseName: "smsDatasources", type: SmsConnection]
    ]
    static constraints = {
        connection(nullable: false)
    }
    static transients = []
}
package datasource

import connection.RepositoryConnection

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: May 13, 2009
* Time: 3:43:43 PM
*/
class RepositoryDatasource extends BaseListeningDatasource{
    static searchable = {
        except = ["connection"];
    };
    static datasources = [:]
    Long id;
    Long version;
    RepositoryConnection connection;
    Long reconnectInterval = 0;
    org.springframework.validation.Errors errors;


    static relations = [
            connection: [isMany: false, reverseName: "repositoryDatasources", type: RepositoryConnection]
    ]
    static constraints = {
        connection(nullable: false)

    }
    static transients = []
}
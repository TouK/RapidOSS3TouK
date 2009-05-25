package connection

import datasource.RepositoryDatasource
import com.ifountain.rcmdb.domain.connection.RepositoryConnectionImpl

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: May 13, 2009
* Time: 3:43:04 PM
*/
class RepositoryConnection extends Connection {
    public static final String RCMDB_REPOSITORY = "RapidCmdbRepository";
    static searchable = {
        except = ["repositoryDatasources"];
    };
    static cascaded = ["repositoryDatasources": true]
    static datasources = [:]
    Long id;
    Long version;
    String connectionClass = RepositoryConnectionImpl.class.name;
    List repositoryDatasources = [];
    org.springframework.validation.Errors errors;
    static relations = [
            repositoryDatasources: [isMany: true, reverseName: "connection", type: RepositoryDatasource]
    ]
    static constraints = {
    }

    static transients = [];
}
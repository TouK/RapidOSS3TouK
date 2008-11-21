package connection

import datasource.HypericDatasource

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Nov 21, 2008
* Time: 9:35:40 AM
*/
class HypericConnection extends Connection{
    static searchable = {
        except = ["hypericDatasources"];

    };
    static cascaded = ["hypericDatasources": true]
    static datasources = [:]

    String baseUrl = "";
    String userPassword = "";
    String username = "";
    String connectionClass = "connection.HypericConnectionImpl";
    List hypericDatasources = [];

    static relations = [
            hypericDatasources: [isMany: true, reverseName: "connection", type: HypericDatasource]
    ]
    static constraints = {
        baseUrl(blank: true, nullable: true)
        userPassword(blank: true, nullable: true)
        username(blank: true, nullable: true)
    }

    static transients = [];
}
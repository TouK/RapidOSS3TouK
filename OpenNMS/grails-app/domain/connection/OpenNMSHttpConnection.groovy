package connection


import datasource.OpenNMSHttpDatasource

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Nov 21, 2008
* Time: 9:35:40 AM
*/
class OpenNMSHttpConnection extends Connection{
    static searchable = {
        except = ["openNMSHttpDatasources"];

    };
    static cascaded = ["openNMSHttpDatasources": true]
    static datasources = [:]

    String baseUrl = "";
    String userPassword = "";
    String username = "";
    String connectionClass = "connection.HypericConnectionImpl";
    List openNMSHttpDatasources = [];

    static relations = [
            openNMSHttpDatasources: [isMany: true, reverseName: "connection", type: OpenNMSHttpDatasource]
    ]
    static constraints = {
        baseUrl(blank: true, nullable: true)
        userPassword(blank: true, nullable: true)
        username(blank: true, nullable: true)
    }

    static transients = [];
}
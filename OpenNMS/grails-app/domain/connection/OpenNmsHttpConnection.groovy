package connection


import datasource.OpenNmsHttpDatasource
import datasource.OpenNmsHttpDatasource

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Nov 21, 2008
* Time: 9:35:40 AM
*/
class OpenNmsHttpConnection extends Connection{
    static searchable = {
        except = ["openNMSHttpDatasources"];

    };
    static cascaded = ["openNMSHttpDatasources": true]
    static datasources = [:]

    String baseUrl = "";
    String userPassword = "";
    String username = "";
    String connectionClass = OpenNmsHttpConnectionImpl.name;
    List openNmsHttpDatasources = [];

    static relations = [
            openNmsHttpDatasources: [isMany: true, reverseName: "connection", type: OpenNmsHttpDatasource]
    ]
    static constraints = {
        baseUrl(blank: true, nullable: true)
        userPassword(blank: true, nullable: true)
        username(blank: true, nullable: true)
    }

    static transients = [];
}
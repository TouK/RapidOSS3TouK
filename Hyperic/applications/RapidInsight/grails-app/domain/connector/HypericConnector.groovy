package connector

import script.CmdbScript
import datasource.HypericDatasource
import connection.HypericConnection

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Dec 1, 2008
* Time: 2:25:40 PM
*/
class HypericConnector {
    static searchable = {
        except = [];
    };
    public static String ALERT = "Alert";
    public static String STATUS = "Status";
    public static String TOPOLOGY = "Topology";
    static datasources = ["RCMDB": ["keys": ["name": ["nameInDs": "name"]]]]
    String name = "";
    String rsOwner = "p";
    String type = ALERT;
    CmdbScript script;
    HypericDatasource datasource;
    HypericConnection connection;

    static relations = [
            connection: [type: HypericConnection, isMany: false],
            script: [type: CmdbScript, isMany: false],
            datasource: [type: HypericDatasource, isMany: false]
    ]
    static constraints = {
        name(blank: false, nullable: false, key: [])
        script(nullable: true)
        datasource(nullable: true)
        type(inList: [ALERT, STATUS, TOPOLOGY])
    }
    static propertyConfiguration = [:]
    static transients = [];

    public String toString()
    {
        return name;
    }
    static def getScriptName(hypericConnector){
        return "${hypericConnector.name}${hypericConnector.type}Script";
    }
    static def getScriptFile(hypericConnector){
        return "Hyperic${hypericConnector.type}Integration";
    }

}
package connection

import datasource.ApgDatabaseDatasource
import datasource.ApgReportDatasource

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Sep 10, 2008
 * Time: 5:52:05 PM
 * To change this template use File | Settings | File Templates.
 */
class ApgConnection extends Connection {

    static searchable = {
        except = ["apgDatabaseDatasources", "apgReportDatasources"];
    };
    static cascaded = ["apgDatabaseDatasources": true, "apgReportDatasources": true]
    static datasources = [:]

    String connectionClass = "com.ifountain.apg.connection.ApgConnectionImpl";
    String wsdlBaseUrl = "http://localhost:58080/APG-WS/";
    String username = "";
    String userPassword = "";
    List apgDatabaseDatasources = [];
    List apgReportDatasources = [];

    static relations = [
            apgDatabaseDatasources: [isMany: true, reverseName: "connection", type: ApgDatabaseDatasource],
            apgReportDatasources: [isMany: true, reverseName: "connection", type: ApgReportDatasource]
    ]
    static constraints = {
        wsdlBaseUrl(blank: true, nullable: true)
        username(blank: true, nullable: true)
        userPassword(blank: true, nullable: true)
    }
    static transients = [];
}
package datasource

import connection.ApgConnection

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Sep 10, 2008
 * Time: 5:52:58 PM
 * To change this template use File | Settings | File Templates.
 */
class ApgDatabaseDatasource extends BaseDatasource{
    static searchable = {
        except = ["connection"];
    };
    static datasources = [:]
    ApgConnection connection;
    Long reconnectInterval = 0;
    static relations = [
            connection: [isMany: false, reverseName: "apgDatabaseDatasources", type: ApgConnection]
    ]
    static constraints = {
        connection(nullable: true)
    }
    static transients = []
}
package connection

import datasource.ApgDatabaseDatasource
import datasource.ApgReportDatasource

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
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
    List apgDatabaseDatasources = [];
    List apgReportDatasources = [];

    static relations = [
            apgDatabaseDatasources: [isMany: true, reverseName: "connection", type: ApgDatabaseDatasource],
            apgReportDatasources: [isMany: true, reverseName: "connection", type: ApgReportDatasource]
    ]
    static constraints = {
        wsdlBaseUrl(blank: true, nullable: true)
    }
    static transients = [];
}
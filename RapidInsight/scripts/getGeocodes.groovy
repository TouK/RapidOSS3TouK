/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Dec 25, 2008
 * Time: 3:03:07 PM
 */
/*
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
import connection.HttpConnection
import datasource.HttpDatasource

KEY = "ABQIAAAA7ipkp7ZXyXVH2UHyOgqZhxT2yXp_ZAY8_ufC3CFXhHIE1NvwkxRnNbZP5arP3T53Mzg-yLZcEMRBew";

def httpConn = HttpConnection.add(name: "geoConn", baseUrl: "http://maps.google.com");
def httpDatasource = HttpDatasource.add(name: "geoDs", connection: httpConn);
RsComputerSystem.list().each {RsComputerSystem device ->
    def location = device.location;
    if (location != "") {
        def params = ["q": location, "output": "csv", "key": KEY];
        def result = httpDatasource.doRequest("/maps/geo", params);
        def codes = result.split(",");
        if (codes[0] == "200") {
            def latitude = codes[2];
            def longitude = codes[3];
            device.update(geocodes: "${latitude}::${longitude}");
        }
    }
}
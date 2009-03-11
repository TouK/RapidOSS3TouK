/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Dec 31, 2008
 * Time: 10:51:36 AM
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

def CONTAINER_PROPERTY = "rsDatasource"
def nodeType = params.nodeType;
def name = params.name;
def location = params.location;

def searchResults = null;
if (nodeType == "Container") {
    searchResults = RsComputerSystem.search("${CONTAINER_PROPERTY}:\"${name.toQuery()}\" AND location:\"${location.toQuery()}\"", params)
}
else {
    searchResults = RsComputerSystem.search("name:\"${name.toQuery()}\" AND location:\"${location.toQuery()}\"", params);
}
def sortOrder = 0;
web.render(contentType: "text/xml") {
    Objects(total: searchResults.total, offset: searchResults.offset) {
        searchResults.results.each {RsComputerSystem result ->
            def props = ["id":result.id, "name":result.name, "className":result.className, "displayName":result.displayName,
            state:result.getState(), "sortOrder":sortOrder++, "rsAlias":result.getClass().name];
            Object(props);
        }
    }
}



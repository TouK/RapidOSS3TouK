import groovy.xml.MarkupBuilder

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Dec 25, 2008
* Time: 3:04:11 PM
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

/*
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
MARKER_URL_PREFIX="/RapidSuite/images/rapidjs/component/gmap/";

def CONTAINER_PROPERTY = "rsDatasource"
def nodeType = params.nodeType;
def name = params.name;

def devices = [];
if (nodeType == "Container") {
    devices = RsComputerSystem.searchEvery("${CONTAINER_PROPERTY}:${name.exactQuery()}");
}
else {
    def topoObj = RsComputerSystem.get(name: name);
    if (topoObj) {
        devices.add(topoObj)
    }
    else {
        throw new Exception("Object with name ${name}")
    }
}
def locations = [:]
devices.each {RsComputerSystem device ->
    if (device.geocodes != "") {
        def location = locations.get(device.location);
        if (location != null) {
            def cntr = location.get("cntr");
            cntr++;
            def deviceState = device.getState();
            if (deviceState > location["state"]) {
                location.put("state", deviceState)
            }
            location.put("cntr", cntr);
        }
        else {
            def geos = device.geocodes.split("::");
            def lat = geos[0];
            def lng = geos[1];
            locations.put(device.location, ["lat": lat, "lng": lng, "cntr": 1, "state": device.getState()]);
        }
    }
}
def sw = new StringWriter();
def builder = new MarkupBuilder(sw);
builder.Locations() {
    locations.each {address, location ->
        def tooltip = getTooltip(address, location);
        //get appropriate marker image for the map which represents the state of the address.
        def marker = getMarker(location);
        def lat = location.get("lat");
        def lng = location.get("lng");
        builder.Location(address: address, lat: lat, lng: lng, tooltip: tooltip, marker: marker, nodeType: nodeType, name: name);
    }
}
web.render(contentType: "text/xml", text: sw.toString())

def getTooltip(address, location) {
    return "<b>" + address + " (" + location.get("cntr") + " devices)</b>"

}

def getMarker(location) {
    def state = location.get("state");
    if (state == 5) {
        return "${MARKER_URL_PREFIX}marker_34_red.png";
    }
    else if (state == 4) {
        return "${MARKER_URL_PREFIX}marker_34_orange.png";
    }
    else if (state == 3) {
        return "${MARKER_URL_PREFIX}marker_34_yellow.png";
    }
    else if (state == 2) {
        return "${MARKER_URL_PREFIX}marker_34_blue.png";
    }
    else if (state == 1) {
        return "${MARKER_URL_PREFIX}marker_34_purple.png";
    }
    else {
        return "${MARKER_URL_PREFIX}marker_34_green.png";
    }
}
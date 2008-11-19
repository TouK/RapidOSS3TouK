def nodeType = params.nodeType;
def name = params.name;

def devices = [];
if (nodeType == "Container") {
    devices = RsComputerSystem.searchEvery("className:\"${name}\"");
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
            location.put("cntr", cntr.toString());
        }
        else {
            def geos = device.geocodes.split("::");
            def lat = geos[0];
            def lng = geos[1];
            locations.put(device.location, ["lat": lat, "lng": lng, "cntr": 1]);
        }
    }
}

web.render(contentType: "text/xml") {
    Locations() {
        locations.each {address, location ->
            def tooltip = getTooltip(address, location);
            //get appropriate marker image for the map which represents the state of the address.
            def marker = getMarker();
            def lat = location.get("lat");
            def lng = location.get("lng");
            Location(Address: address, Lat: lat, Lng: lng, Tooltip: tooltip, Marker: marker);
        }
    }
}

def getTooltip(address, location) {
    return "<b>" + address + " (" + location.get("cntr") + " devices)</b>"

}

def getMarker(){
    return "http://www.mapbuilder.net/img/icons/marker_34_red.png"
//    def state = location.get("state");
//
//    if(state == "1"){
//        return "http://www.mapbuilder.net/img/icons/marker_34_red.png";
//    }
//    else if(state == "2"){
//        return "http://www.mapbuilder.net/img/icons/marker_34_orange.png";
//    }
//    else if(state == "3"){
//        return "http://www.mapbuilder.net/img/icons/marker_34_yellow.png";
//    }
//    else if(state == "4"){
//        return "http://www.mapbuilder.net/img/icons/marker_34_blue.png";
//    }
//    else{
//        return "http://www.mapbuilder.net/img/icons/marker_34_green.png";
//    }
}
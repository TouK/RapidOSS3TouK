def classes = ["Host", "Node", "Switch", "Router", "NetworkConnection", "Cable", "TrunkCable", "Card", "Interface", "IP", "Port"];

web.render(contentType: 'text/xml') {
    Objects() {
        classes.each {className ->
           Object(name:className){
               def results = RsTopologyObject.search("creationClassName:\"${className}\"").results;
               results.each{RsTopologyObject topoObj ->
                   Object(name:topoObj.name, creationClassName:topoObj.creationClassName, rsAlias:topoObj.getClass().getName())
               }
           }
        }
    }
}

def classes = ["Host", "Node", "Switch", "Router", "NetworkConnection", "Cable", "TrunkCable", "Card", "Interface", "IP", "Port"];

web.render(contentType: 'text/xml') {
    Objects() {
        classes.each {className ->
           Object(id:className, name:className, displayName:className, nodeType:'Container'){
               def results = RsTopologyObject.search("creationClassName:\"${className}\"").results;
               results.each{RsTopologyObject topoObj ->
                   Object(id:topoObj.id, name:topoObj.name, displayName:topoObj.displayName, nodeType:'Object', 
                           creationClassName:topoObj.creationClassName, rsAlias:topoObj.getClass().getName())
               }
           }
        }
    }
}

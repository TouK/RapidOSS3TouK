import groovy.xml.MarkupBuilder


def postData = params.postData.data;
def data = postData.tokenize("|");


def nodes;
try {
    nodes= data[0].tokenize("=")[1].tokenize(",");
}
catch(e){
    return;
}
def edges;
try {
    edges= data[1].tokenize("=")[1].tokenize(";");
}
catch(e){
}

def writer = new StringWriter();
def mapDataBuilder = new MarkupBuilder(writer);

mapDataBuilder.graphData {

    nodes.each {
        def device = RsComputerSystem.get( name : it);
        mapDataBuilder.device( id : it, state : device.getState(), load : device.getCpuUsage());
    }

    edges.each {
        def edgeTokens = it.tokenize(",");
        def source = edgeTokens[0];
        def target = edgeTokens[1];
        def links = RsLink.search( "a_ComputerSystemName:${source} z_ComputerSystemName: ${target}").results;
        if( links.size() == 0 )
        	links = RsLink.search( "a_ComputerSystemName:${target} z_ComputerSystemName: ${source}").results;

        if( links.size() != 0 )

        {
            mapDataBuilder.edge( source : source, target : target, state : links[0].getState());
        }

    }

}
return writer.toString();



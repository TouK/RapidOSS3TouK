import groovy.xml.MarkupBuilder



def nodeString = params.nodes;
def edgeString = params.edges;
def nodes = [];
def edges = [];
if(nodeString != null)
{
    nodes = nodeString.splitPreserveAllTokens(";").findAll {it != ""};
}
if(edgeString != null)
{
    edges = edgeString.splitPreserveAllTokens(";").findAll {it != ""};
}

def writer = new StringWriter();
def mapDataBuilder = new MarkupBuilder(writer);

mapDataBuilder.graphData {

    nodes.each {
        def device = RsComputerSystem.get( name : it);
        mapDataBuilder.node( id : it, state : device.getState(), type:device.creationClassName);
    }

    edges.each {
        def edgeTokens = it.splitPreserveAllTokens(",");
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



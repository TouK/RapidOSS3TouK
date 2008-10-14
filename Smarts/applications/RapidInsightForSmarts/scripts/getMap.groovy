import auth.RsUser
import groovy.xml.MarkupBuilder
import script.CmdbScript
import map.TopoMap

def user = RsUser.findByUsername(web.session.username);
if(user == null){
    throw new Exception("User ${web.session.username} does not exist");
}

def username2 = web.session.username;
def mapName2 = params.mapName;

def map = TopoMap.get( mapName : mapName2, username : username2)

def deviceMap = [:];

def devices =  map.consistOfDevices;
def devicesMap = [:];
def devicesToBeExpanded = "";
devices.each{
    devicesMap[it.nodeIdentifier] = it;
    devicesToBeExpanded += "${it.nodeIdentifier},${it.expanded},${it.xlocation},${it.ylocation};"
}
def res = CmdbScript.runScript("expandMap", [params:[nodes:devicesToBeExpanded]]);
def slurper = new XmlSlurper().parseText(res);
def nodeXmls = slurper.node;
def edgeXmls = slurper.edge;

def writer = new StringWriter();
def mapBuilder = new MarkupBuilder(writer);

mapBuilder.graph(layout:map.layout)
{
    nodeXmls.each {
        mapBuilder.node( id: it.@id.text(), model : it.@model.text(), type : it.@type.text(), gauged : it.@gauged.text(), expanded : it.@expanded.text(), expandable : it.@expandable.text(), x: it.@x.text(), y: it.@y.text());
    }

    edgeXmls.each {
        mapBuilder.edge( source : it.@source.text(), target : it.@target.text());
    }

}
return writer.toString();

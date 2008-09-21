import auth.RsUser
import groovy.xml.MarkupBuilder

def user = RsUser.findByUsername(web.session.username);
if(user == null){
    throw new Exception("User ${web.session.username} does not exist");
}

def username2 = web.session.username;
def mapName2 = params.mapName;

def map = TopoMap.get( mapName : mapName2, username : username2)

def deviceMap = [:];

def devices =  map.consistOfDevices;
def devicesToBeExpanded = "";
devices.each{
    devicesToBeExpanded += "${it.nodeIdentifier},${it.expanded}"
}
web.redirect(action:"run", params:[id:"expandMap", nodes:devicesToBeExpanded]);

import auth.RsUser
import groovy.xml.MarkupBuilder
import ui.map.*

def user = RsUser.findByUsername(web.session.username);
if(user == null){
    throw new Exception("User ${web.session.username} does not exist");
}

def writer = new StringWriter();
def mapBuilder = new MarkupBuilder(writer);

MapGroup.add(groupName:"Default", username:web.session.username);
def mapGroups = MapGroup.list();

mapBuilder.Maps
{
    mapGroups.each {MapGroup group ->
        def userName = group.username;
        if( userName.equals(user.username)){
           mapBuilder.Map(id: group.id, name: group.groupName, nodeType: "group",  isPublic:"false") {
              group.maps.each {TopoMap topoMap ->
                  mapBuilder.Map (id: topoMap.id, name: topoMap.mapName, nodeType: "map", isPublic:"false", layout:topoMap.layout)
              }
           }
        }
    }
}
return writer.toString();

import auth.RsUser;

def notificationName = params.name;
def user = RsUser.findByUsername(web.session.username);
def act = params.act;
def rsEvent = RsEvent.get(name:notificationName);
if (rsEvent) {
    /*def userId = RsNotification.search("$user.username").results[0];
    println(userId);
    if(userId == null){
        throw new Exception("No user found in repository with name: ${user.username}")
    }*/

    if (act == "true")
        rsEvent.setOwnership(true, user.username);
    else if (act == "false")
        rsEvent.setOwnership(false, user.username);


    def props = [:];
    def grailsDomainClass = web.grailsApplication.getDomainClass(rsEvent.class.name);
    grailsDomainClass.getProperties().each {rsProperty ->
        props[rsProperty.name] = rsEvent[rsProperty.name];
    }
    web.render(contentType: 'text/xml') {
        Objects {
            Object(props);
        }
    }
}
else {
    throw new Exception("RsEvent with name: ${notificationName} does not exist." );
}

import auth.RsUser;

def eventName = params.eventName;
def instanceName = params.instanceName;
def className = params.className;
def user = RsUser.findByUsername(web.session.username);
def act = params.act;
def rsEvent = RsSmartsNotification.get(eventName:eventName, instanceName:instanceName, className:className);
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
    throw new Exception("RsSmartsNotification with eventname: ${eventName}, instancename: ${instanceName} and classname: ${className} does not exist." );
}

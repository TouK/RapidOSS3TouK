import auth.RsUser;

def notificationName = params.name;
def user = RsUser.findByUsername(web.session.username);
def acknowledged = params.acknowledged;

def rsEvent = RsEvent.get(name:notificationName);
if (rsEvent) {
	    if (acknowledged == "true")
        	rsEvent.acknowledge(true, user.username);
    	else if (acknowledged == "false")
        	rsEvent.acknowledge(false, user.username);

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
else{
    throw new Exception("RsEvent with name: ${notificationName} does not exist." );
}

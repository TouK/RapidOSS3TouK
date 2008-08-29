import auth.RsUser;

def eventName = params.eventName;
def instanceName = params.instanceName;
def className = params.className;
def user = RsUser.findByUsername(web.session.username);
def acknowledged = params.acknowledged;

def rsEvent = RsEvent.get(eventName:eventName, instanceName:instanceName, className:className);
if (rsEvent) {
	    if (acknowledged == "true")
        	rsEvent.acknowledge(true, user);
    	else if (acknowledged == "false")
        	rsEvent.acknowledge(false, user);

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
    throw new Exception("RsEvent with eventname: ${eventName}, instancename: ${instanceName} and classname: ${className} does not exist." );
}

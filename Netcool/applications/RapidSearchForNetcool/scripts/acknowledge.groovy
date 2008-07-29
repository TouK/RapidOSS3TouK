import auth.RsUser;

def netcoolServerName = params.servername;
def serverSerial = params.serverserial;
def user = RsUser.findByUsername(web.session.username);
def acknowledged = params.acknowledged;


def netcoolEvent = NetcoolEvent.get(servername: netcoolServerName, serverserial: serverSerial);
if (netcoolEvent) {
    if (acknowledged == "true")
        netcoolEvent.acknowledge(true, user);
    else if (acknowledged == "false")
        netcoolEvent.acknowledge(false, user);

    def props = [:];
    def grailsDomainClass = web.grailsApplication.getDomainClass(netcoolEvent.class.name);
    grailsDomainClass.getProperties().each {netcoolProperty ->
        props[netcoolProperty.name] = netcoolEvent[netcoolProperty.name];
    }
    web.render(contentType: 'text/xml') {
        Objects {
            Object(props);
        }
    }
}
else{
    throw new Exception("NetcoolEvent with servername: ${netcoolServerName} and serverserial: ${serverSerial} does not exist." );
}

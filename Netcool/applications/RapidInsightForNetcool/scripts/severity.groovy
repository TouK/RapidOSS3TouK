/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Jul 24, 2008
 * Time: 10:55:44 AM
 * To change this template use File | Settings | File Templates.
 */

import auth.RsUser;

def eventName = params.name;
def user = RsUser.findByUsername(web.session.username);
def severity = Integer.parseInt(params.severity);

def netcoolEvent = NetcoolEvent.get(name:eventName);
if (netcoolEvent) {
    netcoolEvent.setSeverity(severity, user);
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
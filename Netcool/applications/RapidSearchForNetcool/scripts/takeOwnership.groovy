/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Jul 23, 2008
 * Time: 6:27:54 PM
 * To change this template use File | Settings | File Templates.
 */

import auth.RsUser;

def netcoolServerName = params.servername;
def serverSerial = params.serverserial;
def user = RsUser.findByUsername(web.session.username);

def netcoolEvent = NetcoolEvent.get(servername: netcoolServerName, serverserial: serverSerial);
if (netcoolEvent) {
    netcoolEvent.assign(user.getProperty("userId"));
    netcoolEvent.setProperty("owneruid", user.getProperty("userId"));
    netcoolEvent.setProperty("acknowledged", 0);
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
else {
    throw new Exception("NetcoolEvent with servername: ${netcoolServerName} and serverserial: ${serverSerial} does not exist.");
}

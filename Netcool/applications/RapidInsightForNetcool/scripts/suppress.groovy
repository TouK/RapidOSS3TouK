/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Jul 28, 2008
 * Time: 10:17:12 AM
 * To change this template use File | Settings | File Templates.
 */
import auth.RsUser;

def eventName = params.name;
def user = RsUser.findByUsername(web.session.username);
def suppress = Integer.parseInt(params.suppressescl);

def netcoolEvent = NetcoolEvent.get(name: eventName);
if (netcoolEvent) {

    netcoolEvent.setSuppressescl(suppress, user);
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


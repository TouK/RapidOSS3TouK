/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Jul 23, 2008
 * Time: 6:27:54 PM
 * To change this template use File | Settings | File Templates.
 */

import auth.RsUser;
import datasource.NetcoolConversionParameter;

def eventName = params.name;
def user = RsUser.findByUsername(web.session.username);

def netcoolEvent = NetcoolEvent.get(name:eventName);
if (netcoolEvent) {
    def userId = NetcoolConversionParameter.search("columnName:OwnerUID AND conversion:$user.username").results[0]
    if(userId == null){
        throw new Exception("No user found in Netcool repository with name: ${user.username}")
    }
    netcoolEvent.assign(user.username);
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

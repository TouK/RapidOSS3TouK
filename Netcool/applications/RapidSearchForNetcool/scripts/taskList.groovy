/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Jul 22, 2008
 * Time: 11:08:26 AM
 * To change this template use File | Settings | File Templates.
 */

def netcoolServerName = params.servername;
def serverSerial = params.serverserial;
def taskList = params.taskList;


def netcoolEvent = NetcoolEvent.get(servername: netcoolServerName, serverserial: serverSerial);
if (netcoolEvent) {
    if (taskList == "true")
        netcoolEvent.addToTaskList(true);
    else if (taskList == "false")
        netcoolEvent.addToTaskList(false);
        
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

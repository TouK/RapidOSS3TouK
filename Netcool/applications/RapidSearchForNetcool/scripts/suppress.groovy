/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Jul 28, 2008
 * Time: 10:17:12 AM
 * To change this template use File | Settings | File Templates.
 */
import auth.RsUser;
import datasource.NetcoolConversionParameter;

def static conversionMap = [:];

def netcoolServerName = params.servername;
def serverSerial = params.serverserial;
def user = RsUser.findByUsername(web.session.username);
def suppress = Integer.parseInt(params.suppress);

def netcoolEvent = NetcoolEvent.get(servername: netcoolServerName, serverserial: serverSerial);
if (netcoolEvent) {
    def convertedValue;

    if (conversionMap.isEmpty())
        conversionMap = NetcoolConversionParameter.search("columnName:SuppressEscl").results;


    conversionMap.each {
        if (it.value == suppress)
            convertedValue = it.conversion;
    }
    netcoolEvent.setProperty("suppressescl", convertedValue);
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


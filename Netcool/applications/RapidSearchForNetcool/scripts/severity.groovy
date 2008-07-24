/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Jul 24, 2008
 * Time: 10:55:44 AM
 * To change this template use File | Settings | File Templates.
 */

import auth.RsUser;
import datasource.NetcoolConversionParameter;

def static conversionMap = [:];

def netcoolServerName = params.servername;
def serverSerial = params.serverserial;
def user = RsUser.findByUsername(web.session.username);
def severity = Integer.parseInt( params.severity);
def convertedValue;

if( conversionMap.isEmpty() )
    conversionMap =  NetcoolConversionParameter.search("columnName:Severity").results;


conversionMap.each{
    if( it.value == severity )
    	convertedValue = it.conversion;
}

def netcoolEvent = NetcoolEvent.get(servername:netcoolServerName, serverserial:serverSerial);

netcoolEvent.setProperty ( "severity", convertedValue);
netcoolEvent.setProperty ( "acknowledged", 0);
netcoolEvent.setSeverity(severity, user);
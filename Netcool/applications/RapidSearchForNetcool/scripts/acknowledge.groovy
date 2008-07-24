/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Jul 22, 2008
 * Time: 11:08:26 AM
 * To change this template use File | Settings | File Templates.
 */

import auth.RsUser;

def netcoolServerName = params.servername;
def serverSerial = params.serverserial;
def user = RsUser.findByUsername(web.session.username);
def acknowledged = params.acknowledged;


def netcoolEvent = NetcoolEvent.get(servername:netcoolServerName, serverserial:serverSerial);
if(acknowledged == "true")
{

	netcoolEvent.setProperty ( "acknowledged", 1);
	netcoolEvent.acknowledge(true, user);
}
else if(acknowledged == "false")
{

	netcoolEvent.setProperty ( "acknowledged", 0);
	netcoolEvent.acknowledge(false, user);
}


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

def netcoolEvent = NetcoolEvent.get(servername:netcoolServerName, serverserial:serverSerial);
netcoolEvent.assign(user.getProperty("userId"));
netcoolEvent.setProperty ( "owneruid", user);

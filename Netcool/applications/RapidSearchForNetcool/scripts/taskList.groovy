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


def netcoolEvent = NetcoolEvent.get(servername:netcoolServerName, serverserial:serverSerial);
if(taskList == "true")
{
	netcoolEvent.addToTaskList(true);
	netcoolEvent.setProperty ( "tasklist", 1);
}
else if(taskList == "false")
{
	netcoolEvent.addToTaskList(false);
	netcoolEvent.setProperty ( "tasklist", 0);
}
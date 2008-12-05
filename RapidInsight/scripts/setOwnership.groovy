/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
import auth.RsUser;

def notificationName = params.name;
def user = RsUser.findByUsername(web.session.username);
def act = params.act;
def rsEvent = RsEvent.get(name:notificationName);
if (rsEvent) {
    /*def userId = RsNotification.search("$user.username").results[0];
    println(userId);
    if(userId == null){
        throw new Exception("No user found in repository with name: ${user.username}")
    }*/

    if (act == "true")
        rsEvent.setOwnership(true, user.username);
    else if (act == "false")
        rsEvent.setOwnership(false, user.username);


    def props = [:];
    def grailsDomainClass = web.grailsApplication.getDomainClass(rsEvent.class.name);
    grailsDomainClass.getProperties().each {rsProperty ->
        props[rsProperty.name] = rsEvent[rsProperty.name];
    }
    web.render(contentType: 'text/xml') {
        Objects {
            Object(props);
        }
    }
}
else {
    throw new Exception("RsEvent with name: ${notificationName} does not exist." );
}

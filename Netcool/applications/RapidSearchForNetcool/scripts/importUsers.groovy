import org.apache.log4j.Logger
import datasource.NetcoolDatasource
import auth.Role
import org.jsecurity.crypto.hash.Sha1Hash
import auth.RsUser
import auth.UserRoleRel

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Aug 14, 2008
 * Time: 9:11:44 AM
 * To change this template use File | Settings | File Templates.
 */

Logger logger = Logger.getLogger("getConversionParameters");

List netcoolDatasources = NetcoolDatasource.list();
if(netcoolDatasources.isEmpty())
{
    logger.warn("No netcool datasource is defined");
}
NetcoolDatasource netcoolDs = netcoolDatasources[0];
def adminRole = Role.get(name:"Administrator");
def userRole = Role.get(name:"User");
def userPassHash = new Sha1Hash("changeme").toHex()
def users = netcoolDs.getUsers();
users.each{
   String userName = it.CONVERSION;
   def user = RsUser.add(username: userName, passwordHash: userPassHash)
   if(userName.equalsIgnoreCase("root")){
       UserRoleRel.add(rsUser: user, role: adminRole);
   }
   else{
       UserRoleRel.add(rsUser: user, role: userRole);
   }

}
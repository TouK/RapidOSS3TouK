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

import javax.naming.NamingException
import connection.LdapConnection
import auth.RsUser



def output = " "

try {
    // LDAP CONFIGURATION
    // Don't forget to create the group configured in localGroupName property
    //
    /*
	//for apache ds
	//Searching with username : when "(|(cn=ldapuser)(cn=Administrator))" used for searchFilter only users specified will be searched
    def ldapConnectionName="apache ds"
    def searchBase = "ou=system"
    def searchFilter="objectClass=person"    
    def searchSubDirectories=true
    def usernameAttribute="uid"
    def localGroupName="userGroup"
    */

    //for ms ds
    //searchBase : dont specify root DN for microsoft active directory server, PartialResultException may occur
    //Searching with username : when "(|(cn=ldapuser)(cn=Administrator))" used for searchFilter only users specified will be searched
    def ldapConnectionName = "ms active directory"
    def searchBase = "CN=Users,DC=molkay,DC=selfip,DC=net"
    def searchFilter = "objectClass=user"
    def searchSubDirectories = true
    def usernameAttribute = "name"
    def localGroupName = "userGroup"

    // BELOW IMPLEMENTATION IS TO TRANSFER USERS

    def ldapConnection = LdapConnection.get(name: ldapConnectionName)
    if (ldapConnection == null)
    {
        logger.warn("No connection found with id ${ldapConnectionName}");

        output += "No connection found with id ${ldapConnectionName}"
        return output
    }



    try {
        ldapConnection.connect()
        output += "Connected to Ldap"
    }
    catch (NamingException e) {
        logger.warn("Could not connect to LDAP: ${e}");

        output += "Could not connect to LDAP: ${e}"
        throw e
    }


    // Look up the DN for the LDAP entry that has a 'uid' value
    // matching the given username.



    def result
    try {
        result = ldapConnection.query(searchBase, searchFilter, searchSubDirectories)
        while (result.hasMore()) {
            def searchResult = result.next()
            def resultAttributes = searchResult.attributes

            def username = resultAttributes.get(usernameAttribute).get()
            def userdn = searchResult.nameInNamespace


            output += " <br> Found userdn  : ${userdn} with username : ${username}"

            if (username != "rsadmin")
            {

                def oldUser = RsUser.get(username: username)
                if (oldUser != null)
                {
                    def oldLdapInformation = oldUser.retrieveLdapInformation();
                    if (oldLdapInformation != null)
                    {
                        oldLdapInformation.remove()
                    }

                }


                try {
                    def rsUser = RsUser.addUser([username: username, password: "",groups:[localGroupName]])
                    if (!rsUser.hasErrors()) {
                        output += "<br>User ${rsUser.username} created"
                        rsUser.addLdapInformation(userdn: userdn, ldapConnection: ldapConnection)
                    }
                    else
                    {
                        logger.warn("User ${username} can not be created. Reason : ${rsUser.errors}");

                        output += "<br> User ${username} can not be created. Reason : ${rsUser.errors}";

                    }

                }
                catch (e)
                {
                    logger.warn("User ${username} can not be created. Reason : ${e}");

                    output += "<br> User ${username} can not be created. Reason : ${e}";
                }

            }

        }
    }
    catch (NamingException ex) {
        logger.warn("Exception occured while searching Ldap Server : ${ex}");

        output += "<br> Exception occured while searching Ldap Server : ${ex}"
    }

    /*
    LdapUserInformation.list().each{
        output+="<br> ${it.userdn} ${it.ldapConnection}"
    }
    */


    ldapConnection.disconnect()
    output += "<br> Closed Ldap connection"

}
catch (Exception e)
{
    logger.warn("Got Exception ${e} ");

    output += "<br> Got Exception ${e} "
}


return output
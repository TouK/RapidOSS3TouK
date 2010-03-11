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
import auth.Group
import auth.Role
import auth.LdapUserInformation

////////////////////////////////
//    LDAP CONFIGURATION
////////////////////////////////

// If group in localGroupName does not exist , please create it first from admin ui

//sample configuration  for Microsoft Active Directory ds
//Please connect to port 3268 if you want to search from root DN, otherwise PartialResultException will occur with   userSearchBase: root DN
//Searching with username : when "(|(cn=ldapuser)(cn=Administrator))" used for searchFilter only users specified will be searched

//user data configuration
def ldapConnectionName = "ldapConnection"
userSearchBase = "CN=Users,DC=molkay,DC=selfip,DC=net"
userSearchFilter = "objectClass=user"
userSearchSubDirectories = true
userNameAttribute = "name"
localGroupName = "rsuser"

//group data configuration
groupSearchBase = "CN=Builtin,DC=molkay,DC=selfip,DC=net"
groupSearchFilter = "objectClass=group"
groupSearchSubDirectories = true
groupNameAttribute = "cn"
groupUsersAttribute="member"

/*
//sample configuration for apache ds
//Searching with username : when "(|(cn=ldapuser)(cn=Administrator))" used for searchFilter only users specified will be searched
def ldapConnectionName="ldapConnection"
userSearchBase = "ou=system"
userSearchFilter="objectClass=person"
userSearchSubDirectories=true
usernameAttribute="uid"
localGroupName="userGroup"
*/

////////////////////////////////
//    LDAP CONFIGURATION ENDED
////////////////////////////////

// BELOW IMPLEMENTATION IS TO TRANSFER USERS


OUTPUT = " "

ldapConnection = LdapConnection.get(name: ldapConnectionName)
if (ldapConnection == null)
{
    logWarn("No connection found with id ${ldapConnectionName}");
    return OUTPUT
}

connectoToLdap();
try{
    createUsers();
    createGroups();
    //def result=doLdapQuery(userSearchBase,userSearchFilter,userSearchSubDirectories);
	//printLdapQueryResult(result);
}
catch(e)
{
    logWarn("Exception occured ${e}");
}
finally
{
    disconnectFromLdap();
}

return OUTPUT



def createUsers()
{
    def results=doLdapQuery(userSearchBase,userSearchFilter,userSearchSubDirectories);
    //printLdapQueryResult(result);
    results.each{  searchResult ->
        def entryDn=searchResult.nameInNamespace;

        def userName=searchResult.get(userNameAttribute)[0];
        if (userName != RsUser.RSADMIN)
        {
            logInfo("found user ${userName} : ${entryDn} , with username : ${userName}")

            def user=RsUser.addUser([username:userName,password:"",groups:[localGroupName]]);
            if(!user.hasErrors())
            {
                logInfo("Added user ${userName}");

                def oldLdapInformation = user.retrieveLdapInformation()?.remove();
                def ldapInformation=user.addLdapInformation(userdn: entryDn, ldapConnection: ldapConnection)
                if(!ldapInformation.hasErrors())
                {
                    logInfo("Added ldapInformation for ${userName}");
                }
                else
                {
                    logWarn("Error occured while adding ldapInformation for user ${userName}. Reason ${ldapInformation.errors}");
                }
            }
            else
            {
                logWarn("Error occured while adding user ${userName}. Reason ${user.errors}");
            }
        }
     }
}


def createGroups()
{
    def userRole=Role.get(name:Role.USER);
    def results=doLdapQuery(groupSearchBase,groupSearchFilter,groupSearchSubDirectories);
    //printLdapQueryResult(result);
    results.each{  searchResult ->
        def entryDn=searchResult.nameInNamespace;

        def groupName=searchResult.get(groupNameAttribute)[0];

        def groupUserDns=[];
        def groupUsers=[];
        if(searchResult.containsKey(groupUsersAttribute))
        {
            groupUserDns=searchResult.get(groupUsersAttribute);

            groupUserDns.each{ groupUserDn ->
                def userIds=LdapUserInformation.getPropertyValues("userdn:${groupUserDn}",["userId"]);
                if(userIds.size()>0)
                {
                    def user=RsUser.get(id:userIds[0].userId);
                    if(user!=null)
                    {
                        groupUsers.add(user);
                    }
                }
            }
        }



        logInfo("found group ${groupName} : ${entryDn} , group Users in ldap : ${groupUserDns}");
        logInfo("found users in RI for group ${groupName} : ${groupUsers.username}}")

        def group=Group.addGroup([name:groupName,role:userRole,users:groupUsers]);
        if(!group.hasErrors())
        {
            logInfo("Added group ${group.name}");
        }
        else
        {
            logWarn("Error occured while adding group ${groupName}. Reason ${group.errors}");
        }
     }
}

def doLdapQuery(searchBase,searchFilter,searchSubDirectories)
{
    def result=null;
    try {
        logDebug("executing query searchBase ${searchBase} , searchFilter ${searchFilter}, searchSubDirectories : ${searchSubDirectories}")
        result = ldapConnection.query(searchBase, searchFilter, searchSubDirectories);
    }
    catch (NamingException ex) {
        logWarn("Exception occured while searching Ldap Server : ${ex}");
        throw ex;
    }
    return result;
}

def printLdapQueryResult(results)
{
    results.each{ searchResult ->
        logDebug("Found entry  : ${searchResult}")
    }
}
def connectoToLdap()
{

    try {
        ldapConnection.connect()
        logInfo("Connected to Ldap");
    }
    catch (NamingException e) {
        logWarn("Could not connect to LDAP: ${e}");
        throw new Exception("Exception occured while connecting to ldap. Reason ${e}",e);
    }
}
def disconnectFromLdap()
{

    try {
        ldapConnection.disconnect()
        logInfo("Closed Ldap connection");
    }
    catch(e)
    {
        logWarn("Could not disconnect from Ldap: ${e}");
    }
}

def logWarn(message)
{
   logger.warn(message);
   OUTPUT += "<br> WARN : ${message}";
}

def logInfo(message)
{
   logger.info(message);
   OUTPUT += "<br> INFO : ${message}";
}

def logDebug(message)
{
   logger.debug(message);
   if(logger.isDebugEnabled())
   {
        OUTPUT += "<br> DEBUG : ${message}";
   }
}



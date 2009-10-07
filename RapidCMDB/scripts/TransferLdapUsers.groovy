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

////////////////////////////////
//    LDAP CONFIGURATION
////////////////////////////////

// If group in localGroupName does not exist , please create it first from admin ui 

//sample configuration  for ms ds
//userSearchBase : dont specify root DN for microsoft active directory server, PartialResultException may occur
//Searching with username : when "(|(cn=ldapuser)(cn=Administrator))" used for searchFilter only users specified will be searched
def ldapConnectionName = "ldapConnection"
userSearchBase = "CN=Users,DC=molkay,DC=selfip,DC=net"
userSearchFilter = "objectClass=user"
userSearchSubDirectories = true
userNameAttribute = "name"
localGroupName = "rsuser"


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
    def result=doLdapQuery(userSearchBase,userSearchFilter,userSearchSubDirectories);
    //printLdapQueryResult(result);
     while (result.hasMore()) {
        def searchResult = result.next();
        def resultAttributes = searchResult.attributes;
        def entryDn=searchResult.nameInNamespace;

        def userName=resultAttributes.get(userNameAttribute).get();
        if (userName != "rsadmin")
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

def printLdapQueryResult(result)
{
    while (result.hasMore()) {
        def searchResult = result.next()
        def resultAttributes = searchResult.attributes;
        logDebug("Found entry  : ${searchResult.nameInNamespace}")
        printLdapAttributes(resultAttributes);

    }
}
def printLdapAttributes(resultAttributes)
{
    resultAttributes.getAll().each{ attribute ->
        logDebug("attribute : ${attribute.getID()}");
        attribute.getAll().each{ value ->
            logDebug( "----- value ${value}");
        }
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
        throw e
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



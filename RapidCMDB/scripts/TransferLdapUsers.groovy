import javax.naming.AuthenticationException
import javax.naming.Context
import javax.naming.NamingException
import javax.naming.directory.BasicAttribute
import javax.naming.directory.BasicAttributes
import javax.naming.directory.InitialDirContext

import javax.naming.directory.DirContext
import javax.naming.NameNotFoundException

import connection.LdapConnection
import auth.RsUser
import auth.LdapUserInformation
import auth.RsUserInformation



def output=""

try{

	/*
	//for apache ds
    def ldapConnectionName="apache ds"
    def searchBase = "ou=system"
    def searchFilter="objectClass=person"
    def searchSubDirectories=true
    def usernameAttribute="uid"
    */

    //for ms ds
    //searchBase : dont specify root DN for microsoft active directory server, PartialResultException may occur
    def ldapConnectionName="ms active directory"
    def searchBase = "CN=Users,DC=molkay,DC=selfip,DC=net"
    def searchFilter="objectClass=user"
    def searchSubDirectories=true
    def usernameAttribute="name"


    def ldapConnection=LdapConnection.get(name:ldapConnectionName)
	if(ldapConnection==null)
	{
		output+="No connection found with id ${ldapConnectionName}"
		return output
	}



    try {
        ldapConnection.connect()
        output+= "Connected to Ldap"
    }
    catch (NamingException e) {
        log.error "Could not connect to LDAP: ${e}"
        output+= "Could not connect to LDAP: ${e}"
        throw e
    }


    // Look up the DN for the LDAP entry that has a 'uid' value
    // matching the given username.



    def result
    try {
        result=ldapConnection.query(searchBase,searchFilter,searchSubDirectories)
        while (result.hasMore()) {
         	def searchResult = result.next()
         	def resultAttributes=searchResult.attributes

         	def username=resultAttributes.get(usernameAttribute).get()
	    	def userdn=searchResult.nameInNamespace


	    	output+=" <br> Found userdn  : ${userdn} with username : ${username}"

            if(username!="rsadmin")
            {

                def oldUser=RsUser.get(username: username)
                if(oldUser!=null)
                {

                    if(oldUser.userInformation != null )
                    {
                        if(oldUser.userInformation instanceof LdapUserInformation)
                        {
                            oldUser.userInformation.remove()
                        }
                    }

                }


                def userInformation=LdapUserInformation.add(userdn:userdn,ldapConnection:ldapConnection)

                def rsUser = RsUser.add(username: username, passwordHash:"",userInformation:userInformation)
                if (!rsUser.hasErrors()) {
                    output+= "<br>User ${rsUser.username} created"

                }
                else
                {
                    output+= "<br> User ${username} can not be created"

                }
            }

        }
    }
    catch (NamingException ex) {

        output+="No such  searchBase DN exists"
    }

	/*
    LdapUserInformation.list().each{
        output+="<br> ${it.userdn} ${it.ldapConnection}"
    }
    */


    ldapConnection.disconnect()
    output+= "<br> Closed Ldap connection"



}
catch(Exception e)
{
    output+="Got Exception ${e} "
}


return output
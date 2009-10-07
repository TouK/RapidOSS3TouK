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
package connection


import org.apache.log4j.Logger
import com.ifountain.rcmdb.test.util.LdapConnectionTestUtils
import com.ifountain.core.test.util.RapidCoreTestCase
import javax.naming.NamingException

/**
* Created by IntelliJ IDEA.
* User: deneme
* Date: Oct 4, 2008
* Time: 10:13:47 AM
* To change this template use File | Settings | File Templates.
*/
class LdapConnectionTest extends RapidCoreTestCase{
    //a user with username:ldapuser , displayname:ldapuser , password:1234 must be created in ldap server
    public void testConnection()
    {
        Map params = LdapConnectionTestUtils.getConnectionParams();

        //ms params
        //def params=["url":"ldap://192.168.1.178","username":"ldapuser@molkay.selfip.net","userPassword":"123"]
        //apache params
        //def params=["url":"ldap://localhost:10389/","username":"uid=admin,ou=system","userPassword":"123"]


        LdapConnectionOperationsMock oper = new LdapConnectionOperationsMock(params);
        assertFalse (oper.isConnected());
        oper.connect();
        assertTrue (oper.isConnected());
        oper.disconnect();
        assertFalse (oper.isConnected());
    }
    public void testAuthentication()
    {
        Map params = LdapConnectionTestUtils.getAuthenticationParams();
        //Map params = LdapConnectionTestUtils.getConnectionParams();
        /*
        //ms params
        def params=["url":"ldap://192.168.1.178"]
        def username="cn=ldapuser,cn=users,DC=molkay,DC=selfip,DC=net";
        def password="123"
        */
        /*
        //apache params
        def params=["url":"ldap://localhost:10389/"]
        def username="uid=tempuser,ou=users,ou=system";
        def password="123"
        */
        LdapConnectionOperationsMock oper = new LdapConnectionOperationsMock(url:params.url)

        assertTrue(oper.checkAuthentication(params.username,params.userPassword))
    }

    public void testAuthenticationFailsWhenParametersInvalid()
    {
        Map params = LdapConnectionTestUtils.getAuthenticationParams();

        LdapConnectionOperationsMock oper = new LdapConnectionOperationsMock(url:params.url)

        assertFalse(oper.checkAuthentication(params.username,params.userPassword+"extra"))
    }
    public void testDisconnectWithoutConnectionThrowsException()
    {
        LdapConnectionOperationsMock oper = new LdapConnectionOperationsMock();
        assertFalse (oper.isConnected());
        try{
            oper.disconnect();
            fail("Should throw exception");
        }
        catch(javax.naming.NamingException e)
        {

        }
        assertFalse (oper.isConnected());
    }
    public void testConnectionThrowsExceptionWhenParametersInvalid()
    {
        LdapConnectionOperations oper = new LdapConnectionOperationsMock();
        assertFalse (oper.isConnected());

        try{
            oper.connect();
            fail("Should throw exception");
        }
        catch(javax.naming.NamingException e)
        {

        }
        assertFalse(oper.isConnected());

    }
    public void testQuery()
    {
        Map params = LdapConnectionTestUtils.getConnectionParams();
        Map queryparams = LdapConnectionTestUtils.getQueryParams();

        LdapConnectionOperationsMock oper = new LdapConnectionOperationsMock(params);
        assertFalse (oper.isConnected());
        oper.connect();
        assertTrue (oper.isConnected());

        def result=oper.query(queryparams.searchBase,queryparams.searchFilter,queryparams.searchSubDirectories)
        assertTrue(result.hasMore());
        oper.disconnect();
        assertFalse (oper.isConnected());
    }
    public void testCheckConnection()
    {
        Map params = LdapConnectionTestUtils.getAuthenticationParams();
        LdapConnectionOperationsMock oper = new LdapConnectionOperationsMock(url:params.url,username:params.username,userPassword:params.userPassword)

        assertTrue(oper.checkConnection())
    }
     public void testCheckConnectionGeneratesExceptionWhenParametersInvalid()
    {
        Map params = LdapConnectionTestUtils.getAuthenticationParams();
        LdapConnectionOperationsMock oper = new LdapConnectionOperationsMock(url:params.url,username:params.username,userPassword:params.userPassword+"extra")

        try{
            oper.checkConnection()
            fail("Should throw Exception")
        }
        catch(NamingException e)
        {
            println e
        }
    }
}


class LdapConnectionOperationsMock extends LdapConnectionOperations
{
    Logger log = Logger.getRootLogger();
    String url="";
    String userPassword ="";
    String username ="";
    String contextFactory ="com.sun.jndi.ldap.LdapCtxFactory";
}
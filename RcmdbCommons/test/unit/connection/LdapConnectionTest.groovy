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

    public void test_Get_Add_Remove_Update_Entry()
    {
        Map params = LdapConnectionTestUtils.getConnectionParams();
        Map queryparams = LdapConnectionTestUtils.getQueryParams();

        LdapConnectionOperationsMock oper = new LdapConnectionOperationsMock(params);
        oper.connect();
        assertTrue (oper.isConnected());



        def nodeDn="CN=testNode,${queryparams.searchBase}".toString();
        
        //clear
        clearDnWithSubs (oper,nodeDn);

        //test add
        def addProps=[:];
        addProps.name="testNode";
        addProps.displayName="testNodeDisplay";
        addProps.objectClass=["top","container"];
        oper.addEntry(nodeDn,addProps);

        def addedEntry=oper.getEntry(nodeDn);
        println addedEntry;
        assertEquals(["testNode"],addedEntry.name)
        assertEquals(["testNodeDisplay"],addedEntry.displayName)
        assertEquals(["top","container"],addedEntry.objectClass)
        assertEquals([nodeDn],addedEntry.distinguishedName)
        
        //test update
        def updateProps=[:];
        updateProps.displayName="testNodeDisplayUpdated";        
        oper.updateEntry(nodeDn,updateProps);

        def updatedEntry=oper.getEntry(nodeDn);
        println updatedEntry;

        assertEquals(["testNode"],updatedEntry.name)
        assertEquals(["testNodeDisplayUpdated"],updatedEntry.displayName)
        assertEquals(["top","container"],updatedEntry.objectClass)
        assertEquals([nodeDn],updatedEntry.distinguishedName)


        //test remove and get nonexisting object 
        oper.removeEntry(nodeDn);

        try{
            def removedEntry=oper.getEntry(nodeDn);
            fail("should throw Exception")
        }
        catch(Exception e)
        {
             assertTrue(e.getMessage().indexOf("The name ${nodeDn} not found in ldap")>=0);
        }

        clearDnWithSubs (oper,nodeDn);

        oper.disconnect();
        assertFalse (oper.isConnected());

    }

    //For this test following entries should exist , test will add user as member to a group , and then remove by not touching other members
    // CN=Administrators,CN=Builtin,DC=molkay,DC=selfip,DC=net
    // CN=berkay,CN=Users,DC=molkay,DC=selfip,DC=net
    public void testUpdateEntry_AddAttribute_RemoveAttribute()
    {
        Map params = LdapConnectionTestUtils.getConnectionParams();
        Map queryparams = LdapConnectionTestUtils.getQueryParams();

        LdapConnectionOperationsMock oper = new LdapConnectionOperationsMock(params);
        oper.connect();
        assertTrue (oper.isConnected());



        def nodeDn="CN=Administrators,CN=Builtin,${queryparams.searchBase}".toString();
        def newMemberUserName="berkay";
        def newMemberUserDn="CN=${newMemberUserName},CN=Users,${queryparams.searchBase}".toString();
        //test add

        def nodeEntry=oper.getEntry(nodeDn);
        def oldMembers=nodeEntry.member;
        println oldMembers;
        def berkayEntry=oldMembers.find{ it.indexOf(newMemberUserName)>0 };
        if(berkayEntry != null )
        {
            oper.updateEntryRemoveAttributes(nodeDn,[member:[newMemberUserDn]]);
        }

        nodeEntry=oper.getEntry(nodeDn);
        oldMembers=nodeEntry.member;
        println oldMembers;
        assertNull ("Berkay user should not be in administrators,delete it from administrators manually",oldMembers.find{ it.indexOf("berkay")>0 });


        //test updateEntryAddAtrribute
        //only add berkay to member , previous member entries will remain

        def updateProps=[:];
        updateProps.member=[newMemberUserDn];
        oper.updateEntryAddAttributes(nodeDn,updateProps);

        def updatedEntry=oper.getEntry(nodeDn);
        def newMembersAfterAdd=updatedEntry.member;
        println newMembersAfterAdd;
        assertEquals(oldMembers.size()+1,newMembersAfterAdd.size());
        assertNotNull (newMembersAfterAdd.find{ it.indexOf(newMemberUserName)>0 });
        oldMembers.each{ oldMamber ->
            assertNotNull (newMembersAfterAdd.find{ it == oldMamber });
        }

        //test updateEntryRemoveAttribute
        //only remove berkay from member , other member entries will remain
        oper.updateEntryRemoveAttributes(nodeDn,updateProps);

        updatedEntry=oper.getEntry(nodeDn);
        def newMembersAfterRemove=updatedEntry.member;
        println newMembersAfterRemove;
        assertEquals(oldMembers.size(),newMembersAfterRemove.size());
        assertNull (newMembersAfterRemove.find{ it.indexOf(newMemberUserName)>0 });
        oldMembers.each{ oldMamber ->
            assertNotNull (newMembersAfterRemove.find{ it == oldMamber });
        }

    }

    public void testQuery()
    {
        Map params = LdapConnectionTestUtils.getConnectionParams();
        Map queryparams = LdapConnectionTestUtils.getQueryParams();

        LdapConnectionOperationsMock oper = new LdapConnectionOperationsMock(params);
        assertFalse (oper.isConnected());
        oper.connect();
        assertTrue (oper.isConnected());

        def nodeDn="CN=testNode,${queryparams.searchBase}".toString();
        def subDn="CN=testsubNode1,${nodeDn}".toString();

        def results=null;

        //clear
        clearDnWithSubs (oper,nodeDn);
        oper.addEntry(nodeDn,[objectClass:["top","container"]]);
        oper.addEntry(subDn,[objectClass:["top","container"]]);


        //search also leafs

        results=oper.query(nodeDn,"objectClass=*",true);        
        assertEquals(2,results.size());
        println results;
        
        def topNode=results.find{it.name.contains("testNode")};
        assertNotNull(topNode);
        assertEquals(["top","container"],topNode.objectClass);
        assertEquals([nodeDn],topNode.distinguishedName)

        def subNode=results.find{it.name.contains("testsubNode1")};
        assertNotNull(subNode);
        assertEquals(["top","container"],subNode.objectClass);
        assertEquals([subDn],subNode.distinguishedName)

        //search only root
        results=oper.query(nodeDn,"objectClass=*",false);
        assertEquals(1,results.size());
        println "rootResults"+results;
        assertEquals(1,results.findAll{it.name.contains("testsubNode1")}.size())

        //search also leafs but with query
        results=oper.query(nodeDn,"name=testsubNode1",true);
        assertEquals(1,results.size());
        println "leafResults"+results;
        assertEquals(1,results.findAll{it.name.contains("testsubNode1")}.size())


        clearDnWithSubs (oper,nodeDn);

        oper.disconnect();
        assertFalse (oper.isConnected());


    }

    public void clearDnWithSubs(oper,nodeDn)
    {
        try{
            def results=oper.query(nodeDn,"objectClass=*",true);
            results.each{ result ->
                try{
                    oper.removeEntry(result.nameInNamespace);
                }
                catch(e){println "ToBeIgnored: "+e}
            }
            oper.removeEntry(nodeDn)
        }
        catch(e2){println e2}
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
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

import javax.naming.Context
import javax.naming.NamingException
import javax.naming.directory.InitialDirContext
import javax.naming.directory.SearchControls

/**
 * Created by IntelliJ IDEA.
 * User: deneme
 * Date: Aug 25, 2008
 * Time: 11:04:42 AM
 * To change this template use File | Settings | File Templates.
 */
class LdapConnectionOperations extends ConnectionOperations
{
    def context;
    
    def checkAuthentication(String authUsername,String authPassword)
    {
        def result=false
        
        try{
            def ctx=_connect(authUsername, authPassword)
            result=true
            ctx.close()
        }
        catch(NamingException e)
        {
            result=false
            logger.error "Could not connect to LDAP for user authentication : ${e}"
        }

        return result

    }

    private InitialDirContext _connect(String username, String password)
    {
        def env = new Hashtable()
        env[Context.INITIAL_CONTEXT_FACTORY] = contextFactory
        env[Context.PROVIDER_URL] = url

        if(username!=null)
        {
            if(password==null)
                password=""

            env[Context.SECURITY_AUTHENTICATION] = "simple"
            env[Context.SECURITY_PRINCIPAL] = username;
            env[Context.SECURITY_CREDENTIALS] = password;    
        }

        InitialDirContext ctx = new InitialDirContext(env)
        return ctx;
    }

    def connect()
    {
        context = _connect(username, userPassword);

    }
    def disconnect()
    {
       if(isConnected())
       {
            context.close();
            context=null; 
       }
       else
       {
           throw new NamingException("Connection is not open");
       }

    }
    def isConnected()
    {
        return context != null;
    }
    def query(searchBase,searchFilter,searchSubDirectories)
    {
        if(isConnected())
        {
            if( searchFilter==null)
                searchFilter=""

            SearchControls searchControls = new SearchControls();
            if ( searchSubDirectories )
            {
                searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE )
            }

            return context.search(searchBase, searchFilter , searchControls)
        }
        else
        {
            throw new NamingException("Connection is not open");
        }
        /*
        def matchAttributes = new BasicAttributes(true)
        if(searchAttribute!="" && searchAttribute!=null &&  searchValue!="" && searchValue!=null )
        {
            matchAttributes.put(new BasicAttribute(searchAttribute, searchValue))
        }
        */
    }
    public boolean checkConnection()
    {
        try{
            def ctx=_connect(username, userPassword)
            ctx.close()
            return true;
        }
        catch(NamingException e)
        {
            logger.error "Could not connect to LDAP for checkConnection : ${e}"
            throw e
        }

    }
}
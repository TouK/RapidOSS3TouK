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
import javax.naming.directory.DirContext
import javax.naming.directory.InitialDirContext
import javax.naming.directory.SearchControls
import javax.naming.directory.Attributes
import javax.naming.directory.BasicAttributes
import javax.naming.directory.BasicAttribute

/**
 * Created by IntelliJ IDEA.
 * User: deneme
 * Date: Aug 25, 2008
 * Time: 11:04:42 AM
 * To change this template use File | Settings | File Templates.
 */
class LdapConnectionOperations extends ConnectionOperations
{
    InitialDirContext context;
    
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

    protected InitialDirContext _connect(String username, String password)
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
        _throwExceptionIfNotConnected();
        context.close();
        context=null;
    }
    def isConnected()
    {
        return context != null;
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
    def query(searchBase,searchFilter,searchSubDirectories)
    {
        _throwExceptionIfNotConnected();

        if( searchFilter==null)
            searchFilter=""

        SearchControls searchControls = new SearchControls();
        if ( searchSubDirectories )
        {
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE )
        }
        def results=[];
        def queryResult=context.search(searchBase, searchFilter , searchControls)
        while(queryResult.hasMore())
        {
              def searchResult = queryResult.next();              
              def searchResultProps=convertAttributesToProps(searchResult.getAttributes());
              searchResultProps.nameInNamespace=searchResult.nameInNamespace;
              results.add(searchResultProps);

        }
        return results;
        /*
        def matchAttributes = new BasicAttributes(true)
        if(searchAttribute!="" && searchAttribute!=null &&  searchValue!="" && searchValue!=null )
        {
            matchAttributes.put(new BasicAttribute(searchAttribute, searchValue))
        }
        */
    }


    public def getEntry(String dn)
    {
        _throwExceptionIfNotConnected();
        try{
            return convertAttributesToProps(context.getAttributes(dn));
        }
        catch(javax.naming.NameNotFoundException e)
        {
           throw new Exception("The name ${dn} not found in ldap server.",e); 
        }
    }

    public void addEntry(String dn,props)
    {
        _throwExceptionIfNotConnected();
        def attributes=convertPropsToAttributes(props);
        context.bind (dn,null,attributes);
    }
    public void updateEntry(String dn,props)
    {
        _throwExceptionIfNotConnected();
        def attributes=convertPropsToAttributes(props);
        context.modifyAttributes(dn,DirContext.REPLACE_ATTRIBUTE,attributes);
    }
    public void removeEntry(String dn)
    {
        _throwExceptionIfNotConnected();
        context.unbind(dn);
    }

    protected void _throwExceptionIfNotConnected()
    {
        if(!isConnected())        
        {
            throw new NamingException("Connection is not open");
        }
    }

    protected def convertAttributesToProps(Attributes attributes)
    {
        def props=[:];
         attributes.getAll().each{ attr ->

            def values=[];
            attr.getAll().each{ value ->
                values.add(value);
            }
            if(isSingleAttribute(attr))
            {
                props.put(attr.getID(),values[0]);
            }
            else
            {
                props.put(attr.getID(),values);
            }
        }
        return props;
    }
    protected boolean isSingleAttribute(attr)
    {
        DirContext metaSchema = attr.getAttributeDefinition();
        Attributes metaAttrs = metaSchema.getAttributes("",["SINGLE-VALUE"] as String[]);
        return metaAttrs?.get("SINGLE-VALUE")?.get() == "true";
    }
    
    protected Attributes convertPropsToAttributes(props)
    {
        Attributes attributes = new BasicAttributes();
        props.each { propName , propValue ->
            BasicAttribute attr = new BasicAttribute(propName);
            if(propValue instanceof List)
            {
                propValue.each{ propValueInList ->
                    attr.add(propValueInList);
                }
            }
            else
            {
                attr.add(propValue);
            }

            attributes.put(attr);
        }
        return attributes;
    }
}
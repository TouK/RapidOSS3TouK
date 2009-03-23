package com.ifountain.rcmdb.domain.operation

import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.codehaus.groovy.runtime.InvokerHelper
import org.codehaus.groovy.runtime.NullObject

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
 * User: Administrator
 * Date: Apr 25, 2008
 * Time: 3:03:18 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractDomainOperation {
    def domainObject;
    public Object getProperty(String propName)
    {
        def prop = this.metaClass.getMetaProperty(propName);
        if(prop != null && propName != "properties")
        {
            return prop.getProperty(this);
        }
        else
        {
            return domainObject.getProperty(propName);
        }
    }

    public Map getProperties()
    {
        domainObject.getProperty("properties");
    }

    public void setProperty(String propName, Object value)
    {
            def prop = AbstractDomainOperation.metaClass.getMetaProperty(propName);
            if(prop != null && propName != "properties")
            {
                prop.setProperty(this, value);
            }
            else
            {
                domainObject.setProperty(propName, value);
            }
    }

    public Object methodMissing(String methodName, Object args)
    {
        def argsInList = InvokerHelper.asList(args)
        def types = [];
        argsInList.each{
            if(it != null)
            {
                types.add(it.class);
            }
            else
            {
                types.add(null);
            }
        }
        if(domainObject.metaClass.getMetaMethod(methodName, types as Object[]) != null)
        {
            return domainObject.invokeMethod(methodName, args);
        }

        throw new MissingMethodException (methodName,  this.class, args); 

    }

}
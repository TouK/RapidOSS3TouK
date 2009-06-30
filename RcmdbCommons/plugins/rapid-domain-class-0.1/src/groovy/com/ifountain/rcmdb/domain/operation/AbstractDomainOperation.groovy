package com.ifountain.rcmdb.domain.operation

import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.codehaus.groovy.runtime.InvokerHelper
import org.codehaus.groovy.runtime.NullObject
import org.apache.log4j.Logger
import com.ifountain.rcmdb.execution.ExecutionContextManager
import com.ifountain.rcmdb.execution.ExecutionContext
import com.ifountain.rcmdb.util.RapidCMDBConstants


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
public class AbstractDomainOperation {
    def domainObject;
    RsSetPropertyWillUpdate rsSetPropertyWillUpdate = new RsSetPropertyWillUpdate();
    def rsUpdatedProps = null;
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

    public static Logger getLogger()
    {
        ExecutionContext context = ExecutionContextManager.getInstance().getExecutionContext();
        if(context != null)
        {
            Logger logger = context[RapidCMDBConstants.LOGGER];
            if(logger == null)
            {
                logger = Logger.getLogger(AbstractDomainOperation);
            }
            return logger;
        }
        else
        {
            return Logger.getLogger(AbstractDomainOperation);
        }
    }


    public void disableSetPropertyWillUpdate(Closure closureToBeExecuted)
    {
        rsSetPropertyWillUpdate.set(false);
        closureToBeExecuted();
        rsSetPropertyWillUpdate.set(true);
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
                if(rsSetPropertyWillUpdate.get())
                {
                    domainObject.setProperty(propName, value);
                }
                else
                {
                    if(rsUpdatedProps != null)
                    {
                        def oldValue = domainObject.getProperty(propName);
                        if(oldValue != value)
                        {
                            rsUpdatedProps.put(propName, oldValue);
                        }
                    }
                    domainObject.setProperty(propName, value, false);                    
                }
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


    public void onLoadWrapper()
    {
        onLoad();
    }

    Map beforeUpdateWrapper(Map params)
    {
        rsUpdatedProps = [:]
        disableSetPropertyWillUpdate {
            beforeUpdate(params);
        }
        return rsUpdatedProps;
    }

    public void afterUpdateWrapper(Map params)
    {
        afterUpdate(params);
    }

    public void beforeInsertWrapper()
    {
        disableSetPropertyWillUpdate {
            beforeInsert();
        }
    }

    public void afterInsertWrapper()
    {
        afterInsert();
    }

    public void beforeDeleteWrapper()
    {
        disableSetPropertyWillUpdate {
            beforeDelete();
        }
    }

    public void afterDeleteWrapper()
    {
        afterDelete();
    }

    def onLoad(){}
    def beforeUpdate(params){}
    def afterUpdate(params){}
    def beforeInsert(){}
    def afterInsert(){}
    def beforeDelete(){}
    def afterDelete(){}
    
}

class RsSetPropertyWillUpdate extends ThreadLocal<Boolean>
{

    protected Boolean initialValue() {
        return true; //To change body of overridden methods use File | Settings | File Templates.
    }


}
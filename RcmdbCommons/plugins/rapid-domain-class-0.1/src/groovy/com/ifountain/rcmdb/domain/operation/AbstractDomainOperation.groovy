package com.ifountain.rcmdb.domain.operation

import org.codehaus.groovy.runtime.InvokerHelper
import org.apache.log4j.Logger
import com.ifountain.rcmdb.util.ExecutionContextManagerUtils


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
    def rsIsBeforeTriggerContinue = false;
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
        Logger logger=ExecutionContextManagerUtils.getLoggerFromCurrentContext();
        if(logger == null)
        {
            logger = Logger.getLogger(AbstractDomainOperation);
        }
        return logger;        
    }


    public void invokeBeforeEventTriggerOperation(Closure closureToBeExecuted)
    {
        rsIsBeforeTriggerContinue = true;
        rsSetPropertyWillUpdate.set(false);
        try{
            closureToBeExecuted();
        }finally{
            rsSetPropertyWillUpdate.set(true);
            rsIsBeforeTriggerContinue = false;
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

    def invokeCompassOperation(String methodName, List args)
    {
        if(methodName == "updateForSetProperty")
        {
            if(rsSetPropertyWillUpdate.get())
            {
                this.domainObject.invokeMethod("_update", args as Object[]);
            }
            else
            {
                args[0].each{propName, value->
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
            return null;
        }
        else
        {
            if(rsIsBeforeTriggerContinue)
            {
                throw new RuntimeException("${methodName} cannot be executed in before triggers");
            }
            return this.domainObject.invokeMethod("_${methodName}", args as Object[]);
        }

    }


    public void onLoadWrapper()
    {
        onLoad();
    }

    public Map beforeUpdateWrapper(Map params)
    {
        rsUpdatedProps = [:]
        invokeBeforeEventTriggerOperation {
            beforeUpdate(params);
        }
        return rsUpdatedProps;
    }

    public void afterUpdateWrapper(Map params)
    {
        afterUpdate(params);
    }

    public Map beforeInsertWrapper()
    {
        rsUpdatedProps = [:]
        invokeBeforeEventTriggerOperation {
            beforeInsert();
        }
        return rsUpdatedProps;
    }

    public void afterInsertWrapper()
    {
        afterInsert();
    }

    public void beforeDeleteWrapper()
    {
        invokeBeforeEventTriggerOperation {
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
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
package com.ifountain.rcmdb.test.util

import com.ifountain.rcmdb.util.RapidCMDBConstants

/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Oct 23, 2008
 * Time: 3:48:45 PM
 * To change this template use File | Settings | File Templates.
 */
class CompassForTests {
//    static List classesToBeInitialized;
    static MockOperationData addOperationData = new MockOperationData();
    static MockOperationData getOperationData = new MockOperationData();
    public static void initialize(List classesToBeInitialized)
    {
         addOperationData.initialize(classesToBeInitialized);
         getOperationData.initialize(classesToBeInitialized);
        classesToBeInitialized.each{Class domainClass->
            domainClass.metaClass.static.add = {Map props->
                return addOperationData.getReturnObject(domainClass, new HashMap(props));
            }
            domainClass.metaClass.static.get = {Map props->
                return getOperationData.getReturnObject(domainClass, new HashMap(props));
            }
            domainClass.metaClass.hasErrors = {
                return false;
            }
        }
    }

    public static void addOperationSupport(Class domainClass, Class operationClass)
    {
        MetaClass mc = domainClass.metaClass;
        if (mc.getMetaProperty(RapidCMDBConstants.OPERATION_PROPERTY_NAME) != null)
        {
            mc.methodMissing = {String name, args ->
                def oprInstance = delegate[RapidCMDBConstants.OPERATION_PROPERTY_NAME];
                if (oprInstance == null)
                {
                    oprInstance = operationClass.newInstance();
                    operationClass.metaClass.getMetaProperty("domainObject").setProperty(oprInstance, delegate);
                    delegate[RapidCMDBConstants.OPERATION_PROPERTY_NAME] = oprInstance;
                }
                try {
                    return oprInstance.invokeMethod(name, args)
                } catch (MissingMethodException e) {
                    if (e.getType().name != oprInstance.class.name || e.getMethod() != name)
                    {
                        throw e;
                    }
                }
                throw new MissingMethodException(name, mc.theClass, args);
            }
            mc.'static'.methodMissing = {String methodName, args ->
                try {
                    return operationClass.metaClass.invokeStaticMethod(operationClass, methodName, args);
                } catch (MissingMethodException e) {
                    if (e.getType().name != operationClass.name || e.getMethod() != methodName)
                    {
                        throw e;
                    }
                }
                throw new MissingMethodException(methodName, mc.theClass, args);
            }
        }
    }
}

class MockOperationData{
    Map operationData = [:]
    Map operationParams = [:]
    Map numberOfAddMethodCalls = [:]
    List classesToBeInitialized;
    public void initialize(List classesToBeInitialized)
    {
        this.classesToBeInitialized = classesToBeInitialized;
        clearAll();
    }

    public void setObjectsWillBeReturned(List objects)
    {
        clearAll();
        objects.each{Object domainObject->
            def addObjects = operationData[domainObject.class.name]
            addObjects.add(domainObject);
        }
    }

    public List getParams(Class domainClass)
    {
        return operationParams[domainClass.name];        
    }

    public Object getReturnObject(Class domainClass, Object params)
    {
        def currentOperationNumber = numberOfAddMethodCalls[domainClass.name];
        operationParams[domainClass.name].add(params);
        Object res = operationData[domainClass.name][currentOperationNumber];
        numberOfAddMethodCalls[domainClass.name] = currentOperationNumber+1;
        return res;
    }

    private void clearAll()
    {
       classesToBeInitialized.each{Class domainClass->
            numberOfAddMethodCalls[domainClass.name] = 0;
            operationParams[domainClass.name] = [];
            operationData[domainClass.name] = [];
        }
    }
}

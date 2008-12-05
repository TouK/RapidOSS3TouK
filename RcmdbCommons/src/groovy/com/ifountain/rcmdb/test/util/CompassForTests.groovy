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
    static Map addMethodData = [:]
    public static void initialize(List classesToBeInitialized)
    {
        classesToBeInitialized.each{Class domainClass->

            domainClass.metaClass.static.add = {Map props->
                return addMethodData[domainClass.name]?.getAt(0);
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

    public static void setAddObjects(List objects)
    {
        objects.each{Object domainObject->
            def addObjects = addMethodData[domainObject.class.name]
            if(addObjects == null)
            {
                addObjects = [];
                addMethodData[domainObject.class.name] = addObjects;
            }
            addObjects.add(domainObject);
        }
    }
}

class MethodData{
    List params;
    Object returnedData;
}
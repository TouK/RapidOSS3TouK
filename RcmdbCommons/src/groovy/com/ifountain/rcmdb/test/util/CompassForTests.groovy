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
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import com.ifountain.rcmdb.domain.util.DomainClassUtils
/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Oct 23, 2008
 * Time: 3:48:45 PM
 * To change this template use File | Settings | File Templates.
 */
class CompassForTests {
//    static List classesToBeInitialized;
    static List injectedClasses = [];
    static MockOperationData addOperationData = new MockOperationData();
    static MockOperationData getOperationData = new MockOperationData();
    static MockOperationData updateOperationData = new MockOperationData();
    static MockOperationData addRelationOperationData = new MockOperationData();
    static MockOperationData removeRelationOperationData = new MockOperationData();
    static Integer countHitsValue= null;
    public static void destroy()
    {
        ExpandoMetaClass.disableGlobally();
        injectedClasses.each{Class cls->
            GroovySystem.getMetaClassRegistry().removeMetaClass (cls);
        }
        ExpandoMetaClass.disableGlobally();
    }
    public static void initialize(List classesToBeInitialized)
    {
         ExpandoMetaClass.enableGlobally();
         injectedClasses.addAll(classesToBeInitialized);
         addOperationData.initialize(classesToBeInitialized);
         getOperationData.initialize(classesToBeInitialized);
         updateOperationData.initialize(classesToBeInitialized);
         addRelationOperationData.initialize(classesToBeInitialized);
         removeRelationOperationData.initialize(classesToBeInitialized);
        classesToBeInitialized.each{Class domainClass->
            def dc=new DefaultGrailsDomainClass(domainClass)
            domainClass.metaClass.static.add = {Map props->
                return addOperationData.getReturnObject(domainClass, new HashMap(props));
            }
            domainClass.metaClass.static.get = {Map props->
                return getOperationData.getReturnObject(domainClass, new HashMap(props));
            }
            domainClass.metaClass.static.get = {Long id->
                return getOperationData.getReturnObject(domainClass, new HashMap(["id":id]));
            }
            domainClass.metaClass.static.update = {Map props->
               def relations = DomainClassUtils.getRelations(dc);
               props.each{key,value->
                    def relation=relations[key];
                    if(relation==null)
                    {
                        try{
                            delegate[key]=value;
                        }catch(e){ println e}
                    }
                    else
                    {
                        value=value instanceof Collection?value:[value]
                        if(relation.isOneToOne() || relation.isManyToOne())
                        {
                            delagate[key]=value[0];
                        }
                        else
                        {
                            //TODO: we do not preserve old relation list here, we always update
                            //if a code calls more than one updates with different relations old relations are always gone
                            if(value[0] != null)
                            {
                                delagate[key]=value;
                            }
                            else
                            {
                                delate[key].clear();
                            }
                        }
                    }
                }

                return updateOperationData.getReturnObject(domainClass, new HashMap(props));
            }
            domainClass.metaClass.static.addRelation = {Map props->
                return addRelationOperationData.getReturnObject(domainClass, new HashMap(props));
            }
            domainClass.metaClass.static.removeRelation = {Map props->
                return removeRelationOperationData.getReturnObject(domainClass, new HashMap(props));
            }
            domainClass.metaClass.static.countHits = { String query ->
                return countHitsValue;
            }
            domainClass.metaClass.hasErrors = {
                return false;
            }
        }
    }

    public static void addOperationSupport(Class domainClass, Class operationClass)
    {
        ExpandoMetaClass.enableGlobally();
        injectedClasses.add(domainClass);
        MetaClass mc = domainClass.metaClass;
        domainClass.setOperationClass(operationClass);
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
    public int getCallCount(Class domainClass)
    {
        return numberOfAddMethodCalls[domainClass.name];
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


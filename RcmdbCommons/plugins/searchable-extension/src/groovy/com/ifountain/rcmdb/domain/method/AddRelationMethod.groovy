package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.domain.util.Relation

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
 * Date: Apr 24, 2008
 * Time: 2:00:27 PM
 * To change this template use File | Settings | File Templates.
 */
class AddRelationMethod extends AbstractRapidDomainMethod{
    def relations;
    public AddRelationMethod(MetaClass mc, Map relations) {
        super(mc); //To change body of overridden methods use File | Settings | File Templates.
        this.relations = relations;
    }

    private List getItemsWillBeAdded(oldValue, currentValue)
    {
        boolean contains = false;
        def alreadyExistingItems = [:]
        oldValue.each
        {
            alreadyExistingItems[it.id] = it;
        }
        def objectsWillBeAdded = [];
        currentValue.each
        {
            if(!alreadyExistingItems.containsKey(it.id))
            {
                objectsWillBeAdded += it;
            }
        }
        return objectsWillBeAdded;
    }
    
    public Object invoke(Object domainObject, Object[] arguments) {

        def props = arguments[0];
        def relatedInstances = [:]
        props.each{key,value->
            Relation relation = relations.get(key);
            if(relation)
            {
                value = getItemsWillBeAdded(domainObject[relation.name], value);
                if(value.size() >0){
                     def relatedClass = relation.otherSideCls;
                     def storage = relatedInstances[relatedClass];
                     if(!storage)
                     {
                        storage = [];
                        relatedInstances[relatedClass] = storage;
                     }
                    if(relation.type == Relation.ONE_TO_ONE)
                    {
                        def oldValue = domainObject[relation.name];
                        if(oldValue)
                        {
                            oldValue[relation.otherSideName] = null;
                            storage.add(oldValue);
                        }
                        domainObject.setProperty(relation.name, value[0], false);
                        value[0].setProperty(relation.otherSideName, domainObject, false);
                    }
                    else if(relation.type == Relation.ONE_TO_MANY)
                    {
                        domainObject[relation.name] += value;
                        value.each
                        {
                            if(it[relation.otherSideName])
                            {
                                def relationToBeRemoved =[:]
                                relationToBeRemoved[relation.otherSideName] = it[relation.otherSideName];
                                it.removeRelation(relationToBeRemoved);
                            }
                            it.setProperty(relation.otherSideName, domainObject, false);
                        }
                    }
                    else if(relation.type == Relation.MANY_TO_ONE)
                    {
                        if(domainObject[relation.name])
                        {
                            def relationToBeRemoved =[:]
                            relationToBeRemoved[relation.name] = domainObject[relation.name];
                            domainObject.removeRelation(relationToBeRemoved);
                        }
                        domainObject.setProperty(relation.name, value[0], false);
                        value[0][relation.otherSideName] += domainObject;
                    }
                    else
                    {
                        domainObject[relation.name] += value;
                        value.each
                        {
                            it[relation.otherSideName] += domainObject;
                        }
                    }
                    storage.addAll(value);
                }
            }
        }
        if(relatedInstances.size() > 0)
        {
            CompassMethodInvoker.index (mc, domainObject);
        }
        relatedInstances.each{instanceClass, instances->
            CompassMethodInvoker.index (instanceClass.metaClass, instances);
        }

        return domainObject;
    }

}
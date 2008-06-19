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
 * Time: 1:42:09 PM
 * To change this template use File | Settings | File Templates.
 */
class RemoveRelationMethod extends AbstractRapidDomainMethod{

    def relations;
    public RemoveRelationMethod(MetaClass mc, Map relations) {
        super(mc);
        this.relations = relations;
    }

    public Object invoke(Object domainObject, Object[] arguments) {
        def props = arguments[0];
        def changedInstances = [:]
        boolean isChanged = false;
        props.each{key,value->
            Relation relation = relations.get(key);
            def storage = [];
            if(relation)
            {
                if(relation.isOneToOne())
                {
                    if(value instanceof Collection)
                    {
                        value = value[0];
                    }
                    if(domainObject[relation.name] && domainObject[relation.name].id == value.id)
                    {
                        domainObject.setProperty(relation.name, null, false);
                        if(relation.hasOtherSide())
                        {
                            storage+= value;
                            value.setProperty(relation.otherSideName, null, false);
                        }
                    }
                }
                else if(relation.isManyToOne())
                {
                    if(value instanceof Collection)
                    {
                        value = value[0];
                    }
                    if(domainObject[relation.name] && domainObject[relation.name].id == value.id)
                    {

                        domainObject.setProperty(relation.name, null, false);
                        if(relation.hasOtherSide())
                        {
                            storage+= value;
                            def otherSideRelatedClasses = value[relation.otherSideName];
                            for(Iterator i = otherSideRelatedClasses.iterator(); i.hasNext(); )
                            {
                                def otherSideClass = i.next();
                                if(otherSideClass.id == domainObject.id)
                                {
                                    i.remove();
                                    break;
                                }
                            }
                        }
                    }
                }
                else if(relation.isOneToMany())
                {
                    def relatedObjects = [:];
                    domainObject[relation.name].each{
                        relatedObjects[it.id] = it;
                    }

                    value.each
                    {

                        relatedObjects.remove(it.id);
                        if(relation.hasOtherSide())
                        {
                            storage += it;
                            it.setProperty(relation.otherSideName, null, false);
                        }
                    }
                    domainObject.setProperty(relation.name, new ArrayList(relatedObjects.values()), false);
                }
                else                        
                {
                    def relatedObjects = [:];
                    domainObject[relation.name].each{
                        relatedObjects[it.id] = it;
                    }

                    value.each{relatedClassToBeRemoved->
                        relatedObjects.remove(relatedClassToBeRemoved.id);
                        if(relation.hasOtherSide())
                        {
                            storage += relatedClassToBeRemoved;
                            def otherClassRelations = relatedClassToBeRemoved[relation.otherSideName];
                            for(Iterator i = otherClassRelations.iterator(); i.hasNext(); )
                            {
                                def otherClassRelation = i.next();
                                if(otherClassRelation.id == domainObject.id)
                                {
                                    i.remove();
                                    break;
                                }
                            }
                        }
                    }
                    domainObject.setProperty(relation.name, new ArrayList(relatedObjects.values()));
                }
                if(storage.size() > 0)
                {
                    def oldStorage = changedInstances[relation.otherSideCls]
                    if(!oldStorage)
                    {
                        changedInstances[relation.otherSideCls] = storage;
                    }
                    else
                    {
                        oldStorage.addAll(storage);
                    }
                }
            }
        }
        CompassMethodInvoker.index (mc, domainObject);
        changedInstances.each{instanceClass, instances->
            CompassMethodInvoker.index (instanceClass.metaClass, instances);
        }
    }
}
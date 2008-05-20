package com.ifountain.rcmdb.domain.method

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import com.ifountain.rcmdb.domain.util.Relation
import org.codehaus.groovy.grails.exceptions.InvalidPropertyException
import com.ifountain.rcmdb.domain.util.DomainClassUtils

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
                        storage+= value;
                        domainObject[relation.name] = null;
                        value[relation.otherSideName] = null;
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
                        storage+= value;
                        domainObject.setProperty(relation.name, null);
                        def otherSideRelatedClasses = value[relation.otherSideName];
                        for(int i=0; i < otherSideRelatedClasses.size(); i++)
                        {
                            if(otherSideRelatedClasses[i].id == domainObject.id)
                            {
                                otherSideRelatedClasses.remove(i);
                                break;
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
                        storage += it;
                        relatedObjects.remove(it.id);
                        it[relation.otherSideName] = null;
                    }
                    domainObject[relation.name] = new ArrayList(relatedObjects.values());
                }
                else
                {
                    def relatedObjects = [:];
                    domainObject[relation.name].each{
                        relatedObjects[it.id] = it;
                    }
                    value.each{relatedClassToBeRemoved->
                        relatedObjects.remove(relatedClassToBeRemoved.id);
                        storage += relatedClassToBeRemoved;
                        def otherClassRelations = relatedClassToBeRemoved[relation.otherSideName];
                        for(int i=0; i < otherClassRelations.size(); i++)
                        {
                            if(otherClassRelations[i].id == domainObject.id)
                            {
                                otherClassRelations.remove(i);
                                break;
                            }
                        }
                    }
                    domainObject[relation.name] = new ArrayList(relatedObjects.values());
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

        if(changedInstances.size() > 0)
        {
            CompassMethodInvoker.index (mc, domainObject);
        }
        changedInstances.each{instanceClass, instances->
            CompassMethodInvoker.index (instanceClass.metaClass, instances);
        }
    }
}
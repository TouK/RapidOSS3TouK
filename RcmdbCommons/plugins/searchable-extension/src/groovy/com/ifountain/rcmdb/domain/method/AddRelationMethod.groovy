package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.domain.util.RelationMetaData
import com.ifountain.rcmdb.domain.property.RelationUtils
import relation.Relation

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

    public boolean isWriteOperation() {
        return true;
    }

    def removeExistingRelations(domainObject, Relation relationObject,  relOtherSideName, relOtherSideClass)
    {
        if(relationObject && !relationObject.relatedObjectIds.isEmpty())
        {
            relationObject.relatedObjectIds.clear()
            relationObject.reindex();
        }
        else
        {
            def relationsToBeRemoved = RelationUtils.getReverseRelationObjects(domainObject, relOtherSideName, relOtherSideClass);
            relationsToBeRemoved.each{Relation previousRelation->
                previousRelation.relatedObjectIds.remove(Relation.getRelKey(domainObject.id))
                previousRelation.reindex();
            }
        }
    }
    protected Object _invoke(Object domainObject, Object[] arguments) {

        def props = arguments[0];
        def flush = true;
        if(arguments.length == 2)
        {
            if(arguments[1] == false)
            {
                flush = false;
            }
        }


        def relatedInstances = [:]
        props.each{key,value->
            if(value)
            {
                RelationMetaData relation = relations.get(key);
                if(relation)
                {
                    Relation domainObjectRelations = Relation.get(objectId:domainObject.id, name:relation.name);
                    def allRefRelationObjs = [:];
                    def referencingRelations = RelationUtils.getReverseRelationObjects(domainObject, relation.otherSideName, relation.getOtherSideCls());
                    if(domainObjectRelations == null)
                    {
                        domainObjectRelations = Relation.add(objectId:domainObject.id, name:relation.name, className:domainObject.class.name);
                    }
                    allRefRelationObjs.putAll (domainObjectRelations.relatedObjectIds);
                    referencingRelations.each{
                        allRefRelationObjs[Relation.getRelKey(it.objectId)] = it.objectId;
                    }
                    
                    value = value instanceof Collection?value:[value];
                    value = value.findAll {!allRefRelationObjs.containsKey(Relation.getRelKey(it.id))}
                    if(value.size() >0){
                        if(relation.type == RelationMetaData.ONE_TO_ONE || relation.type == RelationMetaData.MANY_TO_ONE)
                        {
                            removeExistingRelations(domainObject, domainObjectRelations, relation.otherSideName, relation.otherSideCls);
                            value = [value[0]];

                        }
                        else if(relation.type == RelationMetaData.ONE_TO_MANY)
                        {
                            value.each{newRelatedObject->
                                Relation newRelatedObjectRelations = Relation.get(objectId:newRelatedObject.id, name:relation.otherSideName);
                                removeExistingRelations(newRelatedObject, newRelatedObjectRelations, relation.name, relation.cls);
                            }


                        }
                        value.each{newRelatedObject->
                            domainObjectRelations.relatedObjectIds.put(Relation.getRelKey(newRelatedObject.id), newRelatedObject.id);
                        }
                        domainObjectRelations.reindex();
                    }
                }
            }
        }
        return domainObject;
    }

}
package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.domain.util.RelationMetaData
import com.ifountain.rcmdb.domain.property.RelationUtils
import com.ifountain.rcmdb.domain.util.RelationMetaData
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
                    value = value instanceof Collection?value:[value];
                    def isReverse = RelationUtils.isReverse(domainObject, relation);
                    if(value.size() >0){
                        if(relation.type == RelationMetaData.ONE_TO_ONE || relation.type == RelationMetaData.MANY_TO_ONE)
                        {
                            def relationObjects = RelationUtils.getRelationObjects(domainObject, relation);
                            relationObjects.each{relationObject->
                                relationObject.remove();
                            }
                        }
                        else if(relation.type == RelationMetaData.ONE_TO_MANY)
                        {
                            if(relation.hasOtherSide())
                            {

                                value.each{newRelatedObject->
                                    if(isReverse)
                                    {
                                        Relation.searchEvery("objectId:${newRelatedObject.id} AND name:${relation.otherSideName}")*.remove();
                                    }
                                    else
                                    {
                                        Relation.searchEvery("reverseObjectId:${newRelatedObject.id} AND reverseName:${relation.otherSideName}")*.remove();
                                    }
                                }
                            }
                        }
                        value.each{newRelatedObject->
                            if(!isReverse)
                            {
                                Relation.add(objectId:domainObject.id, reverseObjectId:newRelatedObject.id, name:relation.name, reverseName:relation.otherSideName);
                            }
                            else
                            {
                                Relation.add(objectId:newRelatedObject.id, reverseObjectId:domainObject.id, name:relation.otherSideName, reverseName:relation.name);
                            }
                        }
                    }
                }
            }
        }
        return domainObject;
    }

}
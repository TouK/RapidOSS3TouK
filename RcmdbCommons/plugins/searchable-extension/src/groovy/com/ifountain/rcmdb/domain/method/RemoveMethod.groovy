package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.domain.util.ValidationUtils
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
 * Time: 1:33:26 PM
 * To change this template use File | Settings | File Templates.
 */
class RemoveMethod extends AbstractRapidDomainMethod{
    def relations;
    public RemoveMethod(MetaClass mc, Map relations) {
        super(mc);
        this.relations = relations;
    }

    public boolean isWriteOperation() {
        return true;
    }

    protected Object _invoke(Object domainObject, Object[] arguments) {
        def keyMap = [id:domainObject.id];
        def numberOfExistingObjects = CompassMethodInvoker.countHits(mc, keyMap);
        if(numberOfExistingObjects == 0)
        {
            ValidationUtils.addObjectError (domainObject.errors, "default.not.exist.message", []);            
        }
        else
        {
            def cascadedObjectsToBeRemoved = [];
            def relsToBeRemoved = [:]
            relations.each{relationName,relation->
                def relatedObject = domainObject[relationName];
                if(relatedObject instanceof Collection)
                {
                    relsToBeRemoved[relationName] = relatedObject;
                    if(relation.isCascade)
                    {
                        cascadedObjectsToBeRemoved.addAll(relatedObject);
                    }
                }
                else if(relatedObject != null)
                {
                    relsToBeRemoved[relationName] = [relatedObject];
                    if(relation.isCascade)
                    {
                        cascadedObjectsToBeRemoved.add(relatedObject);
                    }
                }

            }
            if(!relsToBeRemoved.isEmpty())
            {
                domainObject.removeRelation(relsToBeRemoved);
            }
            cascadedObjectsToBeRemoved.each{
                it.remove();
            }
            EventTriggeringUtils.triggerEvent (domainObject, EventTriggeringUtils.BEFORE_DELETE_EVENT);
            CompassMethodInvoker.unindex(mc, domainObject);
            if(!relsToBeRemoved.isEmpty())
            {
                Relation.searchEvery("objectId:${domainObject.id}")*.remove();
            }
        }
    }

}
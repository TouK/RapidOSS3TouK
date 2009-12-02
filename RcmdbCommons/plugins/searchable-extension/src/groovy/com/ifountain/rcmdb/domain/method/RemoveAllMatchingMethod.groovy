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
package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.domain.property.RelationUtils
import com.ifountain.rcmdb.domain.util.RelationMetaData
import com.ifountain.rcmdb.domain.cache.IdCache;
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Nov 13, 2008
 * Time: 5:57:30 PM
 * To change this template use File | Settings | File Templates.
 */
class RemoveAllMatchingMethod extends AbstractRapidDomainWriteMethod {
    List cascadedRelations;
    public RemoveAllMatchingMethod(MetaClass mcp, Map relations) {
        super(mcp); //To change body of overridden methods use File | Settings | File Templates.
        cascadedRelations = [];
        relations.each {relationName, RelationMetaData metaData ->
            if (metaData.isCascade)
            {
                cascadedRelations.add(metaData);
            }
        }
    }

    public String getLockName(Object domainObject, Object[] arguments) {
        return null;
    }



    protected Map _invoke(Object clazz, Object[] arguments) {
        def query = arguments[0];

        clazz.'searchEvery'(query, [raw: {hits, session ->
            hits.iterator().each {hit ->
                def resource = hit.getResource();
                def id = resource.getObject("id");
                session.delete(resource);
                IdCache.get(id).clear();
                cascadedRelations.each {RelationMetaData metaData ->
                    def cascadedObjectsIds = RelationUtils.getRelatedObjectsIdsByObjectId(id, metaData.getName(), metaData.getOtherSideName());
                    cascadedObjectsIds.each {cascadedObjectId, cascadedNumberOfObjects ->
                        def cascadedObject = metaData.getOtherSideCls().'get'(id: cascadedObjectId)
                        if (cascadedObject != null)
                        {
                            cascadedObject.remove();
                        }
                    }
                }
                RelationUtils.removeExistingRelationsById(id)
            }
        }])
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    protected Object executeAfterTriggers(Map triggersMap) {
        return null;
    }

}
package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.domain.property.RelationUtils
import com.ifountain.rcmdb.domain.util.RelationMetaData;
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Nov 13, 2008
 * Time: 5:57:30 PM
 * To change this template use File | Settings | File Templates.
 */
class RemoveAllMatchingMethod extends AbstractRapidDomainStaticMethod{
    List cascadedRelations;
    public RemoveAllMatchingMethod(MetaClass mc, Map relations) {
        super(mc);    //To change body of overridden methods use File | Settings | File Templates.
        cascadedRelations = [];
        relations.each{relationName, RelationMetaData metaData->
            if(metaData.isCascade)
            {
                cascadedRelations.add(metaData);
            }
        }
    }

    public boolean isWriteOperation() {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected Object _invoke(Class clazz, Object[] arguments) {
        def query = arguments[0];

        clazz.'searchEvery'(query, [raw:{hits, session->
            hits.each{hit->
                def resource = hit.getResource();
                def id = resource.getObject("id");
                session.delete(resource);
                cascadedRelations.each{RelationMetaData metaData->
                    def cascadedObjectsIds = RelationUtils.getRelatedObjectsIdsByObjectId(id, metaData.getName(), metaData.getOtherSideName());
                    cascadedObjectsIds.each{cascadedObjectId, cascadedNumberOfObjects->
                        def cascadedObject = metaData.getOtherSideCls().'get'(id:cascadedObjectId)
                        if(cascadedObject != null)
                        {
                            cascadedObject.remove();
                        }
                    }
                }
                RelationUtils.removeExistingRelationsById(id)
            }
        }])
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
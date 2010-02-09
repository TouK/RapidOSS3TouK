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
import com.ifountain.rcmdb.util.CollectionUtils
import com.ifountain.rcmdb.domain.statistics.OperationStatisticResult
import com.ifountain.rcmdb.domain.statistics.OperationStatistics

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 2, 2008
 * Time: 6:20:13 PM
 * To change this template use File | Settings | File Templates.
 */
class GetRelatedObjectPropertyValuesMethod extends AbstractRapidDomainReadMethod{
    Map relations;
    public static final int BATCH_SIZE = 200;
    public GetRelatedObjectPropertyValuesMethod(MetaClass mcp, Map relations) {
        super(mcp);
        this.relations = relations;
    }

    protected Object _invoke(Object domainObject, Object[] arguments) {
        OperationStatisticResult statistics = new OperationStatisticResult(model:domainObject.class.name);
        statistics.start();

        String relName = arguments[0]
        Collection propList = arguments[1]
        Map options = arguments[2]
        String source = arguments[3]
        RelationMetaData relationMetaData = relations[relName];
        def results = [];
        if(relationMetaData != null)
        {
            Map relatedObjectIds = RelationUtils.getRelatedObjectsIds(domainObject, relationMetaData.name, relationMetaData.otherSideName, source);
            def ids = new ArrayList(relatedObjectIds.keySet())
            CollectionUtils.executeForEachBatch (ids, BATCH_SIZE, ){List idsToBeProcessed->
                StringBuffer query = new StringBuffer("id:");
                query.append (idsToBeProcessed.join(" OR id:"));
                if(options.max == null || results.size() < options.max)
                {
                    results.addAll(relationMetaData.otherSideCls.'getPropertyValues'(query.toString(), propList, options));
                }
            }
        }
        if(options.max != null && results.size() >= options.max)
        {
            results = results.subList (0, options.max)
        }
        statistics.stop();

        OperationStatistics.getInstance().addStatisticResult (OperationStatistics.GET_RELATED_MODEL_PROPERTY_VALUES_OPERATION_NAME, statistics);
        OperationStatistics.getInstance().addStatisticResult (OperationStatistics.GET_RELATED_MODEL_PROPERTY_VALUES_OPERATION_NAME, statistics.getSubStatisticsWithObjectCount(results?.size()));
        return results;
    }

}
package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.domain.property.RelationUtils
import com.ifountain.rcmdb.domain.util.ValidationUtils
import com.ifountain.rcmdb.domain.statistics.OperationStatistics
import com.ifountain.rcmdb.domain.statistics.OperationStatisticResult
import com.ifountain.rcmdb.domain.ObjectProcessor

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
class RemoveMethod extends AbstractRapidDomainWriteMethod {
    def relations;
    public RemoveMethod(MetaClass mcp, Map relations) {
        super(mcp);
        this.relations = relations;
    }

    protected Map _invoke(Object domainObject, Object[] arguments) {
        def triggersMap = [shouldCallAfterTriggers: true, domainObject: domainObject];
        OperationStatisticResult statistics = new OperationStatisticResult(model: mc.theClass.name);
        statistics.start();
        def cacheEntry = domainObject.getCacheEntry(domainObject);
        if (!cacheEntry.exist())
        {
            ValidationUtils.addObjectError(domainObject.errors, "default.not.exist.message", []);
            triggersMap.shouldCallAfterTriggers = false;
            return triggersMap;
        }
        else
        {
            statistics.stop();
            OperationStatisticResult beforeDeleteStatistics = new OperationStatisticResult(model: mc.theClass.name);
            beforeDeleteStatistics.start();
            EventTriggeringUtils.getInstance().triggerEvent(domainObject, EventTriggeringUtils.BEFORE_DELETE_EVENT);
            OperationStatistics.getInstance().addStatisticResult(OperationStatistics.BEFORE_DELETE_OPERATION_NAME, beforeDeleteStatistics);
            statistics.start();
            def cascadedObjectsToBeRemoved = [];
            def relsToBeRemoved = [:]
            relations.each {relationName, relation ->
                def relatedObject = domainObject[relationName];
                if (relatedObject instanceof Collection)
                {
                    relsToBeRemoved[relationName] = relatedObject;
                    if (relation.isCascade)
                    {
                        cascadedObjectsToBeRemoved.addAll(relatedObject);
                    }
                }
                else if (relatedObject != null)
                {
                    relsToBeRemoved[relationName] = [relatedObject];
                    if (relation.isCascade)
                    {
                        cascadedObjectsToBeRemoved.add(relatedObject);
                    }
                }

            }
            if (!relsToBeRemoved.isEmpty())
            {
                domainObject.removeRelation(relsToBeRemoved);
            }
            cascadedObjectsToBeRemoved.each {
                it.remove();
            }

            CompassMethodInvoker.unindex(mc, domainObject);
            domainObject.updateCacheEntry(domainObject, false);
            OperationStatistics.getInstance().addStatisticResult(OperationStatistics.REMOVE_OPERATION_NAME, statistics);
            if (!relsToBeRemoved.isEmpty())
            {
                RelationUtils.removeExistingRelationsById(domainObject.id);
            }
            return triggersMap;
        }
    }
    protected Object executeAfterTriggers(Map triggersMap) {
        def domainObject = triggersMap.domainObject;
        if (triggersMap.shouldCallAfterTriggers) {
            OperationStatisticResult afterDeleteStatistics = new OperationStatisticResult(model: mc.theClass.name);
            afterDeleteStatistics.start();
            EventTriggeringUtils.getInstance().triggerEvent(domainObject, EventTriggeringUtils.AFTER_DELETE_EVENT);
            ObjectProcessor.getInstance().repositoryChanged(EventTriggeringUtils.AFTER_DELETE_EVENT, domainObject)
            OperationStatistics.getInstance().addStatisticResult(OperationStatistics.AFTER_DELETE_OPERATION_NAME, afterDeleteStatistics);
        }
        return domainObject;
    }

}
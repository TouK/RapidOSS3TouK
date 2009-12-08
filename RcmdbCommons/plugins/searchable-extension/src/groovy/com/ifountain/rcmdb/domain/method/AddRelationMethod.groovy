package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.domain.util.RelationMetaData
import com.ifountain.rcmdb.domain.property.RelationUtils
import relation.Relation
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import com.ifountain.rcmdb.domain.util.ValidationUtils
import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.rcmdb.domain.statistics.OperationStatisticResult
import com.ifountain.rcmdb.domain.statistics.OperationStatistics
import com.ifountain.rcmdb.domain.cache.IdCacheEntry

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
class AddRelationMethod extends AbstractRapidDomainWriteMethod {
    def relations;
    public AddRelationMethod(MetaClass mcp, Map relations) {
        super(mcp); //To change body of overridden methods use File | Settings | File Templates.
        this.relations = relations;
    }

    protected Map _invoke(Object domainObject, Object[] arguments) {
        OperationStatisticResult statistics = new OperationStatisticResult(model: mc.theClass.name);
        statistics.start();
        def props = arguments[0];
        def source = arguments[1];
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(domainObject, domainObject.getClass().getName());
        domainObject.setProperty(RapidCMDBConstants.ERRORS_PROPERTY_NAME, errors, false);
        IdCacheEntry existingInstanceEntry = mc.theClass.getCacheEntry(domainObject);
        if (!existingInstanceEntry.exist())
        {
            ValidationUtils.addObjectError(domainObject.errors, "default.not.exist.message", []);
            return [domainObject:domainObject];
        }
        def relatedInstances = [:]
        props.each {key, value ->
            if (value)
            {
                RelationMetaData relation = relations.get(key);
                if (relation)
                {
                    def allRefRelationObjs = RelationUtils.getRelatedObjectsIds(domainObject, relation.name, relation.otherSideName);
                    value = value instanceof Collection ? value : [value];
                    def newRelations = [];
                    def updatedRelations = [];
                    value.each {
                        def relInfo = allRefRelationObjs.get(it.id);
                        if (relInfo == null)
                        {
                            newRelations.add(it);
                        }
                        else
                        {
                            updatedRelations.add(relInfo)
                        }
                    }
                    if (source != null)
                    {
                        RelationUtils.updateSource(updatedRelations, source);
                    }
                    value = newRelations;
                    def validValues = [];
                    value.each {relatedObject ->
                        if (!relation.otherSideCls.isInstance(relatedObject))
                        {
                            ValidationUtils.addFieldError(errors, key, relatedObject, "rapidcmdb.invalid.relation.type", [relatedObject.class.name, relation.otherSideCls.name]);
                        }
                        else if (relatedObject.id == null)
                        {
                            ValidationUtils.addFieldError(errors, key, relatedObject, "rapidcmdb.relation.with.nonpersistant.object", [relatedObject]);
                        }
                        else
                        {
                            validValues.add(relatedObject);
                        }
                    }
                    value = validValues;
                    if (value.size() > 0) {
                        if (relation.type == RelationMetaData.ONE_TO_ONE || relation.type == RelationMetaData.MANY_TO_ONE)
                        {
                            RelationUtils.removeExistingRelations(domainObject, relation.name, relation.otherSideName);
                            if (relation.otherSideName != null && relation.type == RelationMetaData.ONE_TO_ONE)
                            {
                                RelationUtils.removeExistingRelations(value[0], relation.otherSideName, relation.name);
                            }
                            value = [value[0]];

                        }
                        else if (relation.type == RelationMetaData.ONE_TO_MANY)
                        {
                            if (relation.otherSideName != null)
                            {
                                value.each {newRelatedObject ->
                                    RelationUtils.removeExistingRelations(newRelatedObject, relation.otherSideName, relation.name);
                                }
                            }

                        }
                        RelationUtils.addRelatedObjects(domainObject, relation, value, source);
                        statistics.numberOfOperations += value.size();
                    }
                }
            }
        }
        statistics.stop();
        OperationStatistics.getInstance().addStatisticResult(OperationStatistics.ADD_RELATION_OPERATION_NAME, statistics);
        return [domainObject:domainObject];
    }

    protected Object executeAfterTriggers(Map triggersMap) {
        return triggersMap?.domainObject;
    }

    public String getDirectoryLockName(Object domainObject, Object[] arguments) {
        return null;
    }


}
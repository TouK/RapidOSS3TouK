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

import com.ifountain.rcmdb.domain.IdGenerator
import com.ifountain.rcmdb.domain.ObjectProcessor
import com.ifountain.rcmdb.domain.cache.IdCacheEntry
import com.ifountain.rcmdb.domain.statistics.OperationStatisticResult
import com.ifountain.rcmdb.domain.statistics.OperationStatistics
import com.ifountain.rcmdb.domain.util.RelationMetaData
import com.ifountain.rcmdb.domain.util.ValidationUtils
import com.ifountain.rcmdb.domain.validator.DomainClassValidationWrapper
import com.ifountain.rcmdb.domain.validator.IRapidValidator
import com.ifountain.rcmdb.util.RapidCMDBConstants
import org.apache.log4j.Logger
import org.springframework.validation.BeanPropertyBindingResult

class AddMethod extends AbstractRapidDomainWriteMethod
{
    Logger logger = Logger.getLogger(AddMethod)
    def relations;
    GetMethod getMethod
    def fieldTypes = [:]
    def defaultValues = [:]
    IRapidValidator validator;
    Class rootDomainClass;
    List keys;
    boolean willReturnErrorIfExist = false;
    public AddMethod(MetaClass mcp, Class rootDomainClass, IRapidValidator validator, Map allFields, Map relations, List keys) {
        super(mcp);
        this.keys = keys;
        this.validator = validator;
        def instance = mcp.theClass.newInstance();
        allFields.each {String fieldName, field ->
            fieldTypes[fieldName] = field.type;
            defaultValues[fieldName] = instance[fieldName];
        }
        this.relations = relations
        this.rootDomainClass = rootDomainClass;
        getMethod = new GetMethod(mc, keys, relations);
    }

    public String getLockName(Object clazz, Object[] args) {
        if (keys.isEmpty()) return null;
        Map params = args[0];
        StringBuffer bf = new StringBuffer(rootDomainClass.name);
        keys.each {keyPropName ->
            bf.append(params[keyPropName]);
        }
        return bf.toString();
    }



    protected Map _invoke(Object clazz, Object[] arguments) {
        def triggersMap = [shouldCallAfterTriggers: true];
        OperationStatisticResult statistics = new OperationStatisticResult(model: mc.theClass.name);
        statistics.start();
        def props = arguments[0];
        props.remove(RapidCMDBConstants.ID_PROPERTY_GSTRING);
        props.remove(RapidCMDBConstants.ID_PROPERTY_STRING);
        IdCacheEntry existingInstanceEntry = clazz.getCacheEntry(props); //
        def instanceOfError = false;
        if (!willReturnErrorIfExist && existingInstanceEntry.exist)
        {
            if (clazz.isAssignableFrom(existingInstanceEntry.alias))
            {
                def existingInstance = getMethod.invoke(clazz, [props, false] as Object[]);
                if (existingInstance == null)
                {
                    def idCacheEntry = clazz.getCacheEntry(props);
                    logger.error("There is a mismatch between IdCache and repository. \nRepository instance:${existingInstance}\nSearch Props:${props} \nSearch class:${clazz} \nIdCacheEntryExist:${idCacheEntry ? idCacheEntry.exist : "<null>"}");
                }
                triggersMap.shouldCallAfterTriggers = false;
                triggersMap.domainObject = existingInstance.update(props);
                return triggersMap;
            }
            else
            {
                instanceOfError = true;
            }
        }

        def sampleBean = clazz.newInstance()
        triggersMap.domainObject = sampleBean;
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(sampleBean, sampleBean.getClass().getName());
        def relatedInstances = [:];
        def addedprops = [:]
        def nullProps = [];
        props.each {propName, value ->
            RelationMetaData relation = relations.get(propName);
            if (!relation)
            {
                def fieldType = fieldTypes[propName];
                if (fieldType)
                {
                    if (value == null)
                    {
                        nullProps.add(propName);
                    }
                    MethodUtils.convertAndSetDomainObjectProperty(errors, sampleBean, propName, fieldType, value);
                }
            }
            else
            {
                def relationMetaData = relations[propName];
                relatedInstances[propName] = value;
                addedprops[propName] = ValidationUtils.getValidationRelationValue(value, relationMetaData);
            }
        }
        if (willReturnErrorIfExist && existingInstanceEntry.exist)
        {
            ValidationUtils.addObjectError(errors, "rapidcmdb.instance.already.exist", [existingInstanceEntry.id]);
        }
        else if (instanceOfError)
        {
            ValidationUtils.addObjectError(errors, "rapidcmdb.invalid.instanceof.existing", [rootDomainClass.name, existingInstanceEntry.class.name]);
        }

        if (errors.hasErrors()) {
            sampleBean.setProperty(RapidCMDBConstants.ERRORS_PROPERTY_NAME, errors, false);
            return triggersMap;
        }
        statistics.stop();
        OperationStatisticResult beforeInsertStatistics = new OperationStatisticResult(model: mc.theClass.name);
        beforeInsertStatistics.start();
        def updatedPropsFromBeforeInsert = EventTriggeringUtils.triggerEvent(sampleBean, EventTriggeringUtils.BEFORE_INSERT_EVENT);
        OperationStatistics.getInstance().addStatisticResult(OperationStatistics.BEFORE_INSERT_OPERATION_NAME, beforeInsertStatistics);
        statistics.start();
        validator.validate(new DomainClassValidationWrapper(sampleBean, addedprops), sampleBean, errors)

        if (errors.hasErrors())
        {
            sampleBean.setProperty(RapidCMDBConstants.ERRORS_PROPERTY_NAME, errors, false);
        }
        else
        {
            sampleBean.setProperty("id", IdGenerator.getInstance().getNextId(), false);
            nullProps.each {propName ->
                if (sampleBean.getProperty(propName) == null)
                {
                    sampleBean.setProperty(propName, defaultValues[propName], false);
                }
            }
            updatedPropsFromBeforeInsert.each {propName, oldPropValue ->
                if (sampleBean.getProperty(propName) == null)
                {
                    sampleBean.setProperty(propName, defaultValues[propName], false);
                }
            }

            Long insertedAt=System.currentTimeMillis();
            sampleBean.setProperty(RapidCMDBConstants.INSERTED_AT_PROPERTY_NAME, insertedAt, false);
            sampleBean.setProperty(RapidCMDBConstants.UPDATED_AT_PROPERTY_NAME, insertedAt, false);
            
            CompassMethodInvoker.index(mc, sampleBean);
            sampleBean.updateCacheEntry(sampleBean, true);
            OperationStatistics.getInstance().addStatisticResult(OperationStatistics.ADD_OPERATION_NAME, statistics);
            if (!relatedInstances.isEmpty())
            {
                sampleBean.addRelation(relatedInstances);
            }
        }
        return triggersMap;
    }

    protected Object executeAfterTriggers(Map triggersMap) {
        def domainObject = triggersMap.domainObject 
        if (!domainObject.hasErrors() && triggersMap.shouldCallAfterTriggers) {
            OperationStatisticResult afterInsertStatistics = new OperationStatisticResult(model: mc.theClass.name);
            afterInsertStatistics.start();
            EventTriggeringUtils.triggerEvent(domainObject, EventTriggeringUtils.AFTER_INSERT_EVENT);
            EventTriggeringUtils.triggerEvent(domainObject, EventTriggeringUtils.ONLOAD_EVENT);
            ObjectProcessor.getInstance().repositoryChanged(EventTriggeringUtils.AFTER_INSERT_EVENT, domainObject);
            OperationStatistics.getInstance().addStatisticResult(OperationStatistics.AFTER_INSERT_OPERATION_NAME, afterInsertStatistics);
        }
        return domainObject;
    }
}

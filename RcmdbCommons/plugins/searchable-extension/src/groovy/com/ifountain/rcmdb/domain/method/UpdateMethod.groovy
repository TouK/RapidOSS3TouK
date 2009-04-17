package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.converter.RapidConvertUtils
import com.ifountain.rcmdb.domain.util.ValidationUtils
import org.apache.commons.beanutils.ConversionException
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import org.springframework.validation.Validator
import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.rcmdb.domain.statistics.OperationStatistics
import com.ifountain.rcmdb.domain.statistics.OperationStatisticResult
import com.ifountain.rcmdb.domain.validator.IRapidValidator
import com.ifountain.rcmdb.domain.validator.DomainClassValidationWrapper

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
 * Time: 2:06:19 PM
 * To change this template use File | Settings | File Templates.
 */
class UpdateMethod extends AbstractRapidDomainWriteMethod{
    public static final String UPDATED_PROPERTIES = "updatedProps"
    def relations;
    def fieldTypes = [:]
    IRapidValidator validator;
    public UpdateMethod(MetaClass mcp, IRapidValidator  validator, Map allFields, Map relations) {
        super(mcp); //To change body of overridden methods use File | Settings | File Templates.
        this.validator = validator;
        allFields.each{fieldName, field->
            fieldTypes[fieldName] = field.type;
        }
        this.relations = relations;
    }

    protected Object _invoke(Object domainObject, Object[] arguments) {
        OperationStatisticResult statistics = new OperationStatisticResult(model:mc.theClass.name);
        statistics.start();
        def props = arguments[0];
        props.remove(RapidCMDBConstants.ID_PROPERTY_GSTRING);
        props.remove(RapidCMDBConstants.ID_PROPERTY_STRING);
        def relationToBeAddedMap = [:]
        def updatedPropsOldValues = [:];
        def updatedRelations = [:];
        def relationToBeRemovedMap = [:]
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(domainObject, domainObject.getClass().getName());
        domainObject.setProperty(RapidCMDBConstants.ERRORS_PROPERTY_NAME, errors, false);
        boolean willBeIndexed = false;
        boolean willRelationsBeIndexed = false;
        props.each{propName,value->
            if(!relations.containsKey(propName))
            {
                def fieldType = fieldTypes[propName];
                if(fieldType)
                {
                    def propValueBeforeUpdate = domainObject.getProperty(propName);
                    updatedPropsOldValues[propName] = propValueBeforeUpdate;
                    MethodUtils.convertAndSetDomainObjectProperty(errors, domainObject, propName, fieldType, value);
                    if(domainObject.getProperty(propName) != propValueBeforeUpdate)
                    {
                        willBeIndexed = true;
                    }
                }
            }
            else
            {
                def currentRelatedObjects = domainObject[propName];
                updatedPropsOldValues[propName] = currentRelatedObjects;
                if(currentRelatedObjects)
                {
                    relationToBeRemovedMap[propName] = currentRelatedObjects;
                }
                if(value)
                {
                    relationToBeAddedMap[propName] = value;
                }
                def relationMetaData = relations[propName];
                updatedRelations[propName] = ValidationUtils.getValidationRelationValue(value, relationMetaData);
                willRelationsBeIndexed = true;
            }
        }
        if(willBeIndexed || willRelationsBeIndexed)
        {
            def triggeredEventParams = [:];
            triggeredEventParams[UPDATED_PROPERTIES] = updatedPropsOldValues;
            EventTriggeringUtils.triggerEvent (domainObject, EventTriggeringUtils.BEFORE_UPDATE_EVENT, triggeredEventParams);
            if(!errors.hasErrors())
            {
                validator.validate (new DomainClassValidationWrapper(domainObject, updatedRelations), domainObject, errors)
            }
            if(!errors.hasErrors())
            {
                if(willBeIndexed)
                {
                    domainObject.index(domainObject);
                }
                statistics.stop();
                domainObject.removeRelation(relationToBeRemovedMap);
                domainObject.addRelation(relationToBeAddedMap);
                statistics.start();
                EventTriggeringUtils.triggerEvent (domainObject, EventTriggeringUtils.AFTER_UPDATE_EVENT, triggeredEventParams);
                EventTriggeringUtils.triggerEvent (domainObject, EventTriggeringUtils.ONLOAD_EVENT);
                OperationStatistics.getInstance().addStatisticResult (OperationStatistics.UPDATE_OPERATION_NAME, statistics);
            }
        }
        return domainObject;
    }


}
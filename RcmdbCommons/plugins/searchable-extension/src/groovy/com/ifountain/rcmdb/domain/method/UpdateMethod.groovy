package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.domain.converter.RapidConvertUtils
import com.ifountain.rcmdb.domain.util.ValidationUtils
import org.apache.commons.beanutils.ConversionException
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import org.springframework.validation.Validator
import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.rcmdb.domain.statistics.OperationStatistics
import com.ifountain.rcmdb.domain.statistics.OperationStatisticResult

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
class UpdateMethod extends AbstractRapidDomainMethod{

    def relations;
    def fieldTypes = [:]
    Validator validator;
    public UpdateMethod(MetaClass mc, Validator validator, Map allFields, Map relations) {
        super(mc); //To change body of overridden methods use File | Settings | File Templates.
        this.validator = validator;
        allFields.each{fieldName, field->
            fieldTypes[fieldName] = field.type;
        }
        this.relations = relations;
    }

    public boolean isWriteOperation() {
        return true;
    }

    protected Object _invoke(Object domainObject, Object[] arguments) {
        OperationStatisticResult statistics = new OperationStatisticResult(model:mc.theClass.name);
        statistics.start();
        def props = arguments[0];
        props.remove("id");
        def relationToBeAddedMap = [:]
        def relationToBeRemovedMap = [:]
        Errors errors = new BeanPropertyBindingResult(domainObject, domainObject.getClass().getName());
        props.each{key,value->
            if(!relations.containsKey(key))
            {
                def fieldType = fieldTypes[key];
                if(fieldType)
                {
                    if(value != null)
                    {
                        try
                        {
                            def converter = RapidConvertUtils.getInstance().lookup (fieldType);
                            domainObject.setProperty (key, converter.convert(fieldType, value), false);
                        }
                        catch(ConversionException exception)
                        {
                            ValidationUtils.addFieldError (errors, key, value, "rapidcmdb.invalid.property.type", [key, fieldType.name, value.class.name]);
                        }
                    }
                    else
                    {
                        domainObject.setProperty (key, value, false);
                    }
                }
            }
            else
            {
                if(value)
                {
                    relationToBeAddedMap[key] = value;
                }
                else
                {
                    def currentRelatedObjects = domainObject[key];
                    if(currentRelatedObjects)
                    {
                        relationToBeRemovedMap[key] = currentRelatedObjects;
                    }
                }
            }
        }

        if(!errors.hasErrors())
        {
            validator.validate (ValidationUtils.createValidationBean(domainObject, props, relations, fieldTypes, true), errors)
        }
        if(!errors.hasErrors())
        {
            EventTriggeringUtils.triggerEvent (domainObject, EventTriggeringUtils.BEFORE_UPDATE_EVENT);
            domainObject.reindex(domainObject);
            statistics.stop();
            domainObject.removeRelation(relationToBeRemovedMap);
            domainObject.addRelation(relationToBeAddedMap);
            statistics.start();
            EventTriggeringUtils.triggerEvent (domainObject, EventTriggeringUtils.ONLOAD_EVENT);
            OperationStatistics.getInstance().addStatisticResult (OperationStatistics.UPDATE_OPERATION_NAME, statistics);
        }
        else
        {
            domainObject.setProperty(RapidCMDBConstants.ERRORS_PROPERTY_NAME, errors, false);
        }
        return domainObject;
    }


}
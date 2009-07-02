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
import com.ifountain.rcmdb.converter.RapidConvertUtils
import com.ifountain.rcmdb.domain.util.RelationMetaData
import com.ifountain.rcmdb.domain.util.ValidationUtils
import org.apache.commons.beanutils.ConversionException
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import org.springframework.validation.Validator
import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.rcmdb.domain.util.RelationMetaData
import com.ifountain.rcmdb.domain.statistics.OperationStatisticResult
import com.ifountain.rcmdb.domain.statistics.OperationStatistics
import com.ifountain.rcmdb.domain.validator.DomainClassValidationWrapper
import com.ifountain.rcmdb.domain.validator.IRapidValidator
import com.ifountain.rcmdb.domain.ObjectProcessor
import com.ifountain.rcmdb.domain.cache.IdCacheEntry

class AddMethod extends AbstractRapidDomainWriteMethod
{
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
        def instance = mcp.theClass.newInstance ();
        allFields.each{String fieldName, field->
            fieldTypes[fieldName] = field.type;
            defaultValues[fieldName] = instance[fieldName];
        }
        this.relations = relations
        this.rootDomainClass=rootDomainClass;
        getMethod = new GetMethod(mc, keys, relations);
    }

    public String getLockName(Object clazz, Object[] args) {
        if(keys.isEmpty()) return null;
        Map params = args[0];
        StringBuffer bf = new StringBuffer(rootDomainClass.name);
        keys.each{keyPropName->
            bf.append(params[keyPropName]);
        }
        return bf.toString();
    }



    protected Object _invoke(Object clazz, Object[] arguments) {
        OperationStatisticResult statistics = new OperationStatisticResult(model:mc.theClass.name);
        statistics.start();
        def props = arguments[0];
        props.remove(RapidCMDBConstants.ID_PROPERTY_GSTRING);
        props.remove(RapidCMDBConstants.ID_PROPERTY_STRING);
        IdCacheEntry existingInstanceEntry = clazz.getCacheEntry(props);//
        def instanceOfError = false;
        if(!willReturnErrorIfExist && existingInstanceEntry.exist)
        {
            if(clazz.isAssignableFrom(existingInstanceEntry.alias) )
            {
                def existingInstance = getMethod.invoke(clazz, [props, false] as Object[]);
                return existingInstance.update(props);
            }
            else
            {
                instanceOfError = true;
            }
        }

        def sampleBean = clazz.newInstance()
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(sampleBean, sampleBean.getClass().getName());
        def relatedInstances = [:];
        def addedprops = [:]
        def nullProps = [];
        props.each{propName,value->
            RelationMetaData relation = relations.get(propName);
            if(!relation)
            {
                def fieldType = fieldTypes[propName];
                if(fieldType)
                {
                    if(value == null)
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
        if(willReturnErrorIfExist && existingInstanceEntry.exist)
        {
            ValidationUtils.addObjectError(errors, "rapidcmdb.instance.already.exist", [existingInstanceEntry.id]);
        }
        else if(instanceOfError)
        {
            ValidationUtils.addObjectError(errors, "rapidcmdb.invalid.instanceof.existing", [rootDomainClass.name,existingInstanceEntry.class.name]);
        }

        if(errors.hasErrors()){
            sampleBean.setProperty(RapidCMDBConstants.ERRORS_PROPERTY_NAME, errors, false);
            return sampleBean;
        }
        EventTriggeringUtils.triggerEvent (sampleBean, EventTriggeringUtils.BEFORE_INSERT_EVENT);
        validator.validate (new DomainClassValidationWrapper(sampleBean, addedprops), sampleBean, errors)

        if(errors. hasErrors())
        {
            sampleBean.setProperty(RapidCMDBConstants.ERRORS_PROPERTY_NAME, errors, false);
        }
        else
        {
            sampleBean.setProperty("id", IdGenerator.getInstance().getNextId(), false);
            nullProps.each{propName->
                sampleBean.setProperty (propName, defaultValues[propName], false);                
            }
            CompassMethodInvoker.index (mc, sampleBean);
            sampleBean.updateCacheEntry(sampleBean, true);
            if(!relatedInstances.isEmpty())
            {
                statistics.stop();
                sampleBean.addRelation(relatedInstances);
                statistics.start();
            }
            EventTriggeringUtils.triggerEvent (sampleBean, EventTriggeringUtils.AFTER_INSERT_EVENT);
            EventTriggeringUtils.triggerEvent (sampleBean, EventTriggeringUtils.ONLOAD_EVENT);
            ObjectProcessor.getInstance().repositoryChanged(EventTriggeringUtils.AFTER_INSERT_EVENT, sampleBean);
            statistics.stop();
            OperationStatistics.getInstance().addStatisticResult (OperationStatistics.ADD_OPERATION_NAME, statistics);
        }
        return sampleBean;
    }

}

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
import com.ifountain.rcmdb.domain.converter.RapidConvertUtils
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

class AddMethod extends AbstractRapidDomainStaticMethod
{
    def relations;
    GetMethod getMethod
    def fieldTypes = [:]
    Validator validator;
    Class rootDomainClass;
    List keys;
    public AddMethod(MetaClass mc, Class rootDomainClass, Validator validator, Map allFields, Map relations, List keys) {
        super(mc);
        this.keys = keys;
        this.validator = validator;
        allFields.each{String fieldName, field->
            fieldTypes[fieldName] = field.type;
        }
        this.relations = relations
        this.rootDomainClass=rootDomainClass;
        getMethod = new GetMethod(mc, keys, relations);
    }

    public boolean isWriteOperation() {
        return true;
    }

    public String getLockName(Object[] args) {
        if(keys.isEmpty()) return null;
        Map params = args[0];
        StringBuffer bf = new StringBuffer(rootDomainClass.name);
        keys.each{keyPropName->
            bf.append(params[keyPropName]);
        }
        return bf.toString();
    }



    protected Object _invoke(Class clazz, Object[] arguments) {
        OperationStatisticResult statistics = new OperationStatisticResult(model:mc.theClass.name);
        statistics.start();
        def props = arguments[0];
        props.remove("id");
        def existingInstance = getMethod.invoke(rootDomainClass, [props, false] as Object[])
        def instanceOfError = false;
        if(existingInstance != null)
        {
            if(clazz.isInstance(existingInstance) )
            {
                return existingInstance.update(props);
            }
            else
            {
                instanceOfError = true;
            }
        }

        def sampleBean = clazz.newInstance()
        Errors errors = new BeanPropertyBindingResult(sampleBean, sampleBean.getClass().getName());
        def relatedInstances = [:];

        props.each{key,value->
            RelationMetaData relation = relations.get(key);
            if(!relation)
            {
                def fieldType = fieldTypes[key];
                if(fieldType)
                {

                    if(value != null)
                    {
                        try
                        {
                            def converter = RapidConvertUtils.getInstance().lookup (fieldType);
                            def propVal = converter.convert (fieldType, value);
                            sampleBean.setProperty(key, propVal, false);
                        }
                        catch(ConversionException exception)
                        {
                            ValidationUtils.addFieldError (errors, key, value, "rapidcmdb.invalid.property.type", [key, fieldType.name, value.class.name]);
                        }
                    }
                    else
                    {
                        sampleBean.setProperty(key, value, false);
                    }
                }
            }
            else
            {
                relatedInstances[key] = value;
            }
        }
        if(instanceOfError)
        {
            ValidationUtils.addObjectError(errors, "rapidcmdb.invalid.instanceof.existing", [rootDomainClass.name,existingInstance.class.name]);
        }
        if(errors.hasErrors()){
            sampleBean.setProperty(RapidCMDBConstants.ERRORS_PROPERTY_NAME, errors, false);
            return sampleBean;
        }
        validator.validate (ValidationUtils.createValidationBean(sampleBean, props, relations, fieldTypes), errors)

        if(errors. hasErrors())
        {
            sampleBean.setProperty(RapidCMDBConstants.ERRORS_PROPERTY_NAME, errors, false);
        }
        else
        {
            sampleBean.setProperty("id", IdGenerator.getInstance().getNextId(), false);
            EventTriggeringUtils.triggerEvent (sampleBean, EventTriggeringUtils.BEFORE_INSERT_EVENT);
            CompassMethodInvoker.index (mc, sampleBean);
            if(!relatedInstances.isEmpty())
            {
                statistics.stop();
                sampleBean.addRelation(relatedInstances);
                statistics.start();
            }
            EventTriggeringUtils.triggerEvent (sampleBean, EventTriggeringUtils.ONLOAD_EVENT);
            statistics.stop();
            OperationStatistics.getInstance().addStatisticResult (OperationStatistics.ADD_OPERATION_NAME, statistics);
        }
        return sampleBean;
    }

}

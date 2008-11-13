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
    public AddMethod(MetaClass mc, Class rootDomainClass, Validator validator, Map allFields, Map relations, List keys) {
        super(mc);
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
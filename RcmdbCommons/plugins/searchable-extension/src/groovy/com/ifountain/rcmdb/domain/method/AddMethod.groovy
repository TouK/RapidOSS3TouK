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

class AddMethod extends AbstractRapidDomainStaticMethod
{
    def relations;
    GetMethod getMethod
    def fieldTypes = [:]
    Validator validator;
    public AddMethod(MetaClass mc, Class rootDomainClass, Validator validator, Map allFields, Map relations, List keys) {
        super(mc);
        this.validator = validator;
        allFields.each{String fieldName, field->
            fieldTypes[fieldName] = field.type;
        }
        this.relations = relations
        getMethod = new GetMethod(mc, rootDomainClass, keys, relations);
    }

    public boolean isWriteOperation() {
        return true;
    }



    protected Object _invoke(Class clazz, Object[] arguments) {
        def props = arguments[0];
        props.remove("id");
        def existingInstance = getMethod.invoke(clazz, [props, false] as Object[])
        if(existingInstance != null)
        {
            return existingInstance.update(props);
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
                            ValidationUtils.addFieldError (errors, key, value, "rapidcmdb.invalid.property.type", [fieldType.name, value.class.name]);
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
                sampleBean.addRelation(relatedInstances);
            }
            EventTriggeringUtils.triggerEvent (sampleBean, EventTriggeringUtils.ONLOAD_EVENT);
        }
        return sampleBean;
    }

}
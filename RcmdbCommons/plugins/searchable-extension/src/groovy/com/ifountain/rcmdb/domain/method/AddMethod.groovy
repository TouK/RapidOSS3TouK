package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.domain.IdGenerator
import com.ifountain.rcmdb.domain.converter.RapidConvertUtils
import com.ifountain.rcmdb.domain.util.Relation
import com.ifountain.rcmdb.domain.util.ValidationUtils
import org.apache.commons.beanutils.ConversionException
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import org.springframework.validation.Validator

class AddMethod extends AbstractRapidDomainStaticMethod
{
    def relations;
    GetMethod getMethod
    def fieldTypes = [:]
    Validator validator;
    public AddMethod(MetaClass mc, Validator validator, Map relations, List keys) {
        super(mc);
        this.validator = validator;
        def fields = mc.getProperties();
        fields.each{field->
            fieldTypes[field.name] = field.type;            
        }
        this.relations = relations
        getMethod = new GetMethod(mc, keys, relations);
    }

    public boolean isWriteOperation() {
        return true;
    }



    protected Object _invoke(Class clazz, Object[] arguments) {
        def props = arguments[0];
        def sampleBean;
        def existingInstance = getMethod.invoke(clazz, [props, false] as Object[])
        if(existingInstance != null)
        {
            sampleBean = existingInstance;
        }
        else
        {
             sampleBean = clazz.newInstance()
        }

        Errors errors = new BeanPropertyBindingResult(sampleBean, sampleBean.getClass().getName());
        def relatedInstances = [:];

        props.each{key,value->
            Relation relation = relations.get(key);
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
        if(relatedInstances.size() > 0)
        {
            relatedInstances = sampleBean.addRelation(relatedInstances, false);
        }
        if(errors.hasErrors()){
            sampleBean.setProperty("errors", errors, false);
            return sampleBean;
        }
        validator.validate (sampleBean, errors)

        if(errors. hasErrors())
        {
            sampleBean.setProperty("errors", errors, false);
        }
        else
        {
            if(existingInstance == null)
            {
                sampleBean.setProperty("id", IdGenerator.getInstance().getNextId(), false);
                EventTriggeringUtils.triggerEvent (sampleBean, EventTriggeringUtils.BEFORE_INSERT_EVENT);
            }
            else
            {
                EventTriggeringUtils.triggerEvent (sampleBean, EventTriggeringUtils.BEFORE_UPDATE_EVENT);
            }
            CompassMethodInvoker.index (mc, sampleBean);
            relatedInstances.each{instanceClass, instances->
                if(!instances.isEmpty())
                {
                    CompassMethodInvoker.index (instanceClass.metaClass, instances);
                }
            }

            EventTriggeringUtils.triggerEvent (sampleBean, EventTriggeringUtils.ONLOAD_EVENT);
        }
        return sampleBean;
    }

}
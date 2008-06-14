package com.ifountain.rcmdb.domain.method
import com.ifountain.rcmdb.domain.IdGenerator
import com.ifountain.rcmdb.domain.util.Relation
import com.ifountain.rcmdb.domain.converter.RapidConvertUtils
import com.ifountain.rcmdb.domain.util.ValidationUtils
import org.springframework.validation.Validator
import org.springframework.validation.Errors
import org.apache.commons.beanutils.ConversionException
import org.springframework.validation.BeanPropertyBindingResult

class AddMethod extends AbstractRapidDomainStaticMethod
{
    def relations;
    def keys;
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
        this.keys = keys;
    }

    public Object invoke(Class clazz, Object[] arguments) {
        def props = arguments[0];
        def sampleBean;
        def keysMap = [:]
        keys.each{keyPropName->
            keysMap[keyPropName] = props[keyPropName];
        }
        def existingInstances = CompassMethodInvoker.search(mc, keysMap);
        if(existingInstances.total != 0)
        {
            sampleBean = existingInstances.results[0];
        }
        else
        {
             sampleBean = clazz.newInstance()
        }

        Errors errors = new BeanPropertyBindingResult(sampleBean, sampleBean.getClass().getName());
        def relatedInstances = [:];
        def modelProperties = [:]
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
                            modelProperties[key] = propVal;
                            sampleBean[key] = propVal;
                        }
                        catch(ConversionException exception)
                        {
                            ValidationUtils.addFieldError (errors, key, value, "rapidcmdb.invalid.property.type", [fieldType.name, value.class.name]);
                        }
                    }
                    else
                    {
                        sampleBean[key] = value;                        
                    }
                }
            }
            else
            {
                relatedInstances[key] = value;
            }
        }
        if(errors.hasErrors()){
            sampleBean.errors = errors;
            return sampleBean;
        }
        validator.validate (sampleBean, errors)

        if(errors. hasErrors())
        {
            sampleBean.setProperty("errors", errors);
        }
        else
        {
            if(existingInstances.total == 0)
            {
                sampleBean["id"] = IdGenerator.getInstance().getNextId();
                EventTriggeringUtils.triggerEvent (sampleBean, EventTriggeringUtils.BEFORE_INSERT_EVENT);
            }
            else
            {
                EventTriggeringUtils.triggerEvent (sampleBean, EventTriggeringUtils.BEFORE_UPDATE_EVENT);
            }
            CompassMethodInvoker.index (mc, sampleBean);
            if(relatedInstances.size() > 0)
            {
                sampleBean.addRelation(relatedInstances);
            }
            EventTriggeringUtils.triggerEvent (sampleBean, EventTriggeringUtils.ONLOAD_EVENT);
        }
        return sampleBean;
    }

}
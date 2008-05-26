package com.ifountain.rcmdb.domain.method
import com.ifountain.rcmdb.domain.IdGenerator
import com.ifountain.rcmdb.domain.util.Relation
import com.ifountain.rcmdb.domain.converter.RapidConvertUtils
import com.ifountain.rcmdb.domain.util.ValidationUtils
import org.springframework.validation.Validator
import org.springframework.validation.Errors
import org.springframework.validation.BeanPropertyBindingResult;
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
        def sampleBean = clazz.newInstance();
        def relatedInstances = [:];
        def modelProperties = [:]
        props.each{key,value->
            Relation relation = relations.get(key);
            if(!relation)
            {
                def fieldType = fieldTypes[key];
                if(fieldType)
                {

                    def converter = RapidConvertUtils.getInstance().lookup (fieldType);
                    def propVal = converter.convert (fieldType, value);
                    modelProperties[key] = propVal;
                    sampleBean[key] = propVal;
                }
            }
            else
            {
                relatedInstances[key] = value;
            }
        }
        Errors errors = ValidationUtils.validate (validator, sampleBean);

        if(errors. hasErrors())
        {
            sampleBean.setProperty("errors", errors);
        }
        else
        {
            def keysMap = [:]
            keys.each{keyPropName->
                keysMap[keyPropName] = props[keyPropName];
            }
            def existingInstances = CompassMethodInvoker.search(mc, keysMap);
            if(existingInstances.total != 0)
            {
                sampleBean = existingInstances.results[0] ;
                modelProperties.each{propName, propVal->
                    sampleBean[propName] = propVal;
                }
            }
            else
            {
                sampleBean["id"] = IdGenerator.getInstance().getNextId();
            }
            CompassMethodInvoker.index (mc, sampleBean);
            if(relatedInstances.size() > 0)
            {
                sampleBean.addRelation(relatedInstances);
            }
        }
        return sampleBean;
    }

}
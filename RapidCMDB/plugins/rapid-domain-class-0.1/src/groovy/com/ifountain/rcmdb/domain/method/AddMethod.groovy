package com.ifountain.rcmdb.domain.method
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import com.ifountain.rcmdb.domain.IdGenerator
import com.ifountain.rcmdb.domain.util.Relation
import org.apache.commons.beanutils.ConvertUtils
import java.lang.reflect.Field
import com.ifountain.rcmdb.domain.converter.RapidConvertUtils;
class AddMethod extends AbstractRapidDomainStaticMethod
{
    def relations;
    def keys;
    def fieldTypes = [:]
    public AddMethod(MetaClass mc, Map relations, List keys) {
        super(mc);
        def fields = mc.theClass.declaredFields;
        fields.each{field->
            fieldTypes[field.name] = field.type;            
        }
        this.relations = relations
        this.keys = keys;
    }

    public Object invoke(Class clazz, Object[] arguments) {
        def props = arguments[0];
        def keysMap = [:]
        keys.each
        {
            keysMap[it] = props[it];     
        }
        def sampleBean = clazz.newInstance();
        def existingInstances = CompassMethodInvoker.search(mc, keysMap);
        if(existingInstances.total != 0)
        {
            sampleBean = existingInstances.results[0] ;
        }
        else
        {
            sampleBean["id"] = IdGenerator.getInstance().getNextId();
        }
        def relatedInstances = [:];
        props.each{key,value->
            Relation relation = relations.get(key);
            if(!relation)
            {
                def fieldType = fieldTypes[key];
                if(fieldType)
                {
                    
                    def converter = RapidConvertUtils.getInstance().lookup (fieldType);
                    sampleBean[key] = converter.convert (fieldType, value);
                }
            }
            else
            {
                relatedInstances[key] = value;
            }
        }
        CompassMethodInvoker.index (mc, sampleBean);
        if(relatedInstances.size() > 0)
        {
            sampleBean.addRelation(relatedInstances);
        }
        return sampleBean;
    }

}
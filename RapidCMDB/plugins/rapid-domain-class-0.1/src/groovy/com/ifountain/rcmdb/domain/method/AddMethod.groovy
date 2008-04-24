package com.ifountain.rcmdb.domain.method
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import com.ifountain.rcmdb.domain.util.DomainClassUtils;
class AddMethod extends AbstractRapidDomainStaticMethod
{
    def relations;
    public AddMethod(MetaClass mc, GrailsDomainClass domainClass) {
        super(mc, domainClass);
         relations = DomainClassUtils.getRelations(domainClass)
    }

    public Object invoke(Class clazz, Object[] arguments) {
        def props = arguments[0];
        def flush = arguments[1];
        def existingInstance = mc.invokeStaticMethod(mc.theClass, "get", [props] as Object[]);
        if(!existingInstance)
        {
            def sampleBean = clazz.newInstance();
            def relationMap = [:]
            props.each{key,value->
                def metaProp = mc.getMetaProperty(key);
                if(metaProp)
                {
                    if(!relations.containsKey(key))
                    {
                        def realValue = DomainClassUtils.getPropertyRealValue(metaProp.type, value);
                        sampleBean.setProperty (key, realValue);
                    }
                    else
                    {
                        relationMap[key] = value;
                    }
                }
            }
            def returnedBean = sampleBean.save(flush:flush);
            if(returnedBean && !relationMap.isEmpty())
            {
                returnedBean.addRelation(relationMap, false);
                returnedBean = returnedBean.save(flush:flush);
            }
            if(!returnedBean)
            {
                return sampleBean;
            }
            else
            {
                if(mc.hasProperty(returnedBean, "onLoad"))
                {
                    returnedBean.onLoad();
                }
                return returnedBean;
            }
        }
        else
        {
            return existingInstance.update(props);
        }
    }

}
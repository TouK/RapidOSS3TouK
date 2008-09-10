package com.ifountain.rcmdb.domain.method

import org.compass.core.CompassHit
import com.ifountain.rcmdb.domain.converter.RapidConvertUtils

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 10, 2008
 * Time: 4:35:20 PM
 * To change this template use File | Settings | File Templates.
 */
class PropertySummaryMethod extends AbstractRapidDomainStaticMethod{

    public PropertySummaryMethod(MetaClass mc) {
        super(mc);    //To change body of overridden methods use File | Settings | File Templates.
    }


    public boolean isWriteOperation() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected Object _invoke(Class clazz, Object[] arguments) {
        if(arguments.length != 2) return [:];
        String query = String.valueOf(arguments[0])
        def propertyList = arguments[1]
        propertyList = propertyList instanceof Collection?propertyList:[propertyList]
        Map summary = [:]
        propertyList.each{String propName->
            summary[propName] = new PropertySummaryMapWrapper();
        }
        def rawDataProcessorClosure = {hits->
            hits.each{CompassHit hit->
                propertyList.each{String propName->
                    def prop = hit.getResource().getProperty(propName);
                    def value ;
                    if(prop == null)
                    {
                        value = null;
                    }
                    else if(prop.getObjectValue() == null)
                    {
                        value = "";
                    }
                    else
                    {
                        value = prop.getObjectValue();
                    }
                    if(summary[propName][value] == null)
                    {
                        summary[propName][value] = 0;
                    }
                    summary[propName][value]++;
                }
            }
        }
        CompassMethodInvoker.searchEvery(mc, query, [raw:rawDataProcessorClosure])
        return summary;  //To change body of implemented methods use File | Settings | File Templates.
    }


}

class PropertySummaryMapWrapper extends LinkedHashMap{
    Class propType;
    public Object get(Object key) {
        if(key != null && propType != String.class && propType != null)
        {
            return super.get(RapidConvertUtils.getInstance().lookup(propType).convert(propType, key));
        }
        return super.get(key);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public Object put(Object key, Object value) {
        if(propType == null && key != null)
        {
            propType = key.class;
        }
        return super.put(key, value);    //To change body of overridden methods use File | Settings | File Templates.
    }


}
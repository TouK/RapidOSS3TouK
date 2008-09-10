package com.ifountain.rcmdb.domain.method

import org.compass.core.CompassHit

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
            summary[propName] = [:]
        }
        def rawDataProcessorClosure = {hits->
            hits.each{CompassHit hit->
                propertyList.each{String propName->
                    def prop = hit.getResource().getProperty(propName);
                    def value = prop?prop.getStringValue():"null";
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
package com.ifountain.rcmdb.domain.method

import org.compass.core.Resource
import org.compass.core.CompassHit

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Nov 14, 2008
 * Time: 9:46:22 AM
 * To change this template use File | Settings | File Templates.
 */
class GetPropertyValuesMethod extends AbstractRapidDomainStaticMethod {

    Map relations;
    public GetPropertyValuesMethod(MetaClass mc, Map relations) {
        super(mc);    //To change body of overridden methods use File | Settings | File Templates.
        this.relations = relations;
    }

    public boolean isWriteOperation() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected Object _invoke(Class clazz, Object[] arguments) {
        String query = arguments[0]
        List propertyList = arguments[1]
        Map options = arguments[2];
        def results = [];
        def raw = {compassHits, session->
            compassHits.each{CompassHit hit->

                Resource res = hit.getResource();
                def propMap = [alias:res.getAlias(), id:res.getObject("id")];
                results.add(propMap);
                propertyList.each{String propName->

                    def prop = res.getProperty(propName);
                    if(prop != null)
                    {
                      propMap[propName] = prop.getObjectValue();
                    }
                }
            }
        }
        options["raw"] = raw;
        clazz.'searchEvery'(query, options);
        return results;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
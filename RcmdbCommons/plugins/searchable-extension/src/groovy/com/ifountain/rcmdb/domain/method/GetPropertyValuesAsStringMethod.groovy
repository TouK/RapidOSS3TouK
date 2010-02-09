package com.ifountain.rcmdb.domain.method

import org.compass.core.CompassHit
import org.compass.core.Resource
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.apache.commons.collections.MapUtils
import com.ifountain.compass.converter.CompassStringConverter
import com.ifountain.rcmdb.domain.statistics.OperationStatisticResult
import com.ifountain.rcmdb.domain.statistics.OperationStatistics

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Nov 10, 2009
* Time: 4:37:18 PM
* To change this template use File | Settings | File Templates.
*/
class GetPropertyValuesAsStringMethod extends AbstractRapidDomainReadMethod {
    def propertyNamesMap = [:];
    public GetPropertyValuesAsStringMethod(MetaClass mc) {
        super(mc);
    }

    public Object _invoke(Object clazz, Object[] arguments) {
        OperationStatisticResult statistics = new OperationStatisticResult(model:clazz.name);
        statistics.start();

        def query = arguments[0];
        def params = arguments[1]
        if(params == null)
        {
            params = [:]
        }
        def results = [];
        def propertyList = params.propertyList;
        def hitProcessingClosure = {CompassHit hit->
            Resource res = hit.getResource();
            def alias = res.getAlias();
            def propNames = propertyList;
            if(propNames == null)
            {
                propNames = getPropertyNames(alias);
            }
            def props = [alias:alias];
            propNames.each{propName->
                def value = res.getValue(propName);
                if(value != null)
                {
                    if(value == CompassStringConverter.EMPTY_VALUE)
                    {
                        value = ""
                    }
                    props[propName] = value;
                }
            }
            results.add(props);
        }
        def total = 0;
        def offset = MapUtils.getIntValue(params, "offset");
        params.raw = {compassHits, session ->
            total = compassHits.length();
            MethodUtils.getCompassHitsSubset(compassHits, params, hitProcessingClosure);
        }
        clazz.search(query, params);

        statistics.stop();
        OperationStatistics.getInstance().addStatisticResult (OperationStatistics.SEARCH_AS_STRING_OPERATION_NAME, statistics);
        OperationStatistics.getInstance().addStatisticResult (OperationStatistics.SEARCH_AS_STRING_OPERATION_NAME, statistics.getSubStatisticsWithObjectCount(results?.size()));

        return [total:total, offset:offset, results:results];
    }

    private synchronized List getPropertyNames(String className)
    {
        def propNames = propertyNamesMap[className]
        if(propNames == null)
        {
            def domainClass = ApplicationHolder.application.getDomainClass(className).clazz
            propNames = domainClass.getNonFederatedPropertyList().findAll {!it.isRelation}.name
            propNames.add("id")
            propertyNamesMap[className] = propNames;
        }
        return propNames;
    }
    
    
}
/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created on Feb 22, 2008
 *
 * Author Sezgin Kucukkaraaslan
 */
package com.ifountain.smarts.datasource.queries;

import com.ifountain.smarts.datasource.BaseSmartsAdapter;
import org.apache.log4j.Logger;

import java.util.List;


public class QueryFactory {

    private static IQuery query;
    
    public static IQuery getFindTopologyInstancesQuery(Logger logger, BaseSmartsAdapter smartsAdapter, String creationClassName,
            String name, List<String> attributes, int fetchSize, boolean expEnabled)
    {
        if(query != null && query instanceof FindTopologyInstancesQuery)
            return query;
        return new FindTopologyInstancesQuery(logger, smartsAdapter, creationClassName,name, attributes, fetchSize, expEnabled);
    }

    public static IQuery getGetPropertiesQuery(Logger logger,BaseSmartsAdapter smartsAdapter, String className,
            String instanceName, List<String> attributes) {
        if(query != null && query instanceof GetPropertiesQuery)
            return query;
        return new GetPropertiesQuery(logger, smartsAdapter, className, instanceName, attributes);
    }
}

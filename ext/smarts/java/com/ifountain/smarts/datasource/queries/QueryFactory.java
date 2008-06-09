/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be 
 * noted in a separate copyright notice. All rights reserved.
 * This file is part of RapidCMDB.
 * 
 * RapidCMDB is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
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

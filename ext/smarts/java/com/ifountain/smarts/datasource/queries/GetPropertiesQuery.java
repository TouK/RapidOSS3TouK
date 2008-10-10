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
import com.ifountain.smarts.util.property.MRArrayToMap;
import com.smarts.remote.SmRemoteException;
import com.smarts.repos.MR_AnyVal;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GetPropertiesQuery implements IQuery {

    private Logger logger;
    private BaseSmartsAdapter smartsAdapter;
    private String className;
    private String instanceName;
    private List<String> attributes;

    public GetPropertiesQuery(Logger logger, BaseSmartsAdapter smartsAdapter,
            String className, String instanceName, List<String> attributes) {
        this.logger = logger;
        this.smartsAdapter = smartsAdapter;
        this.className = className;
        this.instanceName = instanceName;
        this.attributes = attributes;
    }

    @Override
    public Iterator<Map<String,Object>> execute() throws Exception {
        LinkedList<Map<String, Object>> records = new LinkedList<Map<String, Object>>();
        logger.debug("Getting properties of  " + className + ", " + instanceName);
        String[] attArray = attributes.toArray(new String[0]);
        MR_AnyVal[] anyVals = null;
        try {
            anyVals = smartsAdapter.getProperties(className, instanceName, attArray);
        }
         catch (SmRemoteException e) {
            logger.warn(e.toString());
        }

        if(anyVals != null){
            MRArrayToMap mrArrayToMap = new MRArrayToMap();
    
            Map<String, Object> record = mrArrayToMap.getMap(attArray, anyVals);
            records.add(record);
        }
        return new PropertiesSet(records);
    }

}

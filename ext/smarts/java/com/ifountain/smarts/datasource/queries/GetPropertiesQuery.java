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

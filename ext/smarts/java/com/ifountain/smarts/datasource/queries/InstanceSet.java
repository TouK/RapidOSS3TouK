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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

import com.ifountain.smarts.datasource.BaseSmartsAdapter;


public class InstanceSet implements Iterator<Map<String,Object>> {

    private BaseSmartsAdapter smartsAdapter;
    private Logger logger;
    private Map<String, LinkedList<String>> classInstanceMap;
    private List<String> attributes;
    private int fetchSize;
    private boolean noRecordsLeft = false;
    private LinkedList<Map<String, Object>> records;

    public InstanceSet(Logger logger, BaseSmartsAdapter smartsAdapter,
            Map<String, LinkedList<String>> classInstanceMap,List<String> attributes,
            int fetchSize) {
        
        this.logger = logger;
        this.smartsAdapter = smartsAdapter;
        this.classInstanceMap = classInstanceMap;
        this.attributes = attributes;
        this.fetchSize = fetchSize;
        records = new LinkedList<Map<String, Object>>();
    }

    @Override
    public boolean hasNext() {
        if(!noRecordsLeft && records.isEmpty())
        {
            logger.debug("Local record storage is empty. Retrieving next " + fetchSize);
            try
            {
                retrieveRecords();
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
            noRecordsLeft = records.isEmpty();
        }
        return !noRecordsLeft;
    }

    @Override
    public Map<String, Object> next() {
        if(hasNext())
            return records.removeFirst();
        else
            throw new NoSuchElementException();
    }

    @Override
    public void remove() {
    }
    
    private void retrieveRecords() throws Exception
    {
        Iterator<String> classesIterator = classInstanceMap.keySet().iterator();
        while(classesIterator.hasNext()){
            String className = classesIterator.next();
            LinkedList<String> instancesList = classInstanceMap.get(className);
            while(records.size() < fetchSize)
            {
                String instanceName;
                try
                {
                    instanceName = instancesList.removeFirst();
                }
                catch (NoSuchElementException e)
                {
                    logger.info("Finished processing class " + className);
                    break;
                }
                logger.debug("\tProcessing instance : " + instanceName);
                IQuery getPropertiesQuery = QueryFactory.getGetPropertiesQuery(logger, smartsAdapter, className, instanceName, attributes);
                Iterator<Map<String, Object>> propertiesIterator = (Iterator<Map<String, Object>>) getPropertiesQuery.execute();
                
                if(propertiesIterator.hasNext()){
                    Map<String, Object> record = propertiesIterator.next();
                    records.add(record);
                    logger.debug("\t" + record.size()+ " properties retrieved. Record count : " + records.size());
                }
                else{
                    break;
                }
            }
            if(records.size() == fetchSize)
            {
                break;
            }
        }
    }

    public LinkedList<Map<String, Object>> getRecords() {
        return records;
    }

    protected Map<String, LinkedList<String>> getClassInstanceMap() {
        return classInstanceMap;
    }

}

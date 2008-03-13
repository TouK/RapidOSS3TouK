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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ifountain.smarts.datasource.BaseSmartsAdapter;
import com.smarts.remote.SmRemoteException;
import com.smarts.repos.MR_Choice;
import com.smarts.repos.MR_Ref;

public class FindTopologyInstancesQuery implements IQuery {

    private int fetchSize;
    private boolean expressionsEnabled;
    private Logger logger = null;
    protected BaseSmartsAdapter smartsAdapter;
    private String creationClassName;
    private String name;
    private List<String> attributes;
    public FindTopologyInstancesQuery(Logger logger,
            BaseSmartsAdapter smartsAdapter, String creationClassName,
            String name, List<String> attributes, int fetchSize,
            boolean expressionsEnabled) {
        this.logger = logger;
        this.smartsAdapter = smartsAdapter;
        this.creationClassName = creationClassName;
        this.name = name;
        this.attributes = attributes;
        this.fetchSize = fetchSize;
        this.expressionsEnabled = expressionsEnabled;
    }

    @Override
    public Iterator<Map<String,Object>> execute() throws Exception {
        Map<String, LinkedList<String>> classInstanceMap = new HashMap<String, LinkedList<String>>();
        populateClassInstanceMap(classInstanceMap, creationClassName);
        return new InstanceSet(logger, smartsAdapter, classInstanceMap, attributes, fetchSize);
    }

    protected void populateClassInstanceMap(Map<String, LinkedList<String>> classInstanceMap, String className) throws Exception {
        logger.debug("Finding instances of class : " + className + " with instance pattern " + name);
        MR_Ref[] instances = null;
        if(expressionsEnabled){
            try {
                instances = smartsAdapter.findInstances(className, name, MR_Choice.NONE);
            } catch (SmRemoteException e) {
                logger.warn(e.toString());
            }
        }
        else{
            instances = new MR_Ref[]{new MR_Ref(className, name)};
        }
        if(instances != null){
            logger.info("Found " + instances.length + " instances for class " + className);
            if(instances.length > 0){
                LinkedList<String> instancesList = new LinkedList<String>();
                classInstanceMap.put(className, instancesList);
                for (int j = 0; j < instances.length; j++)
                {
                    if(!instancesList.contains(instances[j].getInstanceName())){
                        instancesList.add(instances[j].getInstanceName());
                    }
                }
            }
            if(expressionsEnabled)
            {
                try {
                    String[] children = smartsAdapter.getChildren(className);
                    for (int i = 0; i < children.length; i++) {
                        String child = children[i];
                        populateClassInstanceMap(classInstanceMap, child);
                    }
                } 
                 catch (SmRemoteException e) {
                }
            }
        }
    }

}

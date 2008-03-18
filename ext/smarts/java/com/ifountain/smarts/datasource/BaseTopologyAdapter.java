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
 * Created on Feb 21, 2008
 *
 * Author Sezgin
 */
package com.ifountain.smarts.datasource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ifountain.smarts.datasource.queries.IQuery;
import com.ifountain.smarts.datasource.queries.QueryFactory;
import com.ifountain.smarts.util.SmartsPropertyHelper;
import com.smarts.repos.MR_AnyVal;
import com.smarts.repos.MR_AnyValObjRef;
import com.smarts.repos.MR_Choice;
import com.smarts.repos.MR_Ref;
import com.smarts.repos.MR_ValType;

public class BaseTopologyAdapter extends BaseSmartsAdapter {

    public BaseTopologyAdapter()
	{
		super();
	}

//	protected Logger logger;

    public BaseTopologyAdapter(String datasourceName, long reconnectInterval, Logger logger) {
        super(datasourceName, reconnectInterval, logger);
    }

    public Iterator<Map<String,Object>> fetchObjects(String className, String instanceName, int fetchSize) throws Exception
    {
        return fetchObjects(className, instanceName, null, true, fetchSize);
    }
    public Iterator<Map<String,Object>> fetchObjects(String className, String instanceName, boolean expEnabled, int fetchSize) throws Exception
    {
        return fetchObjects(className, instanceName, null, expEnabled, fetchSize);
    }
    
    public Iterator<Map<String,Object>> fetchObjects(String className, String instanceName, List<String> attributes, boolean expEnabled, int fetchSize) throws Exception
    {
        if(attributes == null || attributes.isEmpty())
        {
            String[]propNames = getAttributeNames(className);
            String[]relationNames = getRelationNames(className);
            String[] allAtts = new String[relationNames.length+propNames.length];
            System.arraycopy(propNames, 0, allAtts, 0, propNames.length);
            System.arraycopy(relationNames, 0, allAtts, propNames.length, relationNames.length);
            attributes = Arrays.asList(allAtts);
        }
        IQuery query = QueryFactory.getFindTopologyInstancesQuery(logger, this, className, instanceName, attributes, fetchSize, expEnabled);
        return query.execute();
    }
    
    public List<Map<String, Object>> getObjects(String className, String instanceName, List<String> attributes, boolean expEnabled) throws Exception{
        List<Map<String, Object>> objects = new ArrayList<Map<String, Object>>();
        Iterator<Map<String, Object>> records = fetchObjects(className, instanceName, attributes, expEnabled, 10);
        while (records.hasNext()) {
            Map<String, Object> object = records.next();
            objects.add(object);
        }
        return objects;
    }
    
    public Map<String, Object> getObject(String className, String instanceName) throws Exception
    {
        return getObject(className, instanceName, null);
    }
    
    public Map<String, Object> getObject(String className, String instanceName, List<String> atts) throws Exception
    {
        List<Map<String, Object>> objects = getObjects(className, instanceName, atts, false);
        return objects.get(0);
    }    
    
    public List<Map<String, Object>> getObjects(String className, String instanceName) throws Exception
    {
        return getObjects(className, instanceName, null, true);
    }
    public List<Map<String, Object>> getObjects(String className, String instanceName, boolean expEnabled) throws Exception
    {
        return getObjects(className, instanceName, null, expEnabled);
    }
    
    
    public void deleteTopologyInstance(String className, String instanceName) throws Exception
    {
        invokeOperation(className,instanceName,"remove",new MR_AnyVal[]{});
    }
    
    public void createTopologyInstanceWithProperties(String className, String instanceName, Map<String, String> properties) throws Exception
    {
        if(!instanceExists(className, instanceName))
        {
            createInstance(className, instanceName);
        }
        updateTopologyInstanceWithProperties(className, instanceName, properties);
    }
    public void createTopologyInstance(String className, String instanceName, String relationName, MR_AnyValObjRef relationSet) throws Exception
    {
        insert(className, instanceName, relationName, relationSet);
    }

    
    public void updateTopologyInstanceWithProperties(String className , String instanceName,Map<String, String> properties)
    {
        for (Iterator<String> iter = properties.keySet().iterator(); iter.hasNext();)
        {
            String propertyName = iter.next();
            String propertyValue = properties.get(propertyName);
            updateProperty(className, instanceName, propertyName, propertyValue);
        }
    }
    public void addRelationshipBetweenTopologyObjects(String firstClassName, String firstInstanceName,
            String secondClassName, String secondInstanceName,
            String relationName) throws Exception
    {
        boolean oneToOne = isRelationshipOneToOne(firstClassName, relationName);
        if(oneToOne)
        {
            put(firstClassName, firstInstanceName, relationName, new MR_AnyValObjRef(new MR_Ref(secondClassName, secondInstanceName)));
        }
        else
        {
            insert(firstClassName, firstInstanceName, relationName, new MR_AnyValObjRef(new MR_Ref(secondClassName, secondInstanceName)));
        }
    }
    
    public void removeTopologyRelationship(String firstClassName, String firstInstanceName, String secondClassName, String secondInstanceName, String relationName)
        throws Exception
    {
       boolean isOneToOne = isRelationshipOneToOne(firstClassName, relationName);
       if(!isOneToOne)
       {
          remove(firstClassName, firstInstanceName,relationName, new MR_AnyValObjRef(new MR_Ref(secondClassName, secondInstanceName)));
       }
       else
       {
           String reverseRelationName = getReverseRelation(firstClassName, relationName);
           remove(secondClassName, secondInstanceName,reverseRelationName, new MR_AnyValObjRef(new MR_Ref(firstClassName, firstInstanceName)));
       }
    }
    
    public MR_Ref[] getAllInstances(String classNameRegExp, String instanceNameRegExp, boolean includeChildInstances) throws Exception
    {
        MR_Ref[] res = null;
        if(includeChildInstances)
        {
            res = findInstances(classNameRegExp, instanceNameRegExp, MR_Choice.EXPAND_SUBCLASSES);
        }
        else
        {
            res = findInstances(classNameRegExp, instanceNameRegExp, MR_Choice.NONE);
        }
        if(res == null)
        {
            res = new MR_Ref[0];
        }
        return res;
    }
    
    private boolean isRelationshipOneToOne(String className, String relationName) throws Exception
    {
        int relationType = relationshipExists(className, relationName);
        return (relationType == MR_ValType.MR_OBJREF);
    }
    
    private int relationshipExists(String className, String relationName) throws Exception
    {
        String[] relationNamesAvailable = getRelationNames(className);
        int[] relationTypesAvailable = getRelationTypes(className);
        int relationIndex = -1;
        for (int i = 0; i < relationNamesAvailable.length; i++)
        {
            String currentName = relationNamesAvailable[i];
            if(currentName.equals(relationName))
            {
                relationIndex = i;
            }
        }
        if(relationIndex == -1)
        {
            throw new Exception("Relation : [" + relationName + "] do not exist in [" + className + "]");
        }
        return relationTypesAvailable[relationIndex];
    }
    
    private void updateProperty(String className, String instanceName, String propertyName, String propertyValue)
    {
        try {
            MR_AnyVal inChargePropertyValue = SmartsPropertyHelper.getPropertyValue(this, className, propertyName, propertyValue);
            put(className, instanceName, propertyName, inChargePropertyValue);
        } catch (Exception e) {
        }
        
    }

	@Override
	public Map<String, Object> getObject(Map<String, String> ids, List<String> fieldsToBeRetrieved) throws Exception
	{
		return getObject(ids.get("CreationClassName"), ids.get("Name"), fieldsToBeRetrieved);
	}
}

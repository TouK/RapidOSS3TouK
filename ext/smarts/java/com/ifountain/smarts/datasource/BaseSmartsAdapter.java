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
 * Created on Feb 20, 2008
 *
 * Author Sezgin
 */
package com.ifountain.smarts.datasource;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ifountain.core.datasource.BaseAdapter;
import com.ifountain.smarts.datasource.actions.CreateInstanceAction;
import com.ifountain.smarts.datasource.actions.DeleteInstanceAction;
import com.ifountain.smarts.datasource.actions.FindInstancesAction;
import com.ifountain.smarts.datasource.actions.GetAction;
import com.ifountain.smarts.datasource.actions.GetAllPropertiesAction;
import com.ifountain.smarts.datasource.actions.GetAttributeNamesAction;
import com.ifountain.smarts.datasource.actions.GetAttributeTypesAction;
import com.ifountain.smarts.datasource.actions.GetChildrenAction;
import com.ifountain.smarts.datasource.actions.GetInstancesAction;
import com.ifountain.smarts.datasource.actions.GetPropNamesAction;
import com.ifountain.smarts.datasource.actions.GetPropTypeAction;
import com.ifountain.smarts.datasource.actions.GetPropertiesAction;
import com.ifountain.smarts.datasource.actions.GetRelationNamesAction;
import com.ifountain.smarts.datasource.actions.GetRelationTypesAction;
import com.ifountain.smarts.datasource.actions.GetReverseRelationAction;
import com.ifountain.smarts.datasource.actions.InsertAction;
import com.ifountain.smarts.datasource.actions.InstanceExistsAction;
import com.ifountain.smarts.datasource.actions.InvokeOperationAction;
import com.ifountain.smarts.datasource.actions.InvokeOperationWithNativeParamsAction;
import com.ifountain.smarts.datasource.actions.PutAction;
import com.ifountain.smarts.datasource.actions.RemoveAction;
import com.smarts.repos.MR_AnyVal;
import com.smarts.repos.MR_PropertyNameValue;
import com.smarts.repos.MR_Ref;

public abstract class BaseSmartsAdapter extends BaseAdapter{

    public BaseSmartsAdapter(){
		super();
	}

	public BaseSmartsAdapter(String datasourceName, long reconnectInterval, Logger logger) {
        super(datasourceName, reconnectInterval, logger);
    }
    
    public abstract Map<String, Object> getObject(Map<String, String> ids, List<String> fieldsToBeRetrieved) throws Exception;

    public void createInstance(String className, String instanceName) throws Exception{
        CreateInstanceAction action = new CreateInstanceAction(className, instanceName);
        executeAction(action);
    }
    public void deleteInstance(String className, String instanceName) throws Exception{
        DeleteInstanceAction action = new DeleteInstanceAction(className, instanceName);
        executeAction(action);
    }
    public MR_Ref[] findInstances(String classRegExp, String instanceRegExp, long flags) throws Exception{
        FindInstancesAction action = new FindInstancesAction(classRegExp, instanceRegExp, flags);
        executeAction(action);
        return action.getInstances();
    }
    public MR_AnyVal get(String className,String instanceName,String propertyName) throws Exception{
        GetAction action = new GetAction(className, instanceName, propertyName);
        executeAction(action);
        return action.getPropertyValue();
    }
    public MR_PropertyNameValue[] getAllProperties(String className,String instanceName,long propertyTypeFlag) throws Exception{
        GetAllPropertiesAction action = new GetAllPropertiesAction(className, instanceName, propertyTypeFlag);
        executeAction(action);
        return action.getAllProperties();
    }
    public String[] getAttributeNames(String className) throws Exception{
        GetAttributeNamesAction action = new GetAttributeNamesAction(className);
        executeAction(action);
        return action.getAttributeNames();
    }
    public int[] getAttributeTypes(String className) throws Exception{
        GetAttributeTypesAction action = new GetAttributeTypesAction(className);
        executeAction(action);
        return action.getAttributeTypes();
    }
    public String[] getChildren(String className) throws Exception{
        GetChildrenAction action = new GetChildrenAction(className);
        executeAction(action);
        return action.getChildren();
    }
    public String[] getInstances() throws Exception{
        GetInstancesAction action = new GetInstancesAction();
        executeAction(action);
        return action.getInstances();
    }
    public String[] getInstances(String className) throws Exception{
        GetInstancesAction action = new GetInstancesAction(className);
        executeAction(action);
        return action.getInstances();
    }
    public MR_AnyVal[] getProperties(String className, String instanceName, String[] propertyNames) throws Exception{
        GetPropertiesAction action = new GetPropertiesAction(className, instanceName, propertyNames);
        executeAction(action);
        return action.getProperties();
    }
    public String[] getRelationNames(String className) throws Exception{
        GetRelationNamesAction action = new GetRelationNamesAction(className);
        executeAction(action);
        return action.getRelationNames();
    }
    public String[] getPropNames(String className) throws Exception{
        GetPropNamesAction action = new GetPropNamesAction(className);
        executeAction(action);
        return action.getPropNames();
    }
    public int getPropType(String className, String propertyName) throws Exception{
        GetPropTypeAction action = new GetPropTypeAction(className, propertyName);
        executeAction(action);
        return action.getPropType();
    }
    public int[] getRelationTypes(String className) throws Exception{
        GetRelationTypesAction action = new GetRelationTypesAction(className);
        executeAction(action);
        return action.getRelationTypes();
    }
    public void insert(String className, String instanceName, String propertyName, MR_AnyVal propertyValue) throws Exception{
        InsertAction action = new InsertAction(className, instanceName, propertyName, propertyValue);
        executeAction(action);
    }
    public boolean instanceExists(String className, String instanceName) throws Exception{
        InstanceExistsAction action = new InstanceExistsAction(className, instanceName);
        executeAction(action);
        return action.instanceExists();
    }
    public MR_AnyVal invokeOperationWithNativeParams(String className, String instanceName, String opName, MR_AnyVal[] opParams) throws Exception{
    	InvokeOperationWithNativeParamsAction action = new InvokeOperationWithNativeParamsAction(className, instanceName, opName, opParams);
        executeAction(action);
        return action.getInvokeResult();
    }
    public Object invokeOperation(String className, String instanceName, String opName, List<String>  opParams) throws Exception{
        InvokeOperationAction action = new InvokeOperationAction(logger, className, instanceName, opName, opParams);
        executeAction(action);
        return action.getInvokeResult();
    }
    public void put(String className, String instanceName, String propertyName, MR_AnyVal propertyValue) throws Exception{
        PutAction action = new PutAction(className, instanceName, propertyName, propertyValue);
        executeAction(action);
    }
    public void remove(String className, String instanceName,String propertyName, MR_AnyVal propertyValue) throws Exception{
        RemoveAction action = new RemoveAction(className, instanceName, propertyName, propertyValue);
        executeAction(action);
    }
    public String getReverseRelation(String className, String realationName) throws Exception{
        GetReverseRelationAction action = new GetReverseRelationAction(className, realationName);
        executeAction(action);
        return action.getReverseRelation();
    }
    
    
}

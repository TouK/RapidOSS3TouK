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
package com.ifountain.smarts.util;


import com.ifountain.smarts.datasource.BaseNotificationAdapter;
import com.ifountain.smarts.datasource.BaseSmartsAdapter;
import com.ifountain.smarts.datasource.BaseTopologyAdapter;
import com.smarts.repos.*;
import com.smarts.remote.SmRepositoryInterfaceHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmartsPropertyHelper {

    public static MR_PropertyNameValue[] getNotificationAttributes(BaseNotificationAdapter notificationAdapter, String className, String instanceName, String eventName, int attributeTypes) throws Exception
    {
        String notificationInstanceName = notificationAdapter.findNotificationName(className, instanceName, eventName);
        if (notificationInstanceName == null)
            return null; // Notification was not found

        MR_PropertyNameValue[] nameValuePairs = notificationAdapter.getAllProperties(SmartsConstants.NOTIFICATION_CLASS, notificationInstanceName, attributeTypes);
        return nameValuePairs;
    }
    
    public static MR_PropertyNameValue[] getNotificationAttributes(BaseNotificationAdapter notificationAdapter, String className, String instanceName, String eventName) throws Exception
    {
        return getNotificationAttributes(notificationAdapter, className,instanceName,eventName,MR_PropertyNameValue.MR_ATTRS_ONLY);
    }

    public static MR_AnyVal getPropertyValueFromList(MR_PropertyNameValue[] propertyNameValues, String propertyName)
    {
        for (int i = 0; i < propertyNameValues.length; i++)
        {
            MR_PropertyNameValue currentProperty = propertyNameValues[i];
            if(currentProperty.getPropertyName().equals(propertyName))
                return currentProperty.getPropertyValue();
        }
        return null;
    }
    public static MR_PropertyNameValue[][] getProperties(BaseTopologyAdapter adapter, String classNameRegExp, String instanceNameRegExp, String[] propertyNames, boolean bringPropertiesOfChildClasses) throws Exception
    {
        MR_Ref[] res = adapter.getAllInstances(classNameRegExp, instanceNameRegExp, bringPropertiesOfChildClasses);
        MR_PropertyNameValue[][] foundProperties = new MR_PropertyNameValue[res.length][propertyNames.length];
        for (int i = 0; i < res.length; i++)
        {
            String className = res[i].getClassName();
            String instanceName = res[i].getInstanceName();
            MR_AnyVal[] properties = adapter.getProperties(className, instanceName, propertyNames);
            if(properties == null || properties.length != propertyNames.length)
            {
                throw new Exception("One of the requested properties cannot be found in Class <" + className + "> instance <" + instanceName + ">");
            }
            else
            {
                for (int j = 0; j < properties.length; j++)
                {
                    foundProperties[i][j] = new MR_PropertyNameValue(propertyNames[j], properties[j]);
                }
            }
        }
        return foundProperties;
    }

    public static List<String> getNonExisentPropertyNames(BaseSmartsAdapter smartsAdapter, String className, String [] properties) throws Exception
    {
        List<String> nonExitentPropertyNames = new ArrayList<String>();
        String[] propNames = smartsAdapter.getPropNames(className);
        String []relationNames = smartsAdapter.getRelationNames(className);
        String [] allNames = new String[propNames.length + relationNames.length];
        System.arraycopy(propNames,0, allNames, 0, propNames.length);
        System.arraycopy(relationNames,0, allNames,  propNames.length,relationNames.length);
        for (int i = 0; i < properties.length; i++)
        {
            boolean isFound = false;
            for (int j = 0; j < allNames.length; j++)
            {
                if(properties[i].equals(allNames[j]))
                {
                    isFound = true;
                    break;
                }
            }
            if(!isFound)
            {
                nonExitentPropertyNames.add(properties[i]);
            }
        }
        return nonExitentPropertyNames;
    }
    
    public static Map<String, String> getAllPropertiesOfInstanceAsMap(BaseSmartsAdapter smartsAdapter, String className, String instanceName) throws Exception
    {
        MR_PropertyNameValue[] value_list = smartsAdapter.getAllProperties(className, instanceName, MR_PropertyNameValue.MR_ATTRS_ONLY);
        return getPropertyNameValuesAsMapOfStrings(value_list);
    }
    
    public static Map<String, String> getPropertyNameValuesAsMapOfStrings(MR_PropertyNameValue[] value_list)
    {
        Map<String, String> map = new HashMap<String, String>();
        if (value_list == null)
        {
            return map;
        }
        for (int i = 0; i < value_list.length; i++)
        {
            MR_PropertyNameValue value = value_list[i];
            String property_name = value.getPropertyName();
            MR_AnyVal property_val = value.getPropertyValue();
            if (property_val != null)
            {
                map.put(property_name, property_val.toString());
            }
            else
            {
                map.put(property_name, "");
            }
        }
        return map;
    }
    
    public static MR_AnyVal getPropertyValue(BaseSmartsAdapter adapter, String className,String propertyName, String value) throws Exception
    {
        int type = adapter.getPropType(className, propertyName);

        MR_AnyVal mr_anyVal = getAsMrAnyVal(type, value);
        if (mr_anyVal == null) {
            throw new Exception(propertyName + " of " + className + " is not supported for update");
        } else {
            return mr_anyVal;
        }
    }

    public static String[] getObservablePropertyNamesOfanICNotification(SmRepositoryInterfaceHandler domainManager, String notificationListName) throws Exception
    {
		MR_AnyVal val = domainManager.get(SmartsConstants.NOTIFICATION_LIST_CLASS, notificationListName, SmartsConstants.COLUMNINFOBYTYPE);
		MR_AnyValArraySet set = (MR_AnyValArraySet) val;
		String [] propNames = new String[set.getArraySetValue().length];
		for (int i = 0 ; i < set.getArraySetValue().length ; i++)
		{
			MR_AnyValArray array = set.getArraySetValue()[i];
			MR_AnyVal[] mrAnyValArray = array.getArrayValue();
			int index = ((MR_AnyValInt)mrAnyValArray[0]).getIntValue();
			propNames[index] = ((MR_AnyValString)mrAnyValArray[1]).getStringValue();
		}
		return propNames;
    }

    public static MR_AnyVal getAsMrAnyVal(int type, String value) {
        if (type == MR_ValType.MR_BOOLEAN) {
            if ("true".equalsIgnoreCase(value)
                    || "false".equalsIgnoreCase(value)) {
                return new MR_AnyValBoolean(new Boolean(value));
            } else {
                throw new RuntimeException("Invalid boolean value");
            }
        } else if (type == MR_ValType.MR_INT) {
            return new MR_AnyValInt(Integer.parseInt(value));
        } else if (type == MR_ValType.MR_UNSIGNEDINT) {
            return new MR_AnyValUnsignedInt(Integer.parseInt(value));
        } else if (type == MR_ValType.MR_LONG) {
            return new MR_AnyValLong(Long.parseLong(value));
        } else if (type == MR_ValType.MR_UNSIGNEDLONG) {
            return new MR_AnyValUnsignedLong(Long.parseLong(value));
        } else if (type == MR_ValType.MR_SHORT) {
            return new MR_AnyValShort(Short.parseShort(value));
        } else if (type == MR_ValType.MR_UNSIGNEDSHORT) {
            return new MR_AnyValUnsignedShort(Short.parseShort(value));
        } else if (type == MR_ValType.MR_FLOAT) {
            return new MR_AnyValFloat(Float.parseFloat(value));
        } else if (type == MR_ValType.MR_DOUBLE) {
            return new MR_AnyValDouble(Double.parseDouble(value));
        } else if (type == MR_ValType.MR_STRING) {
            return new MR_AnyValString(value);
        }
        return null; // The type is not supported!
    }
}

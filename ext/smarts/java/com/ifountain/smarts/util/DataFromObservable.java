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
 * Created on Mar 12, 2008
 *
 * Author Sezgin Kucukkaraaslan
 */
package com.ifountain.smarts.util;

import com.smarts.repos.MR_AnyVal;

public class DataFromObservable {

    int       eventType;
    String    className;
    String    instanceName;
    String    propertyName;
    MR_AnyVal value;

    public DataFromObservable(String className,String instanceName, String propertyName, MR_AnyVal value, int type)
    {
        this.className = className;
        this.eventType = type;
        this.instanceName = instanceName;
        this.propertyName = propertyName;
        this.value = value;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public int getEventType()
    {
        return eventType;
    }

    public void setEventType(int eventType)
    {
        this.eventType = eventType;
    }

    public String getInstanceName()
    {
        return instanceName;
    }

    public void setInstanceName(String instanceName)
    {
        this.instanceName = instanceName;
    }

    public String getPropertyName()
    {
        return propertyName;
    }

    public void setPropertyName(String propertyName)
    {
        this.propertyName = propertyName;
    }

    public MR_AnyVal getValue()
    {
        return value;
    }

    public void setValue(MR_AnyVal value)
    {
        this.value = value;
    }

    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("eventType: <").append(eventType).append(">, ");
        buffer.append("className: <").append(className).append(">, ");
        buffer.append("instanceName: <").append(instanceName).append(">, ");
        buffer.append("propertyName: <").append(propertyName).append(">, ");
        if(value != null){
            Object stringValue = value.getValue();
            if(stringValue == null)
                stringValue = "";
            buffer.append("propertyValue: <").append(stringValue.toString()).append(">");
        }
        return buffer.toString();
    }
}

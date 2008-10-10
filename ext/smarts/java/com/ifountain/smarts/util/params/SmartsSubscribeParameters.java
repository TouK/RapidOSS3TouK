/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created on Mar 12, 2008
 *
 * Author Sezgin Kucukkaraaslan
 */
package com.ifountain.smarts.util.params;

public class SmartsSubscribeParameters {

    String className;
    String instanceName;
    String[] parameters;

    public SmartsSubscribeParameters(String className, String instanceName, String[] parameters)
    {
        this.className = className;
        this.instanceName = instanceName;
        this.parameters = parameters;
    }
    
    public String getClassName()
    {
        return className;
    }
    
    public void setClassName(String className)
    {
        this.className = className;
    }
    
    public String getInstanceName()
    {
        return instanceName;
    }
    
    public void setInstanceName(String instanceName)
    {
        this.instanceName = instanceName;
    }
    
    public String[] getParameters()
    {
        return parameters;
    }
    
    public void setParameters(String[] parameters)
    {
        this.parameters = parameters;
    }
    
    public String getParameter(int index)
    {
        if(parameters != null && index < parameters.length && index >= 0)
        {
            return parameters[index];
        }
        return null;
    }

    public String toString()
    {
        String res = "";
        res += "ClassName <" + className + "> InstanceName <" + instanceName + ">";
        if(parameters != null)
        {
            for (int i = 0; i < parameters.length; i++)
            {
                res += " Property" + (i + 1) + " <" + parameters[i] + ">";
            }
        }
        else
        {
            res += " No property subscription.";
        }
        return res;
    }
}

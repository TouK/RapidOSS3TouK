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
/*
 * Created on Feb 1, 2008
 *
 */
package com.ifountain.comp.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration.AbstractConfiguration;

public abstract class RapidConfig extends AbstractConfiguration
{
    private Map<String, Object> configParams = new HashMap<String, Object>();
    @Override
    protected void addPropertyDirect(String propertyName, Object propertyValue)
    {
        configParams.put(propertyName, propertyValue);
    }

    @Override
    public void clearProperty(String propertyName)
    {
        configParams.remove(propertyName);
    }

    @Override
    public boolean containsKey(String propertyName)
    {
        return configParams.containsKey(propertyName);
    }

    @Override
    public Iterator<String> getKeys()
    {
        return configParams.keySet().iterator();
    }

    @Override
    public Object getProperty(String propertyName)
    {
        return configParams.get(propertyName);
    }

    @Override
    public boolean isEmpty()
    {
        return configParams.isEmpty();
    }
    public abstract void load() throws Exception;

}
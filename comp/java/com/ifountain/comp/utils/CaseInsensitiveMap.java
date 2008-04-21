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
 * Created on Jul 18, 2007
 *
 */
package com.ifountain.comp.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CaseInsensitiveMap implements Map
{
    private Map map;
    private Map lowercaseMap;
    public CaseInsensitiveMap()
    {
        map = new HashMap();
        lowercaseMap = new HashMap();
    }
    public CaseInsensitiveMap(Map params)
    {
        this();
        putAll(params);
    }
    public void clear()
    {
        map.clear();
        lowercaseMap.clear();
    }
    private String getCaseInsensiteKey(Object key)
    {
        return String.valueOf(key).toLowerCase();
    }
    public boolean containsKey(Object key)
    {
        return lowercaseMap.containsKey(getCaseInsensiteKey(key));
    }

    public boolean containsValue(Object value)
    {
        return map.containsValue(value);
    }

    public Set entrySet()
    {
        return map.entrySet();
    }

    public Object get(Object key)
    {
        String caseInsensiteKey = getCaseInsensiteKey(key);
        if(lowercaseMap.containsKey(caseInsensiteKey))
        {
            return map.get(lowercaseMap.get(caseInsensiteKey));
        }
        return null;
    }

    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    public Set keySet()
    {
        return map.keySet();
    }

    public Object put(Object key, Object value)
    {
        Object realKey = key;
        String caseInsensiteKey = getCaseInsensiteKey(key);
        if(lowercaseMap.containsKey(caseInsensiteKey))
        {
            realKey = lowercaseMap.get(caseInsensiteKey);
        }
        else
        {
            lowercaseMap.put(caseInsensiteKey, realKey);
        }
        return map.put(realKey, value);
    }

    public void putAll(Map arg0)
    {
        Set entrySet = arg0.entrySet();
        for (Iterator iter = entrySet.iterator(); iter.hasNext();)
        {
            Entry entry = (Entry) iter.next();
            put(entry.getKey(), entry.getValue());
        }
    }

    public Object remove(Object key)
    {
        String caseInsensiteKey = getCaseInsensiteKey(key);
        if(lowercaseMap.containsKey(caseInsensiteKey))
        {
            Object realKey = lowercaseMap.remove(caseInsensiteKey);
            return map.remove(realKey);
        }
        return null;
    }

    public int size()
    {
        return map.size();
    }

    public Collection values()
    {
        return map.values();
    }
    
    public String toString() {
    	return map.toString();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if(o instanceof Map)
        {
            Map other = (Map) o;
            Set entrySet = other.entrySet();
            if(entrySet.size() != this.entrySet().size()){
                return false;
            }
            for (Iterator iterator = entrySet.iterator(); iterator.hasNext();)
            {
                Entry e = (Entry) iterator.next();
                String key = (String)e.getKey();
                Object value = e.getValue();
                if(!this.containsKey(key) || (this.get(key) == null && value != null) || (this.get(key) != null && !this.get(key).equals(value))){
                    return false;
                }
            }
        }
        return true;
    }
}

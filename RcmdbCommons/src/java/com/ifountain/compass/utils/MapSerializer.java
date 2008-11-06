package com.ifountain.compass.utils;

import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Nov 6, 2008
 * Time: 8:54:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class MapSerializer {
    public static final String ENRTY_SPLITTER = ""+(char)936;
    public static final String KEY_VALUE_SPLITTER = ""+(char)937;
    public static String convertToString(Map map)
    {
        StringBuffer str = new StringBuffer();
        for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
           Map.Entry entry = (Map.Entry) it.next();
           str.append(entry.getKey()).append(KEY_VALUE_SPLITTER).append(entry.getValue()).append(ENRTY_SPLITTER);
        }
        
        return str.toString();
    }

    public static Map convertToMap(String str)
    {
        Map map = new HashMap();
        if(str.length() != 0)
        {
            String[] mapEntries = str.split(ENRTY_SPLITTER);
            for(int i=0; i < mapEntries.length; i++)
            {
                String entry = mapEntries[i];
                String[] keyValue = entry.split(KEY_VALUE_SPLITTER);
                map.put(keyValue[0], keyValue[1]);
            }
        }
        return map;
    }
}

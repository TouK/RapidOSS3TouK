/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
    public static final String SPLITTER = ""+(char)936;
    public static String convertToString(Map map)
    {
        StringBuffer str = new StringBuffer();
        for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
           Map.Entry entry = (Map.Entry) it.next();
           str.append(entry.getKey()).append(SPLITTER).append(entry.getValue()).append(SPLITTER);
        }
        if(str.length() > 0)
        {
            return str.substring(0, str.length()-1);
        }
        else
        {
            return "";
        }
    }

    public static Map convertToMap(String str)
    {
        Map map = new HashMap();
        if(str.length() != 0)
        {
            String[] mapEntries = str.split(SPLITTER,-1);
            for(int i=0; i < mapEntries.length; i+=2)
            {
                String key = mapEntries[i];
                String value = mapEntries[i+1];
                map.put(key, value);
            }
        }
        return map;
    }
}

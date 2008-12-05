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
package com.ifountain.compass.search;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 26, 2008
 * Time: 2:20:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class FilterManager
{
    private static FilterStorage filterStorage = new FilterStorage();

    public static void addFilter(String filter)
    {
        ((List)filterStorage.get()).add(filter);
    }

    public static void clearFilters()
    {
        ((List)filterStorage.get()).clear();
    }

    public static String getQuery(String query)
    {

        List filterList = (List)filterStorage.get();
        if(filterList.isEmpty()) return query;

        StringBuffer bf = new StringBuffer("(");
        bf.append(query).append(") AND (");
        for(int i=0; i < filterList.size(); i++)
        {
            String filter = (String)filterList.get(i);
            bf.append("(").append(filter).append(") OR ");
        }
        bf.delete(bf.length()-4, bf.length());
        bf.append(")");
        return bf.toString();
    }
}

class FilterStorage extends InheritableThreadLocal
{
    protected Object initialValue() {
        return new ArrayList();
    }
}
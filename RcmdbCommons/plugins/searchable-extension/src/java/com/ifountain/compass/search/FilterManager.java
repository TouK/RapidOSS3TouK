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

import com.ifountain.session.SessionManager;

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
public class FilterManager {
    public static String SESSION_FILTER_KEY = "searchfilters";
    public static String CLASS_FILTERS = "classFilters";
    public static String GROUP_FILTERS = "groupFilters";
    public static final String DEFAULT_FILTER = "rsOwner:p";

    public static String getQuery(String query, String className) {
        Map searchFilters = (Map) SessionManager.getInstance().getSession().get(SESSION_FILTER_KEY);
        if (searchFilters == null) return query;
        List filterList = new ArrayList();
        List groupFilters = (List) searchFilters.get(GROUP_FILTERS);
        filterList.addAll(groupFilters);
        if (groupFilters.size() > 0) {
            filterList.add(DEFAULT_FILTER);
        }
        if (className != null) {
            Map allClassFilters = (Map) searchFilters.get(CLASS_FILTERS);
            List classFilters = (List) allClassFilters.get(className);
            if (classFilters != null) {
                filterList.addAll(classFilters);
            }
        }
        if (filterList.isEmpty()) return query;
        StringBuffer bf = new StringBuffer("(");
        bf.append(query).append(") AND (");
        for (int i = 0; i < filterList.size(); i++) {
            String filter = (String) filterList.get(i);
            bf.append("(").append(filter).append(") OR ");
        }
        bf.delete(bf.length() - 4, bf.length());
        bf.append(")");
        return bf.toString();
    }
}
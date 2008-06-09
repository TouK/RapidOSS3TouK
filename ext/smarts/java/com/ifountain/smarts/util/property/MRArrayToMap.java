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
 * Created on Feb 22, 2008
 *
 * Author Sezgin Kucukkaraaslan
 */
package com.ifountain.smarts.util.property;

import com.smarts.repos.MR_AnyVal;

import java.util.HashMap;
import java.util.Map;

public class MRArrayToMap {

    public Map<String, Object> getMap(String[] names, MR_AnyVal[] values) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            MR_AnyVal value = values[i];
            try{
                MRToEntry mrToField = MRToEntryFactory.getMRToEntry(name,  value);
                mrToField.putToMap(map);
            }
            catch(Exception e){
            }
        }
        return map;
    }
}

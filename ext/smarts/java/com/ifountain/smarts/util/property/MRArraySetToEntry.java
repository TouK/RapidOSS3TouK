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

import java.util.HashMap;
import java.util.Map;

import com.smarts.repos.MR_AnyVal;
import com.smarts.repos.MR_AnyValArray;
import com.smarts.repos.MR_AnyValArraySet;

public class MRArraySetToEntry extends MRToEntry {

    protected MRArraySetToEntry(String name, MR_AnyVal value) {
        super(name, value);
    }

    @Override
    public Object getValue() {
        MR_AnyValArray[] mr_anyValArrays = ((MR_AnyValArraySet)propertyValue).getArraySetValue();
        Map<String, Object>[] records = new HashMap[mr_anyValArrays.length];
        for (int i = 0; i < mr_anyValArrays.length; i++) {
            records[i] = new HashMap<String, Object>();
            MR_AnyVal[] vals = mr_anyValArrays[i].getArrayValue();
            for (int j = 0; j < vals.length; j++) {
                MR_AnyVal anyVal = vals[j];
                MRToEntry mrToEntry = MRToEntryFactory.getMRToEntry("element" + j, anyVal);
                mrToEntry.putToMap(records[i]);
            }
        }
        return records;
    }

}

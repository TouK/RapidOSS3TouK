/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created on Feb 22, 2008
 *
 * Author Sezgin Kucukkaraaslan
 */
package com.ifountain.smarts.util.property;

import com.smarts.repos.MR_AnyVal;
import com.smarts.repos.MR_AnyValArray;
import com.smarts.repos.MR_AnyValArraySet;

import java.util.HashMap;
import java.util.Map;

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

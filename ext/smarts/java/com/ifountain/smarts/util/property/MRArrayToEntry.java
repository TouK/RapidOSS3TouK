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

import java.util.HashMap;
import java.util.Map;

public class MRArrayToEntry extends MRToEntry {

    protected MRArrayToEntry(String name, MR_AnyVal value) {
        super(name, value);
    }

    @Override
    public Object getValue() {
        MR_AnyVal[] vals = ((MR_AnyValArray) propertyValue).getArrayValue();
        Map<String, Object>[] records = new HashMap[1];
        records[0] = new HashMap<String, Object>();
        for (int i = 0; i < vals.length; i++) {
            MR_AnyVal anyVal = vals[i];
            MRToEntry mrToEntry = MRToEntryFactory.getMRToEntry("element" + i, anyVal);
            mrToEntry.putToMap(records[0]);
        }
        return records;
    }

}

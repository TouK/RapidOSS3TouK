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

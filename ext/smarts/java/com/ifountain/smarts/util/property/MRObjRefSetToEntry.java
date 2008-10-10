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
import com.smarts.repos.MR_AnyValObjRefSet;

import java.util.HashMap;
import java.util.Map;

public class MRObjRefSetToEntry extends MRToEntry {

    protected MRObjRefSetToEntry(String name, MR_AnyVal value) {
        super(name, value);
    }

    @Override
    public Object getValue() {
        MR_AnyValObjRefSet objrefset = (MR_AnyValObjRefSet)propertyValue;
        Map<String, Object>[] records = new HashMap[objrefset.getObjRefSetValue().length];
        
        for(int i = 0 ; i < objrefset.getObjRefSetValue().length; i++) {
            String objectinstancename = objrefset.getObjRefSetValue()[i].getInstanceName();
            String objectclassname = objrefset.getObjRefSetValue()[i].getClassName();
            records[i] = new HashMap<String, Object>();
            records[i].put("CreationClassName", objectclassname);
            records[i].put("Name", objectinstancename);
        }
        return records;
    }

}

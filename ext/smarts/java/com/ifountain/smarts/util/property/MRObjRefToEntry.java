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
import com.smarts.repos.MR_AnyValObjRef;

import java.util.HashMap;
import java.util.Map;

public class MRObjRefToEntry extends MRToEntry {

    protected MRObjRefToEntry(String name, MR_AnyVal value) {
        super(name, value);
    }

    @Override
    public Object getValue() {
        MR_AnyValObjRef objref = (MR_AnyValObjRef)propertyValue;
        String objectinstancename = objref.getObjRefValue().getInstanceName();
        String objectclassname = objref.getObjRefValue().getClassName();
        
        Map<String, Object>[] records = new HashMap[1];
        Map<String, Object> record = new HashMap<String, Object>();
        record.put("CreationClassName", objectclassname);
        record.put("Name", objectinstancename);
        records[0] = record;
        return records;
    }
}

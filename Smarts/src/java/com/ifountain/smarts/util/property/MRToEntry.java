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

import java.util.Map;

public abstract class MRToEntry {

    protected String propertyName;
    protected MR_AnyVal propertyValue;

    protected MRToEntry(String name, MR_AnyVal value)
    { 
        propertyName = name;
        propertyValue = value;
    }
    public void putToMap(Map<String, Object> map){
        map.put(propertyName, getValue());
    }
    public abstract Object getValue() ;
}

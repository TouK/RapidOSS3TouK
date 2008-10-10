/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created on Feb 22, 2008
 *
 * Author Sezgin Kucukkaraaslan
 */
package com.ifountain.smarts.datasource.queries;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class PropertiesSet implements Iterator<Map<String, Object>> {

    
    private LinkedList<Map<String, Object>> records;

    public PropertiesSet(LinkedList<Map<String, Object>> records) {
        this.records = records;
    }

    @Override
    public boolean hasNext() {
        return !records.isEmpty();
    }

    @Override
    public Map<String, Object> next() {
        return records.removeFirst();
    }

    @Override
    public void remove() {
    }

}

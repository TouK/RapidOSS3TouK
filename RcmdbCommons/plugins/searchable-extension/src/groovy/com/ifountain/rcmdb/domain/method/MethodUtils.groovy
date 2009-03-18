package com.ifountain.rcmdb.domain.method

import org.compass.core.CompassHits
import org.apache.commons.collections.MapUtils;
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Mar 18, 2009
 * Time: 6:08:46 PM
 * To change this template use File | Settings | File Templates.
 */
class MethodUtils {

    public static List getCompassHitsSubset(CompassHits compassHits, Map options) {
        List hitList = new ArrayList();
        def maxOption = options["max"]
        int offset = MapUtils.getIntValue(options, "offset");
        int max = MapUtils.getIntValue(options, "max");
        if (maxOption == null || max > compassHits.length())
        {
            max = compassHits.length();
        }
        int low = offset;
        int high = Math.min(low + max, compassHits.length());
        Iterator hitIterator = compassHits.iterator();
        for (int i = 0; i < low && i < high; i++) {
            hitIterator.next();
        }
        while (low < high) {
            hitList.add(hitIterator.next());
            low++
        }
        return hitList;
    }
}
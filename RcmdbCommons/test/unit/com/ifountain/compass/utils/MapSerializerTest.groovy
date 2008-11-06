package com.ifountain.compass.utils

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Nov 6, 2008
 * Time: 8:54:31 AM
 * To change this template use File | Settings | File Templates.
 */
class MapSerializerTest extends RapidCmdbTestCase{
    public void testMapSerializer()
    {
        Map map = new LinkedHashMap();
        map["prop1"] = "prop1Value"
        map["prop2"] = "prop2Value"
        String strValue = MapSerializer.convertToString(map);
        String expectedStrValue = "prop1${MapSerializer.KEY_VALUE_SPLITTER}prop1Value${MapSerializer.ENRTY_SPLITTER}prop2${MapSerializer.KEY_VALUE_SPLITTER}prop2Value${MapSerializer.ENRTY_SPLITTER}";
        assertEquals (expectedStrValue, strValue);

        Map deserializedMap = MapSerializer.convertToMap(strValue);
        assertEquals (map, deserializedMap);

        map.clear();
        strValue = MapSerializer.convertToString(map);
        expectedStrValue = "";
        assertEquals (expectedStrValue, strValue);

        deserializedMap = MapSerializer.convertToMap(strValue);
        assertEquals (map, deserializedMap);
    }
}
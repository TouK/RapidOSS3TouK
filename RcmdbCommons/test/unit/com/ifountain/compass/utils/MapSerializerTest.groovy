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
        String expectedStrValue = "prop1${MapSerializer.SPLITTER}prop1Value${MapSerializer.SPLITTER}prop2${MapSerializer.SPLITTER}prop2Value";
        assertEquals (expectedStrValue, strValue);

        Map deserializedMap = MapSerializer.convertToMap(strValue);
        assertEquals (map, deserializedMap);

        map.clear();
        strValue = MapSerializer.convertToString(map);
        expectedStrValue = "";
        assertEquals (expectedStrValue, strValue);

        deserializedMap = MapSerializer.convertToMap(strValue);
        assertEquals (map, deserializedMap);

         map["prop1"] = ""
        strValue = MapSerializer.convertToString(map);
        expectedStrValue = "prop1${MapSerializer.SPLITTER}";
        assertEquals (expectedStrValue, strValue);
        deserializedMap = MapSerializer.convertToMap(strValue);
        assertEquals (map, deserializedMap);
    }
}
/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
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
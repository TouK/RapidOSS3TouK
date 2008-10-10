/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created on Feb 22, 2008
 *
 * Author Sezgin Kucukkaraaslan
 */
package com.ifountain.smarts.util.property;


import com.ifountain.smarts.test.util.SmartsTestCase;
import com.smarts.repos.MR_AnyVal;
import com.smarts.repos.MR_AnyValObjRefSet;
import com.smarts.repos.MR_Ref;

import java.util.Map;

public class MRObjRefSetToEntryTest extends SmartsTestCase {

    public void testGetFieldForTopology() throws Exception {
        MR_AnyVal propValue = null;

        MR_Ref mr_ref1 = new MR_Ref("Switch", "mySwitch");
        MR_Ref mr_ref2 = new MR_Ref("Router", "myRouter");
        MR_Ref[] mr_refs = {mr_ref1, mr_ref2};

        propValue = new MR_AnyValObjRefSet(mr_refs);

        MRObjRefSetToEntry objRefToEntry = new MRObjRefSetToEntry("PropertyName", propValue);

        Object value = objRefToEntry.getValue();
        assertTrue(value instanceof Map[]);
        Map<String, Object>[] records = (Map[]) value;
        assertEquals(2, records.length);
        Map<String, Object> record1 = records[0];
        Map<String, Object> record2 = records[1];
        assertEquals(2, record1.size());
        assertEquals("Switch", record1.get("CreationClassName"));
        assertEquals("mySwitch", record1.get("Name"));
        assertEquals("Router", record2.get("CreationClassName"));
        assertEquals("myRouter", record2.get("Name"));
    }
}

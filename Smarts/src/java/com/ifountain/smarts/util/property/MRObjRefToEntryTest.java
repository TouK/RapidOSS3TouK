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
import com.smarts.repos.MR_AnyValObjRef;
import com.smarts.repos.MR_Ref;

import java.util.Map;

public class MRObjRefToEntryTest extends SmartsTestCase {

    public void testGetFieldForTopology() throws Exception {

        MR_AnyVal propValue = null;

        MR_Ref mr_ref = new MR_Ref("Switch", "mySwitch");
        propValue = new MR_AnyValObjRef(mr_ref);

        MRObjRefToEntry objRefToEntry = new MRObjRefToEntry("PropertyName", propValue);

        Object value = objRefToEntry.getValue();
        Map<String, Object>[] records = (Map[]) value;
        assertEquals(1, records.length);
        Map<String, Object> record = records[0];
        assertEquals(2, record.size());
        assertEquals("Switch", record.get("CreationClassName"));
        assertEquals("mySwitch", record.get("Name"));
    }
}

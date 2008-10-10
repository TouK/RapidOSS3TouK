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
import com.ifountain.smarts.test.util.SmartsTestUtils;
import com.smarts.repos.*;

import java.util.HashMap;
import java.util.Map;

public class MRArraySetToEntryTest extends SmartsTestCase {

    public void testGetValueForTopology() throws Exception {
        SmartsTestUtils.createNotification("Switch", "mySwitch", "Down", new HashMap<String, Object>());

        MR_AnyVal propValue = null;

        MR_Ref mr_ref1 = new MR_Ref("Router", "myRouter");
        MR_AnyVal mr_anyVal1 = new MR_AnyValString("Description");
        MR_AnyVal mr_anyVal2 = new MR_AnyValObjRef(mr_ref1);
        MR_AnyVal[] mr_anyVals1 = {mr_anyVal1, mr_anyVal2};

        MR_Ref mr_ref2 = new MR_Ref("ICS_Notification", "NOTIFICATION-Switch_mySwitch_Down");
        MR_AnyVal mr_anyValA = new MR_AnyValString("AuditText");
        MR_AnyVal mr_anyValB = new MR_AnyValObjRef(mr_ref2);
        MR_AnyVal[] mr_anyVals2 = {mr_anyValA, mr_anyValB};

        MR_AnyValArray mr_anyValArray1 = new MR_AnyValArray(mr_anyVals1);
        MR_AnyValArray mr_anyValArray2 = new MR_AnyValArray(mr_anyVals2);
        MR_AnyValArray[] mr_anyValArrays = {mr_anyValArray1, mr_anyValArray2};
        propValue = new MR_AnyValArraySet(mr_anyValArrays);

        MRArraySetToEntry mrArraySetToEntry = new MRArraySetToEntry("PropertyName", propValue);

        Object value = mrArraySetToEntry.getValue();
        assertTrue(value instanceof Map[]);
        Map<String, Object>[] records = (Map[]) value;
        assertEquals(2, records.length);
        Map<String, Object> record1 = records[0];
        Map<String, Object> record2 = records[1];
        assertEquals(2, record1.size());
        assertEquals("Description", record1.get("element0"));
        Map<String, Object>[] nestedRecords1 = (Map[]) record1.get("element1");
        assertEquals(1, nestedRecords1.length);
        assertEquals("Router", nestedRecords1[0].get("CreationClassName"));
        assertEquals("myRouter", nestedRecords1[0].get("Name"));
        assertEquals(2, record2.size());
        assertEquals("AuditText", record2.get("element0"));
        Map<String, Object>[] nestedRecords2 = (Map[]) record2.get("element1");
        assertEquals(1, nestedRecords2.length);
        assertEquals("ICS_Notification", nestedRecords2[0].get("CreationClassName"));
        assertEquals("NOTIFICATION-Switch_mySwitch_Down", nestedRecords2[0].get("Name"));
    }
}

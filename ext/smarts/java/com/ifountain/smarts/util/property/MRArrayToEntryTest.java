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
import com.smarts.repos.*;

import java.util.Map;

public class MRArrayToEntryTest extends SmartsTestCase {

    public void testGetFieldForTopology() throws Exception {

        MR_AnyVal propValue = null;

        MR_Ref mr_ref = new MR_Ref("Router", "myRouter");
        MR_AnyVal mr_anyVal1 = new MR_AnyValString("Description");
        MR_AnyVal mr_anyVal2 = new MR_AnyValObjRef(mr_ref);
        MR_AnyVal[] mr_anyVals = {mr_anyVal1, mr_anyVal2};

        propValue = new MR_AnyValArray(mr_anyVals);

        MRArrayToEntry mrArrayToEntry = new MRArrayToEntry("PropertyName", propValue);

        Object value = mrArrayToEntry.getValue();
        assertTrue(value instanceof Map[]);
        Map<String, Object>[] records = (Map[]) value;
        assertEquals(1, records.length);
        Map<String, Object> record = records[0];
        assertEquals(2, record.size());
        
        assertEquals("Description", record.get("element0"));
        Map<String, Object>[] nestedRecords = (Map[]) record.get("element1");
        assertEquals(1, nestedRecords.length);
        assertEquals("Router", nestedRecords[0].get("CreationClassName"));
        assertEquals("myRouter", nestedRecords[0].get("Name"));
    }
}

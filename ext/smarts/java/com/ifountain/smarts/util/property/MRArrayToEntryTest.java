/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be 
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
/**
 * Created on Feb 22, 2008
 *
 * Author Sezgin Kucukkaraaslan
 */
package com.ifountain.smarts.util.property;

import java.util.Map;

import com.ifountain.smarts.test.util.SmartsTestCase;
import com.smarts.repos.MR_AnyVal;
import com.smarts.repos.MR_AnyValArray;
import com.smarts.repos.MR_AnyValObjRef;
import com.smarts.repos.MR_AnyValString;
import com.smarts.repos.MR_Ref;

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

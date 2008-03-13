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
import com.smarts.repos.MR_AnyValDoubleSet;
import com.smarts.repos.MR_AnyValStringSet;

public class MRPrimitiveSetToEntryTest extends SmartsTestCase {

    public void testGetFieldForDoubleSet() throws Exception {
        double[] doubles = {1, 1.1, 1.11};
        MR_AnyValDoubleSet mr_anyValDoubleSet = new MR_AnyValDoubleSet(doubles);

        MRPrimitiveSetToEntry mrDoubleSetToEntry = new MRPrimitiveSetToEntry("PropertyName", mr_anyValDoubleSet);

        Object value = mrDoubleSetToEntry.getValue();
        assertTrue(value instanceof Map[]);
        Map<String, Object>[] records = (Map[]) value;
        assertEquals(1, records.length);
        Map<String, Object> record = records[0];
        assertEquals(3, record.size());
        assertEquals("1.0", record.get("element0"));
        assertEquals("1.1", record.get("element1"));
        assertEquals("1.11", record.get("element2"));
    }

    public void testGetFieldForStringSet() throws Exception {
        String[] doubles = {"a", "ab", "abc"};
        MR_AnyValStringSet mr_AnyValStringSet = new MR_AnyValStringSet(doubles);

        MRPrimitiveSetToEntry mrDoubleSetToEntry = new MRPrimitiveSetToEntry("PropertyName", mr_AnyValStringSet);

        Object value = mrDoubleSetToEntry.getValue();
        assertTrue(value instanceof Map[]);
        Map<String, Object>[] records = (Map[]) value;
        assertEquals(1, records.length);
        Map<String, Object> record = records[0];
        assertEquals(3, record.size());
        assertEquals("a", record.get("element0"));
        assertEquals("ab", record.get("element1"));
        assertEquals("abc", record.get("element2"));
    }
}

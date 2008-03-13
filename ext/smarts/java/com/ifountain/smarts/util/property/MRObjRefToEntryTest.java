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
import com.smarts.repos.MR_AnyValObjRef;
import com.smarts.repos.MR_Ref;

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

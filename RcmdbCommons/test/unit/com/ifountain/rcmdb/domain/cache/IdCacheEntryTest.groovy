package com.ifountain.rcmdb.domain.cache

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: May 29, 2009
* Time: 2:18:34 PM
* To change this template use File | Settings | File Templates.
*/
class IdCacheEntryTest extends RapidCmdbWithCompassTestCase{
    public void testSetProperties()
    {
        IdCacheEntry entry = new IdCacheEntry();
        entry.setProperties (IdCacheEntryTest, 1);
        assertTrue (entry.exist);
        assertEquals (IdCacheEntryTest, entry.alias);
        assertEquals (1, entry.getId());

        entry.clear();
        assertFalse(entry.exist);
        assertEquals (null, entry.alias);
        assertEquals (-1, entry.getId());
    }

    public void testSetPropertyMethodWillNotAssignPropValues()
    {
        IdCacheEntry entry = new IdCacheEntry();
        entry.exist = true;
        entry.id = 1000;
        entry.alias = null;
        assertFalse(entry.exist);
        assertEquals (null, entry.alias);
        assertEquals (-1, entry.getId());
    }
}
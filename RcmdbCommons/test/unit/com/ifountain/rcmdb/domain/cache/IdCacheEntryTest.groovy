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
        IdCache.initialize (10000);
        def id = 1;
        assertFalse (IdCache.get(id).exist());
        IdCacheEntry entry = new IdCacheEntry();
        entry.setProperties (IdCacheEntryTest, 1);
        assertTrue (IdCache.get(id).exist());
        assertSame(entry, IdCache.get(id));
        assertTrue (entry.exist);
        assertEquals (IdCacheEntryTest, entry.alias);
        assertEquals (1, entry.getId());
        assertTrue (entry.exist());

        entry.clear();
        assertFalse (IdCache.get(id).exist());
        assertNotSame(entry, IdCache.get(id));
        assertFalse(entry.exist);
        assertEquals (null, entry.alias);
        assertEquals (-1, entry.getId());
        assertFalse (entry.exist());
    }

    public void testSetPropertyMethodWillNotAssignPropValues()
    {
        IdCache.initialize (10000);
        IdCacheEntry entry = new IdCacheEntry();
        entry.exist = true;
        entry.id = 1000;
        entry.alias = null;
        assertFalse(entry.exist);
        assertEquals (null, entry.alias);
        assertEquals (-1, entry.getId());
    }
}
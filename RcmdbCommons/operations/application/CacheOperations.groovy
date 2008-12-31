package application

import com.ifountain.rcmdb.util.RCMDBDataStore

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Dec 31, 2008
* Time: 10:50:51 AM
* To change this template use File | Settings | File Templates.
*/
class CacheOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {
    public static void store(Object key, Object value)
    {
        com.ifountain.rcmdb.util.RCMDBDataStore.put(key, value);
    }

    public static Object retrieve(Object key)
    {
        com.ifountain.rcmdb.util.RCMDBDataStore.get(key);
    }

    public static Object remove(Object key)
    {
        com.ifountain.rcmdb.util.RCMDBDataStore.remove (key);
    }

    public static void clear()
    {
        RCMDBDataStore.clear();           
    }
}
package application

import com.ifountain.rcmdb.util.DataStore

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Dec 31, 2008
* Time: 10:50:51 AM
* To change this template use File | Settings | File Templates.
*/
public class CacheOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {
    public static void store(Object key, Object value)
    {
        com.ifountain.rcmdb.util.DataStore.put(key, value);
    }

    public static Object retrieve(Object key)
    {
        com.ifountain.rcmdb.util.DataStore.get(key);
    }

    public static Object remove(Object key)
    {
        com.ifountain.rcmdb.util.DataStore.remove (key);
    }

    public static void clear()
    {
        DataStore.clear();
    }
}
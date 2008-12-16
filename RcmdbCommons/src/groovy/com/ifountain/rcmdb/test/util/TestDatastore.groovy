package com.ifountain.rcmdb.test.util
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 16, 2008
 * Time: 5:44:47 PM
 * To change this template use File | Settings | File Templates.
 */
class TestDatastore {
    private static Map constants = new HashMap();

    public static boolean containsKey(Object key) {

        return constants.containsKey(key);
    }

    public static boolean containsValue(Object value) {

        return constants.containsValue(value);
    }

    public static Object remove(Object key) {

        return constants.remove(key);
    }

    public static Object get(Object key)
    {
        return constants.get(key);
    }

    public static void put(Object key, Object value)
    {
        constants.put(key, value);
    }

    public static void clear()
    {
        constants.clear();
    }
}
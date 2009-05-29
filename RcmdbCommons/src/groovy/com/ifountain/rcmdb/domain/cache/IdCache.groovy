package com.ifountain.rcmdb.domain.cache

import org.apache.log4j.Logger
import org.apache.commons.collections.map.CaseInsensitiveMap
import java.util.Map.Entry

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: May 29, 2009
* Time: 10:39:57 AM
* To change this template use File | Settings | File Templates.
*/
public class IdCache {
    private static Logger logger = Logger.getLogger(IdCache);
    private static int maxSize;
    private static Map idCacheMap;
    public static synchronized void initialize(int size)
    {
        logger.info ("IdCache is initialized with size ${size}");
        maxSize = size;
        idCacheMap = Collections.synchronizedMap(new CaseInsensitiveMap());
    }

    public static synchronized int size()
    {
        return idCacheMap.size();
    }
    public static synchronized void clearCache()
    {
        idCacheMap.clear();
    }

    public static synchronized IdCacheEntry markAsDeleted(Class domainClass, Map params)
    {
        def keyMap = getKeyMap(domainClass, params);
        def cacheKeyString =  getCacheString(domainClass, keyMap);
        def entry = idCacheMap[cacheKeyString];
        if(entry != null)
        {
            entry.clear();
        }
    }

    private static Map getKeyMap(Class domainClass, Map params)
    {
        def keyMap = [:]
        domainClass.keySet().each{
            keyMap[it.name] = params[it.name]
        }
        return keyMap;
    }

    private static String getCacheString(Class domainClass, Map keyMap)
    {
        def rootClass = domainClass.getRootClass();
        return rootClass.name+keyMap.values().join("_");
    }

    private static void checkSize()
    {
        if(size() == maxSize)
        {
            def halfSize = maxSize/2;
            def keys = new ArrayList(idCacheMap.keySet());
            for(int i=0; i < halfSize; i++)
            {
                def key = keys.remove ((int)(Math.random()*keys.size()));
                idCacheMap.remove (key)
            }
        }
    }
    public static synchronized IdCacheEntry get(Class domainClass, Map params)
    {
        def keyMap = getKeyMap(domainClass, params);
        def cacheKeyString =  getCacheString(domainClass, keyMap);
        def entry = idCacheMap[cacheKeyString];
        if(entry == null)
        {
            checkSize();
            entry = new IdCacheEntry();
            def instanceFromCompass = domainClass.getFromHierarchy(keyMap)
            if(instanceFromCompass != null)
            {
                entry.setProperties(instanceFromCompass.class.name, instanceFromCompass.id);
            }
            idCacheMap[cacheKeyString] = entry;
        }
        return entry;
    }
}
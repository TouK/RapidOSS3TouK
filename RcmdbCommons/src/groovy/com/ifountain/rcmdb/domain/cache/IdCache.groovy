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

    public static synchronized IdCacheEntry markAsDeleted(Class domainClass, object)
    {
        def keyMap = getKeyMap(domainClass, object);
        def cacheKeyString =  getCacheString(domainClass, keyMap);
        def entry = idCacheMap[cacheKeyString];
        if(entry != null)
        {
            entry.clear();
        }
    }

    private static Map getKeyMap(Class domainClass, object)
    {
        def keyMap = [:]
        domainClass.keySet().each{
            keyMap[it.name] = object[it.name]
        }
        if(keyMap.isEmpty())
        {
            keyMap["id"] = object.id;
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
            if(logger.isDebugEnabled())
            {
                logger.debug ("Max size for idcache reached. Will delete ${halfSize} number of records from cache");
            }
            def keys = new ArrayList(idCacheMap.keySet());
            for(int i=0; i < halfSize; i++)
            {
                def key = keys.remove ((int)(Math.random()*keys.size()));
                idCacheMap.remove (key)
            }
        }
    }
    public static synchronized IdCacheEntry update(Object domainObject, boolean exist)
    {
        def keyMap = getKeyMap(domainObject.class, domainObject);
        def cacheKeyString =  getCacheString(domainObject.class, keyMap);
        def entry = idCacheMap[cacheKeyString];
        if(entry == null)
        {
            checkSize();
            entry = new IdCacheEntry();
            idCacheMap[cacheKeyString] = entry;
        }
        if(exist)
        {
            entry.setProperties (domainObject.class, domainObject.id);
        }
        else
        {
            entry.clear();
        }
        return entry;
    }
    public static synchronized IdCacheEntry get(Class domainClass, object)
    {
        def keyMap = getKeyMap(domainClass, object);
        def cacheKeyString =  getCacheString(domainClass, keyMap);
        def entry = idCacheMap[cacheKeyString];
        if(entry == null)
        {
            checkSize();
            entry = new IdCacheEntry();
            def instanceFromCompass = domainClass.getFromHierarchy(keyMap)
            if(instanceFromCompass != null)
            {
                entry.setProperties(instanceFromCompass.class, instanceFromCompass.id);
            }
            idCacheMap[cacheKeyString] = entry;
        }
        return entry;
    }
}
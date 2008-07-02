package com.ifountain.rcmdb.domain.method

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 15, 2008
* Time: 9:33:20 AM
* To change this template use File | Settings | File Templates.
*/
class CompassMethodInvoker {
    public static void unindex(MetaClass mc, objects)
    {
        if(!(objects instanceof Collection))
        {
            objects = [objects];
        }
        mc.invokeStaticMethod(mc.theClass, "unindex", [objects] as Object[]);
    }

    public static void index(MetaClass mc, objects)
    {
        if(!(objects instanceof Collection))
        {
            objects = [objects];
        }
        mc.invokeStaticMethod(mc.theClass, "index", [objects] as Object[]);
    }

    public static void reindex(MetaClass mc, objects)
    {
        if(!(objects instanceof Collection))
        {
            objects = [objects];
        }
        mc.invokeStaticMethod(mc.theClass, "reindex", [objects] as Object[]);
    }

    public static Object search(MetaClass mc, Map keys)
    {
        search (mc, keys, true);
    }

    public static Object search(MetaClass mc, Map keys, boolean triggerEvent)
    {
        if(keys.isEmpty()) return [total:0, results:[]]
        def queryBuffer = new StringBuffer("");
        for(key in keys){
            queryBuffer.append(key.key).append(":\"").append(key.value).append("\" AND ");
        }
        String query = queryBuffer.toString();
        if(keys.size() > 0)
        {
            query = query.substring(0, query.length() - 5);
        }
        return search(mc, query, triggerEvent);
    }

    public static Object countHits(MetaClass mc, Map keys)
    {
        if(keys.isEmpty()) return 0;
        def queryBuffer = new StringBuffer("");
        for(key in keys){
            queryBuffer.append(key.key).append(":\"").append(key.value).append("\" AND ");
        }
        String query = queryBuffer.toString();
        if(keys.size() > 0)
        {
            query = query.substring(0, query.length() - 5);
        }
        return mc.invokeStaticMethod(mc.theClass, "countHits", [query] as Object[]);
    }


    public static Object search(MetaClass mc, String query)
    {
        return mc.invokeStaticMethod(mc.theClass, "search", [query] as Object[]);
    }

    public static Object search(MetaClass mc, String query, boolean willTriggerEvents)
    {
        if(willTriggerEvents)
        {
            return mc.invokeStaticMethod(mc.theClass, "search", [query] as Object[]);
        }
        else
        {
            return mc.invokeStaticMethod(mc.theClass, "searchWithoutTriggering", [query] as Object[]);
        }
    }

    public static Object searchEvery(MetaClass mc, String query)
    {
        return mc.invokeStaticMethod(mc.theClass, "searchEvery", [query] as Object[]);
    }

    public static Object search(MetaClass mc, String query, Map options)
    {
        return mc.invokeStaticMethod(mc.theClass, "search", [query, options] as Object[]);
    }
}
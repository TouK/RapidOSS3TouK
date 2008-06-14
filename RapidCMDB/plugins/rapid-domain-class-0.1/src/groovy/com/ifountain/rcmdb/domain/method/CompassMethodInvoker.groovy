package com.ifountain.rcmdb.domain.method

import java.util.Map.Entry

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

    public static Object search(MetaClass mc, Map keys)
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
        return search(mc, query);
    }

    public static Object search(MetaClass mc, String query)
    {
        return mc.invokeStaticMethod(mc.theClass, "search", [query] as Object[]);
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
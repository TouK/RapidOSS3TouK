package com.ifountain.rcmdb.domain.cache
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: May 29, 2009
 * Time: 10:40:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class IdCacheEntry {
    String alias;
    boolean exist;
    long id = -1;

    public void clear()
    {
        id = -1;
        alias = null;
        exist = false;
    }

    public void setProperties(String alias, long id)
    {
        this.id = id;
        this.alias = alias;
        exist = true;
    }
}
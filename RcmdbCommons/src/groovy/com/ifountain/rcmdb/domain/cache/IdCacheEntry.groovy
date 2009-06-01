package com.ifountain.rcmdb.domain.cache
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: May 29, 2009
 * Time: 10:40:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class IdCacheEntry {
    Class alias;
    boolean exist;
    long id = -1;

    public void clear()
    {
        id = -1;
        alias = null;
        exist = false;
    }

    public void setProperties(Class alias, long id)
    {
        this.id = id;
        this.alias = alias;
        exist = true;
    }

    public boolean exist()
    {
        return (id != -1 && alias != null && exist == true);
    }

    public void setAlias(Class alias){}
    public void setId(int id){}
    public void setExist(boolean exist){}
}
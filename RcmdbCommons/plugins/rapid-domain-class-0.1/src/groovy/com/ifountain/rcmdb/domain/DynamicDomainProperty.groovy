package com.ifountain.rcmdb.domain
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Apr 28, 2008
 * Time: 11:32:15 AM
 * To change this template use File | Settings | File Templates.
 */
class DynamicDomainProperty extends MetaProperty{
    WeakHashMap map = new WeakHashMap()
    public DynamicDomainProperty(String propName, Class propType) {
        super(propName, propType);
    }

    public Object getProperty(Object object) {
        return map.get(object);
    }

    public void setProperty(Object object, Object propertyValue) {
        map.put (object, propertyValue)
    }

}
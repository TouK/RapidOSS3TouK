package com.ifountain.rcmdb.domain.property
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jun 22, 2009
 * Time: 3:07:37 PM
 * To change this template use File | Settings | File Templates.
 */
public interface FederatedPropertyManager {
    public boolean isFederated(Class domainClass, String propName)
    public boolean isLazy(Class domainClass, String propName)
}
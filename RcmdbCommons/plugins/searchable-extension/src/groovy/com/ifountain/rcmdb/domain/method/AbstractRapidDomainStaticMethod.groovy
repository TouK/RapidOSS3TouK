package com.ifountain.rcmdb.domain.method

import org.codehaus.groovy.grails.commons.metaclass.AbstractStaticMethodInvocation

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Apr 24, 2008
* Time: 1:09:05 PM
* To change this template use File | Settings | File Templates.
*/
abstract class AbstractRapidDomainStaticMethod{
    MetaClass mc;
    public AbstractRapidDomainStaticMethod(MetaClass mc) {
        this.mc = mc;
    }

    public Object invoke(Class clazz, Object[] arguments) {
//        if(isWriteOperation())
//        {
//            synchronized (WriteOperationSynchronizer.writeOperationLock)
//            {
//                return _invoke(clazz, arguments);
//            }
//        }
//        else
//        {
            return _invoke(clazz, arguments);
//        }

    }
    abstract boolean isWriteOperation();
    abstract protected Object _invoke(Class clazz, Object[] arguments);
    
}
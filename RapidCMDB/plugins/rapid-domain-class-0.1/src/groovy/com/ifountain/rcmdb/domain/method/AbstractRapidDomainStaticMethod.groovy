package com.ifountain.rcmdb.domain.method

import org.codehaus.groovy.grails.commons.metaclass.AbstractStaticMethodInvocation
import org.codehaus.groovy.grails.commons.GrailsDomainClass

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Apr 24, 2008
* Time: 1:09:05 PM
* To change this template use File | Settings | File Templates.
*/
abstract class AbstractRapidDomainStaticMethod extends AbstractStaticMethodInvocation{
    MetaClass mc;
    public AbstractRapidDomainStaticMethod(MetaClass mc) {
        this.mc = mc;
    }

    public Object invoke(Class clazz, String methodName, Object[] arguments) {
        return invoke(clazz, arguments); //To change body of implemented methods use File | Settings | File Templates.
    }
    abstract Object invoke(Class clazz, Object[] arguments);
    
}
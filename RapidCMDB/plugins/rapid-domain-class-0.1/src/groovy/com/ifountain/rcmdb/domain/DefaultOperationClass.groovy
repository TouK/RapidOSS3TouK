package com.ifountain.rcmdb.domain

import org.codehaus.groovy.grails.commons.AbstractInjectableGrailsClass

/**
* Created by IntelliJ IDEA.
* User: mustafa
* Date: Apr 25, 2008
* Time: 1:09:28 AM
* To change this template use File | Settings | File Templates.
*/
class DefaultOperationClass extends AbstractInjectableGrailsClass{
    public static final String OPERATIONS = "Operations";
    public DefaultOperationClass(Class clazz) {
        super(clazz, OPERATIONS); //To change body of overridden methods use File | Settings | File Templates.
    }

}
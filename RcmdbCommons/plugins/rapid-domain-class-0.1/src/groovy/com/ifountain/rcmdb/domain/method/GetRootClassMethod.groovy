package com.ifountain.rcmdb.domain.method

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import com.ifountain.rcmdb.domain.util.DomainClassUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: May 29, 2009
* Time: 11:29:26 AM
* To change this template use File | Settings | File Templates.
*/
class GetRootClassMethod {
    Class rootClass;
    public GetRootClassMethod(GrailsDomainClass domainClass, grailsDomainClasses) {
        rootClass = DomainClassUtils.getParentDomainClass(domainClass, grailsDomainClasses)
    }
}
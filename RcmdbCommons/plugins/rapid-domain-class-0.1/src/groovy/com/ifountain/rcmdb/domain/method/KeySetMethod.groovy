package com.ifountain.rcmdb.domain.method

import org.codehaus.groovy.grails.commons.GrailsDomainClass

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Nov 20, 2008
 * Time: 4:50:27 PM
 * To change this template use File | Settings | File Templates.
 */
class KeySetMethod {
    List keys;
    public KeySetMethod(GrailsDomainClass dc) {
        keys = [];
        def getPropListMethod = new GetPropertiesMethod(dc);
        getPropListMethod.allDomainClassProperties.each{RapidDomainClassProperty prop->
            if(prop.isKey)
            {
                keys.add(prop);
            }
        }
        keys = Collections.unmodifiableList(keys);
    }
}
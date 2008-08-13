package com.ifountain.rcmdb.test.util

import com.ifountain.rcmdb.domain.MockIdGeneratorStrategy
import com.ifountain.rcmdb.domain.IdGenerator
import org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 7, 2008
 * Time: 6:28:03 PM
 * To change this template use File | Settings | File Templates.
 */
class RapidCmdbWithCompassTestCase extends RapidCmdbMockTestCase{
    def initialize(List classesToBeLoaded, List pluginsToLoad)
    {
        IdGenerator.initialize(new MockIdGeneratorStrategy());
        pluginsToLoad +=  DomainClassGrailsPlugin;
        pluginsToLoad +=  gcl.loadClass("SearchableGrailsPlugin");
        pluginsToLoad +=  gcl.loadClass("SearchableExtensionGrailsPlugin");
        pluginsToLoad +=  gcl.loadClass("RapidDomainClassGrailsPlugin");
        super.initialize(classesToBeLoaded, pluginsToLoad);
    }

}

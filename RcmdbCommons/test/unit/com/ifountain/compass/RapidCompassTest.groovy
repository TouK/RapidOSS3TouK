package com.ifountain.compass;

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassEnvironment;
import org.compass.core.Compass;
import org.codehaus.groovy.grails.plugins.searchable.compass.config.SearchableCompassConfiguratorFactory;
import org.codehaus.groovy.grails.plugins.searchable.compass.test.AbstractSearchableCompassTests;
import org.codehaus.groovy.grails.plugins.searchable.test.compass.TestCompassFactory;
import org.springframework.core.io.ResourceLoader
import org.codehaus.groovy.grails.plugins.searchable.test.compass.TestCompassUtils
import org.codehaus.groovy.grails.plugins.searchable.TestUtils
import org.compass.core.spi.AliasedObject
import org.compass.core.CompassQueryBuilder
import org.compass.core.CompassQueryBuilder.CompassQueryStringBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 24, 2008
 * Time: 4:10:00 PM
 * To change this template use File | Settings | File Templates.
 */


class CompassThread extends Thread{
    def closure;
    public void run()
    {
        closure();
    }
}
class CompassTestObject {
    static searchable = true // modified by tests
    static hasMany = [:]
    Long id
    Long version
    String prop1;


}
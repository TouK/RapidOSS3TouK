package com.ifountain.compass

import com.ifountain.rcmdb.test.util.AbstractSearchableCompassTests
import org.compass.core.Compass
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.ifountain.rcmdb.test.util.compass.TestCompassFactory
import com.ifountain.rcmdb.domain.converter.CompassStringConverter
import com.ifountain.rcmdb.test.util.compass.TestCompassUtils
import org.compass.core.CompassQueryBuilder
import org.compass.core.CompassQuery
import com.ifountain.compass.analyzer.WhiteSpaceLowerCaseAnalyzer

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 16, 2009
* Time: 5:38:32 PM
* To change this template use File | Settings | File Templates.
*/
class CompassStringMarshallingTest extends AbstractSearchableCompassTests {
    Compass compass;

    public void setUp() {
        super.setUp()
    }

    public void tearDown() {
        super.tearDown();
        if (compass)
        {
            compass.close();
        }
    }

    public void testEmptyAndNullStringProperty()
    {
        GrailsApplication application = TestCompassFactory.getGrailsApplication([CompassTestObject])
        ApplicationHolder.application = application;
        CompositeDirectoryWrapperProvider provider = new CompositeDirectoryWrapperProvider();
        Map mappings = [:];

        compass = TestCompassFactory.getCompass(application, null, false, DefaultCompassConfiguration.getDefaultSettings(null));
        def id = 0;
        def instancesToBeSaved = [
                new CompassTestObject(id: id++, prop1: ""),
                new CompassTestObject(id: id++, prop1: null),
                new CompassTestObject(id: id++, prop1: "part1 ${CompassStringConverter.EMPTY_VALUE} part3")
        ] as Object[];
        TestCompassUtils.saveToCompass(compass, instancesToBeSaved)


        TestCompassUtils.withCompassQueryBuilder (compass, {CompassQueryBuilder builder->
            CompassQuery query = builder.queryString ("prop1:\"\"").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            def hits = query.hits();
            assertEquals(2, hits.length());
            CompassTestObject obj1 = hits.hit(0).getData();
            CompassTestObject obj2 = hits.hit(1).getData();
            assertEquals (instancesToBeSaved[0].id, obj1.id);
            assertEquals (instancesToBeSaved[1].id, obj2.id);
            assertEquals ("", obj1.prop1);
            assertEquals ("", obj2.prop1);

        })
    }
}
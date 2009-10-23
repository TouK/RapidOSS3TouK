package com.ifountain.compass.integration

import com.ifountain.compass.CompassConstants
import com.ifountain.compass.CompassTestObject
import com.ifountain.compass.CompositeDirectoryWrapperProvider
import com.ifountain.compass.DefaultCompassConfiguration
import com.ifountain.rcmdb.test.util.AbstractSearchableCompassTests
import com.ifountain.rcmdb.test.util.compass.TestCompassFactory
import com.ifountain.rcmdb.test.util.compass.TestCompassUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.compass.core.*
import com.ifountain.rcmdb.util.RapidCMDBConstants

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 13, 2009
* Time: 12:15:48 PM
* To change this template use File | Settings | File Templates.
*/
class CompassUnTokenizedFieldTest extends AbstractSearchableCompassTests {
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

    public void testCreationOfUntokenizedFields()
    {
        GrailsApplication application = TestCompassFactory.getGrailsApplication([CompassTestObject])
        ApplicationHolder.application = application;
        CompositeDirectoryWrapperProvider provider = new CompositeDirectoryWrapperProvider();
        Map mappings = [:];

        compass = TestCompassFactory.getCompass(application, null, false, DefaultCompassConfiguration.getDefaultSettings(null));
        def id = 0;
        def instancesToBeSaved = [
                new CompassTestObject(id: id++, prop1: "propertytoken1 propertytoken2 propertytoken3 propertytoken4"),
                new CompassTestObject(id: id++, prop1: "Propertytoken1 propertytoken2 propertytoken3 propertytoken4"),
                new CompassTestObject(id: id++, prop1: "PROPertytoken1 propertytoken2 propertytoken3 propertytoken4"),
                new CompassTestObject(id: id++, prop1: "propertytoken2 propertytoken3 propertytoken4"),
                new CompassTestObject(id: id++, prop1: "propertytoken3 propertytoken4"),
                new CompassTestObject(id: id++, prop1: "propertytoken4"),
                new CompassTestObject(id: id++, prop1: "")
        ] as Object[];
        TestCompassUtils.saveToCompass(compass, instancesToBeSaved)

        //test untokenized property is saved
        TestCompassUtils.withCompassQueryBuilder(compass, {CompassQueryBuilder builder ->
            CompassQuery query = builder.queryString("${CompassConstants.UN_TOKENIZED_FIELD_PREFIX}prop1:\"${instancesToBeSaved[0].prop1}\"").toQuery();
            query.addSort("id", CompassQuery.SortPropertyType.STRING);
            CompassHits hits = query.hits();
            assertEquals(3, hits.length());
            CompassHit hit = hits.hit(0);
            assertEquals(instancesToBeSaved[0].prop1, hit.getData().prop1);
            assertEquals("untokenized field should be lowercased", instancesToBeSaved[0].prop1.toLowerCase(), hit.getResource().getValue("${CompassConstants.UN_TOKENIZED_FIELD_PREFIX}prop1"));
            hit = hits.hit(1);
            assertEquals(instancesToBeSaved[1].prop1, hit.getData().prop1);
            assertEquals("untokenized field should be lowercased", instancesToBeSaved[1].prop1.toLowerCase(), hit.getResource().getValue("${CompassConstants.UN_TOKENIZED_FIELD_PREFIX}prop1"));
            hit = hits.hit(2);
            assertEquals(instancesToBeSaved[2].prop1, hit.getData().prop1);
            assertEquals("untokenized field should be lowercased", instancesToBeSaved[2].prop1.toLowerCase(), hit.getResource().getValue("${CompassConstants.UN_TOKENIZED_FIELD_PREFIX}prop1"));

            //queried data should be lowercased too
            query = builder.queryString("${CompassConstants.UN_TOKENIZED_FIELD_PREFIX}prop1:\"${instancesToBeSaved[0].prop1.toUpperCase()}\"").toQuery();
            query.addSort("id", CompassQuery.SortPropertyType.STRING);
            hits = query.hits();
            assertEquals("query should be lower cased for untokenized fields", 3, hits.length());

        })

        //test tokenized property is saved
        TestCompassUtils.withCompassQueryBuilder(compass, {CompassQueryBuilder builder ->
            def queryStr = "prop1:\"${instancesToBeSaved[0].prop1}\"";
            CompassQuery query = builder.queryString(queryStr).toQuery();
            query.addSort("id", CompassQuery.SortPropertyType.STRING);
            CompassHits hits = query.hits();
            assertEquals(3, hits.length());

            CompassHit hit = hits.hit(0);
            assertEquals(instancesToBeSaved[0].prop1, hit.getData().prop1);
            hit = hits.hit(1);
            assertEquals(instancesToBeSaved[1].prop1, hit.getData().prop1);
            hit = hits.hit(2);
            assertEquals(instancesToBeSaved[2].prop1, hit.getData().prop1);
        })

        //Test all properties have untokenized field
        TestCompassUtils.withCompassQueryBuilder(compass, {CompassQueryBuilder builder ->
            CompassQuery query = builder.queryString("${CompassConstants.UN_TOKENIZED_FIELD_PREFIX}prop1:\"${instancesToBeSaved[0].prop1}\"").toQuery();
            CompassHits hits = query.hits();
            def propList = application.getDomainClass(CompassTestObject.name).getProperties().findAll {it.name != "version" && it.name != RapidCMDBConstants.ERRORS_PROPERTY_NAME&& it.name != RapidCMDBConstants.OPERATION_PROPERTY_NAME&& it.name != RapidCMDBConstants.DYNAMIC_PROPERTY_STORAGE};
            hits.iterator().each {CompassHit hit->
                Resource res = hit.getResource();
                propList.each{domainProp->
                    assertTrue ("no untokenized property for ${domainProp.name}".toString(), res.getProperty("${CompassConstants.UN_TOKENIZED_FIELD_PREFIX}${domainProp.name}".toString()) != null);
                }
            }
        })

    }

}
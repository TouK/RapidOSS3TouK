package com.ifountain.compass

import com.ifountain.rcmdb.test.util.AbstractSearchableCompassTests
import org.compass.core.Compass
import org.codehaus.groovy.grails.commons.GrailsApplication
import com.ifountain.rcmdb.test.util.compass.TestCompassFactory
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.plugins.searchable.compass.mapping.DomainClassMappingHelper
import org.codehaus.groovy.grails.plugins.searchable.compass.mapping.CompassClassMapping
import com.ifountain.compass.converter.CompassStringConverter
import com.ifountain.rcmdb.test.util.compass.TestCompassUtils
import org.compass.core.CompassSession
import org.compass.core.CompassTransaction
import org.compass.core.CompassQuery
import org.compass.core.CompassHits
import org.compass.core.CompassQueryBuilder
import org.compass.core.CompassHit
import com.ifountain.compass.converter.CompassLongConverter
import org.compass.core.Resource

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

        compass = TestCompassFactory.getCompass(application, null, false, [
                "compass.converter.string.type": CompassStringConverter.class.name,
                "compass.converter.long.type": CompassLongConverter.class.name,
                "compass.converter.long.format": "#000000000000000000000000000000"

        ]);
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
            def propList = application.getDomainClass(CompassTestObject.name).getProperties().findAll {it.name != "id" && it.name != "version"};
            hits.iterator().each {CompassHit hit->
                Resource res = hit.getResource();
                propList.each{domainProp->
                    assertTrue ("no untokenized property for ${domainProp.name}".toString(), res.getProperty("${CompassConstants.UN_TOKENIZED_FIELD_PREFIX}${domainProp.name}".toString()) != null);
                }
            }
        })

    }

}
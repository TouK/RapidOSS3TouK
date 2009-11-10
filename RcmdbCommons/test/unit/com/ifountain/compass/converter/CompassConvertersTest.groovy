package com.ifountain.compass.converter

import com.ifountain.compass.CompassTestObject
import com.ifountain.compass.CompositeDirectoryWrapperProvider
import com.ifountain.compass.DefaultCompassConfiguration
import com.ifountain.compass.converter.CompassStringConverter
import com.ifountain.rcmdb.domain.util.DomainClassDefaultPropertyValueHolder
import com.ifountain.rcmdb.test.util.AbstractSearchableCompassTests
import com.ifountain.rcmdb.test.util.compass.TestCompassFactory
import com.ifountain.rcmdb.test.util.compass.TestCompassUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.compass.core.Compass
import org.compass.core.CompassQuery
import org.compass.core.CompassQueryBuilder
import com.ifountain.compass.CompassConstants
import java.text.SimpleDateFormat

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 17, 2009
* Time: 3:27:47 PM
* To change this template use File | Settings | File Templates.
*/
class CompassConvertersTest extends AbstractSearchableCompassTests {
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

    public void testGetNullPropertyValue()
    {
        DomainClassDefaultPropertyValueHolder.initialize ([CompassTestObject]);
        def defaultValueForProp1 = "defaultValue1";

        def convertedNullValue = CompassConverterUtils.getNullPropertyValue (CompassTestObject.name, "prop1", defaultValueForProp1);
        assertEquals(new CompassTestObject().prop1, convertedNullValue);

        def defaultValueForProp48 = "defaultValue48";
        convertedNullValue = CompassConverterUtils.getNullPropertyValue (CompassTestObject.name, "prop48", defaultValueForProp48);
        assertEquals(defaultValueForProp48, convertedNullValue);

        convertedNullValue = CompassConverterUtils.getNullPropertyValue (CompassTestObject.name, "${CompassConstants.UN_TOKENIZED_FIELD_PREFIX}prop1", defaultValueForProp1);
        assertEquals("If it is an untokenized field it should return default value of tokenized property", new CompassTestObject().prop1, convertedNullValue);

        DomainClassDefaultPropertyValueHolder.destroy();
        convertedNullValue = CompassConverterUtils.getNullPropertyValue (CompassTestObject.name, "prop1", defaultValueForProp1);
        assertEquals("If any exception occurred in DefaultPropertyHolder it will return given default value", defaultValueForProp1, convertedNullValue);

    }

    public void testLongMarshallingUnmarshalling()
    {
        DomainClassDefaultPropertyValueHolder.initialize ([CompassTestObject]);
        GrailsApplication application = TestCompassFactory.getGrailsApplication([CompassTestObject])
        ApplicationHolder.application = application;

        compass = TestCompassFactory.getCompass(application, null, false, DefaultCompassConfiguration.getDefaultSettings(null));
        def id = 0;
        def instancesToBeSaved = [
                new CompassTestObject(id:id++, prop49:null, prop50:null),
                new CompassTestObject(id:id++, prop49:4, prop50:7)
        ] as Object[];
        TestCompassUtils.saveToCompass(compass, instancesToBeSaved)


        TestCompassUtils.withCompassQueryBuilder (compass, {CompassQueryBuilder builder->
            CompassQuery query = builder.queryString ("prop49:0").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            def hits = query.hits();
            assertEquals(1, hits.length());
            CompassTestObject obj1 = hits.hit(0).getData();
            assertEquals (instancesToBeSaved[0].id, obj1.id);
            assertEquals (new Long(0), obj1.prop49);
            assertEquals (new Long(5), obj1.prop50);


            query = builder.queryString ("prop50:5").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            hits = query.hits();
            assertEquals(1, hits.length());
            obj1 = hits.hit(0).getData();
            assertEquals (instancesToBeSaved[0].id, obj1.id);
            assertEquals (new Long(0), obj1.prop49);
            assertEquals (new Long(5), obj1.prop50);


            query = builder.queryString ("prop49:4").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            hits = query.hits();
            assertEquals(1, hits.length());
            obj1 = hits.hit(0).getData();
            assertEquals (instancesToBeSaved[1].id, obj1.id);
            assertEquals (new Long(4), obj1.prop49);
            assertEquals (new Long(7), obj1.prop50);

            query = builder.queryString ("prop50:7").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            hits = query.hits();
            assertEquals(1, hits.length());
            obj1 = hits.hit(0).getData();
            assertEquals (instancesToBeSaved[1].id, obj1.id);
            assertEquals (new Long(4), obj1.prop49);
            assertEquals (new Long(7), obj1.prop50);
        })
    }

    public void testDateMarshallingUnmarshalling()
    {
        DomainClassDefaultPropertyValueHolder.initialize ([CompassTestObject]);
        GrailsApplication application = TestCompassFactory.getGrailsApplication([CompassTestObject])
        ApplicationHolder.application = application;

        ConfigObject  confObj = new ConfigObject();
        confObj.setProperty ("rapidcmdb.date.format", "yyyy-MM-dd HH:mm:ss");
        compass = TestCompassFactory.getCompass(application, null, false, DefaultCompassConfiguration.getDefaultSettings(confObj));
        def id = 0;
        def instancesToBeSaved = [
                new CompassTestObject(id:id++, prop51:null, prop52:null),
                new CompassTestObject(id:id++, prop51:new Date(), prop52:new Date(new Date().getTime()+1000))
        ] as Object[];
        TestCompassUtils.saveToCompass(compass, instancesToBeSaved)
        def df = new SimpleDateFormat("\"yyyy-MM-dd HH:mm:ss\"");
        def dfCompare = new SimpleDateFormat("\"yyyy-MM-dd\"");

        TestCompassUtils.withCompassQueryBuilder (compass, {CompassQueryBuilder builder->
            CompassQuery query = builder.queryString ("prop51:${df.format (new Date(0))}").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            def hits = query.hits();
            assertEquals(1, hits.length());
            CompassTestObject obj1 = hits.hit(0).getData();
            assertEquals (instancesToBeSaved[0].id, obj1.id);
            assertEquals (dfCompare.format(new Date(0)), dfCompare.format(obj1.prop51));
            assertEquals (dfCompare.format(new CompassTestObject().prop52), dfCompare.format(obj1.prop52));


            query = builder.queryString ("prop52:${df.format (new CompassTestObject().prop52)}").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            hits = query.hits();
            assertEquals(1, hits.length());
            obj1 = hits.hit(0).getData();
            assertEquals (instancesToBeSaved[0].id, obj1.id);
            assertEquals (dfCompare.format(new Date(0)), dfCompare.format(obj1.prop51));
            assertEquals (dfCompare.format(new CompassTestObject().prop52), dfCompare.format(obj1.prop52));

            query = builder.queryString ("prop51:${df.format (instancesToBeSaved[1].prop51)}").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            hits = query.hits();
            assertEquals(1, hits.length());
            obj1 = hits.hit(0).getData();
            assertEquals (instancesToBeSaved[1].id, obj1.id);
            assertEquals (dfCompare.format(instancesToBeSaved[1].prop51), dfCompare.format(obj1.prop51));
            assertEquals (dfCompare.format(instancesToBeSaved[1].prop52), dfCompare.format(obj1.prop52));

            query = builder.queryString ("prop52:${df.format (instancesToBeSaved[1].prop52)}").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            hits = query.hits();
            assertEquals(1, hits.length());
            obj1 = hits.hit(0).getData();
            assertEquals (instancesToBeSaved[1].id, obj1.id);
            assertEquals (dfCompare.format(instancesToBeSaved[1].prop51), dfCompare.format(obj1.prop51));
            assertEquals (dfCompare.format(instancesToBeSaved[1].prop52), dfCompare.format(obj1.prop52));
        })
    }


    public void testDoubleMarshallingUnmarshalling()
    {
        DomainClassDefaultPropertyValueHolder.initialize ([CompassTestObject]);
        GrailsApplication application = TestCompassFactory.getGrailsApplication([CompassTestObject])
        ApplicationHolder.application = application;

        compass = TestCompassFactory.getCompass(application, null, false, DefaultCompassConfiguration.getDefaultSettings(null));
        def id = 0;
        def instancesToBeSaved = [
                new CompassTestObject(id:id++, prop53:null, prop54:null),
                new CompassTestObject(id:id++, prop53:5.5, prop54:7.5)
        ] as Object[];
        TestCompassUtils.saveToCompass(compass, instancesToBeSaved)

        TestCompassUtils.withCompassQueryBuilder (compass, {CompassQueryBuilder builder->
            CompassQuery query = builder.queryString ("prop53:${0}").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            def hits = query.hits();
            assertEquals(1, hits.length());
            CompassTestObject obj1 = hits.hit(0).getData();
            assertEquals (instancesToBeSaved[0].id, obj1.id);
            assertEquals (new Double(0), obj1.prop53);
            assertEquals (new CompassTestObject().prop54, obj1.prop54);


            query = builder.queryString ("prop54:${new CompassTestObject().prop54}").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            hits = query.hits();
            assertEquals(1, hits.length());
            obj1 = hits.hit(0).getData();
            assertEquals (instancesToBeSaved[0].id, obj1.id);
            assertEquals (new Double(0), obj1.prop53);
            assertEquals (new CompassTestObject().prop54, obj1.prop54);

            query = builder.queryString ("prop53:${instancesToBeSaved[1].prop53}").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            hits = query.hits();
            assertEquals(1, hits.length());
            obj1 = hits.hit(0).getData();
            assertEquals (instancesToBeSaved[1].id, obj1.id);
            assertEquals (instancesToBeSaved[1].prop53, obj1.prop53);
            assertEquals (instancesToBeSaved[1].prop54, obj1.prop54);

            query = builder.queryString ("prop54:${instancesToBeSaved[1].prop54}").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            hits = query.hits();
            assertEquals(1, hits.length());
            obj1 = hits.hit(0).getData();
            assertEquals (instancesToBeSaved[1].id, obj1.id);
            assertEquals (instancesToBeSaved[1].prop53, obj1.prop53);
            assertEquals (instancesToBeSaved[1].prop54, obj1.prop54);
        })
    }

    public void testBooleanMarshallingUnmarshalling()
    {
        DomainClassDefaultPropertyValueHolder.initialize ([CompassTestObject]);
        GrailsApplication application = TestCompassFactory.getGrailsApplication([CompassTestObject])
        ApplicationHolder.application = application;

        compass = TestCompassFactory.getCompass(application, null, false, DefaultCompassConfiguration.getDefaultSettings(null));
        def id = 0;
        def instancesToBeSaved = [
                new CompassTestObject(id:id++, prop55:null, prop56:null),
                new CompassTestObject(id:id++, prop55:true, prop56:false)
        ] as Object[];
        TestCompassUtils.saveToCompass(compass, instancesToBeSaved)

        TestCompassUtils.withCompassQueryBuilder (compass, {CompassQueryBuilder builder->
            CompassQuery query = builder.queryString ("prop55:${false}").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            def hits = query.hits();
            assertEquals(1, hits.length());
            CompassTestObject obj1 = hits.hit(0).getData();
            assertEquals (instancesToBeSaved[0].id, obj1.id);
            assertEquals (new Boolean(false), obj1.prop55);
            assertEquals (new CompassTestObject().prop56, obj1.prop56);


            query = builder.queryString ("prop56:${new CompassTestObject().prop56}").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            hits = query.hits();
            assertEquals(1, hits.length());
            obj1 = hits.hit(0).getData();
            assertEquals (instancesToBeSaved[0].id, obj1.id);
            assertEquals (new Boolean(false), obj1.prop55);
            assertEquals (new CompassTestObject().prop56, obj1.prop56);

            query = builder.queryString ("prop55:${instancesToBeSaved[1].prop55}").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            hits = query.hits();
            assertEquals(1, hits.length());
            obj1 = hits.hit(0).getData();
            assertEquals (instancesToBeSaved[1].id, obj1.id);
            assertEquals (instancesToBeSaved[1].prop55, obj1.prop55);
            assertEquals (instancesToBeSaved[1].prop56, obj1.prop56);

            query = builder.queryString ("prop56:${instancesToBeSaved[1].prop56}").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            hits = query.hits();
            assertEquals(1, hits.length());
            obj1 = hits.hit(0).getData();
            assertEquals (instancesToBeSaved[1].id, obj1.id);
            assertEquals (instancesToBeSaved[1].prop55, obj1.prop55);
            assertEquals (instancesToBeSaved[1].prop56, obj1.prop56);
        })
    }

}
package com.ifountain.compass.integration

import com.ifountain.compass.CompassTestObject
import com.ifountain.compass.CompositeDirectoryWrapperProvider
import com.ifountain.rcmdb.test.util.AbstractSearchableCompassTests
import com.ifountain.rcmdb.test.util.compass.TestCompassFactory
import com.ifountain.rcmdb.test.util.compass.TestCompassUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.compass.core.Compass
import org.compass.core.CompassQuery
import org.compass.core.CompassQueryBuilder
import com.ifountain.compass.DefaultCompassConfiguration
import com.ifountain.compass.converter.CompassStringConverter
import com.ifountain.rcmdb.domain.util.DomainClassDefaultPropertyValueHolder
import com.ifountain.compass.CompassConstants
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import com.ifountain.rcmdb.domain.generation.ModelGenerator


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
        DomainClassDefaultPropertyValueHolder.initialize ([CompassTestObject]);
        GrailsApplication application = TestCompassFactory.getGrailsApplication([CompassTestObject])
        ApplicationHolder.application = application;
        Map mappings = [:];

        compass = TestCompassFactory.getCompass(application, null, false, DefaultCompassConfiguration.getDefaultSettings(null));
        def id = 0;
        def instancesToBeSaved = [
                new CompassTestObject(id: id++, prop1: "", prop48:"nonnullvalue", prop57:"anotherValue"),
                new CompassTestObject(id: id++, prop1: null, prop48:"nonnullvalue", prop57:"anotherValue"),
                new CompassTestObject(id: id++, prop1: "part1 ${CompassStringConverter.EMPTY_VALUE} part3", prop48:"nonnullvalue", prop57:"anotherValue"),
                new CompassTestObject(id: id++, prop1:"anotherValue", prop48: null, prop57:"anotherValue"),
                new CompassTestObject(id: id++, prop1:"anotherValue", prop48:"anotherValue", prop57: null),
        ] as Object[];
        TestCompassUtils.saveToCompass(compass, instancesToBeSaved)


        TestCompassUtils.withCompassQueryBuilder (compass, {CompassQueryBuilder builder->
            CompassQuery query = builder.queryString ("prop1:\"\"").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            def hits = query.hits();
            assertEquals(1, hits.length());
            CompassTestObject obj1 = hits.hit(0).getData();
            assertEquals (instancesToBeSaved[0].id, obj1.id);
            assertEquals ("", obj1.prop1);

            //test null property will be saved as its default value
            query = builder.queryString ("prop1:\"${CompassTestObject.newInstance().prop1}\"").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            hits = query.hits();
            assertEquals(1, hits.length());
            CompassTestObject obj2 = hits.hit(0).getData();
            assertEquals (instancesToBeSaved[1].id, obj2.id);
            assertEquals ("Null object properties should be saved as their default value if default value exist", CompassTestObject.newInstance().prop1, obj2.prop1);

            //test null property will be saved as its default value
            query = builder.queryString ("prop57:\"\"").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            hits = query.hits();
            assertEquals(1, hits.length());
            CompassTestObject obj5 = hits.hit(0).getData();
            assertEquals (instancesToBeSaved[4].id, obj5.id);
            assertEquals (instancesToBeSaved[4].prop1, obj5.prop1);
            println "<${obj5.prop57}>"
            assertEquals ("Null property will be saved as empty if property default value is empty", new CompassTestObject().prop57, obj5.prop57);


            //test null property will be saved as empty if its default value is null too
            query = builder.queryString ("prop48:\"\"").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            hits = query.hits();
            assertEquals(1, hits.length());
            CompassTestObject obj4 = hits.hit(0).getData();
            assertEquals (instancesToBeSaved[3].id, obj4.id);
            assertEquals ("Null object properties should be saved as empty if their default value not exist", "", obj4.prop48);
        })
    }

    public void testNullStringPropertyWithBlankDefaultValue()
    {
        def model1Name = "Model1";
        def prop1 = [name: "prop1", type: ModelGenerator.STRING_TYPE, defaultValue:"     "];
        def model1MetaProps = [name: model1Name]

        def modelProps = [prop1];
        def keyPropList = [prop1];


        def model1Text = ModelGenerationTestUtils.getModelText(model1MetaProps, modelProps, keyPropList, []);
        def gcl = new GroovyClassLoader();
        gcl.parseClass(model1Text);
        def modelClass = gcl.loadClass(model1Name)
        DomainClassDefaultPropertyValueHolder.initialize ([modelClass]);
        GrailsApplication application = TestCompassFactory.getGrailsApplication([modelClass], gcl)
        ApplicationHolder.application = application;
        Map mappings = [:];

        compass = TestCompassFactory.getCompass(application, null, false, DefaultCompassConfiguration.getDefaultSettings(null));
        def instanceToBeSaved1 = modelClass.newInstance();
        instanceToBeSaved1.id = 10;
        instanceToBeSaved1.prop1 = null;
        TestCompassUtils.saveToCompass(compass, [instanceToBeSaved1] as Object[])


        TestCompassUtils.withCompassQueryBuilder (compass, {CompassQueryBuilder builder->
            CompassQuery query = builder.queryString ("prop1:\"\"").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            def hits = query.hits();
            assertEquals(1, hits.length());
            def obj1 = hits.hit(0).getData();
            assertEquals (instanceToBeSaved1.id, obj1.id);
            assertEquals ("", obj1.prop1);
        })
    }

    public void testNullStringsWillBeConvetredToEmptyIfClassDoesnotExistInDefaultValues()
    {
        DomainClassDefaultPropertyValueHolder.destroy();
        GrailsApplication application = TestCompassFactory.getGrailsApplication([CompassTestObject])
        ApplicationHolder.application = application;
        Map mappings = [:];

        compass = TestCompassFactory.getCompass(application, null, false, DefaultCompassConfiguration.getDefaultSettings(null));
        def id = 0;
        def instancesToBeSaved = [
                new CompassTestObject(id: id++, prop1: null)
        ] as Object[];
        TestCompassUtils.saveToCompass(compass, instancesToBeSaved)


        TestCompassUtils.withCompassQueryBuilder (compass, {CompassQueryBuilder builder->
            CompassQuery query = builder.queryString ("prop1:\"\"").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            def hits = query.hits();
            assertEquals(1, hits.length());
            CompassTestObject obj1 = hits.hit(0).getData();
            assertEquals (instancesToBeSaved[0].id, obj1.id);
            assertEquals ("", obj1.prop1);
        })
    }
}
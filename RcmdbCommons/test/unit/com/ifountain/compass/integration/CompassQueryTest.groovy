package com.ifountain.compass.integration

import com.ifountain.rcmdb.test.util.AbstractSearchableCompassTests
import org.compass.core.Compass
import org.codehaus.groovy.grails.commons.GrailsApplication
import com.ifountain.compass.CompositeDirectoryWrapperProvider
import com.ifountain.compass.CompassTestObject
import com.ifountain.rcmdb.test.util.compass.TestCompassUtils
import com.ifountain.rcmdb.test.util.compass.TestCompassFactory
import org.compass.core.CompassQuery
import org.compass.core.CompassQueryBuilder
import com.ifountain.compass.DefaultCompassConfiguration
import org.compass.core.CompassHits
import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.ifountain.compass.utils.QueryParserUtils
import com.ifountain.rcmdb.util.RapidStringUtilities
import com.ifountain.compass.query.ExactQueryParseException
import org.compass.core.engine.SearchEngineQueryParseException
import org.apache.commons.lang.exception.ExceptionUtils;
/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 18, 2009
* Time: 2:01:28 PM
* To change this template use File | Settings | File Templates.
*/
class CompassQueryTest extends AbstractSearchableCompassTests {
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

    public void testExactPhrase()
    {
        GrailsApplication application = TestCompassFactory.getGrailsApplication([CompassTestObject])
        ApplicationHolder.application = application;
        CompositeDirectoryWrapperProvider provider = new CompositeDirectoryWrapperProvider();
        Map mappings = [:];

        compass = TestCompassFactory.getCompass(application, null, false, DefaultCompassConfiguration.getDefaultSettings(null));
        def id = 0;
        def instancesToBeSaved = [
                new CompassTestObject(id: id++, prop1: "x y z"),
                new CompassTestObject(id: id++, prop1: "x y"),
                new CompassTestObject(id: id++, prop1: "y z"),
                new CompassTestObject(id: id++, prop1: "x"),
                new CompassTestObject(id: id++, prop1: "${QueryParserUtils.EXACT_QUERY_START}x x${QueryParserUtils.EXACT_QUERY_END}"),
                new CompassTestObject(id: id++, prop1: "${QueryParserUtils.EXACT_QUERY_START}x"),
                new CompassTestObject(id: id++, prop1: "x${QueryParserUtils.EXACT_QUERY_END}")
        ] as Object[];
        TestCompassUtils.saveToCompass(compass, instancesToBeSaved)


        TestCompassUtils.withCompassQueryBuilder (compass, {CompassQueryBuilder builder->
            String propQuery = "x"
            CompassQuery query = builder.queryString ("prop1:${QueryParserUtils.toExactQuery(propQuery)}").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            CompassHits hits = query.hits();
            assertEquals (1, hits.length());
            assertEquals (instancesToBeSaved[3].id, hits.data(0).id);


            propQuery = "${QueryParserUtils.EXACT_QUERY_START}X"
            query = builder.queryString ("prop1:${QueryParserUtils.toExactQuery(propQuery)}").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            hits = query.hits();
            assertEquals (1, hits.length());
            assertEquals (instancesToBeSaved[5].id, hits.data(0).id);

            propQuery = "x${QueryParserUtils.EXACT_QUERY_END}"
            query = builder.queryString ("prop1:${QueryParserUtils.toExactQuery(propQuery)}").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            hits = query.hits();
            assertEquals (1, hits.length());
            assertEquals (instancesToBeSaved[6].id, hits.data(0).id);


            propQuery = "x\\${QueryParserUtils.EXACT_QUERY_END}"
            query = builder.queryString ("prop1:\"${propQuery}\"").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            hits = query.hits();
            assertEquals (2, hits.length());
            assertEquals (instancesToBeSaved[4].id, hits.data(0).id);
            assertEquals (instancesToBeSaved[6].id, hits.data(1).id);


            propQuery = "\\${QueryParserUtils.EXACT_QUERY_START}x x\\${QueryParserUtils.EXACT_QUERY_END}"
            query = builder.queryString ("prop1:\"${propQuery}\"").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            hits = query.hits();
            assertEquals (1, hits.length());
            assertEquals (instancesToBeSaved[4].id, hits.data(0).id);
        });
    }

    public void testWithMultipleEmptySpace()
    {
        GrailsApplication application = TestCompassFactory.getGrailsApplication([CompassTestObject])
        ApplicationHolder.application = application;
        CompositeDirectoryWrapperProvider provider = new CompositeDirectoryWrapperProvider();
        Map mappings = [:];

        compass = TestCompassFactory.getCompass(application, null, false, DefaultCompassConfiguration.getDefaultSettings(null));
        def id = 0;
        def instancesToBeSaved = [
                new CompassTestObject(id: id++, prop2: "x"),
                new CompassTestObject(id: id++, prop2: "x y"),
                new CompassTestObject(id: id++, prop2: "x y z"),
                new CompassTestObject(id: id++, prop2: "x y z t"),
        ] as Object[];
        TestCompassUtils.saveToCompass(compass, instancesToBeSaved)


        TestCompassUtils.withCompassQueryBuilder (compass, {CompassQueryBuilder builder->
            String propQuery = "x"
            CompassQuery query = builder.queryString ("prop2:x").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            CompassHits hits = query.hits();
            assertEquals (4, hits.length());
            assertEquals (instancesToBeSaved[0].id, hits.data(0).id);
            assertEquals (instancesToBeSaved[1].id, hits.data(1).id);
            assertEquals (instancesToBeSaved[2].id, hits.data(2).id);
            assertEquals (instancesToBeSaved[3].id, hits.data(3).id);

            query = builder.queryString ("prop2:\"x y\"").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            hits = query.hits();
            assertEquals (3, hits.length());
            assertEquals (instancesToBeSaved[1].id, hits.data(0).id);
            assertEquals (instancesToBeSaved[2].id, hits.data(1).id);
            assertEquals (instancesToBeSaved[3].id, hits.data(2).id);

            query = builder.queryString ("prop2:\"x y z\"").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            hits = query.hits();
            assertEquals (2, hits.length());
            assertEquals (instancesToBeSaved[2].id, hits.data(0).id);
            assertEquals (instancesToBeSaved[3].id, hits.data(1).id);

            query = builder.queryString ("prop2:\"x y z t\"").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            hits = query.hits();
            assertEquals (1, hits.length());
            assertEquals (instancesToBeSaved[3].id, hits.data(0).id);
        });
    }


    public void testExactPhraseWithEmptyString()
    {
        GrailsApplication application = TestCompassFactory.getGrailsApplication([CompassTestObject])
        ApplicationHolder.application = application;
        CompositeDirectoryWrapperProvider provider = new CompositeDirectoryWrapperProvider();
        Map mappings = [:];

        compass = TestCompassFactory.getCompass(application, null, false, DefaultCompassConfiguration.getDefaultSettings(null));
        def id = 0;
        def instancesToBeSaved = [
                new CompassTestObject(id: id++, prop1: ""),
                new CompassTestObject(id: id++, prop1: "x")
        ] as Object[];
        TestCompassUtils.saveToCompass(compass, instancesToBeSaved)


        TestCompassUtils.withCompassQueryBuilder (compass, {CompassQueryBuilder builder->
            String propQuery = ""
            CompassQuery query = builder.queryString ("prop1:${QueryParserUtils.toExactQuery(propQuery)}").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            CompassHits hits = query.hits();
            assertEquals (1, hits.length());
            assertEquals (instancesToBeSaved[0].id, hits.data(0).id);

            query = builder.queryString ("alias:* NOT prop1:${QueryParserUtils.toExactQuery(propQuery)}").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            hits = query.hits();
            assertEquals (1, hits.length());
            assertEquals (instancesToBeSaved[1].id, hits.data(0).id);

        });
    }

    public void testExactPhraseValidQueries()
    {
        GrailsApplication application = TestCompassFactory.getGrailsApplication([CompassTestObject])
        ApplicationHolder.application = application;
        CompositeDirectoryWrapperProvider provider = new CompositeDirectoryWrapperProvider();
        Map mappings = [:];

        compass = TestCompassFactory.getCompass(application, null, false, DefaultCompassConfiguration.getDefaultSettings(null));
        def id = 0;
        def instancesToBeSaved = [
        ] as Object[];
        TestCompassUtils.saveToCompass(compass, instancesToBeSaved)
        TestCompassUtils.withCompassQueryBuilder (compass, {CompassQueryBuilder builder->

            String propName = "prop1";
            def propQueriesToBeTested = ["\\(deneme"]
            propQueriesToBeTested.each{propQuery->
                try
                {
                    builder.queryString ("${propName}:\"${propQuery}\"").toQuery();
                }
                catch(Throwable e)
                {
                    fail("Should not throw exception since exact match query is for propQuery <${propQuery}>. Exception is ${e.toString()}");
                }
            }
        });
    }

    
    public void testExactPhrasethrowsExceptionIfQueryIsInvalid()
    {
        GrailsApplication application = TestCompassFactory.getGrailsApplication([CompassTestObject])
        ApplicationHolder.application = application;
        CompositeDirectoryWrapperProvider provider = new CompositeDirectoryWrapperProvider();
        Map mappings = [:];

        compass = TestCompassFactory.getCompass(application, null, false, DefaultCompassConfiguration.getDefaultSettings(null));
        def id = 0;
        def instancesToBeSaved = [
        ] as Object[];
        TestCompassUtils.saveToCompass(compass, instancesToBeSaved)

        TestCompassUtils.withCompassQueryBuilder (compass, {CompassQueryBuilder builder->
            String propName = "prop1";
            def propQueriesToBeTested = ["(deneme"]
            propQueriesToBeTested.each{propQuery->
                try
                {
                    builder.queryString ("${propName}:\"${propQuery}\"").toQuery();
                    fail("Should throw exception since exact match query is not valid for propQuery <${propQuery}>");
                }
                catch(SearchEngineQueryParseException e)
                {
                    assertTrue (e.toString().indexOf(new ExactQueryParseException(propName, propQuery).getMessage())>=0);
                }
            }



        });
    }

    public void testAllQueriesWorksProperly()
    {
        GrailsApplication application = TestCompassFactory.getGrailsApplication([CompassTestObject])
        ApplicationHolder.application = application;
        CompositeDirectoryWrapperProvider provider = new CompositeDirectoryWrapperProvider();
        Map mappings = [:];

        compass = TestCompassFactory.getCompass(application, null, false, DefaultCompassConfiguration.getDefaultSettings(null));
        def id = 0;
        def instancesToBeSaved = [
                new CompassTestObject(id: id++, prop1: "x"),
        ] as Object[];
        TestCompassUtils.saveToCompass(compass, instancesToBeSaved)


        TestCompassUtils.withCompassQueryBuilder(compass, {CompassQueryBuilder builder ->
            CompassQuery query = builder.queryString("x").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            CompassHits hits = query.hits();
            assertEquals(1, hits.length());

            builder.queryString("${QueryParserUtils.toExactQuery ("x")}").toQuery().addSort("id", CompassQuery.SortDirection.AUTO);
            hits = query.hits();
            assertEquals(1, hits.length());
        });

    }
}
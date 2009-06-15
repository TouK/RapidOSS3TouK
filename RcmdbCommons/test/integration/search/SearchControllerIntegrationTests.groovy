package search

import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import org.codehaus.groovy.grails.commons.ApplicationHolder
import datasource.BaseDatasource
import com.ifountain.rcmdb.test.util.IntegrationTestUtils
import com.ifountain.rcmdb.converter.RapidConvertUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jun 15, 2009
* Time: 5:48:33 PM
* To change this template use File | Settings | File Templates.
*/
class SearchControllerIntegrationTests extends RapidCmdbIntegrationTestCase{
    static transactional = false;
    def RsEventJournal;
    void setUp() throws Exception {
        super.setUp();
        RsEventJournal = ApplicationHolder.application.classLoader.loadClass("RsEventJournal")
        BaseDatasource.removeAll();
        RsEventJournal.removeAll();
    }

    void tearDown() throws Exception {
        super.tearDown();
    }
    public void testSearch()
    {
        def expectedProperties = ["id", "rsAlias", "rsOwner", "sortOrder", "name"]
        for (i in 0..9) {
            BaseDatasource.add(name: "ds${i}");
        }

        def maxCount = 5;
        def controller = new SearchController();
        controller.params["max"] = "${maxCount}";
        controller.params["sort"] = "name"
        controller.params["order"] = "desc"
        controller.params["searchIn"] = BaseDatasource.class.name
        controller.index();
        def objectsXml = new XmlSlurper().parseText(controller.response.contentAsString);
        def objects = objectsXml.Object;
        assertEquals(maxCount, objects.size());
        assertEquals("ds9", objects[0].@name.toString());

        def attributes = objects[0].attributes();
        assertEquals(expectedProperties.size(), attributes.size())


        //test by changing max number and sort order
        IntegrationTestUtils.resetController (controller);
        maxCount = 10;
        controller.params["max"] = "${maxCount}";
        controller.params["sort"] = "name"
        controller.params["order"] = "asc"
        controller.params["searchIn"] = BaseDatasource.class.name
        controller.index();
        objectsXml = new XmlSlurper().parseText(controller.response.contentAsString);
        objects = objectsXml.Object;
        assertEquals(maxCount, objects.size());
        assertEquals("ds0", objects[0].@name.toString());
    }

    public void testSearchWithQuery()
    {
        def expectedProperties = ["id", "rsAlias", "rsOwner", "sortOrder", "name"]
        for (i in 0..9) {
            BaseDatasource.add(name: "ds${i}");
        }

        def maxCount = 5;
        def controller = new SearchController();
        controller.params["max"] = "${maxCount}";
        controller.params["query"] = "name:ds0"
        controller.params["sort"] = "name"
        controller.params["order"] = "desc"
        controller.params["searchIn"] = BaseDatasource.class.name
        controller.index();
        def objectsXml = new XmlSlurper().parseText(controller.response.contentAsString);
        def objects = objectsXml.Object;
        assertEquals(1, objects.size());
        assertEquals("ds0", objects[0].@name.toString());

    }

    public void testSearchWithDifferentSortProperty()
    {
        def expectedProperties = ["id", "rsAlias", "rsOwner", "sortOrder", "name"]
        for (i in 0..9) {
            BaseDatasource.add(name: "ds${9-i}");
        }

        def searchContParams = [:];
        def maxCount = 5;
        searchContParams["max"] = "${maxCount}";
        searchContParams["sort"] = "name"
        searchContParams["order"] = "desc"
        searchContParams["searchIn"] = BaseDatasource.class.name

        def controller = new SearchController();
        controller.params.putAll(searchContParams)
        controller.index();
        def objectsXml = new XmlSlurper().parseText(controller.response.contentAsString);
        def objects = objectsXml.Object;
        assertEquals(maxCount, objects.size());
        assertEquals("ds9", objects[0].@name.toString());


        IntegrationTestUtils.resetController (controller);
        searchContParams.sort = "id"
        controller.params.putAll(searchContParams)
        controller.index();
        objectsXml = new XmlSlurper().parseText(controller.response.contentAsString);
        objects = objectsXml.Object;
        assertEquals(maxCount, objects.size());
        assertEquals("ds0", objects[0].@name.toString());
    }

    public void testSearchWithDateProps()
    {
        def datePropValues = [];
        10.times {
            datePropValues.add(new Date(System.currentTimeMillis() + it * 1000));
        }

        datePropValues.each {
            RsEventJournal.add(rsTime: it);
        }
        def controller = new SearchController();
        controller.params["max"] = "10";
        controller.params["sort"] = "id"
        controller.params["order"] = "asc"
        controller.params["searchIn"] = RsEventJournal.name
        controller.index();
        def objectsXml = new XmlSlurper().parseText(controller.response.contentAsString);
        def objects = objectsXml.Object;
        assertEquals(10, objects.size());
        for (int i = 0; i < datePropValues.size(); i++) {
            assertEquals(RapidConvertUtils.getInstance().lookup(String).convert(String, datePropValues[i]), objects[i].@rsTime.toString());
        }
    }
}
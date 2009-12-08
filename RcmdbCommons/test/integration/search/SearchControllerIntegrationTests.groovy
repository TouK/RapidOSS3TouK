package search

import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import org.codehaus.groovy.grails.commons.ApplicationHolder
import datasource.BaseDatasource
import com.ifountain.rcmdb.test.util.IntegrationTestUtils
import com.ifountain.rcmdb.converter.RapidConvertUtils
import org.apache.commons.lang.StringUtils

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
    def SmartsObjectModel;
    void setUp() throws Exception {
        super.setUp();
        RsEventJournal = ApplicationHolder.application.classLoader.loadClass("RsEventJournal")
        SmartsObjectModel = ApplicationHolder.application.classLoader.loadClass("SmartsObject")
        SmartsObjectModel.removeAll();
        BaseDatasource.removeAll();
        RsEventJournal.removeAll();
    }

    void tearDown() throws Exception {
        super.tearDown();
    }
    public void testSearch()
    {
        def expectedProperties = ["id", "rsAlias", "rsOwner", "__sortOrder", "name", "rsInsertedAt", "rsUpdatedAt"]
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


    public void testSearchWithPropertyList()
    {
        def propertyList = ["rsOwner", "name"]
        def expectedProperties = ["id", "rsAlias", "__sortOrder"]
        expectedProperties.addAll (propertyList)
        for (i in 0..9) {
            BaseDatasource.add(name: "ds${i}");
        }

        def maxCount = 5;
        def controller = new SearchController();
        controller.params["max"] = "${maxCount}";
        controller.params["sort"] = "name"
        controller.params["order"] = "desc"
        controller.params["propertyList"] = propertyList.join(" ,")
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


    public void testExportAsXml()
    {
        def expectedProperties = ["id", "rsAlias", "rsOwner", "name", "rsInsertedAt", "rsUpdatedAt"]
        def expectedDatasources = []
        for (i in 0..9) {
            BaseDatasource ds = BaseDatasource.add(name: "ds${i}");
            expectedDatasources.add([id:ds.id.toString(), rsAlias:BaseDatasource.class.name, rsOwner:ds.rsOwner, name:ds.name])
        }
        expectedDatasources = expectedDatasources.sort{it.name}.reverse()

        def maxCount = 5;
        def controller = new SearchController();
        controller.params["max"] = "${maxCount}";
        controller.params["sort"] = "name"
        controller.params["order"] = "desc"
        controller.params["searchIn"] = BaseDatasource.class.name
        controller.params["type"] = "xml"
        controller.export();
        def content = controller.response.contentAsString;
        checkXmlFileContent(content, maxCount, expectedProperties, expectedDatasources);


        //test by changing max number and sort order
        expectedDatasources = expectedDatasources.reverse()
        IntegrationTestUtils.resetController (controller);
        maxCount = 10;
        controller.params["max"] = "${maxCount}";
        controller.params["sort"] = "name"
        controller.params["order"] = "asc"
        controller.params["searchIn"] = BaseDatasource.class.name
        controller.params["type"] = "xml"
        controller.export();
        content = controller.response.contentAsString;
        checkXmlFileContent(content, maxCount, expectedProperties, expectedDatasources);
    }

    public void testExportAsXmlWithFederatedProps()
    {
        def federatedProperties = SmartsObjectModel.getFederatedPropertyList();
        assertTrue (federatedProperties.size() > 0);
        def expectedProperties = SmartsObjectModel.getNonFederatedPropertyList().name;
        expectedProperties.add("rsAlias");
        def smartsObject = SmartsObjectModel.add(name:"object1");
        def expectedSmartsobjectPropValues = [];
        def propvalues = [:]
        SmartsObjectModel.getNonFederatedPropertyList().each{
            propvalues[it.name] = smartsObject[it.name];
        }
        propvalues["rsAlias"] = SmartsObjectModel.name;

        def maxCount = 5;
        def controller = new SearchController();
        controller.params["max"] = "${maxCount}";
        controller.params["sort"] = "name"
        controller.params["order"] = "desc"
        controller.params["searchIn"] = SmartsObjectModel.name
        controller.params["type"] = "xml"
        controller.export();
        String content = controller.response.contentAsString;
        checkXmlFileContent(content, 1, expectedProperties, expectedSmartsobjectPropValues);
        federatedProperties.each{
            assertTrue (content.indexOf(it.name) < 0);
        }

    }

    public void testExportAsCsv()
    {
        def expectedProperties = ["id", "rsAlias", "rsOwner", "name", "rsInsertedAt", "rsUpdatedAt"]
        def expectedDatasources = []
        for (i in 0..9) {
            BaseDatasource ds = BaseDatasource.add(name: "ds${i}");
            //prop to be escaped;
            ds.rsOwner = "\"p";
            expectedDatasources.add([id:ds.id.toString(), rsAlias:BaseDatasource.class.name, rsOwner:ds.rsOwner, name:ds.name])
        }
        expectedDatasources = expectedDatasources.sort{it.name}.reverse()

        def maxCount = 5;
        def controller = new SearchController();
        controller.params["max"] = "${maxCount}";
        controller.params["sort"] = "name"
        controller.params["order"] = "desc"
        controller.params["searchIn"] = BaseDatasource.class.name
        controller.params["type"] = "csv"
        controller.export();
        String content = controller.response.contentAsString;
        checkCsvFileContent(content, maxCount, expectedProperties, expectedDatasources);


        //test by changing max number and sort order
        expectedDatasources = expectedDatasources.reverse()
        IntegrationTestUtils.resetController (controller);
        maxCount = 10;
        controller.params["max"] = "${maxCount}";
        controller.params["sort"] = "name"
        controller.params["order"] = "asc"
        controller.params["searchIn"] = BaseDatasource.class.name
        controller.params["type"] = "csv"
        controller.export();
        content = controller.response.contentAsString;
        checkCsvFileContent(content, maxCount, expectedProperties, expectedDatasources);
    }

    public void testExportAsCsvWithFederatedProperties()
    {
        def federatedProperties = SmartsObjectModel.getFederatedPropertyList();
        assertTrue (federatedProperties.size() > 0);
        def expectedProperties = SmartsObjectModel.getNonFederatedPropertyList().name;
        expectedProperties.add("rsAlias");
        def smartsObject = SmartsObjectModel.add(name:"object1");
        def expectedSmartsobjectPropValues = [];
        def propvalues = [:]
        SmartsObjectModel.getNonFederatedPropertyList().each{
            propvalues[it.name] = smartsObject[it.name];
        }
        propvalues["rsAlias"] = SmartsObjectModel.name;

        def maxCount = 5;
        def controller = new SearchController();
        controller.params["max"] = "${maxCount}";
        controller.params["sort"] = "name"
        controller.params["order"] = "desc"
        controller.params["searchIn"] = SmartsObjectModel.name
        controller.params["type"] = "csv"
        controller.export();
        String content = controller.response.contentAsString;
        checkCsvFileContent(content, 1, expectedProperties, expectedSmartsobjectPropValues);
        federatedProperties.each{
            assertTrue (content.indexOf(it.name) < 0);
        }
    }

    private void checkCsvFileContent(content, maxCount, expectedProperties, expectedObjectPropValues)
    {
        def lines = StringUtils.split(content, "\n");
        def colNames = StringUtils.split(lines[0], ",");
        assertEquals ("Expected ${expectedProperties} bu was ${colNames}".toString(), expectedProperties.size(), colNames.length)
        def objects = [];
        for(int i=1; i < lines.length; i++)
        {
            def line = lines[i];
            def colValues = StringUtils.split(line, ",");
            def props = [:]
            for(int j=0; j < colNames.length; j++)
            {
                def colName = colNames[j];
                def colValue = colValues[j];
                props[colName] = colValue;
            }
            objects.add(props);

        }
        assertEquals(maxCount, objects.size());
        objects.eachWithIndex{obj,i->
            assertEquals(expectedProperties.size(), obj.size())
            def baseDsProps = expectedObjectPropValues[i];
            baseDsProps.each{String propName, Object value->
                String escapedValue = value.toString().replaceAll("\"", "\"\"");
                assertEquals(obj.toString(), "\"${escapedValue}\"".toString(), obj["\"${propName}\""]);
            }
        }
    }

    private void checkXmlFileContent(content, maxCount, expectedProperties, expectedObjectPropValues)
    {
        def objectsXml = new XmlSlurper().parseText(content);
        def objects = objectsXml.Object;
        assertEquals(maxCount, objects.size());
        objects.eachWithIndex{obj,i->
            def attributes = obj.attributes();
            assertEquals(expectedProperties.size(), attributes.size())
            def baseDsProps = expectedObjectPropValues[i];
            baseDsProps.each{String propName, Object value->
                assertEquals(value, obj.@"${propName}".toString());
            }
        }
    }

    public void testSearchWithFederatedProperties()
    {
        def federatedProperties = SmartsObjectModel.getFederatedPropertyList();
        assertTrue (federatedProperties.size() > 0);
        def expectedProperties = SmartsObjectModel.getNonFederatedPropertyList().name;
        expectedProperties.add("__sortOrder");
        expectedProperties.add("rsAlias");
        SmartsObjectModel.add(name:"object1");

        def maxCount = 5;
        def controller = new SearchController();
        controller.params["max"] = "${maxCount}";
        controller.params["sort"] = "name"
        controller.params["order"] = "desc"
        controller.params["searchIn"] =SmartsObjectModel.name
        controller.index();
        String content = controller.response.contentAsString;
        def objectsXml = new XmlSlurper().parseText(content);
        def objects = objectsXml.Object;
        assertEquals(1, objects.size());
        assertEquals("object1", objects[0].@name.toString());

        def attributes = objects[0].attributes();
        assertEquals(expectedProperties.size(), attributes.size())

        federatedProperties.each{
            assertTrue (content.indexOf(it.name) < 0);
            assertFalse (attributes.containsKey(it.name));
        }
    }

    public void testSearchWithQuery()
    {
        def expectedProperties = ["id", "rsAlias", "rsOwner", "__sortOrder", "name"]
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
        def expectedProperties = ["id", "rsAlias", "rsOwner", "__sortOrder", "name"]
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
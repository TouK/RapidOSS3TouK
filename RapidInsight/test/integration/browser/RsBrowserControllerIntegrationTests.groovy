package browser

import auth.RsUser
import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import connection.Connection
import connection.SnmpConnection
import datasource.BaseDatasource
import search.SearchQuery
import search.SearchQueryGroup
import com.ifountain.rcmdb.converter.RapidConvertUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 6, 2009
* Time: 2:01:07 PM
* To change this template use File | Settings | File Templates.
*/
class RsBrowserControllerIntegrationTests extends RapidCmdbIntegrationTestCase {
    static transactional = false;
    def RsEventJournal;
    void setUp() throws Exception {
        super.setUp();
        RsEventJournal = ApplicationHolder.application.classLoader.loadClass("RsEventJournal")
        BaseDatasource.removeAll();
        RsEventJournal.removeAll();
        Connection.removeAll();
        SearchQuery.removeAll();
        SearchQueryGroup.removeAll();
    }

    void tearDown() throws Exception {
        super.tearDown();
    }

    void testClasses() throws Exception {
        def controller = new RsBrowserController();
        controller.params["sort"] = "className"
        controller.params["order"] = "desc"
        def model = controller.classes();

        def domainClasses = controller.grailsApplication.domainClasses;
        def domainClassList = domainClasses.sort {first, second ->
            return first.fullName < second.fullName ? 1 : -1;
        }
        def actualDomainClassList = model.domainClassList;
        assertEquals(domainClassList.size(), actualDomainClassList.size())
        domainClassList.eachWithIndex {domainClass, i ->
            assertEquals(domainClass, actualDomainClassList[i])
        }
    }

    void testClassesWithXml() {
        def controller = new RsBrowserController();
        controller.params["format"] = "xml";
        controller.classes();
        def domainClassArray = [];
        def gApp = controller.grailsApplication
        def domainClasses = gApp.domainClasses;

        def classesXml = new XmlSlurper().parseText(controller.response.contentAsString);
        def classTypes = classesXml.Class;
        assertEquals(2, classTypes.size())
        def systemClasses = classTypes[0]
        def appClasses = classTypes[1]

        def rootClasses = systemClasses.Class;
        rootClasses.each {
            domainClassArray.add(it.@name)
            def actualClass = gApp.getDomainClass(it.@name.toString()).clazz

            assertTrue(actualClass.name.indexOf(".") > -1)
            assertEquals(Object.class, actualClass.superclass)
            _assertChildClasses(it, gApp, domainClassArray, true);
        }
        rootClasses = appClasses.Class;
        rootClasses.each {
            domainClassArray.add(it.@name)
            def actualClass = gApp.getDomainClass(it.@name.toString()).clazz
            assertTrue(actualClass.name.indexOf(".") < 0)
            assertEquals(Object.class, actualClass.superclass)
            _assertChildClasses(it, gApp, domainClassArray, false);
        }
        assertEquals(domainClasses.size(), domainClassArray.size());
    }

    def _assertChildClasses(classNode, gApp, domainClassArray, isSystem) {
        def childClasses = classNode.Class;
        childClasses.each {
            domainClassArray.add(it.@name)
            def actualClass = gApp.getDomainClass(it.@name.toString()).clazz
            assertEquals(classNode.@name.toString(), actualClass.superclass.name);
            if (isSystem) {
                assertTrue(actualClass.name.indexOf(".") > -1)
            }
            else {
                assertFalse(actualClass.name.indexOf(".") > -1)
            }
            _assertChildClasses(it, gApp, domainClassArray, isSystem);
        }
    }


    void testListDomainWithAllProperties() {

        def expectedProperties = BaseDatasource.getPropertiesList().findAll {it.name != "id" && !it.isRelation && !it.isOperationProperty || (it.isRelation && (it.isOneToOne() || it.isManyToOne()))}
        for (i in 0..9) {
            BaseDatasource.add(name: "ds${i}");
        }
        def controller = new RsBrowserController();
        controller.params["max"] = "5";
        controller.params["sort"] = "name"
        controller.params["order"] = "desc"
        controller.params["domain"] = "baseDatasource"
        controller.listDomain();

        def model = controller.modelAndView.model;
        def datasources = model.objectList;
        assertEquals(5, datasources.size());
        assertEquals("ds9", datasources[0].name)

        def propertyList = model.propertyList;
        assertEquals(expectedProperties.size(), propertyList.size())
        assertEquals(10, model.count)

    }

    public void testListDomainWithDatePropertiesAsXml() {
        def expectedProperties = RsEventJournal.getPropertiesList().findAll {!it.isRelation && !it.isOperationProperty || (it.isRelation && (it.isOneToOne() || it.isManyToOne()))}
        def datePropValues = [];
        10.times {
            datePropValues.add(new Date(System.currentTimeMillis() + it * 1000));
        }

        datePropValues.each {
            RsEventJournal.add(rsTime: it);
        }
        def controller = new RsBrowserController();
        controller.params["max"] = "10";
        controller.params["sort"] = "id"
        controller.params["order"] = "asc"
        controller.params["domain"] = "rsEventJournal"
        controller.params["format"] = "xml"
        controller.listDomain();
        def objectsXml = new XmlSlurper().parseText(controller.response.contentAsString);
        def objects = objectsXml.Object;
        assertEquals(10, objects.size());
        for (int i = 0; i < datePropValues.size(); i++) {
            assertEquals(RapidConvertUtils.getInstance().lookup(String).convert(String, datePropValues[i]), objects[i].@rsTime.toString());
        }
    }

    void testListDomainWithAllPropertiesWithXml() {
        def expectedProperties = BaseDatasource.getPropertiesList().findAll {!it.isRelation && !it.isOperationProperty || (it.isRelation && (it.isOneToOne() || it.isManyToOne()))}
        for (i in 0..9) {
            BaseDatasource.add(name: "ds${i}");
        }
        def controller = new RsBrowserController();
        controller.params["max"] = "5";
        controller.params["sort"] = "name"
        controller.params["order"] = "desc"
        controller.params["domain"] = "baseDatasource"
        controller.params["format"] = "xml"
        controller.listDomain();
        def objectsXml = new XmlSlurper().parseText(controller.response.contentAsString);
        def objects = objectsXml.Object;
        assertEquals(5, objects.size());
        assertEquals("ds9", objects[0].@name.toString());

        def attributes = objects[0].attributes();
        assertEquals(expectedProperties.size(), attributes.size())
    }

    void testListDomainWithOnlyKeyProperties() {
        for (i in 0..9) {
            Connection.add(name: "conn${i}");
        }
        def controller = new RsBrowserController();
        controller.params["max"] = "5";
        controller.params["sort"] = "name"
        controller.params["order"] = "desc"
        controller.params["domain"] = "connection"
        controller.listDomain();

        def model = controller.modelAndView.model;
        def datasources = model.objectList;
        assertEquals(5, datasources.size());
        assertEquals("conn9", datasources[0].name)

        def propertyList = model.propertyList;
        assertEquals(BaseDatasource.keySet().size(), propertyList.size())
        assertEquals(10, model.count)
    }

    void testListDomainWithOnlyKeyPropertiesWithXml() {
        for (i in 0..9) {
            Connection.add(name: "conn${i}");
        }
        def controller = new RsBrowserController();
        controller.params["max"] = "5";
        controller.params["sort"] = "name"
        controller.params["order"] = "desc"
        controller.params["domain"] = "connection"
        controller.params["format"] = "xml"
        controller.listDomain();
        def objectsXml = new XmlSlurper().parseText(controller.response.contentAsString);
        def objects = objectsXml.Object;
        assertEquals(5, objects.size());
        assertEquals("conn9", objects[0].@name.toString());

        def attributes = objects[0].attributes();
        assertEquals(BaseDatasource.keySet().size() + 1, attributes.size()) // id is included if not in keyset
    }

    void testShow() {
        def connection = Connection.add(name: "conn1");
        assertFalse(connection.hasErrors())

        def controller = new RsBrowserController();
        controller.params["id"] = "${connection.id}"
        controller.params["domain"] = "connection"
        controller.show();

        def model = controller.modelAndView.model;
        def propertiesList = model.propertyList;

        def props = Connection.getPropertiesList();
        assertEquals(props.size(), propertiesList.size());
        props.eachWithIndex {p, i ->
            assertEquals(p.name, propertiesList[i].name);
        }

        def keys = model.keys;
        def keySet = Connection.keySet();
        assertEquals(keySet.size(), keys.size())

        keys.eachWithIndex {p, i ->
            assertEquals(p.name, keySet[i].name)
        }
        assertEquals(connection.id, model.domainObject.id)
    }

    void testShowWithXml() {
        def connection = Connection.add(name: "conn1");
        assertFalse(connection.hasErrors())

        def controller = new RsBrowserController();
        controller.params["id"] = "${connection.id}"
        controller.params["domain"] = "connection"
        controller.params["format"] = "xml"
        controller.show();

        def props = Connection.getPropertiesList().findAll {!it.isKey && it.name != 'id' && !(it.isRelation && (it.isOneToMany() || it.isManyToMany()))};
        def keySet = Connection.keySet();
        def objectXml = new XmlSlurper().parseText(controller.response.contentAsString);

        def allNodes = objectXml.depthFirst().collect {it}
        assertEquals((props.size() + keySet.size() + 1), allNodes.size() - 1) //all nodes includes the root node

        assertEquals("id", allNodes[1].name())
        assertEquals("${connection.id}", allNodes[1].text())
        keySet.eachWithIndex {p, i ->
            assertEquals(p.name, allNodes[i + 2].name())
            assertEquals("${connection[p.name]}", allNodes[i + 2].text())
        }

        props.eachWithIndex {p, i ->
            assertEquals(p.name, allNodes[keySet.size() + i + 2].name())
            assertEquals("${connection[p.name]}", allNodes[keySet.size() + i + 2].text())
        }
    }

    void testSearchWithPublicSearchQuery() {
        Connection.add(name: "a1")
        Connection.add(name: "a2")
        Connection.add(name: "a3")
        Connection.add(name: "b1")
        Connection.add(name: "b2")
        Connection.add(name: "b3");
        def searchQueryGroup = SearchQueryGroup.add(name: "querygroup", username: RsUser.RSADMIN, type: "connection")
        def searchQuery = SearchQuery.add(name: "myConnections", username: RsUser.RSADMIN, isPublic: true, sortProperty: "name", type: "connection",
                sortOrder: "desc", query: "name:a*", group: searchQueryGroup);
        assertFalse(searchQuery.hasErrors())

        def controller = new RsBrowserController();
        def params = [:]
        params["domain"] = "connection"
        params["max"] = "2"
        params["searchQuery"] = searchQuery.name;
        controller._search(params);

        def model = controller.modelAndView.model;
        def objectList = model.objectList;
        assertEquals(2, objectList.size());
        assertEquals("a3", objectList[0].name)
        assertEquals("a2", objectList[1].name)
        assertEquals(3, model.count);
    }

    public void testSearchWithDatePropertiesAsXml() {
        def expectedProperties = RsEventJournal.getPropertiesList().findAll {!it.isRelation && !it.isOperationProperty || (it.isRelation && (it.isOneToOne() || it.isManyToOne()))}
        def datePropValues = [];
        10.times {
            datePropValues.add(new Date(System.currentTimeMillis() + it * 1000));
        }

        datePropValues.each {
            RsEventJournal.add(rsTime: it);
        }
        def controller = new RsBrowserController();
        def params = [:]
        params["max"] = "10";
        params["domain"] = "rsEventJournal"
        params["query"] = "alias:*"
        params["format"] = "xml"
        controller._search(params);
        def objectsXml = new XmlSlurper().parseText(controller.response.contentAsString);
        def objects = objectsXml.Object;
        assertEquals(10, objects.size());
        for (int i = 0; i < datePropValues.size(); i++) {
            assertEquals(RapidConvertUtils.getInstance().lookup(String).convert(String, datePropValues[i]), objects[i].@rsTime.toString());
        }
    }

    void testSearchWithUserSearchQuery() {
        def username = "newuser"
        Connection.add(name: "a1")
        Connection.add(name: "a2")
        Connection.add(name: "a3")
        Connection.add(name: "b1")
        Connection.add(name: "b2")
        Connection.add(name: "b3");
        def searchQueryGroup = SearchQueryGroup.add(name: "querygroup", username: username, type: "connection")
        def searchQuery = SearchQuery.add(name: "myConnections", username: username, sortProperty: "id", type: "connection",
                sortOrder: "asc", query: "name:b*", group: searchQueryGroup);
        assertFalse(searchQuery.hasErrors())

        def controller = new RsBrowserController();
        controller.session.username = username;
        def params = [:]
        params["domain"] = "connection"
        params["max"] = "2"
        params["searchQuery"] = searchQuery.name;
        params["sort"] = "name"; //if sort property is given it overrides searchQuery sortProperty
        params["order"] = "desc";
        controller._search(params);

        def model = controller.modelAndView.model;
        def objectList = model.objectList;
        assertEquals(2, objectList.size());
        assertEquals("b3", objectList[0].name)
        assertEquals("b2", objectList[1].name)
        assertEquals(3, model.count);
    }

    void testSearchWithOpenQuery() {
        def username = "newuser"
        Connection.add(name: "a1")
        Connection.add(name: "a2")
        Connection.add(name: "a3")
        Connection.add(name: "b1")
        Connection.add(name: "b2")
        Connection.add(name: "b3");
        def searchQueryGroup = SearchQueryGroup.add(name: "querygroup", username: username, type: "connection")
        def searchQuery = SearchQuery.add(name: "myConnections", username: username, sortProperty: "id", type: "connection",
                sortOrder: "asc", query: "name:b*", group: searchQueryGroup);
        assertFalse(searchQuery.hasErrors())

        def controller = new RsBrowserController();
        controller.session.username = username;
        def params = [:]
        params["domain"] = "connection"
        params["max"] = "2"
        params["searchQuery"] = searchQuery.name;
        params["query"] = "alias:*"; //if query is given does not look given searchQuery
        params["sort"] = "name"; //if sort property is given it overrides searchQuery sortProperty
        params["order"] = "asc";
        controller._search(params);

        def model = controller.modelAndView.model;
        def objectList = model.objectList;
        assertEquals(2, objectList.size());
        assertEquals("a1", objectList[0].name)
        assertEquals("a2", objectList[1].name)
        assertEquals(6, model.count);
    }


    void testSearchWithXml() {
        Connection.add(name: "a1")
        Connection.add(name: "a2")
        Connection.add(name: "a3")
        SnmpConnection.add(name: "b1", host: "0.0.0.0")
        SnmpConnection.add(name: "b2", host: "0.0.0.0")
        SnmpConnection.add(name: "b3", host: "0.0.0.0");

        def controller = new RsBrowserController();
        def params = [:]
        params["domain"] = "connection"
        params["query"] = "alias:*";
        params["sort"] = "name";
        params["order"] = "asc";
        params["format"] = "xml";
        controller._search(params);

        def objectsXml = new XmlSlurper().parseText(controller.response.contentAsString);
        def objects = objectsXml.Object;
        assertEquals(6, objects.size());
        for (i in 0..2) {
            def object = objects[i];
            assertEquals("a${i + 1}", object.@name.toString())
            assertEquals(Connection.class.name, object.@rsAlias.toString())
            assertEquals("${i}", object.@sortOrder.toString())
        }

        for (i in 3..5) {
            def object = objects[i];
            assertEquals("b${i - 2}", object.@name.toString())
            assertEquals(SnmpConnection.class.name, object.@rsAlias.toString())
            assertEquals("${i}", object.@sortOrder.toString())
            assertEquals("0.0.0.0", object.@host.toString())
        }

    }

}
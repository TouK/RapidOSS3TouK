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
import connection.SmsConnection
import datasource.SmsDatasource

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
    def SmartsObjectModel = null;
    def RsTopologyObject;

    void setUp() throws Exception {
        super.setUp();

        RsEventJournal = ApplicationHolder.application.classLoader.loadClass("RsEventJournal")
        SmartsObjectModel = ApplicationHolder.application.classLoader.loadClass("SmartsObject")
        RsTopologyObject = ApplicationHolder.application.classLoader.loadClass("RsTopologyObject")
        BaseDatasource.removeAll();
        RsEventJournal.removeAll();
        Connection.removeAll();
        SearchQuery.removeAll();
        SmartsObjectModel.removeAll();
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

        def expectedProperties = BaseDatasource.getPropertiesList().findAll {!it.isRelation && !it.isOperationProperty}
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
        def expectedProperties = RsEventJournal.getPropertiesList().findAll {!it.isRelation && !it.isOperationProperty}
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
        controller.params["all"] = "true"
        controller.listDomain();
        println controller.response.contentAsString;
        def objectsXml = new XmlSlurper().parseText(controller.response.contentAsString);
        def objects = objectsXml.Object;
        assertEquals(10, objects.size());
        for (int i = 0; i < datePropValues.size(); i++) {
            assertEquals(RapidConvertUtils.getInstance().lookup(String).convert(String, datePropValues[i]), objects[i].@rsTime.toString());
        }
    }

    void testListDomainWithAllPropertiesWithXml() {
        def expectedProperties = BaseDatasource.getPropertiesList().findAll {!it.isRelation && !it.isOperationProperty}
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


    void testListDomainIgnoresFederatedProperties() {
        def federatedProperties = SmartsObjectModel.getPropertiesList().findAll {return it.isFederated}
        assertTrue(federatedProperties.size() > 0);
        def expectedProperties = SmartsObjectModel.getPropertiesList().findAll {!it.isRelation && !it.isOperationProperty && !it.isFederated}
        SmartsObjectModel.add(name: "device0");
        def controller = new RsBrowserController();
        controller.params["max"] = "100";
        controller.params["sort"] = "name"
        controller.params["order"] = "desc"
        controller.params["domain"] = "smartsObject"
        controller.params["format"] = "xml"
        controller.listDomain();
        String content = controller.response.contentAsString;
        def objectsXml = new XmlSlurper().parseText(content);
        def objects = objectsXml.Object;
        assertEquals(1, objects.size());
        assertEquals("device0", objects[0].@name.toString());
        federatedProperties.each {
            assertTrue(content.indexOf(it.name) < 0);
            assertFalse(objects[0].attributes().containsKey(it.name));
        }
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
        //keyset plus id property
        assertEquals(BaseDatasource.keySet().size()+1, propertyList.size())
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

        def props = Connection.getPropertiesList().findAll {!it.isKey && it.name != 'id' && !it.isRelation && !it.isOperationProperty};
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

    void testShowBringsFederatedProperties() {
        def smartsObjectInstance = SmartsObjectModel.add(name: "obj1");
        assertFalse(smartsObjectInstance.hasErrors())

        def controller = new RsBrowserController();
        controller.params["id"] = "${smartsObjectInstance.id}"
        controller.params["domain"] = "smartsObject"
        controller.params["format"] = "xml"
        controller.show();

        def federatedProps = SmartsObjectModel.getPropertiesList().findAll {it.isFederated};
        assertEquals(1, federatedProps.size());
        def props = SmartsObjectModel.getPropertiesList().findAll {!it.isKey && it.name != 'id' && !it.isRelation && !it.isOperationProperty};
        def keySet = SmartsObjectModel.keySet();
        String content = controller.response.contentAsString;
        def objectXml = new XmlSlurper().parseText(content);

        def allNodes = objectXml.depthFirst().collect {it}
        assertEquals((props.size() + keySet.size() + 1), allNodes.size() - 1) //all nodes includes the root node

        assertEquals("id", allNodes[1].name())
        assertEquals("${smartsObjectInstance.id}", allNodes[1].text())
        keySet.eachWithIndex {p, i ->
            assertEquals(p.name, allNodes[i + 2].name())
            assertEquals("${smartsObjectInstance[p.name]}", allNodes[i + 2].text())
        }

        props.eachWithIndex {p, i ->
            assertEquals(p.name, allNodes[keySet.size() + i + 2].name())
            if (!p.isFederated) {
                assertEquals("${smartsObjectInstance[p.name]}", allNodes[keySet.size() + i + 2].text())
            }
        }
        println smartsObjectInstance['federatedProperty']

        federatedProps.each {p ->
            assertTrue(content.indexOf(p.name) > -1)
        }
    }
    void testShowWithFederatedPropertiesExcluded() {
        def smartsObjectInstance = SmartsObjectModel.add(name: "obj1");
        assertFalse(smartsObjectInstance.hasErrors())

        def controller = new RsBrowserController();
        controller.params["id"] = "${smartsObjectInstance.id}"
        controller.params["domain"] = "smartsObject"
        controller.params["federatedProperties"] = "false"
        controller.params["format"] = "xml"
        controller.show();

        def federatedProps = SmartsObjectModel.getPropertiesList().findAll {it.isFederated};
        assertEquals(1, federatedProps.size());
        def props = SmartsObjectModel.getPropertiesList().findAll {!it.isKey && !it.isFederated && it.name != 'id' && !it.isRelation && !it.isOperationProperty};
        def keySet = SmartsObjectModel.keySet();
        String content = controller.response.contentAsString;
        def objectXml = new XmlSlurper().parseText(content);

        def allNodes = objectXml.depthFirst().collect {it}
        assertEquals((props.size() + keySet.size() + 1), allNodes.size() - 1) //all nodes includes the root node

        assertEquals("id", allNodes[1].name())
        assertEquals("${smartsObjectInstance.id}", allNodes[1].text())
        keySet.eachWithIndex {p, i ->
            assertEquals(p.name, allNodes[i + 2].name())
            assertEquals("${smartsObjectInstance[p.name]}", allNodes[i + 2].text())
        }

        props.eachWithIndex {p, i ->
            assertEquals(p.name, allNodes[keySet.size() + i + 2].name())
            assertEquals("${smartsObjectInstance[p.name]}", allNodes[keySet.size() + i + 2].text())
        }

        federatedProps.each {p ->
            assertTrue(content.indexOf(p.name) < 0)
        }
    }

    void testShowDoesNotBringOperationProperties() {
        def ds = RsTopologyObject.add(name: "ds1");
        assertFalse(ds.hasErrors());
        def controller = new RsBrowserController();
        controller.params["id"] = "${ds.id}"
        controller.params["domain"] = "rsTopologyObject"
        controller.params["format"] = "xml"
        controller.show();

        def operationProps = RsTopologyObject.getPropertiesList().findAll {it.isOperationProperty};
        assertTrue(operationProps.size() > 0);
        def props = RsTopologyObject.getPropertiesList().findAll {!it.isKey && it.name != 'id' && !it.isRelation && !it.isOperationProperty};
        def keySet = RsTopologyObject.keySet();
        String content = controller.response.contentAsString;
        def objectXml = new XmlSlurper().parseText(content);

        def allNodes = objectXml.depthFirst().collect {it}
        assertEquals((props.size() + keySet.size() + 1), allNodes.size() - 1) //all nodes includes the root node

        assertEquals("id", allNodes[1].name())
        assertEquals("${ds.id}", allNodes[1].text())
        keySet.eachWithIndex {p, i ->
            assertEquals(p.name, allNodes[i + 2].name())
            assertEquals("${ds[p.name]}", allNodes[i + 2].text())
        }

        props.eachWithIndex {p, i ->
            assertEquals(p.name, allNodes[keySet.size() + i + 2].name())
            assertEquals("${ds[p.name]}", allNodes[keySet.size() + i + 2].text())
        }
        operationProps.each {p ->
            assertTrue(content.indexOf(p.name) < 0)
        }
    }

    void testShowWithOperationPropertiesIncluded() {
        def ds = RsTopologyObject.add(name: "ds1");
        assertFalse(ds.hasErrors());
        def controller = new RsBrowserController();
        controller.params["id"] = "${ds.id}"
        controller.params["domain"] = "rsTopologyObject"
        controller.params["format"] = "xml"
        controller.params["operationProperties"] = "true"
        controller.show();

        def operationProps = RsTopologyObject.getPropertiesList().findAll {it.isOperationProperty};
        assertTrue(operationProps.size() > 0);
        def props = RsTopologyObject.getPropertiesList().findAll {!it.isKey && it.name != 'id' && !it.isRelation};
        def keySet = RsTopologyObject.keySet();
        String content = controller.response.contentAsString;
        def objectXml = new XmlSlurper().parseText(content);

        def allNodes = objectXml.depthFirst().collect {it}
        assertEquals((props.size() + keySet.size() + 1), allNodes.size() - 1) //all nodes includes the root node

        assertEquals("id", allNodes[1].name())
        assertEquals("${ds.id}", allNodes[1].text())
        keySet.eachWithIndex {p, i ->
            assertEquals(p.name, allNodes[i + 2].name())
            assertEquals("${ds[p.name]}", allNodes[i + 2].text())
        }

        props.eachWithIndex {p, i ->
            assertEquals(p.name, allNodes[keySet.size() + i + 2].name())
            def text = ds[p.name] == null ? '' : "${ds[p.name]}";
            assertEquals(text, allNodes[keySet.size() + i + 2].text())
        }
        operationProps.each {p ->
            assertTrue(content.indexOf(p.name) > -1)
        }
    }

    void testShowDoesNotBringRelations() {
        def connection = SmsConnection.add(name: "conn", host: "host", username: "user");
        println connection.errors;
        assertFalse(connection.hasErrors());
        def ds1 = SmsDatasource.add(name: "ds1", connection: connection);
        def ds2 = SmsDatasource.add(name: "ds2", connection: connection);
        def controller = new RsBrowserController();
        controller.params["id"] = "${connection.id}"
        controller.params["domain"] = "smsConnection"
        controller.params["format"] = "xml"
        controller.show();

        def relationProps = SmsConnection.getPropertiesList().findAll {it.isRelation};
        assertTrue(relationProps.size() > 0);
        def props = SmsConnection.getPropertiesList().findAll {!it.isKey && it.name != 'id' && !it.isRelation && !it.isOperationProperty};
        def keySet = SmsConnection.keySet();
        String content = controller.response.contentAsString;
        def objectXml = new XmlSlurper().parseText(content);

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
        relationProps.each {p ->
            assertTrue(content.indexOf(p.name) < 0)
        }
    }

    void testShowWithRelationsIncluded() {
        def connection = SmsConnection.add(name: "conn", host: "host", username: "user");
        println connection.errors;
        assertFalse(connection.hasErrors());
        def ds1 = SmsDatasource.add(name: "ds1", connection: connection);
        def ds2 = SmsDatasource.add(name: "ds2", connection: connection);
        def controller = new RsBrowserController();
        controller.params["id"] = "${connection.id}"
        controller.params["domain"] = "smsConnection"
        controller.params["relations"] = "true"
        controller.params["format"] = "xml"
        controller.show();

        def relationProps = SmsConnection.getPropertiesList().findAll {it.isRelation};
        assertTrue(relationProps.size() > 0);
        def props = SmsConnection.getPropertiesList().findAll {!it.isKey && it.name != 'id' && !it.isOperationProperty};
        def keySet = SmsConnection.keySet();
        String content = controller.response.contentAsString;
        def objectXml = new XmlSlurper().parseText(content);

        def allNodes = objectXml.depthFirst().collect {it}.findAll {it.name() != 'Object'}
        assertEquals((props.size() + keySet.size() + 1), allNodes.size())

        assertEquals("id", allNodes[0].name())
        assertEquals("${connection.id}", allNodes[0].text())
        keySet.eachWithIndex {p, i ->
            assertEquals(p.name, allNodes[i + 1].name())
            assertEquals("${connection[p.name]}", allNodes[i + 1].text())
        }

        props.eachWithIndex {p, i ->
            assertEquals(p.name, allNodes[keySet.size() + i + 1].name())
            if (!p.isRelation) {
                assertEquals("${connection[p.name]}", allNodes[keySet.size() + i + 1].text())
            }
            else {
                assertEquals(connection[p.name].size(), objectXml[p.name].Object.size())
            }
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
        def expectedProperties = RsEventJournal.getPropertiesList().findAll {!it.isRelation && !it.isOperationProperty}
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

    public void testSearchIgnoresFederatedProperties() {
        def federatedProps = SmartsObjectModel.getPropertiesList().findAll {it.isFederated}
        assertTrue(federatedProps.size() > 0);
        def expectedProperties = SmartsObjectModel.getPropertiesList().findAll {!it.isRelation && !it.isFederated && !it.isOperationProperty}
        SmartsObjectModel.add(name: "dev1");
        def controller = new RsBrowserController();
        def params = [:]
        params["max"] = "10";
        params["domain"] = "smartsObject"
        params["query"] = "alias:*"
        params["format"] = "xml"
        controller._search(params);
        String content = controller.response.contentAsString;
        def objectsXml = new XmlSlurper().parseText(content);
        def objects = objectsXml.Object;
        assertEquals(1, objects.size());
        federatedProps.each {prop ->
            assertTrue(content.indexOf(prop.name) < 0);
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
            assertEquals("${i}", object.@__sortOrder.toString())
        }

        for (i in 3..5) {
            def object = objects[i];
            assertEquals("b${i - 2}", object.@name.toString())
            assertEquals(SnmpConnection.class.name, object.@rsAlias.toString())
            assertEquals("${i}", object.@__sortOrder.toString())
            assertEquals("0.0.0.0", object.@host.toString())
        }
    }

    void testPropertiesAndOperations() {
        def controller = new RsBrowserController();
        def params = [:]
        params["domain"] = "baseDatasource"
        controller._propertiesAndOperations(params);

        def resultXml = new XmlSlurper().parseText(controller.response.contentAsString);

        def operations = BaseDatasource.getOperations();
        def keys = BaseDatasource.keySet();
        def pureProps = BaseDatasource.getNonFederatedPropertyList().findAll {return !it.isKey}
        def federatedProps = BaseDatasource.getFederatedPropertyList()
        def relations = BaseDatasource.getRelationPropertyList();

        def keyProps = resultXml.Properties.Keys.Property;
        assertEquals(keys.size(), keyProps.size())
        keys.eachWithIndex {p, i ->
            assertEquals(p.name, keyProps[i].@name.toString())
            if(p.isRelation){
                assertEquals(p.relatedModel.name, keyProps[i].@type.toString())
            }
            else{
               assertEquals(p.type.name, keyProps[i].@type.toString()) 
            }

        }

        def simpleProps = resultXml.Properties.SimpleProperties.Property;
        assertEquals(pureProps.size(), simpleProps.size())
        pureProps.eachWithIndex {p, i ->
            assertEquals(p.name, simpleProps[i].@name.toString())
            assertEquals(p.type.name, simpleProps[i].@type.toString())
        }

        def federateds = resultXml.Properties.FederatedProperties.Property;
        assertEquals(federatedProps.size(), federateds.size())
        federatedProps.eachWithIndex {p, i ->
            assertEquals(p.name, federateds[i].@name.toString())
            assertEquals(p.type.name, federateds[i].@type.toString())
        }

        def relationProps = resultXml.Properties.Relation.Property;
        assertEquals(relations.size(), relationProps.size())
        relations.eachWithIndex {p, i ->
            assertEquals(p.name, relationProps[i].@name.toString())
            assertEquals(p.relatedModel.name, relationProps[i].@type.toString())
        }

        def operationProps = resultXml.Operations.Operation;
        assertEquals(operations.size(), operationProps.size())
        operations.eachWithIndex {op, i ->
            assertEquals(op.name, operationProps[i].@name.toString())
            assertEquals(op.description, operationProps[i].@description.toString())
            assertEquals(op.returnType.toString(), operationProps[i].@returnType.toString())
            assertEquals(op.parameters.name.join(","), operationProps[i].@parameters.toString())
        }
    }

}
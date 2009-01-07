package browser

import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import connection.Connection
import datasource.BaseDatasource
import com.ifountain.rcmdb.test.util.IntegrationTestUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 6, 2009
* Time: 2:01:07 PM
* To change this template use File | Settings | File Templates.
*/
class RsBrowserControllerIntegrationTests extends RapidCmdbIntegrationTestCase {

    void setUp() throws Exception {
        super.setUp();
        BaseDatasource.removeAll();
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

    void testListDomainWithOnlyKeyProperties() {
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
        assertEquals(BaseDatasource.getPropertiesList().size() - 1, propertyList.size()) // id is not sent
        assertEquals(10, model.count)

    }

    void testListDomainWithOnlyKeyPropertiesWithXml() {
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
        println "response ${controller.response.contentAsString}"
        def objectsXml = new XmlSlurper().parseText(controller.response.contentAsString);
        def objects = objectsXml.Object;
        assertEquals(5, objects.size());
        assertEquals("ds9", objects[0].@name.toString());

        def attributes = objects[0].attributes();
        assertEquals(BaseDatasource.getPropertiesList().size(), attributes.size())
    }

}
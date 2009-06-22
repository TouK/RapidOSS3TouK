package browser

import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import org.springframework.validation.BindException
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import connection.Connection
import com.ifountain.rcmdb.domain.util.ControllerUtils

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 4, 2009
* Time: 2:17:40 PM
*/
class RsBrowserCrudControllerIntegrationTests extends RapidCmdbIntegrationTestCase {
    def messageSource;
    public void setUp() {
        super.setUp();
        Connection.removeAll();
        messageSource = ServletContextHolder.servletContext.getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT).getBean("messageSource")
    }
    public void tearDown() {
        super.tearDown();
    }

    void testSaveRendersExceptionIfDomainClassNotFound() throws Exception {
        def className = "invalidClass"
        RsBrowserCrudController controller = new RsBrowserCrudController();
        controller.params["__rsBrowserClassName"] = className;
        controller.save();

        def errors = new BindException(controller, controller.class.name);
        errors.reject("default.class.not.found", [className] as Object[], "");
        assertTrue(controller.response.contentAsString.indexOf(messageSource.getMessage(errors.allErrors[0], Locale.ENGLISH)) > -1)
    }

    void testSuccessfulSave() throws Exception {
        RsBrowserCrudController controller = new RsBrowserCrudController();
        controller.params["__rsBrowserClassName"] = Connection.class.name;
        controller.params["name"] = "newConnection";
        controller.save();
        assertEquals(1, Connection.list().size());
        def connection = Connection.get(name: "newConnection");
        assertNotNull(connection)
        assertEquals(ControllerUtils.convertSuccessToXml("${Connection.class.name} ${connection.id} created"), controller.response.contentAsString)
    }

    void testSaveRendersExceptionIfObjectCannotBeAdded() {
        RsBrowserCrudController controller = new RsBrowserCrudController();
        controller.params["__rsBrowserClassName"] = Connection.class.name;
        controller.save();
        assertEquals(0, Connection.list().size());

        def errors = new BindException(new Connection(), Connection.class.name);
        errors.reject("default.blank.message", ["name", "class ${Connection.class.name}"] as Object[], "");
        assertTrue(controller.response.contentAsString.indexOf(messageSource.getMessage(errors.allErrors[0], Locale.ENGLISH)) > -1)
    }

    void testUpdateRendersExceptionIfDomainClassNotFound() throws Exception {
        def className = "invalidClass"
        RsBrowserCrudController controller = new RsBrowserCrudController();
        controller.params["__rsBrowserClassName"] = className;
        controller.update();

        def errors = new BindException(controller, controller.class.name);
        errors.reject("default.class.not.found", [className] as Object[], "");
        assertTrue(controller.response.contentAsString.indexOf(messageSource.getMessage(errors.allErrors[0], Locale.ENGLISH)) > -1)
    }

    void testUpdateRendersExceptionIfObjectNotFound() throws Exception {
        RsBrowserCrudController controller = new RsBrowserCrudController();
        controller.params["__rsBrowserClassName"] = Connection.class.name;
        controller.params["id"] = "1000";
        controller.update();

        def errors = new BindException(controller, controller.class.name);
        errors.reject("default.object.not.found", [Connection.class.name, "1000"] as Object[], "");
        assertTrue(controller.response.contentAsString.indexOf(messageSource.getMessage(errors.allErrors[0], Locale.ENGLISH)) > -1)
    }

    void testSuccessfulUpdate() throws Exception {
        def connection = Connection.add(name: "oldConnection");
        assertFalse(connection.hasErrors())
        RsBrowserCrudController controller = new RsBrowserCrudController();
        controller.params["__rsBrowserClassName"] = Connection.class.name;
        controller.params["name"] = "newConnection";
        controller.params["connectionClass"] = "myConnectionClass";
        controller.params["id"] = "${connection.id}";
        controller.update();

        assertNull(Connection.get(name: "oldConnection"))
        connection = Connection.get(name: "newConnection");
        assertNotNull(connection)
        assertEquals("myConnectionClass", connection.connectionClass);
        assertEquals(ControllerUtils.convertSuccessToXml("${Connection.class.name} ${connection.id} updated"), controller.response.contentAsString)
    }

    void testUpdateRendersExceptionIfObjectCannotBeUpdate() {
        def connection = Connection.add(name: "oldConnection");
        assertFalse(connection.hasErrors())
        def anotherConnection = Connection.add(name: "anotherConnection")
        assertFalse(anotherConnection.hasErrors())
        RsBrowserCrudController controller = new RsBrowserCrudController();
        controller.params["__rsBrowserClassName"] = Connection.class.name;
        controller.params["name"] = anotherConnection.name;
        controller.params["id"] = "${connection.id}";
        controller.update();

        def errors = new BindException(new Connection(), Connection.class.name);
        errors.reject("default.not.unique.message", ["name", "class ${Connection.class.name}", anotherConnection.name] as Object[], "");
        assertTrue(controller.response.contentAsString.indexOf(messageSource.getMessage(errors.allErrors[0], Locale.ENGLISH)) > -1)
    }

    void testDeleteRendersExceptionIfDomainClassNotFound() throws Exception {
        def className = "invalidClass"
        RsBrowserCrudController controller = new RsBrowserCrudController();
        controller.params["__rsBrowserClassName"] = className;
        controller.delete();

        def errors = new BindException(controller, controller.class.name);
        errors.reject("default.class.not.found", [className] as Object[], "");
        assertTrue(controller.response.contentAsString.indexOf(messageSource.getMessage(errors.allErrors[0], Locale.ENGLISH)) > -1)
    }

    void testDeleteRendersExceptionIfObjectNotFound() throws Exception {
        RsBrowserCrudController controller = new RsBrowserCrudController();
        controller.params["__rsBrowserClassName"] = Connection.class.name;
        controller.params["id"] = "1000";
        controller.delete();

        def errors = new BindException(controller, controller.class.name);
        errors.reject("default.object.not.found", [Connection.class.name, "1000"] as Object[], "");
        assertTrue(controller.response.contentAsString.indexOf(messageSource.getMessage(errors.allErrors[0], Locale.ENGLISH)) > -1)
    }

    void testSuccessfulDelete() {
        def connection = Connection.add(name: "oldConnection");
        assertFalse(connection.hasErrors())
        RsBrowserCrudController controller = new RsBrowserCrudController();
        controller.params["__rsBrowserClassName"] = Connection.class.name;
        controller.params["id"] = "${connection.id}";
        controller.delete();

        assertEquals(0, Connection.list().size());
        assertEquals(ControllerUtils.convertSuccessToXml("${Connection.class.name} ${connection.id} deleted"), controller.response.contentAsString)
    }

}
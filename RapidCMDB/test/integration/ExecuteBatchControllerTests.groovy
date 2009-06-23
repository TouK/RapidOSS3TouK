import com.ifountain.comp.utils.XMLTestUtils
import com.ifountain.rcmdb.test.util.IntegrationTestUtils
import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import com.ifountain.rcmdb.util.RapidCMDBConstants
import groovy.xml.MarkupBuilder

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Apr 11, 2008
 * Time: 9:58:10 AM
 */
class ExecuteBatchControllerTests extends RapidCmdbIntegrationTestCase {
    static transactional = false;
    private ExecuteBatchController ebc;

    void setUp() {
        super.setUp();
        SmartsObject.list()*.remove();

    }
    void testControllerReturnsExceptionWhenDataParameterIsNullOrEmpty() throws Exception {
        def ebc = new ExecuteBatchController();
        ebc.index();
        def errorXml = getErrorsAsXML([ebc.message(code: "default.missing.mandatory.parameter", args: [RapidCMDBConstants.DATA_PARAMETER])]);
        XMLTestUtils.compareXml(errorXml, ebc.response.contentAsString);

        IntegrationTestUtils.resetController(ebc);

        ebc.params[RapidCMDBConstants.DATA_PARAMETER] = " ";
        ebc.index();
        XMLTestUtils.compareXml(errorXml, ebc.response.contentAsString);
    }

    void testAddObject() throws Exception {
        def ebc = new ExecuteBatchController();
        ebc.params[RapidCMDBConstants.DATA_PARAMETER] = createAddObjectAction(SmartsObject.class.getName(), ["name": "router1", "creationClassName": "Router"])
        ebc.index();
        XMLTestUtils.compareXml(getSuccesXml(1, 1), ebc.response.contentAsString);
        def smartsObjects = SmartsObject.list();
        smartsObjects.each{
            println it.properties;
        }
        def smartsObject = SmartsObject.get(name: "router1", creationClassName:"Router");
        assertNotNull(smartsObject);
        assertEquals("Router", smartsObject.creationClassName);
    }

    void testActionFailsIfAddObjectFails() {
        def ebc = new ExecuteBatchController();
        ebc.params[RapidCMDBConstants.DATA_PARAMETER] = createAddObjectAction(SmartsObject.class.getName(), ["creationClassName": "Router"])
        ebc.index();
        def errorXml = getErrorsAsXML(["Exception occured in action 1: " + ebc.message(code: "default.blank.message", args: ["name", "class SmartsObject"])]);
        XMLTestUtils.compareXml(errorXml, ebc.response.contentAsString);
        assertEquals(0, SmartsObject.count());
    }

    void testAddObjectFailsIfModelDoesNotExist() {
        def ebc = new ExecuteBatchController();
        def invalidModelName = "InvalidModel";
        ebc.params[RapidCMDBConstants.DATA_PARAMETER] = createAddObjectAction(invalidModelName, ["name": "router1"])
        ebc.index();
        def errorXml = getErrorsAsXML(["Exception occured in action 1: " + ebc.message(code: "model.doesnot.exist", args: [invalidModelName])]);
        XMLTestUtils.compareXml(errorXml, ebc.response.contentAsString);
    }

    void testMultipleExecution() {
        def ebc = new ExecuteBatchController();
        def writer = new StringWriter();
        def builder = new MarkupBuilder(writer);
        builder.Actions() {
            builder.Action() {
                builder.ActionType(ExecuteBatchController.ADD_OBJECT)
                builder.Model(Name: SmartsObject.class.getName()) {
                    builder.name("router1")
                    builder.creationClassName("Router")
                }
            }
            builder.Action() {
                builder.ActionType(ExecuteBatchController.ADD_OBJECT)
                builder.Model(Name: SmartsObject.class.getName()) {
                    builder.name("router2")
                    builder.creationClassName("Router")
                }
            }
        }
        ebc.params[RapidCMDBConstants.DATA_PARAMETER] = writer.toString();
        ebc.index();
        XMLTestUtils.compareXml(getSuccesXml(2, 2), ebc.response.contentAsString);
        def smartsObject1 = SmartsObject.get(name: "router1", creationClassName:"Router");
        assertNotNull(smartsObject1);
        def smartsObject2 = SmartsObject.get(name: "router2", creationClassName:"Router");
        assertNotNull(smartsObject2);
    }

    void testRemove() {
        def ebc = new ExecuteBatchController();
        ebc.params[RapidCMDBConstants.DATA_PARAMETER] = createRemoveObjectAction(SmartsObject.class.getName(), ["name": "router1", "creationClassName":"Router"])

        def smartsObject = SmartsObject.add(name: "router1", creationClassName: "Router");
        assertFalse(smartsObject.hasErrors());
        ebc.index();
        XMLTestUtils.compareXml(getSuccesXml(1, 1), ebc.response.contentAsString);
        assertNull(SmartsObject.get(name: "router1", creationClassName:"Router"));
    }

    void testRemoveActionFailsIfObjectDoesNotExist() {
        def ebc = new ExecuteBatchController();
        def keys = ["name": "router1"];
        ebc.params[RapidCMDBConstants.DATA_PARAMETER] = createRemoveObjectAction(SmartsObject.class.getName(), keys)
        ebc.index();
        println "response: " + ebc.response.contentAsString;
        def errorXml = getErrorsAsXML(["Exception occured in action 1: " + ebc.message(code: "model.object.doesnot.exist", args: [SmartsObject.class.getName(), keys.toString()])]);
        XMLTestUtils.compareXml(errorXml, ebc.response.contentAsString);
    }

    void testAddRelation() {
        def ebc = new ExecuteBatchController();
        ebc.params[RapidCMDBConstants.DATA_PARAMETER] = createRelationAction(ExecuteBatchController.ADD_RELATION,
                DeviceComponent.class.getName(), ["name": "devComp", "creationClassName": "DeviceComponent"], Device.class.getName(),
                ["name": "device", "creationClassName":"Device"], "partOf");


        def devComp = DeviceComponent.add(name: "devComp", creationClassName: "DeviceComponent");
        assertFalse(devComp.hasErrors());
        def device = Device.add(name: "device", creationClassName: "Device", description: "descr", discoveredLastAt: 0);
        assertFalse(device.hasErrors());

        ebc.index();
        XMLTestUtils.compareXml(getSuccesXml(1, 1), ebc.response.contentAsString);
        devComp = DeviceComponent.get(name: "devComp", creationClassName:"DeviceComponent");
        def partOf = devComp.partOf;
        assertNotNull(partOf);
        assertEquals("device", partOf.name)
    }

    void testAddRelationThrowsExceptionIfObjectDoesNotExist() {
        def ebc = new ExecuteBatchController();
        def keys = ["name": "devComp", "creationClassName":"DeviceComponent"]
        ebc.params[RapidCMDBConstants.DATA_PARAMETER] = createRelationAction(ExecuteBatchController.ADD_RELATION,
                DeviceComponent.class.getName(), keys, Device.class.getName(), ["name": "device", "creationClassName":"Device"], "partOf");


        def device = Device.add(name: "device", creationClassName: "DeviceComponent", description: "descr", discoveredLastAt: 0);
        assertFalse(device.hasErrors());


        ebc.index();
        def errorXml = getErrorsAsXML(["Exception occured in action 1: " + ebc.message(code: "model.object.doesnot.exist", args: [DeviceComponent.class.getName(), keys.toString()])]);
        XMLTestUtils.compareXml(errorXml, ebc.response.contentAsString);
    }
    void testAddRelationThrowsExceptionIfRelatedObjectDoesNotExist() {
        def ebc = new ExecuteBatchController();
        def keys = ["name": "device", "creationClassName":"Device"]
        ebc.params[RapidCMDBConstants.DATA_PARAMETER] = createRelationAction(ExecuteBatchController.ADD_RELATION,
                DeviceComponent.class.getName(), ["name": "devComp", "creationClassName":"DeviceComponent"], Device.class.getName(), keys, "partOf");

        def devComp = DeviceComponent.add(name: "devComp", creationClassName: "DeviceComponent");
        assertFalse(devComp.hasErrors());

        ebc.index();
        def errorXml = getErrorsAsXML(["Exception occured in action 1: " + ebc.message(code: "model.object.doesnot.exist", args: [Device.class.getName(), keys.toString()])]);
        XMLTestUtils.compareXml(errorXml, ebc.response.contentAsString);
    }

    void testAddRelationThrowsExceptionIfRelatedModelClassDoesNotExist() {
        def invalidModelName = "invalidModel";
        def ebc = new ExecuteBatchController();
        ebc.params[RapidCMDBConstants.DATA_PARAMETER] = createRelationAction(ExecuteBatchController.ADD_RELATION,
                DeviceComponent.class.getName(), ["name": "devComp", "creationClassName":"DeviceComponent"],
                invalidModelName, ["name": "device", "creationClassName":"Device"], "partOf");

        ebc.index();
        def errorXml = getErrorsAsXML(["Exception occured in action 1: " + ebc.message(code: "model.doesnot.exist", args: [invalidModelName])]);
        XMLTestUtils.compareXml(errorXml, ebc.response.contentAsString);
    }

    void testAddRelationThrowsExceptionIfRelationNameIsNotGiven() {
        def ebc = new ExecuteBatchController();
        ebc.params[RapidCMDBConstants.DATA_PARAMETER] = createRelationAction(ExecuteBatchController.ADD_RELATION,
                DeviceComponent.class.getName(), ["name": "devComp", "creationClassName":"DeviceComponent"],
                Device.class.getName(), ["name": "device", "creationClassName":"Device"], "");

        ebc.index();
        def errorXml = getErrorsAsXML(["Exception occured in action 1: " + ebc.message(code: "default.missing.mandatory.parameter", args: [RapidCMDBConstants.RELATION_NAME])]);
        XMLTestUtils.compareXml(errorXml, ebc.response.contentAsString);
    }

    void testRemoveRelationThrowsExceptionIfRelatedModelClassDoesNotExist() {
        def invalidModelName = "invalidModel";
        def ebc = new ExecuteBatchController();
        ebc.params[RapidCMDBConstants.DATA_PARAMETER] = createRelationAction(ExecuteBatchController.REMOVE_RELATION,
                DeviceComponent.class.getName(), ["name": "devComp", "creationClassName":"DeviceComponent"],
                invalidModelName, ["name": "device", "creationClassName":"Device"], "partOf");

        ebc.index();
        def errorXml = getErrorsAsXML(["Exception occured in action 1: " + ebc.message(code: "model.doesnot.exist", args: [invalidModelName])]);
        XMLTestUtils.compareXml(errorXml, ebc.response.contentAsString);
    }

    void testRemoveRelationThrowsExceptionIfRelationNameIsNotGiven() {
        def ebc = new ExecuteBatchController();
        ebc.params[RapidCMDBConstants.DATA_PARAMETER] = createRelationAction(ExecuteBatchController.REMOVE_RELATION,
                DeviceComponent.class.getName(), ["name": "devComp", "creationClassName":"DeviceComponent"],
                Device.class.getName(), ["name": "device", "creationClassName":"Device"], "");

        ebc.index();
        def errorXml = getErrorsAsXML(["Exception occured in action 1: " + ebc.message(code: "default.missing.mandatory.parameter", args: [RapidCMDBConstants.RELATION_NAME])]);
        XMLTestUtils.compareXml(errorXml, ebc.response.contentAsString);
    }

    void testRemoveRelationThrowsExceptionIfObjectDoesNotExist() {
        def ebc = new ExecuteBatchController();
        def keys = ["name": "devComp", "creationClassName":"DeviceComponent"]
        ebc.params[RapidCMDBConstants.DATA_PARAMETER] = createRelationAction(ExecuteBatchController.REMOVE_RELATION,
                DeviceComponent.class.getName(), keys, Device.class.getName(), ["name": "device", "creationClassName":"Device"], "partOf");


        def device = Device.add(name: "device", creationClassName: "DeviceComponent", description: "descr", discoveredLastAt: 0);
        assertFalse(device.hasErrors());


        ebc.index();
        def errorXml = getErrorsAsXML(["Exception occured in action 1: " + ebc.message(code: "model.object.doesnot.exist", args: [DeviceComponent.class.getName(), keys.toString()])]);
        XMLTestUtils.compareXml(errorXml, ebc.response.contentAsString);
    }
    void testRemoveRelationThrowsExceptionIfRelatedObjectDoesNotExist() {
        def ebc = new ExecuteBatchController();
        def keys = ["name": "device", "creationClassName":"Device"]
        ebc.params[RapidCMDBConstants.DATA_PARAMETER] = createRelationAction(ExecuteBatchController.REMOVE_RELATION,
                DeviceComponent.class.getName(), ["name": "devComp", "creationClassName":"DeviceComponent"], Device.class.getName(), keys, "partOf");

        def devComp = DeviceComponent.add(name: "devComp", creationClassName: "DeviceComponent");
        assertFalse(devComp.hasErrors());

        ebc.index();
        def errorXml = getErrorsAsXML(["Exception occured in action 1: " + ebc.message(code: "model.object.doesnot.exist", args: [Device.class.getName(), keys.toString()])]);
        XMLTestUtils.compareXml(errorXml, ebc.response.contentAsString);
    }

    void testRemoveRelation() {
        def ebc = new ExecuteBatchController();
        ebc.params[RapidCMDBConstants.DATA_PARAMETER] = createRelationAction(ExecuteBatchController.REMOVE_RELATION,
                DeviceComponent.class.getName(), ["name": "devComp", "creationClassName":"DeviceComponent"],
                Device.class.getName(), ["name": "device", "creationClassName":"Device"], "partOf");


        def devComp = DeviceComponent.add(name: "devComp", creationClassName: "DeviceComponent");
        assertFalse(devComp.hasErrors());
        def device = Device.add(name: "device", creationClassName: "Device", description: "descr", discoveredLastAt: 0);
        assertFalse(device.hasErrors());
        devComp.addRelation(partOf: device);
        assertNotNull(devComp.partOf);

        ebc.index();
        XMLTestUtils.compareXml(getSuccesXml(1, 1), ebc.response.contentAsString);
        devComp = DeviceComponent.get(name: "devComp", creationClassName:"DeviceComponent");
        println devComp.properties
        assertNull(devComp.partOf);
    }
    void testUpdateObject() throws Exception {
        def ebc = new ExecuteBatchController();
        ebc.params[RapidCMDBConstants.DATA_PARAMETER] = createUpdateObjectAction(SmartsObject.class.getName(),
                ["name": "router1", "creationClassName":"Router"], ["creationClassName": "newRouter"])
        def object = SmartsObject.add(name: "router1", creationClassName: "Router");
        assertFalse(object.hasErrors());

        ebc.index();
        XMLTestUtils.compareXml(getSuccesXml(1, 1), ebc.response.contentAsString);
        def smartsObject = SmartsObject.get(name: "router1", creationClassName:"newRouter");
        assertNotNull(smartsObject);
    }

//    void testActionFailsIfUpdateObjectFails() {
//        def ebc = new ExecuteBatchController();
//        ebc.params[RapidCMDBConstants.DATA_PARAMETER] = createUpdateObjectAction(SmartsObject.class.getName(),
//                ["name": "router1", "creationClassName":"Router"], ["creationClassName": "null"])
//        def object = SmartsObject.add(name: "router1", creationClassName: "Router");
//        assertFalse(object.hasErrors());
//        ebc.index();
//        def errorXml = getErrorsAsXML(["Exception occured in action 1: " + ebc.message(code: "default.null.message", args: ["creationClassName", "class SmartsObject"])]);
//        XMLTestUtils.compareXml(errorXml, ebc.response.contentAsString);
//        object = SmartsObject.get(name: "router1");
//        assertEquals("Router", object.creationClassName);
//    }
    void testUpdateActionFailsIfObjectDoesNotExist() {
        def ebc = new ExecuteBatchController();
        def keys = ["name": "router1", "creationClassName":"Router"];
        ebc.params[RapidCMDBConstants.DATA_PARAMETER] = createUpdateObjectAction(SmartsObject.class.getName(), keys, ["creationClassName": "newRouter"])
        ebc.index();
        def errorXml = getErrorsAsXML(["Exception occured in action 1: " + ebc.message(code: "model.object.doesnot.exist", args: [SmartsObject.class.getName(), keys.toString()])]);
        XMLTestUtils.compareXml(errorXml, ebc.response.contentAsString);
    }

    def createAddObjectAction(model, params) {
        def writer = new StringWriter();
        def builder = new MarkupBuilder(writer);
        builder.Actions() {
            builder.Action() {
                builder.ActionType(ExecuteBatchController.ADD_OBJECT)
                builder.Model(Name: model) {
                    params.each {key, value ->
                        builder."${key}"(value)
                    }
                }
            }
        }
        return writer.toString();
    }
    def createUpdateObjectAction(model, keys, params) {
        def writer = new StringWriter();
        def builder = new MarkupBuilder(writer);
        builder.Actions() {
            builder.Action() {
                builder.ActionType(ExecuteBatchController.UPDATE_OBJECT)
                builder.Model(Name: model) {
                    builder.Keys() {
                        keys.each {key, value ->
                            builder."${key}"(value)
                        }
                    }
                    params.each {key, value ->
                        builder."${key}"(value)
                    }
                }
            }
        }
        return writer.toString();
    }

    def createRelationAction(actionType, model, modelKeys, relatedModel, relatedModelKeys, relationName) {
        def writer = new StringWriter();
        def builder = new MarkupBuilder(writer);
        builder.Actions() {
            builder.Action() {
                builder.ActionType(actionType)
                builder.RelationName(relationName)
                builder.Model(Name: model) {
                    modelKeys.each {key, value ->
                        builder."${key}"(value)
                    }
                }
                builder.RelatedModel(Name: relatedModel) {
                    relatedModelKeys.each {key, value ->
                        builder."${key}"(value)
                    }
                }
            }
        }
        return writer.toString();
    }

    def createRemoveObjectAction(model, keys) {
        def writer = new StringWriter();
        def builder = new MarkupBuilder(writer);
        builder.Actions() {
            builder.Action() {
                builder.ActionType(ExecuteBatchController.REMOVE_OBJECT)
                builder.Model(Name: model) {
                    keys.each {key, value ->
                        builder."${key}"(value)
                    }
                }
            }
        }
        return writer.toString();
    }

    def getSuccesXml(allCount, successCount) {
        def writer = new StringWriter();
        def builder = new MarkupBuilder(writer);
        builder.Successful(ExecuteBatchController.getSuccessMessage(allCount, successCount))
        return writer.toString();
    }
    def getErrorsAsXML(errorList) {
        def writer = new StringWriter();
        def builder = new MarkupBuilder(writer);
        builder.Errors() {
            for (error in errorList) {
                builder.Error(Message: error);
            }
        }
        return writer.toString();
    }
}
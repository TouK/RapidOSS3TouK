package ui.map

import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import auth.RsUser
import com.ifountain.rcmdb.test.util.IntegrationTestUtils
import com.ifountain.rcmdb.domain.util.ControllerUtils
import script.CmdbScript

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Oct 5, 2009
* Time: 11:35:57 AM
* To change this template use File | Settings | File Templates.
*/
class TopoMapControllerIntegrationTests extends RapidCmdbIntegrationTestCase{
    static transactional = false;
    def RsLink;
    def RsComputerSystem;
    public void setUp() {
        super.setUp();
        ["RsComputerSystem","RsLink"].each{ className ->
             setProperty(className,this.class.classLoader.loadClass(className));
        }
        TopoMap.removeAll();
        MapGroup.removeAll();
        RsComputerSystem.removeAll();
        RsLink.removeAll();
        CmdbScript.list().each {
            CmdbScript.deleteScript(it);
        }
        CmdbScript.addScript(name:"expandMap");
    }
    public void tearDown() {
           super.tearDown();
    }
    public void testListWithGroups(){
        def username="testuser";

        //test mymaps created
        def controller=new TopoMapController();
        controller.session.username=username;
        controller.listWithGroups();

        def myMapsGroup=MapGroup.searchEvery("groupName:${MapGroup.MY_MAPS().exactQuery()} AND username:${username.exactQuery()}")[0];
        assertNotNull (myMapsGroup);

        //test listWithGroups
        def adminUser = RsUser.RSADMIN;
        def defaultMapGroup= MapGroup.add(groupName: "Default", username: adminUser,isPublic:true);
        assertFalse(defaultMapGroup.hasErrors());

        def otherUserGroup=MapGroup.add(groupName:"other",username:"otheruser",isPublic:false);

        def defaultMap1=TopoMap.add(mapName:"defaultMap1",username:adminUser,group:defaultMapGroup,nodePropertyList:"name",nodes:"a",layout:0, isPublic:true);
        assertFalse(defaultMap1.errors.toString(),defaultMap1.hasErrors());

        def myMap1=TopoMap.add(mapName:"myMap1",username:username,group:myMapsGroup,nodePropertyList:"name",nodes:"a",layout:0);
        assertFalse(myMap1.hasErrors());

        def myMap2=TopoMap.add(mapName:"myMap2",username:username,group:myMapsGroup,nodePropertyList:"name",nodes:"a",layout:0);
        assertFalse(myMap2.hasErrors());



        IntegrationTestUtils.resetController (controller);
        controller.session.username=username;
        controller.listWithGroups();


        def listData=getListXmlData(controller.response.getContentAsString());
        assertEquals(2,listData.size());

        def myMapsGroupData=listData[myMapsGroup.id.toString()];
        assertNotNull (myMapsGroupData);

        assertEquals(myMapsGroup.id.toString(),myMapsGroupData.id);
        assertEquals(MapGroup.MY_MAPS(),myMapsGroupData.name);
        assertEquals("false",myMapsGroupData.isPublic);
        assertEquals("group",myMapsGroupData.nodeType);

        assertEquals(2,myMapsGroupData.maps.size());

        def myMap1Data=myMapsGroupData.maps[myMap1.id.toString()];
        assertNotNull (myMap1Data);

        assertEquals(myMap1.id.toString(),myMap1Data.id);
        assertEquals("myMap1",myMap1Data.name);
        assertEquals("false",myMap1Data.isPublic);
        assertEquals("map",myMap1Data.nodeType);

        def defaultMapGroupData=listData[defaultMapGroup.id.toString()];
        assertNotNull (defaultMapGroupData);

        assertEquals(defaultMapGroup.id.toString(),defaultMapGroupData.id);
        assertEquals("Default",defaultMapGroupData.name);
        assertEquals("true",defaultMapGroupData.isPublic);
        assertEquals("group",defaultMapGroupData.nodeType);


        assertEquals(1,defaultMapGroupData.maps.size());

        def defaultMap1Data=defaultMapGroupData.maps[defaultMap1.id.toString()];
        assertNotNull (defaultMap1Data);

        assertEquals(defaultMap1.id.toString(),defaultMap1Data.id);
        assertEquals("defaultMap1",defaultMap1Data.name);
        assertEquals("false",defaultMap1Data.isPublic);
        assertEquals("map",defaultMap1Data.nodeType);
    }

    def getListXmlData(xmlData){
        def resultXml = new XmlSlurper().parseText(xmlData);

        println xmlData

        def results=[:]

        resultXml.Map.each {    groupDataRow ->
            def groupData=[:];
            groupData.putAll(groupDataRow.attributes());
            groupData.maps=[:];
            
            results[groupData.id]=groupData;
            
            groupDataRow.Map.each{ mapDataRow ->
                def mapData=[:];
                mapData.putAll(mapDataRow.attributes());
                groupData.maps[mapData.id]=mapData;
            }
        }
        println "result parsed from xml ${results}"
        return results;
    }
    
    public void testSaveWithNoGroupNameSuccessfully()
    {
        def saveParams=[:];
        saveParams.mapName="myMap";
        saveParams.layout="1";
        saveParams.nodes="nodes";
        saveParams.nodePropertyList="nodePropertyList";
        saveParams.mapProperties="mapProperties";
        saveParams.mapPropertyList="mapPropertyList";
        
        def username="testuser";
        
        def controller=new TopoMapController();
        controller.session.username=username;
        controller.params.putAll(saveParams);

        controller.save();

        assertEquals(1,TopoMap.count());
        assertEquals(1,MapGroup.count());
        
        def map=TopoMap.list()[0];
        assertNotNull(map);
        
        assertEquals(ControllerUtils.convertSuccessToXml("TopoMap ${map.id} created"),controller.response.getContentAsString())

        def myMapGroup=MapGroup.get(groupName:MapGroup.MY_MAPS(),username:username);
        assertNotNull(myMapGroup);

        assertEquals(myMapGroup.id,map.group.id);
        saveParams.each{ propName , propVal ->
            assertEquals(propVal,map[propName].toString());
        }
    }

    public void testSaveWithGroupNameSuccessfully()
    {
        def saveParams=[:];
        saveParams.mapName="myMap";
        saveParams.groupName="aGroup";
        saveParams.layout="1";
        saveParams.nodes="nodes";
        saveParams.nodePropertyList="nodePropertyList";
        saveParams.mapProperties="mapProperties";
        saveParams.mapPropertyList="mapPropertyList";

        def username="testuser";

        def controller=new TopoMapController();
        controller.session.username=username;
        controller.params.putAll(saveParams);

        controller.save();

        assertEquals(1,TopoMap.count());
        assertEquals(1,MapGroup.count());

        def map=TopoMap.list()[0];
        assertNotNull(map);

        assertEquals(ControllerUtils.convertSuccessToXml("TopoMap ${map.id} created"),controller.response.getContentAsString())

        def myMapGroup=MapGroup.get(groupName:saveParams.groupName,username:username);
        assertNotNull(myMapGroup);

        assertEquals(myMapGroup.id,map.group.id);
        saveParams.each{ propName , propVal ->
            if(propName!="groupName")
            {
                assertEquals(propVal,map[propName].toString());
            }
        }
    }

    public void testSaveExistingMapGeneratesError()
    {
        def username="testuser";
        
        def saveParams=[:];
        saveParams.mapName="myMap";        
        saveParams.layout="1";
        saveParams.nodes="nodes";
        saveParams.nodePropertyList="nodePropertyList";
        saveParams.mapProperties="mapProperties";
        saveParams.mapPropertyList="mapPropertyList";

        def mapSaveParams=[:];
        mapSaveParams.putAll(saveParams);
        mapSaveParams.username=username;

        def aMap=TopoMap.add(mapSaveParams);
        assertFalse(aMap.errors.toString(),aMap.hasErrors());


        def controller=new TopoMapController();
        controller.session.username=username;
        controller.params.putAll(saveParams);

        controller.save();

        assertTrue(controller.response.getContentAsString().indexOf("Object with entered keys already exists")>=0);
    }

    public void testUpdateSuccessfully()
    {
        def username="testuser";

        
        def saveParams=[:];
        saveParams.mapName="myMap";
        saveParams.layout="1";
        saveParams.nodes="nodes";
        saveParams.nodePropertyList="nodePropertyList";
        saveParams.mapProperties="mapProperties";
        saveParams.mapPropertyList="mapPropertyList";

        def mapSaveParams=[:];
        mapSaveParams.putAll(saveParams);
        mapSaveParams.username=username;

        def aMap=TopoMap.add(mapSaveParams);
        assertFalse(aMap.errors.toString(),aMap.hasErrors());

        def updateParams=[:];
        updateParams.id=aMap.id.toString();
        updateParams.putAll(saveParams);
        updateParams.layout="3";
        updateParams.nodes="nodes2";
                

        def controller=new TopoMapController();
        controller.session.username=username;
        controller.params.putAll(updateParams);

        controller.update();

        

        assertEquals(1,TopoMap.count());
        assertEquals(1,MapGroup.count());

        def map=TopoMap.list()[0];
        assertNotNull(map);

        assertEquals(ControllerUtils.convertSuccessToXml("TopoMap ${map.id} updated"),controller.response.getContentAsString())

        def myMapGroup=MapGroup.get(groupName:MapGroup.MY_MAPS(),username:username);
        assertNotNull(myMapGroup);

        assertEquals(myMapGroup.id,map.group.id);
        updateParams.each{ propName , propVal ->
            assertEquals(propVal,map[propName].toString());
        }
    }

    public void testLoadMap()
    {
        def node1=RsComputerSystem.add(name:"node1");
        def node2=RsComputerSystem.add(name:"node2");
        def link1=RsLink.add(name:"l1",a_ComputerSystemName:node1.name,z_ComputerSystemName:node2.name);

        assertFalse(node1.hasErrors());
        assertFalse(node2.hasErrors());
        assertFalse(link1.hasErrors());

        def saveParams=[:];
        saveParams.mapName="myMap";
        saveParams.layout="1";
        saveParams.nodes="node1,true";
        saveParams.nodePropertyList="name,expanded";
        saveParams.mapProperties="";
        saveParams.mapPropertyList="mapType";
        saveParams.username="testuser";
        
        def topoMap=TopoMap.add(saveParams);
        assertFalse(topoMap.errors.toString(),topoMap.hasErrors());

        def controller=new TopoMapController();
        controller.params.id=topoMap.id.toString();

        controller.load();

        def xmlData=controller.response.getContentAsString();
        def resultXml = new XmlSlurper().parseText(xmlData);

        println xmlData

        def graph=resultXml.attributes();

        def nodes=[:]

        resultXml.node.each {    dataRow ->
            def data=[:];
            data.putAll(dataRow.attributes());
            nodes.put(data.id,data);
        }

        def edges=[:]

        resultXml.edge.each {    dataRow ->
            def data=[:];
            data.putAll(dataRow.attributes());
            edges.put(data.id,data);
        }
        
        println "graph : ${graph}"
        println "nodes : ${nodes}"
        println "edges : ${edges}"

        assertEquals(saveParams.nodePropertyList,graph.nodePropertyList);
        assertEquals(saveParams.mapPropertyList,graph.mapPropertyList);
        assertEquals(saveParams.mapProperties,graph.mapProperties);
        assertEquals(saveParams.layout,graph.layout);

        assertEquals(2,nodes.size());
        assertEquals(1,edges.size());

        
        assertTrue(nodes.containsKey(node1.name.toString()));
        assertTrue(nodes.containsKey(node2.name.toString()));
        assertTrue(edges.containsKey(link1.name.toString()));
        

    }
    
}
package scriptTests

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase

import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.test.util.RsApplicationTestUtils
import application.RsApplication

import com.ifountain.rcmdb.test.util.scripting.ScriptManagerForTest

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 19, 2009
* Time: 9:06:30 AM
* To change this template use File | Settings | File Templates.
*/
class GetMapDataScriptTests  extends RapidCmdbWithCompassTestCase {

    def RsComputerSystem;
    def RsTopologyObject;
    def RsLink;
    def RsObjectState;
    def RsEvent;
    def RsComputerSystemOperations;
    def RsLinkOperations;

    public void setUp() {
        super.setUp();
        ["RsComputerSystem","RsTopologyObject","RsLink","RsObjectState","RsEvent","RsComputerSystemOperations","RsLinkOperations"].each{ className ->
             setProperty(className,gcl.loadClass(className));
        }

        initialize([RsComputerSystem,RsTopologyObject,RsLink,RsObjectState,RsEvent,RsApplication], []);
        CompassForTests.addOperationSupport (RsComputerSystem,RsComputerSystemOperations);
        CompassForTests.addOperationSupport (RsLink,RsLinkOperations);
        RsApplicationTestUtils.initializeRsApplicationOperations(RsApplication);

        initializeScriptManager();

    }

    public void tearDown() {
        super.tearDown();
    }
    void initializeScriptManager()
    {
        def base_directory = getWorkspacePath()+"/RapidModules/RapidInsight/scripts"
        println "base path is :"+new File(base_directory).getCanonicalPath();

        ScriptManagerForTest.initialize (gcl,base_directory);
        ScriptManagerForTest.addScript('getMapData');

    }
    public void testGetMapDataWith1Node()
    {
        def source=RsComputerSystem.add(name:"start",model:"smodel",className:"sclass");
        assertFalse(source.hasErrors())


        def params=[:];
        params.nodes="start"


        def getMapData=getMapDataFromScript(params);
        assertEquals(1,getMapData.nodes.size());
        assertEquals(0,getMapData.edges.size());

        def nodeData=getMapData.nodes[source.name];
        checkNodeData(nodeData,source);


    }
    public void testGetMapDataWith1To1NodesAndWithLinkDuplicateAndWithLinkReverse()
    {

        def source=RsComputerSystem.add(name:"start",model:"smodel",className:"sclass");
        assertFalse(source.hasErrors())
        def target=RsComputerSystem.add(name:"end",model:"emodel",className:"eclass");
        assertFalse(target.hasErrors())
        def link1=RsLink.add(name:"l1",a_ComputerSystemName:source.name,z_ComputerSystemName:target.name);
        assertFalse(link1.hasErrors())

        // add a duplicate link
        def link1Duplicate=RsLink.add(name:"l1Duplicate",a_ComputerSystemName:source.name,z_ComputerSystemName:target.name);
        assertFalse(link1Duplicate.hasErrors())

         // add a reverse link
        def link1Reverse=RsLink.add(name:"l1Reverse",a_ComputerSystemName:target.name,z_ComputerSystemName:source.name);
        assertFalse(link1Reverse.hasErrors())

        def params=[:];
        params.nodes="start;end";
        params.edges="l1,end,start"

        def getMapData=getMapDataFromScript(params);
        assertEquals(2,getMapData.nodes.size());
        assertEquals(1,getMapData.edges.size());


        def sourceNodeData=getMapData.nodes[source.name];
        def targetNodeData=getMapData.nodes[target.name];
        def edgeData=getEdgeFrom(getMapData.edges,link1.name)

        checkNodeData(sourceNodeData,source);
        checkNodeData(targetNodeData,target);
        checkEdgeData(edgeData,link1,source.name,target.name);


        //check that duplicate link data exists in xml
        params.nodes="start;end";
        params.edges="l1,end,start;l1Duplicate,start,end;"

        def duplicateGetMapData=getMapDataFromScript(params);

        assertEquals(2,duplicateGetMapData.nodes.size());
        assertEquals(2,duplicateGetMapData.edges.size());

        def sourceDuplicateNodeData=duplicateGetMapData.nodes[source.name];
        def targetDuplicateNodeData=duplicateGetMapData.nodes[target.name];
        def edgeDuplicateData=getEdgeFrom(duplicateGetMapData.edges,link1Duplicate.name)
        edgeData=getEdgeFrom(duplicateGetMapData.edges,link1.name)

        checkNodeData(sourceDuplicateNodeData,source);
        checkNodeData(targetDuplicateNodeData,target);
        checkEdgeData(edgeDuplicateData,link1Duplicate,source.name,target.name);
        checkEdgeData(edgeData,link1,source.name,target.name);


        //check that reverse link data exists in xml
        params.nodes="start;end";
        params.edges="l1,end,start;l1Duplicate,start,end;l1Reverse,start,end;"

        def reverseGetMapData=getMapDataFromScript(params);
        assertEquals(2,reverseGetMapData.nodes.size());
        assertEquals(3,reverseGetMapData.edges.size());

        def sourceReverseNodeData=reverseGetMapData.nodes[source.name];
        def targetReverseNodeData=reverseGetMapData.nodes[target.name];
        def edgeReverseData=getEdgeFrom(reverseGetMapData.edges,link1Reverse.name);
        edgeDuplicateData=getEdgeFrom(reverseGetMapData.edges,link1Duplicate.name)
        edgeData=getEdgeFrom(reverseGetMapData.edges,link1.name)

        checkNodeData(sourceReverseNodeData,source);
        checkNodeData(targetReverseNodeData,target);
        checkEdgeData(edgeReverseData,link1Reverse,source.name,target.name);
        checkEdgeData(edgeDuplicateData,link1Duplicate,source.name,target.name);
        checkEdgeData(edgeData,link1,source.name,target.name);

    }
     public void  testGetMapDataWithNNodesAndMEdges()
    {
        def sourceNode=RsComputerSystem.add(name:"sourceNode",model:"model1",className:"class1");
        assertFalse(sourceNode.hasErrors())


        def nodeCount=3;
        def linkCount=5;


        def targets=[:];
        def links=[:];
        nodeCount.times{ counter ->
            def target=RsComputerSystem.add(name:"target${counter}",model:"modelt",className:"classt");
            targets[target.name]=target;
            assertFalse(target.hasErrors())
        }
        linkCount.times{ counter ->
            def link=RsLink.add(name:"l${counter}",a_ComputerSystemName:"source${counter}",z_ComputerSystemName:"target${counter}");
            links[link.name]=link;
            assertFalse(link.hasErrors())
        }


        assertEquals(1+nodeCount,RsComputerSystem.countHits("alias:*"))
        assertEquals(linkCount,RsLink.countHits("alias:*"))


        //call with 1  , 2  ... N nodes
        nodeCount.times { counter ->
            def nodeCounter=counter+1;
            def params=[:];
            params.nodes="sourceNode;";

            nodeCounter.times{ subCounter ->
                def targetName="target${subCounter}".toString()
                params.nodes+="${targetName};"

            }
            def getMapData=getMapDataFromScript(params);
            assertEquals(1+nodeCounter,getMapData.nodes.size());
            assertEquals(0,getMapData.edges.size());

            //check sourceNode data
            def sourceNodeData=getMapData.nodes[sourceNode.name];
            checkNodeData(sourceNodeData,sourceNode);

            //check other nodes
            nodeCounter.times{ subCounter ->
                def targetName="target${subCounter}".toString();
                def targetNode=targets[targetName];
                def targetNodeData=getMapData.nodes[targetName];
                checkNodeData(targetNodeData,targetNode);
            }

        }

        //call with 1  , 2  ... M edges
        linkCount.times { counter ->
            def linkCounter=counter+1;
            def params=[:];
            params.edges="";

            linkCounter.times{ subCounter ->
                params.edges+="l${subCounter},source${subCounter},target${subCounter};"

            }
            def getMapData=getMapDataFromScript(params);
            assertEquals(0,getMapData.nodes.size());
            assertEquals(linkCounter,getMapData.edges.size());
            //check edges
            linkCounter.times{ subCounter ->
                def sourceName="source${subCounter}".toString();
                def targetName="target${subCounter}".toString();
                def link=links["l"+subCounter];
                def linkData=getEdgeFrom(getMapData.edges,link.name)
                checkEdgeData (linkData,link,sourceName,targetName);

            }
        }
   }

    def checkNodeData(nodeData,node)
    {
        assertEquals(node.name,nodeData.id);
        assertEquals(node.name,nodeData.displayName);
        assertEquals(node.model,nodeData.model);
        assertEquals(node.className,nodeData.type);
        assertEquals(node.getState().toString(),nodeData.state);

    }
    def getEdgeFrom(edges,edgeName)
    {
        return edges[edgeName];
    }
    def checkEdgeData(edgeData,edge,sourceName,targetName)
    {
        assertEquals(edge.getState().toString(),edgeData.state)
        assertEquals(edge.getName().toString(),edgeData.id)
        if(edgeData==null)
        {
            fail("edge ${edgeData} is not same as ${sourceName} to ${targetName}")
        }
        if(edgeData.source==sourceName)
        {
            assertEquals(edgeData.target,targetName)
        }
        else if(edgeData.source==targetName)
        {
            assertEquals(edgeData.target,sourceName)
        }
        else
        {
            fail("edge ${edgeData} is not same as ${sourceName} to ${targetName}")
        }
    }

     def getMapDataFromScript(params){
        params.nodePropertyList="name"

//         FileUtils.copyFileToDirectory (new File("${base_directory}/scripts/getMapData.groovy"),new File("$script_base_directory/$ScriptManager.SCRIPT_DIRECTORY"));
//         FileUtils.copyFileToDirectory (new File("${base_directory}/scripts/mapConfiguration.groovy"),new File("$script_base_directory/$ScriptManager.SCRIPT_DIRECTORY"));

        def scriptResult=ScriptManagerForTest.runScript("getMapData",["params":params]);

        def resultXml = new XmlSlurper().parseText(scriptResult);

        println scriptResult

        def results=[:]
        results.nodes=[:];
        results.edges=[:];
        def nodeList=[];
        def edgeList=[];

        def nodeProps=["id","state","type","displayName","model","gauged"];

        resultXml.node.each {    dataRow->
            def nodeData=[:];
            nodeProps.each{ propName ->
                nodeData[propName]=dataRow.@"${propName}".toString();
            }
            results.nodes.put(nodeData.id,nodeData);
            nodeList.add(nodeData);
        }
        def edgeProps=["source","target","state","id"];
        resultXml.edge.each {    dataRow->
            def edgeData=[:];
            edgeProps.each{ propName ->
                edgeData[propName]=dataRow.@"${propName}".toString();
            }
            results.edges.put(edgeData.id,edgeData);
            edgeList.add(edgeData);
        }
        assertEquals("duplicate node entry exists",nodeList.size(),results.nodes.size());
        assertEquals("duplicate edge entry exists",edgeList.size(),results.edges.size());
        println "result parsed from xml ${results}"
        return results;
    }


}




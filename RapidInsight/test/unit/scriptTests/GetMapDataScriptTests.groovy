package scriptTests

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.scripting.ScriptManager
import script.CmdbScript
import script.CmdbScriptOperations
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.test.util.RsApplicationTestUtils
import application.RsApplication
import org.apache.commons.io.FileUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 19, 2009
* Time: 9:06:30 AM
* To change this template use File | Settings | File Templates.
*/
class GetMapDataScriptTests  extends RapidCmdbWithCompassTestCase {

    def static script_base_directory = "../testoutput/";
    def classes=[:];

    public void setUp() {
        super.setUp();
        ["RsComputerSystem","RsTopologyObject","RsLink","RsObjectState","RsEvent","RsComputerSystemOperations","RsLinkOperations"].each{ className ->
            classes[className]=gcl.loadClass(className);
        }

        initialize([CmdbScript,classes.RsComputerSystem,classes.RsTopologyObject,classes.RsLink,classes.RsObjectState,classes.RsEvent,RsApplication], []);
        CompassForTests.addOperationSupport (CmdbScript,CmdbScriptOperations);
        CompassForTests.addOperationSupport (classes.RsComputerSystem,classes.RsComputerSystemOperations);
        CompassForTests.addOperationSupport (classes.RsLink,classes.RsLinkOperations);
        RsApplicationTestUtils.initializeRsApplicationOperations(RsApplication);

        initializeScriptManager();
        def script=CmdbScript.addScript([name:"getMapData",type: CmdbScript.ONDEMAND],true)
        assertFalse(script.errors.toString(),script.hasErrors());
    }

    public void tearDown() {

        super.tearDown();
    }
    void initializeScriptManager()
    {
        def base_directory = getWorkspacePath()+"/RapidModules/RapidInsight"
        println "base path is :"+new File(base_directory).getCanonicalPath();

        if (new File(script_base_directory).exists())
        {
            FileUtils.deleteDirectory(new File(script_base_directory));
        }
        new File("$script_base_directory/$ScriptManager.SCRIPT_DIRECTORY").mkdirs();

        ScriptManager manager = ScriptManager.getInstance();
        manager.initialize(this.class.getClassLoader(), script_base_directory, [], [:]);


        FileUtils.copyFileToDirectory (new File("${base_directory}/scripts/getMapData.groovy"),new File("$script_base_directory/$ScriptManager.SCRIPT_DIRECTORY"));
        FileUtils.copyFileToDirectory (new File("${base_directory}/scripts/mapConfiguration.groovy"),new File("$script_base_directory/$ScriptManager.SCRIPT_DIRECTORY"));


    }
    public void testGetMapDataWith1Node()
    {
        def source=classes.RsComputerSystem.add(name:"start",model:"smodel",className:"sclass");
        assertFalse(source.hasErrors())


        def params=[:];
        params.nodes="start"


        def getMapData=getMapDataFromScript(params);
        assertEquals(1,getMapData.nodes.size());
        assertEquals(0,getMapData.edges.size());

        def nodeData=getMapData.nodes[source.name];
        checkNodeData(nodeData,source);


    }
    public void testExpandMapWith1To1NodesAndWithLinkDuplicateAndWithLinkReverse()
    {

        def source=classes.RsComputerSystem.add(name:"start",model:"smodel",className:"sclass");
        assertFalse(source.hasErrors())
        def target=classes.RsComputerSystem.add(name:"end",model:"emodel",className:"eclass");
        assertFalse(target.hasErrors())
        def link1=classes.RsLink.add(name:"l1",a_ComputerSystemName:source.name,z_ComputerSystemName:target.name);
        assertFalse(link1.hasErrors())

        def params=[:];
        params.nodes="start;end";
        params.edges="end,start"

        def getMapData=getMapDataFromScript(params);
        assertEquals(2,getMapData.nodes.size());
        assertEquals(1,getMapData.edges.size());


        def sourceNodeData=getMapData.nodes[source.name];
        def targetNodeData=getMapData.nodes[target.name];
        def edgeData=getEdgeFrom(getMapData.edges,source.name,target.name)

        checkNodeData(sourceNodeData,source);
        checkNodeData(targetNodeData,target);
        checkEdgeData(edgeData,link1,source.name,target.name);

        // add a duplicate link ,  and check that result is same
        def link1Duplicate=classes.RsLink.add(name:"l1Duplicate",a_ComputerSystemName:source.name,z_ComputerSystemName:target.name);
        assertFalse(link1Duplicate.hasErrors())

        def duplicateGetMapData=getMapDataFromScript(params);

        assertEquals(2,duplicateGetMapData.nodes.size());
        assertEquals(1,duplicateGetMapData.edges.size());

        def sourceDuplicateNodeData=duplicateGetMapData.nodes[source.name];
        def targetDuplicateNodeData=duplicateGetMapData.nodes[target.name];
        def edgeDuplicateData=getEdgeFrom(duplicateGetMapData.edges,source.name,target.name)

        checkNodeData(sourceDuplicateNodeData,source);
        checkNodeData(targetDuplicateNodeData,target);
        checkEdgeData(edgeDuplicateData,link1,source.name,target.name);

        // add a reverse link , link is duplicated , and check that result is same
        def link1Reverse=classes.RsLink.add(name:"l1Reverse",a_ComputerSystemName:target.name,z_ComputerSystemName:source.name);
        assertFalse(link1Reverse.hasErrors())

        def reverseGetMapData=getMapDataFromScript(params);
        assertEquals(2,reverseGetMapData.nodes.size());
        assertEquals(1,reverseGetMapData.edges.size());

        def sourceReverseNodeData=reverseGetMapData.nodes[source.name];
        def targetReverseNodeData=reverseGetMapData.nodes[target.name];
        def edgeReverseData=getEdgeFrom(reverseGetMapData.edges,source.name,target.name)

        checkNodeData(sourceReverseNodeData,source);
        checkNodeData(targetReverseNodeData,target);
        checkEdgeData(edgeReverseData,link1,source.name,target.name);

    }
     public void  testExpandMapWithNNodesAndMEdges()
    {
        def sourceNode=classes.RsComputerSystem.add(name:"sourceNode",model:"model1",className:"class1");
        assertFalse(sourceNode.hasErrors())


        def nodeCount=3;
        def linkCount=5;


        def targets=[:];
        def links=[:];
        nodeCount.times{ counter ->
            def target=classes.RsComputerSystem.add(name:"target${counter}",model:"modelt",className:"classt");
            targets[target.name]=target;
            assertFalse(target.hasErrors())
        }
        linkCount.times{ counter ->
            def link=classes.RsLink.add(name:"l${counter}",a_ComputerSystemName:"source${counter}",z_ComputerSystemName:"target${counter}");
            links[link.name]=link;
            assertFalse(link.hasErrors())
        }


        assertEquals(1+nodeCount,classes.RsComputerSystem.countHits("alias:*"))
        assertEquals(linkCount,classes.RsLink.countHits("alias:*"))


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
                params.edges+="source${subCounter},target${subCounter};"

            }
            def getMapData=getMapDataFromScript(params);
            assertEquals(0,getMapData.nodes.size());
            assertEquals(linkCounter,getMapData.edges.size());
            //check edges
            linkCounter.times{ subCounter ->
                def sourceName="source${subCounter}".toString();
                def targetName="target${subCounter}".toString();
                def link=links["l"+counter];
                def linkData=getEdgeFrom(getMapData.edges,sourceName,targetName)
                checkEdgeData (linkData,link,sourceName,targetName);

            }
        }

        //call with 1 .. N nodes and for each call use 1 .. M links
        nodeCount.times { counter ->
            def nodeCounter=counter+1;
            def params=[:];
            params.nodes="sourceNode;";
            params.edges="";

            linkCount.times{ linkMainCounter ->
                def linkCounter=linkMainCounter+1;
                linkCounter.times{ subCounter ->
                    params.edges+="source${subCounter},target${subCounter};"

                }

                nodeCounter.times{ subCounter ->
                    def targetName="target${subCounter}".toString()
                    params.nodes+="${targetName};"

                }


                def getMapData=getMapDataFromScript(params);
                assertEquals(1+nodeCounter,getMapData.nodes.size());
                assertEquals(linkCounter,getMapData.edges.size());

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

                //check edges
                linkCounter.times{ subCounter ->
                    def sourceName="source${subCounter}".toString();
                    def targetName="target${subCounter}".toString();
                    def link=links["l"+counter];
                    def linkData=getEdgeFrom(getMapData.edges,sourceName,targetName)
                    checkEdgeData (linkData,link,sourceName,targetName);

                }

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
    def getEdgeFrom(edges,sourceName,targetName)
    {
        def edgeData=edges[getEdgeMapKey(sourceName,targetName)];
        if(edgeData==null)
        {
            edgeData=edges[getEdgeMapKey(targetName,sourceName)];
        }
        return edgeData;

    }
    def getEdgeMapKey(sourceName,targetName)
    {
        return sourceName+""+targetName;
    }
    def checkEdgeData(edgeData,edge,sourceName,targetName)
    {
        assertEquals(edge.getState().toString(),edgeData.state)
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

        def script=CmdbScript.get(name:"getMapData")

        def scriptResult=CmdbScript.runScript(script,["params":params]);

        def resultXml = new XmlSlurper().parseText(scriptResult);

        println scriptResult

        def results=[:]
        results.nodes=[:];
        results.edges=[:];

        def nodeProps=["id","state","type","displayName","model","gauged"];

        resultXml.node.each {    dataRow->
            def nodeData=[:];
            nodeProps.each{ propName ->
                nodeData[propName]=dataRow.@"${propName}".toString();
            }
            results.nodes.put(nodeData.id,nodeData);
        }
        def edgeProps=["source","target","state"];
        resultXml.edge.each {    dataRow->
            def edgeData=[:];
            edgeProps.each{ propName ->
                edgeData[propName]=dataRow.@"${propName}".toString();
            }
            results.edges.put(getEdgeMapKey(edgeData.source,edgeData.target),edgeData);
        }
        println "result parsed from xml ${results}"
        return results;
    }


}




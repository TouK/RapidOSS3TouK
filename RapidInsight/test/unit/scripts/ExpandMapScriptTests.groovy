import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.scripting.ScriptManager
import script.CmdbScript
import script.CmdbScriptOperations
import com.ifountain.rcmdb.test.util.CompassForTests

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 19, 2009
* Time: 9:06:30 AM
* To change this template use File | Settings | File Templates.
*/
class ExpandMapScriptTests  extends RapidCmdbWithCompassTestCase {


    public void setUp() {
        super.setUp();
        initialize([CmdbScript,RsComputerSystem,RsTopologyObject,RsLink,relation.Relation], []);
        CompassForTests.addOperationSupport (CmdbScript,CmdbScriptOperations);
        initializeScriptManager();
    }

    public void tearDown() {

        super.tearDown();
    }
    void initializeScriptManager()
    {
          //to run in Hudson
        def base_directory = "../RapidSuite";
        //def canonicalPath=new File(System.getProperty("base.dir", ".")).getCanonicalPath();
        def canonicalPath=new File(".").getCanonicalPath();
        //to run in developer pc
        if(canonicalPath.endsWith("RapidModules"))
        {
            base_directory = "RapidInsight"
        }
        println "base path is :"+new File(base_directory).getCanonicalPath();

        ScriptManager manager = ScriptManager.getInstance();
        manager.initialize(this.class.getClassLoader(), base_directory, [], [:]);
        //new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY").mkdirs();

    }
    public void testExpandMapWith1Node()
    {
        def script=CmdbScript.addScript([name:"expandMap",type: CmdbScript.ONDEMAND])
        assertFalse(script.hasErrors());

        def source=RsComputerSystem.add(name:"start",model:"smodel",className:"sclass");
        assertFalse(source.hasErrors())

        //calling with no expandedNodeName
        def params=[:];
        params.nodes="start,false,50,100;"

        def expandData=getExpandMapData(params);
        assertEquals(1,expandData.nodes.size());
        assertEquals(0,expandData.edges.size());

        def nodeData=expandData.nodes[source.name];

        assertEquals(source.name,nodeData.id);
        assertEquals(source.model,nodeData.model);
        assertEquals(source.className,nodeData.type);
        assertEquals("true",nodeData.gauged);
        assertEquals("false",nodeData.expanded);
        assertEquals("false",nodeData.expandable);
        assertEquals("50",nodeData.x);
        assertEquals("100",nodeData.y);

        //calling with expandedNodeName
        params=[:];
        params.nodes="start,false,50,100;"
        params.expandedNodeName="start";
        expandData=getExpandMapData(params);
        assertEquals(1,expandData.nodes.size());
        assertEquals(0,expandData.edges.size());

        nodeData=expandData.nodes[source.name];

        assertEquals(source.name,nodeData.id);
        assertEquals(source.model,nodeData.model);
        assertEquals(source.className,nodeData.type);
        assertEquals("true",nodeData.gauged);
        assertEquals("true",nodeData.expanded);
        assertEquals("true",nodeData.expandable);
        assertEquals("50",nodeData.x);
        assertEquals("100",nodeData.y);

    }
     public void testExpandMapWith1To1Node()
    {
        def script=CmdbScript.addScript([name:"expandMap",type: CmdbScript.ONDEMAND])
        assertFalse(script.hasErrors());

        def source=RsComputerSystem.add(name:"start",model:"smodel",className:"sclass");
        assertFalse(source.hasErrors())
        def target=RsComputerSystem.add(name:"end",model:"emodel",className:"eclass");
        assertFalse(target.hasErrors())
        def link1=RsLink.add(name:"l1",a_ComputerSystemName:source.name,z_ComputerSystemName:target.name,connectedSystems:[source,target]);
        assertFalse(link1.hasErrors())

        //calling with no expandedNodeName
        def params=[:];
        params.nodes="start,false,50,100;"
        params.expandedNodeName="start";
        def expandData=getExpandMapData(params);
        assertEquals(2,expandData.nodes.size());
        assertEquals(1,expandData.edges.size());

        def sourceNodeData=expandData.nodes[source.name];

        assertEquals(source.name,sourceNodeData.id);
        assertEquals(source.model,sourceNodeData.model);
        assertEquals(source.className,sourceNodeData.type);
        assertEquals("true",sourceNodeData.gauged);
        assertEquals("true",sourceNodeData.expanded);
        assertEquals("true",sourceNodeData.expandable);
        assertEquals("50",sourceNodeData.x);
        assertEquals("100",sourceNodeData.y);

        def targetNodeData=expandData.nodes[target.name];

        assertEquals(target.name,targetNodeData.id);
        assertEquals(target.model,targetNodeData.model);
        assertEquals(target.className,targetNodeData.type);
        assertEquals("true",targetNodeData.gauged);
        assertEquals("false",targetNodeData.expanded);
        assertEquals("false",targetNodeData.expandable);
        assertEquals("",targetNodeData.x);
        assertEquals("",targetNodeData.y);


    }

     def getExpandMapData(params){
        def script=CmdbScript.get(name:"expandMap")

        def scriptResult=CmdbScript.runScript(script,["params":params]);

        def resultXml = new XmlSlurper().parseText(scriptResult);

        println scriptResult

        def results=[:]
        results.nodes=[:];
        results.edges=[];

        def nodeProps=["id","model","type","gauged","expanded","expandable","x","y"];

        resultXml.node.each {    dataRow->
            def nodeData=[:];
            nodeProps.each{ propName ->
                nodeData[propName]=dataRow.@"${propName}".toString();
            }
            results.nodes.put(nodeData.id,nodeData);
        }
        def edgeProps=["source","target"];
        resultXml.edge.each {    dataRow->
            def edgeData=[:];
            edgeProps.each{ propName ->
                edgeData[propName]=dataRow.@"${propName}".toString();
            }
            results.edges.add(edgeData);
        }
        println "result parsed from xml ${results}"
        return results;
    }


}

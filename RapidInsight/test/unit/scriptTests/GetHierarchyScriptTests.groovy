package scriptTests

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.scripting.ScriptManagerForTest

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 18, 2010
* Time: 6:14:17 PM
* To change this template use File | Settings | File Templates.
*/
class GetHierarchyScriptTests extends RapidCmdbWithCompassTestCase {
    def RsComputerSystem;

   public void setUp() {
        super.setUp();
        ["RsComputerSystem"].each{ className ->
            setProperty(className,gcl.loadClass(className));
        }

        clearMetaClasses();

        initialize([RsComputerSystem], []);
        initializeScriptManager();

    }

    public void tearDown() {
        clearMetaClasses();
        super.tearDown();
    }
    public void clearMetaClasses()
    {
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(RsComputerSystem)        
        ExpandoMetaClass.enableGlobally();
    }

    void initializeScriptManager()
    {
        def base_directory = getWorkspacePath()+"/RapidModules/RapidInsight/scripts"
        println "base path is :"+new File(base_directory).getCanonicalPath();
        ScriptManagerForTest.initialize(gcl,base_directory);
        ScriptManagerForTest.addScript('getHierarchy');
    }

    public void testGetHierarchy()
    {
        RsComputerSystem.metaClass.getState={ ->
            return Integer.parseInt(delegate.name.remove("com"));
        }
        
        def now=Date.now();
        def com1=RsComputerSystem.add(name:"com1",displayName:"dis1",rsDatasource:"rs1");
        def com2=RsComputerSystem.add(name:"com2",displayName:"dis2",rsDatasource:"rs1");
        def com3=RsComputerSystem.add(name:"com3",displayName:"dis3",rsDatasource:"rs2");

        assertEquals(3,RsComputerSystem.count());
        def data=getScriptData([:]);
        assertEquals(2,data.size());

        //rs1 Containter
        assertEquals("rs1",data[0].id);
        assertEquals("rs1",data[0].name);
        assertEquals("rs1",data[0].displayName);
        assertEquals("Container",data[0].nodeType);
        assertEquals("2",data[0].state);

        assertEquals(2,data[0].objects.size());

        assertEquals(com1.id.toString(),data[0].objects[0].id);
        assertEquals("com1",data[0].objects[0].name);
        assertEquals("dis1",data[0].objects[0].displayName);
        assertEquals("Object",data[0].objects[0].nodeType);
        assertEquals("rs1",data[0].objects[0].rsDatasource);
        assertEquals("1",data[0].objects[0].state);

        assertEquals(com2.id.toString(),data[0].objects[1].id);
        assertEquals("com2",data[0].objects[1].name);
        assertEquals("dis2",data[0].objects[1].displayName);
        assertEquals("Object",data[0].objects[1].nodeType);
        assertEquals("rs1",data[0].objects[1].rsDatasource);
        assertEquals("2",data[0].objects[1].state);

        //rs2 Containter
        assertEquals("rs2",data[1].id);
        assertEquals("rs2",data[1].name);
        assertEquals("rs2",data[1].displayName);
        assertEquals("Container",data[1].nodeType);
        assertEquals("3",data[1].state);

        assertEquals(1,data[1].objects.size());

        assertEquals(com3.id.toString(),data[1].objects[0].id);
        assertEquals("com3",data[1].objects[0].name);
        assertEquals("dis3",data[1].objects[0].displayName);
        assertEquals("Object",data[1].objects[0].nodeType);
        assertEquals("rs2",data[1].objects[0].rsDatasource);
        assertEquals("3",data[1].state);

    }


    def getScriptData(params){
        def result=ScriptManagerForTest.runScript("getHierarchy",["params":params]);

        def resultXml = new XmlSlurper().parseText(result);
        def results=[]
        resultXml.Object.each{  containerRow ->
            def container=containerRow.attributes();
            container.objects=[];
            containerRow.Object.each{  objectRow ->
                container.objects.add(objectRow.attributes());
            }
            container.objects=container.objects.sort{it.name};
            results.add(container);
        }
        results=results.sort{it.name};
        println "xml result : ${result}"
        println "result parsed from xml ${results}"
        return results;
    }
}
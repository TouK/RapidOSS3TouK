package scriptTests

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.scripting.ScriptManager
import com.ifountain.rcmdb.test.util.CompassForTests
import script.CmdbScript
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import script.CmdbScriptOperations
import org.apache.commons.io.FileUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: May 15, 2009
* Time: 1:26:05 PM
* To change this template use File | Settings | File Templates.
*/
class RemoveAllScriptTest extends RapidCmdbWithCompassTestCase {
    def baseDir = "../testoutput"
    Class parentClass;
    Class childClass;
    Class model3Class;
    File removeAllScriptFile;
    File removeAllScriptCopiedTargetFile;
    public void setUp() {
        super.setUp();
        def model1Name = "ParentModel";
        def model2Name = "ChildModel";
        def model3Name = "Model3";
        def prop1 = [name: "prop1", type: ModelGenerator.STRING_TYPE];
        def model1MetaProps = [name: model1Name]
        def model2MetaProps = [name: model2Name, parentModel: model1Name]
        def model3MetaProps = [name: model3Name]

        def modelProps = [prop1];
        def keyPropList = [prop1];


        def model1Text = ModelGenerationTestUtils.getModelText(model1MetaProps, modelProps, keyPropList, []);
        def model2Text = ModelGenerationTestUtils.getModelText(model2MetaProps, modelProps, keyPropList, []);
        def model3Text = ModelGenerationTestUtils.getModelText(model3MetaProps, modelProps, keyPropList, []);
        gcl.parseClass(model1Text + model2Text + model3Text);
        parentClass = gcl.loadClass(model1Name)
        childClass = gcl.loadClass(model2Name)
        model3Class = gcl.loadClass(model3Name)
        def compassClasses = [CmdbScript, parentClass, childClass, model3Class];
        initialize(compassClasses, []);
        CompassForTests.addOperationSupport(CmdbScript, CmdbScriptOperations);
    }

    public void tearDown() {

        super.tearDown();
    }
    void initializeScriptManager()
    {
        ScriptManager manager = ScriptManager.getInstance();
        manager.initialize(gcl, baseDir, [], [:]);
    }

    void copyScriptFile()
    {
        def scriptDirectory=getWorkspacePath()+"/RapidModules/RapidInsight"

        removeAllScriptFile = new File("${scriptDirectory}/scripts/removeAll.groovy");
        removeAllScriptCopiedTargetFile = new File("${baseDir}/scripts/removeAll.groovy");
        FileUtils.deleteDirectory(removeAllScriptCopiedTargetFile.parentFile);
        removeAllScriptCopiedTargetFile.parentFile.mkdirs();
        FileUtils.copyFile(removeAllScriptFile, removeAllScriptCopiedTargetFile);
    }

    public void testRemoveAll()
    {
        copyScriptFile();
        initializeScriptManager();
        def script = CmdbScript.addScript([name: "removeAll", type: CmdbScript.ONDEMAND])
        assertFalse(script.hasErrors());
        def allObjects = [];
        allObjects << parentClass.add(prop1: "obj1");
        allObjects << parentClass.add(prop1: "obj2");
        allObjects << childClass.add(prop1: "obj3");
        allObjects << childClass.add(prop1: "obj4");
        allObjects << model3Class.add(prop1: "obj5");
        allObjects << model3Class.add(prop1: "obj6");
        allObjects.each {
            assertFalse(it.hasErrors());
        }
        CmdbScript.runScript(script, [:])
        assertEquals(0, parentClass.count());
        assertEquals(0, childClass.count());
        assertEquals(0, model3Class.count());
        assertEquals(1, CmdbScript.count());
    }

    public void testRemoveAllWithExcludedModelList()
    {
        String excludedClassesRegExp = "def\\s+excludedList\\s*=\\s*\\[\\];"
        copyScriptFile();
        String scriptText = removeAllScriptCopiedTargetFile.getText();
        assertTrue ("ExcludedList variable is not defined in script", !scriptText.equals(scriptText.replaceFirst(excludedClassesRegExp, "")));
        scriptText = scriptText.replaceAll(excludedClassesRegExp, """def excludedList = ["${model3Class.name}"]""")

        removeAllScriptCopiedTargetFile.setText (scriptText);
        initializeScriptManager();
        def script = CmdbScript.addScript([name: "removeAll", type: CmdbScript.ONDEMAND])
        assertFalse(script.hasErrors());
        def allObjects = [];
        allObjects << parentClass.add(prop1: "obj1");
        allObjects << parentClass.add(prop1: "obj2");
        allObjects << childClass.add(prop1: "obj3");
        allObjects << childClass.add(prop1: "obj4");
        allObjects << model3Class.add(prop1: "obj5");
        allObjects << model3Class.add(prop1: "obj6");
        allObjects.each {
            assertFalse(it.hasErrors());
        }
        CmdbScript.runScript(script, [:])
        assertEquals(0, parentClass.count());
        assertEquals(0, childClass.count());
        assertEquals(2, model3Class.count());
        assertEquals(1, CmdbScript.count());
    }

    public void testRemoveAllWithExcludedModelListIncludingChildModelName()
    {
        String excludedClassesRegExp = "def\\s+excludedList\\s*=\\s*\\[\\];"
        copyScriptFile();
        String scriptText = removeAllScriptCopiedTargetFile.getText();
        assertTrue ("ExcludedList variable is not defined in script", !scriptText.equals(scriptText.replaceFirst(excludedClassesRegExp, "")));
        scriptText = scriptText.replaceAll(excludedClassesRegExp, """def excludedList = ["${childClass.name}"]""")

        removeAllScriptCopiedTargetFile.setText (scriptText);
        initializeScriptManager();
        def script = CmdbScript.addScript([name: "removeAll", type: CmdbScript.ONDEMAND])
        assertFalse(script.hasErrors());
        def allObjects = [];
        allObjects << parentClass.add(prop1: "obj1");
        allObjects << parentClass.add(prop1: "obj2");
        allObjects << childClass.add(prop1: "obj3");
        allObjects << childClass.add(prop1: "obj4");
        allObjects << model3Class.add(prop1: "obj5");
        allObjects << model3Class.add(prop1: "obj6");
        allObjects.each {
            assertFalse(it.hasErrors());
        }
        CmdbScript.runScript(script, [:])
        assertEquals(0, parentClass.count()-childClass.count());
        assertEquals(2, childClass.count());
        assertEquals(0, model3Class.count());
        assertEquals(1, CmdbScript.count());
    }
}
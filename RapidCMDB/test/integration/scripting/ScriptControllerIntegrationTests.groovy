package scripting

import script.ScriptController
import script.CmdbScript
import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Apr 18, 2008
* Time: 1:29:49 PM
* To change this template use File | Settings | File Templates.
*/
class ScriptControllerIntegrationTests extends RapidCmdbIntegrationTestCase{

    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.

    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }

    

    public void testSave()
    {
        String scriptName = "script1"
        def script = new CmdbScript(name:scriptName);
        def scriptController = new ScriptController();
        scriptController.params["name"] = script.name;
        scriptController.save();
        
        script = CmdbScript.findByName(scriptName);

        assertEquals("/script/show/" + script.id, scriptController.response.redirectedUrl);
        IntegrationTestUtils.resetController(scriptController);
    }
}
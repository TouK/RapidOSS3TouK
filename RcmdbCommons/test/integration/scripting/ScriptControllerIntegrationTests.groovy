/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
package scripting

import com.ifountain.rcmdb.scripting.ScriptManager
import com.ifountain.rcmdb.test.util.IntegrationTestUtils
import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import script.CmdbScript
import script.ScriptController
import grails.util.GrailsWebUtil
import org.codehaus.groovy.grails.commons.ApplicationHolder
import auth.Group
import com.ifountain.rcmdb.util.DataStore

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Apr 18, 2008
* Time: 1:29:49 PM
* To change this template use File | Settings | File Templates.
*/
class ScriptControllerIntegrationTests extends RapidCmdbIntegrationTestCase{
    static transactional = false;
    String expectedScriptMessage;
    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        expectedScriptMessage = "script successfully executed";
        CmdbScript.reloadOperations();
        CmdbScript.list().each{
            CmdbScript.deleteScript(it)
        }
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }


    def createSimpleScript(scriptName)
    {
        def scriptFile = new File("${System.getProperty("base.dir")}/$ScriptManager.SCRIPT_DIRECTORY/${scriptName}.groovy");
        scriptFile.write ("""return "$expectedScriptMessage" """);
    }

    def createErrornousScript(scriptName)
    {
        def scriptFile = new File("${System.getProperty("base.dir")}/$ScriptManager.SCRIPT_DIRECTORY/${scriptName}.groovy");
        scriptFile.write ("return \"$expectedScriptMessage");
    }

    def deleteSimpleScript(scriptName)
    {
        new File("${System.getProperty("base.dir")}/$ScriptManager.SCRIPT_DIRECTORY/${scriptName}.groovy").delete();

    }

    public void testSave()
    {
        String scriptName = "script1"
        createSimpleScript(scriptName);
        try
        {
            def scriptController = new ScriptController();
            scriptController.params["name"] = scriptName;
            scriptController.save();

            CmdbScript script = CmdbScript.findByName(scriptName);
            assertNotNull (script);
            assertEquals (scriptName, script.scriptFile);
            assertEquals("/script/show/" + script.id, scriptController.response.redirectedUrl);
        }
        finally
        {
            deleteSimpleScript (scriptName);
        }
    }

    public void testSaveWithGroups()
    {
        String scriptName = "script1"
        createSimpleScript(scriptName);
        try
        {
            def gr1 = Group.add(name:"gr1")
            def gr2 = Group.add(name:"gr2")
            def gr3 = Group.add(name:"gr3")
            assertFalse (gr1.hasErrors());
            assertFalse (gr2.hasErrors());
            assertFalse (gr3.hasErrors());
            def scriptController = new ScriptController();
            scriptController.params["name"] = scriptName;
            scriptController.params["allowedGroups.id"] = "${gr1.id},${gr2.id}";
            scriptController.save();

            CmdbScript script = CmdbScript.findByName(scriptName);
            assertNotNull (script);
            assertEquals (scriptName, script.scriptFile);
            def groups = script.allowedGroups.sort{it.name}
            assertEquals (2, groups.size());
            assertEquals (gr1.name, groups[0].name);
            assertEquals (gr2.name, groups[1].name);

            assertEquals("/script/show/" + script.id, scriptController.response.redirectedUrl);
        }
        finally
        {
            deleteSimpleScript (scriptName);
        }
    }

    public void testSaveWithScriptFileName()
    {
        String scriptFileName = "script1File"
        String scriptName = "script1"
        createSimpleScript(scriptFileName);
        try
        {
            def scriptController = new ScriptController();
            scriptController.params["name"] = scriptName;
            scriptController.params["scriptFile"] = scriptFileName;
            scriptController.save();

            CmdbScript script = CmdbScript.findByName(scriptName);
            assertNotNull (script);
            assertEquals (scriptFileName, script.scriptFile);
            assertEquals("/script/show/" + script.id, scriptController.response.redirectedUrl);
        }
        finally
        {
            deleteSimpleScript (scriptName);
        }
    }
    public void testSaveReturndErrorIfSyntaxExceptionExistInScript()
    {
        String scriptFileName = "script1File"
        String scriptName = "script1"
        createErrornousScript(scriptFileName);
        try
        {
            def scriptController = new ScriptController();
            scriptController.params["name"] = scriptName;
            scriptController.params["scriptFile"] = scriptFileName;
            scriptController.save();

            def script = CmdbScript.findByName(scriptName);
            assertNull (script);
            assertEquals(scriptName, scriptController.modelAndView.model.cmdbScript.name);
        }
        finally
        {
            deleteSimpleScript (scriptName);
        }
    }

    public void testReload()
    {
        String scriptName = "script1"
        createSimpleScript(scriptName);
        try
        {
            def scriptController = new ScriptController();
            scriptController.params["name"] = scriptName;
            scriptController.save();

            def script = CmdbScript.findByName(scriptName);

            IntegrationTestUtils.resetController (scriptController);
            scriptController.params["id"] = script.name;
            scriptController.run();
            assertEquals(expectedScriptMessage, scriptController.response.contentAsString);

            expectedScriptMessage = "changed message"
            createSimpleScript(scriptName);
            IntegrationTestUtils.resetController (scriptController);
            scriptController.params["id"] = script.name;
            scriptController.reload();
            assertEquals("/script/show/" + script.id, scriptController.response.redirectedUrl);

            IntegrationTestUtils.resetController (scriptController);
            scriptController.params["id"] = script.name;
            scriptController.run();
            assertEquals(expectedScriptMessage, scriptController.response.contentAsString);
        }
        finally
        {
            deleteSimpleScript (scriptName);
        }
    }

    public void testReloadReturnsErrorIfScriptDoesnotExist()
    {
        String scriptName = "script1"
        try
        {
            def scriptController = new ScriptController();
            scriptController.params["id"] = scriptName;
            scriptController.reload();
            assertEquals(ScriptController.SCRIPT_DOESNOT_EXIST, scriptController.flash.message);
            assertEquals("/script/list", scriptController.response.redirectedUrl);
        }
        finally
        {
            deleteSimpleScript (scriptName);
        }
    }
    public void testReloadReturnsErrorIfScriptContainsSysntaxErrors()
    {
        String scriptName = "script1"
        createSimpleScript(scriptName);
        try
        {
            def scriptController = new ScriptController();
            scriptController.params["name"] = scriptName;
            scriptController.save();

            def script = CmdbScript.findByName(scriptName);

            createErrornousScript(scriptName);
            IntegrationTestUtils.resetController (scriptController);
            scriptController.params["id"] = script.name;
            scriptController.reload();
            assertTrue(scriptController.flash.message.indexOf("Exception occurred") >= 0);
            assertEquals("/script/show/" + script.id, scriptController.response.redirectedUrl);

            IntegrationTestUtils.resetController (scriptController);
            scriptController.params["id"] = script.name;
            scriptController.run();
            assertEquals(expectedScriptMessage, scriptController.response.contentAsString);
        }
        finally
        {
            deleteSimpleScript (scriptName);
        }
    }


    public void testControllerParamatersAndMethodsAreAvailableToScript()
    {
        String scriptName = "script1"
        def scriptFile = new File("${System.getProperty("base.dir")}/$ScriptManager.SCRIPT_DIRECTORY/${scriptName}.groovy");
        scriptFile.write ("""
        web.render(text:web.session.toString(), contentType: "text/html", encoding: "UTF-8");
        return "<This will be discarded/>"
        """);
        try
        {
            def scriptController = new ScriptController();
            scriptController.params["name"] = scriptName;
            scriptController.save();

            IntegrationTestUtils.resetController (scriptController);
            scriptController.params["id"] = scriptName;
            scriptController.run();
            assertEquals(scriptController.session.toString(), scriptController.response.contentAsString);
            assertEquals (GrailsWebUtil.getContentType("text/html", ""), scriptController.response.contentType);
        }
        finally
        {
            deleteSimpleScript (scriptName);
        }
    }

    public void testRunReturnsErrorIfScriptDoesnotExist()
    {
        String scriptName = "script1"
        try
        {
            def scriptController = new ScriptController();
            scriptController.params["id"] = scriptName;
            scriptController.run();
            assertEquals(ScriptController.SCRIPT_DOESNOT_EXIST, scriptController.flash.message);
            assertEquals("/script/list", scriptController.response.redirectedUrl);
        }
        finally
        {
            deleteSimpleScript (scriptName);
        }
    }

    public void testRunWithReturningNothing()
    {
        String scriptName = "script1"
        def scriptFile = new File("${System.getProperty("base.dir")}/$ScriptManager.SCRIPT_DIRECTORY/${scriptName}.groovy");
        scriptFile.write ("return null");
        try
        {
            def scriptController = new ScriptController();
            scriptController.params["name"] = scriptName;
            scriptController.save();

            IntegrationTestUtils.resetController (scriptController);
            scriptController.params["id"] = scriptName;
            scriptController.run();
            assertEquals("", scriptController.response.contentAsString);
            assertEquals (GrailsWebUtil.getContentType("text/html", ""), scriptController.response.contentType);
        }
        finally
        {
            deleteSimpleScript (scriptName);
        }
    }

    public void testRunWithReturningNonStringObject()
    {
        String scriptName = "script1"
        def scriptFile = new File("${System.getProperty("base.dir")}/$ScriptManager.SCRIPT_DIRECTORY/${scriptName}.groovy");
        scriptFile.write ("return new Integer(0)");
        try
        {
            def scriptController = new ScriptController();
            scriptController.params["name"] = scriptName;
            scriptController.save();

            IntegrationTestUtils.resetController (scriptController);
            scriptController.params["id"] = scriptName;
            scriptController.run();
            assertEquals("0", scriptController.response.contentAsString);
            assertEquals (GrailsWebUtil.getContentType("text/html", ""), scriptController.response.contentType);
        }
        finally
        {
            deleteSimpleScript (scriptName);
        }
    }

    public void testRunWithReturningXml()
    {
        String scriptName = "script1"
        def scriptFile = new File("${System.getProperty("base.dir")}/$ScriptManager.SCRIPT_DIRECTORY/${scriptName}.groovy");
        scriptFile.write ("""return "<Records/>";""");
        try
        {
            def scriptController = new ScriptController();
            scriptController.params["name"] = scriptName;
            scriptController.save();

            IntegrationTestUtils.resetController (scriptController);
            scriptController.params["id"] = scriptName;
            scriptController.run();
            assertEquals("<Records/>", scriptController.response.contentAsString);
            assertEquals (GrailsWebUtil.getContentType("text/xml", ""), scriptController.response.contentType);
        }
        finally
        {
            deleteSimpleScript (scriptName);
        }
    }
    public void testRunWithScriptDrawingImageToWebResponse()
    {
        String scriptName = "script1"
        def scriptFile = new File("${System.getProperty("base.dir")}/$ScriptManager.SCRIPT_DIRECTORY/${scriptName}.groovy");
        scriptFile.write ("""
            import com.ifountain.rcmdb.util.DataStore

            def bufImage = new java.awt.image.BufferedImage(5, 5, java.awt.image.BufferedImage.TYPE_INT_RGB);
            java.awt.Graphics g = bufImage.getGraphics();
            g.setColor(new java.awt.Color(255, 0, 0));
            g.fillRect(0,0,5,5);
            g.dispose();

            org.apache.commons.io.output.ByteArrayOutputStream baos = new org.apache.commons.io.output.ByteArrayOutputStream();
            javax.imageio.ImageIO.write(bufImage, 'png', baos);
            byte[] bytesOut = baos.toByteArray();
            DataStore.put("imageBytes",bytesOut);

            com.ifountain.rcmdb.domain.util.ControllerUtils.drawImageToWeb(bufImage,"image/png","png",web.response);


        """);
        try
        {
            def scriptController = new ScriptController();
            scriptController.params["name"] = scriptName;
            scriptController.save();

            IntegrationTestUtils.resetController (scriptController);
            scriptController.params["id"] = scriptName;
            scriptController.run();

            assertEquals("image/png", scriptController.response.contentType);


            byte[] content = scriptController.response.getContentAsByteArray()
            byte[] realData = DataStore.get("imageBytes");

            assertEquals(realData.length, content.length)

            for(int i = 0; i < realData.length; i++)
                assertEquals(realData[i], content[i])

        }
        finally
        {
            deleteSimpleScript (scriptName);
        }
    }

    public void testRunReturnsErrorIfScriptContainsErrors()
    {
        def exceptionMessage = "Error occurred";
        String scriptName = "script1"
        def scriptFile = new File("${System.getProperty("base.dir")}/$ScriptManager.SCRIPT_DIRECTORY/${scriptName}.groovy");
        scriptFile.write ("throw new Exception(\"$exceptionMessage\")");
        try
        {
            def scriptController = new ScriptController();
            scriptController.params["name"] = scriptName;
            scriptController.save();

            IntegrationTestUtils.resetController (scriptController);
            scriptController.params["id"] = scriptName;
            scriptController.run();
            assertTrue(scriptController.response.contentAsString, scriptController.response.contentAsString.indexOf(exceptionMessage) >= 0);
        }
        finally
        {
            deleteSimpleScript (scriptName);
        }
    }
}
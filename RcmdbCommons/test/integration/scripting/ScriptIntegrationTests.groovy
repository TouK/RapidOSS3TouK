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
import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import script.CmdbScript
import org.codehaus.groovy.grails.commons.ApplicationHolder

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Apr 18, 2008
* Time: 5:13:37 PM
* To change this template use File | Settings | File Templates.
*/
class ScriptIntegrationTests extends RapidCmdbIntegrationTestCase{
    static transactional = false;
    def messageService;
    String expectedScriptMessage;
    String scriptName = "script1";
    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        expectedScriptMessage = "script successfully executed";
        CmdbScript.list().each{
            CmdbScript.deleteScript(it)
        }
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        deleteSimpleScript(scriptName);
    }

    public void testSimpleScriptAdd()
    {
        def scriptFile = scriptName + "File"
        createSimpleScript (scriptFile);
        def script = CmdbScript.add(name:scriptName, scriptFile:scriptFile)
        assertNotNull(script)
        assertFalse(script.hasErrors())
    }
    public void testValidatesScriptBeforeAddAndIfScriptIsInvalidRetunsError()
    {
        def scriptFile = scriptName + "File"
        createErrornousScript(scriptFile);
        def script = CmdbScript.add(name:scriptName, scriptFile:scriptFile)
        assertNotNull(script)
        assertTrue(script.hasErrors())
        assertEquals("script.compilation.error", script.errors.allErrors[0].code);
    }
    public void testNameisUnique()
    {
        def scriptFile = scriptName + "File"
        createSimpleScript(scriptFile);
        def script = CmdbScript.add(name:scriptName, scriptFile:scriptFile)
        assertFalse(script.hasErrors())

        def scriptFile2 = scriptName + "File2"
        createSimpleScript(scriptFile2);
        script = new CmdbScript(name:scriptName, scriptFile:scriptFile2)
        script.validate();
        assertTrue(script.hasErrors())
        println script.errors;
        assertEquals("default.not.unique.message", script.errors.allErrors[0].code);
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

}
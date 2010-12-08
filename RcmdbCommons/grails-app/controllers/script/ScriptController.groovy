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
package script

import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.scripting.ScriptManager
import junit.framework.Test
import junit.framework.TestResult
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest
import org.apache.tools.ant.taskdefs.optional.junit.XMLJUnitResultFormatter
import auth.Group

class ScriptController {
    public static final String SUCCESSFULLY_CREATED = "Script created";
    public static final String SCRIPT_DOESNOT_EXIST = "Script does not exist";

    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST', run: ['POST', 'GET']]

    def list = {
        if (!params.max) params.max = 100
        if (!params.sort) params.sort = "name"
        def searchQuery=params.query?params.query:"alias:*";
        [cmdbScriptList: CmdbScript.search(searchQuery, params).results, searchQuery: searchQuery]
    }


    def show = {
        def cmdbScript = CmdbScript.get([id: params.id])

        if (!cmdbScript) {
            flash.message = "CmdbScript not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (cmdbScript.class != CmdbScript)
            {
                def controllerName = cmdbScript.class.simpleName;
                if (controllerName.length() == 1)
                {
                    controllerName = controllerName.toLowerCase();
                }
                else
                {
                    controllerName = controllerName.substring(0, 1).toLowerCase() + controllerName.substring(1);
                }
                redirect(action: show, controller: controllerName, id: params.id)
            }
            else
            {
                return [cmdbScript: cmdbScript]
            }
        }
    }

    def create = {
        def cmdbScript = new CmdbScript()
        cmdbScript.properties = params
        def availableGroups = Group.list([sort:"name"]);
        return ['cmdbScript': cmdbScript, availableGroups:availableGroups,allowedGroups:[]]
    }

    def save = {
        params.logFile=params.name;
        def classProperties = ControllerUtils.getClassProperties(params, CmdbScript);
        classProperties["listenToRepository"] = params["listenToRepository"] == "on"
        def script = CmdbScript.addScript(classProperties, true)
        if (script.hasErrors()) {
            render(view: 'create', controller: 'script', model: [cmdbScript: script,availableGroups:availableGroupsForAllowedGroups(classProperties.allowedGroups),allowedGroups:classProperties.allowedGroups])
        }
        else {
            flash.message = SUCCESSFULLY_CREATED
            redirect(action: show, id:script.id)
        }
    }

    def availableGroupsForAllowedGroups(allowedGroups)
    {
        def availableGroups = Group.list([sort:"name"]);
        def allowedGroupNames = [:];
        allowedGroups.each {
            allowedGroupNames[it.name] = it;
        };
        return availableGroups.findAll {!allowedGroupNames.containsKey(it.name)}
    }

    def delete = {
        def script = CmdbScript.get([id: params.id])
        if (script) {
            CmdbScript.deleteScript(script);
            flash.message = "Script ${params.id} deleted"
            redirect(action: list)
        }
        else {
            flash.message = "Script not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        CmdbScript cmdbScript = CmdbScript.get([id: params.id])

        if (!cmdbScript) {
            flash.message = "CmdbScript not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def allowedGroups = cmdbScript.allowedGroups.sort{it.name};
            def availableGroups=availableGroupsForAllowedGroups(allowedGroups)

            return [cmdbScript: cmdbScript, availableGroups:availableGroups,allowedGroups:allowedGroups]
        }
    }

    def update = {
        def script = CmdbScript.get([id: params.id])
        if (script) {
            params.logFile=params.name;
            def classProperties = ControllerUtils.getClassProperties(params, CmdbScript);
            classProperties["listenToRepository"] = params["listenToRepository"] == "on"
            script = CmdbScript.updateScript(script,classProperties, true);
            if (script.hasErrors()) {
                render(view: 'edit', model: [cmdbScript: script,availableGroups:availableGroupsForAllowedGroups(classProperties.allowedGroups),allowedGroups:classProperties.allowedGroups])
            }
            else {
                flash.message = "Script ${params.id} updated"
                redirect(action: show, id:script.id)
            }
        }
        else {
            flash.message = "Script not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def reload = {
        def script = CmdbScript.get(name:params.id);
        if (script)
        {
            try
            {
                script.reload();
                flash.message = "Script ${script.name} reloaded successfully.";
                if(params.targetURI){
                    redirect(uri:params.targetURI);
                }
                else{
                    redirect(action: show, id: script.id);
                }

            }
            catch (t)
            {
                flash.message = "Exception occurred while reloading script ${script.name}. Reason : ${t.getMessage()}";
                if(params.targetURI){
                    redirect(uri:params.targetURI);
                }
                else{
                    redirect(action: show, controller: 'script', id: script.id);
                }

            }

        }
        else
        {
            flash.message = SCRIPT_DOESNOT_EXIST
            redirect(action: list, controller: 'script');
        }
    }
    def run =
    {
        def script = CmdbScript.get(name:params.id);
        if (script)
        {
            try
            {
                def controllerDelegateMetaClass = new DelegateMt(this);
                def scriptParams = ["web": this, "params": params];
                def result = CmdbScript.runScript(script, scriptParams);
                if (controllerDelegateMetaClass.isRendered) return;
                if(response.contentType != null)
                {
                    if(response.contentType.indexOf("image")>=0)
                    {
                        return;
                    }
                }
                if (result == null) {
                    result = "";
                }
                else
                {
                    result = String.valueOf(result);
                }
                def contentType = "text/html"
                if (result.startsWith("<") && !result.startsWith("<html>")) {
                    contentType = "text/xml"
                }
                render(text: String.valueOf(result), contentType: contentType, encoding: "UTF-8");
            }
            catch (t)
            {
                addError("script.execute.error", [t.toString()])
                def scriptLogger=CmdbScript.getScriptLogger(script);
                scriptLogger.warn("", org.codehaus.groovy.runtime.StackTraceUtils.deepSanitize(t));
                render(text: errorsToXml(errors), contentType: "text/xml")
            }

        }
        else
        {
            if(params.format!="xml")
            {
                flash.message = SCRIPT_DOESNOT_EXIST;
                redirect(action: list, controller: 'script');
            }
            else
            {
                render(text: ControllerUtils.convertErrorToXml("${params.id} ${SCRIPT_DOESNOT_EXIST}"), contentType: "text/xml");
            }

        }
    }

    def start = {
        CmdbScript script = CmdbScript.get(name:params.id);
        if (script)
        {
            try {
                CmdbScript.startListening(script);
                flash.message = "Script ${params.id} started to listen"
                if(params.targetURI){
                    redirect(uri:params.targetURI);
                }
                else{
                    redirect(action: show, id: script.id)    
                }
            }
            catch (e) {
                log.warn("Exception occurred while starting script", e);
                addError("script.start.error", [e.getMessage()])
                flash.errors = this.errors;
                if(params.targetURI){
                    redirect(uri:params.targetURI);
                }
                else{
                    redirect(action: show, id: script.id)    
                }
            }
        }
        else
        {
            flash.message = SCRIPT_DOESNOT_EXIST
            redirect(action: list, controller: 'script');
        }
    }

    def stop = {
        CmdbScript script = CmdbScript.get(name:params.id);
        if (script)
        {
            try {
                CmdbScript.stopListening(script);
                flash.message = "Script ${params.id} stopped to listen"
                if(params.targetURI){
                    redirect(uri:params.targetURI);
                }
                else{
                    redirect(action: show, id: script.id)    
                }
            }
            catch (e) {
                log.warn("Exception occurred while stopping script", e);
                addError("script.stop.error", [e.getMessage()])
                flash.errors = this.errors;
                if(params.targetURI){
                    redirect(uri:params.targetURI);
                }
                else{
                    redirect(action: show, id: script.id)    
                }

            }
        }
        else
        {
            flash.message = SCRIPT_DOESNOT_EXIST
            redirect(action: list, controller: 'script');
        }
    }

    def stopRunningScripts = {
        CmdbScript script = CmdbScript.get(name:params.id);
        if (script)
        {
            CmdbScript.stopRunningScripts(script);
            flash.message = "Running instances of script '${script.name}' has been marked as stopped."
            if(params.targetURI){
                redirect(uri:params.targetURI);
            }
            else{
                redirect(action: show, id: script.id)
            }
        }
        else
        {
            flash.message = SCRIPT_DOESNOT_EXIST
            redirect(action: list, controller: 'script');
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("script.CmdbScript")
        if (modelClass)
        {
            try
            {

                modelClass.metaClass.invokeStaticMethod(modelClass, "reloadOperations", [] as Object[]);
                flash.message = "Model operations reloaded"
                redirect(action:list)
            } catch (t)
            {
                flash.message = "Exception occurred while reloading model operations Reason:${t.toString()}"
                 redirect(action:list)
            }
        }
        else
        {
            flash.message = "Model currently not loaded by application. You should reload application."
            redirect(action:list)
        }
    }


    def enable = {
        def script = CmdbScript.get([name:params.id])
        if (script) {
            CmdbScript.updateScript(script, [enabled:true], true);
            if (!script.hasErrors()) {
                flash.message = "Script with name ${params.id} successfully enabled."
            }
            else {
                flash.errors = script.errors;
            }

            if(params.targetURI){
                redirect(uri:params.targetURI);
            }
            else{
                redirect(action: show, id: script.id)
            }
        }
        else {
            flash.message = "Script not found with name ${params.id}"

            if(params.targetURI){
                redirect(uri:params.targetURI);
            }
            else{
                redirect(action: list)
            }
        }
    }

    def disable = {
        def script = CmdbScript.get([name:params.id])
        if (script) {
            CmdbScript.updateScript(script, [enabled:false], true);
            if (!script.hasErrors()) {
                flash.message = "Script with name ${params.id} successfully disabled."
            }
            else {
                flash.errors = script.errors;
            }

            if(params.targetURI){
                redirect(uri:params.targetURI);
            }
            else{
                redirect(action: show, id: script.id)
            }
        }
        else {
            flash.message = "Script not found with name ${params.id}"

            if(params.targetURI){
                redirect(uri:params.targetURI);
            }
            else{
                redirect(action: list)
            }
        }
    }

     def updateLogLevel = {
        def script = CmdbScript.get([name:params.id])
        if (script) {
            CmdbScript.updateLogLevel(script, params.logLevel ? params.logLevel : script.logLevel, true);
            if (!script.hasErrors()) {
                flash.message = "Script with name ${params.id} successfully updated."
            }
            else {
                flash.errors = script.errors;
            }

            if(params.targetURI){
                redirect(uri:params.targetURI);
            }
            else{
                redirect(action: show, id: script.id)
            }
        }
        else {
            flash.message = "Script not found with name ${params.id}"

            if(params.targetURI){
                redirect(uri:params.targetURI);
            }
            else{
                redirect(action: list)
            }
        }
    }

    def test =
    {
        def testDir = "test/reports"
        new AntBuilder().mkdir(dir: testDir);
        def script = CmdbScript.get(name:params.id);
        if (script)
        {
            def bindings = ["params": params]
            def renderText = "";
            new File("${testDir}/TEST-${script.name}.xml").withOutputStream {xmlOut ->
                def savedOut = System.out
                def savedErr = System.err
                try {
                    def outBytes = new ByteArrayOutputStream()
                    def errBytes = new ByteArrayOutputStream()
                    System.out = new PrintStream(outBytes)
                    System.err = new PrintStream(errBytes)
                    def xmlOutput = new XMLJUnitResultFormatter(output: xmlOut)
                    def junitTest = new JUnitTest(script.name)
                    xmlOutput.startTestSuite(junitTest)
                    def errorCount = 0;
                    def start = System.currentTimeMillis();
                    try
                    {
                        def result = ScriptManager.getInstance().runScript(script.name, bindings,CmdbScript.getScriptLogger(script));
                        junitTest.setRunTime(System.currentTimeMillis() - start)
                        renderText = String.valueOf(result);
                    }
                    catch (Throwable t)
                    {
                        junitTest.setRunTime(System.currentTimeMillis() - start)
                        xmlOutput.addError(new MockScriptTest(script.name), org.codehaus.groovy.runtime.StackTraceUtils.deepSanitize(t));
                        renderText = t.toString();
                        errorCount = 1;
                    }
                    junitTest.setCounts(1, 0, errorCount);
                    def outString = outBytes.toString()
                    def errString = errBytes.toString()
                    xmlOutput.setSystemOutput(outString)
                    xmlOutput.setSystemError(errString)
                    xmlOutput.endTestSuite(junitTest)
                }
                catch (Throwable t2) {
                    renderText = t2.toString();
                }
                finally {
                    System.out = savedOut
                    System.err = savedErr
                }
            }
            render(text: renderText, contentType: "text/html", encoding: "UTF-8");

        }
        else
        {
            flash.message = SCRIPT_DOESNOT_EXIST
            redirect(action: list, controller: 'script');
        }
    }
}
class DelegateMt extends DelegatingMetaClass
{
    boolean isRendered = false;
    ScriptController controller;
    public DelegateMt(ScriptController controller)
    {
        super(controller.metaClass);
        controller.setMetaClass(this);
    }

    public Object invokeMethod(Object o, String s, Object o1) {
        if (s == "render" || s == "redirect")
        {
            metaClass.getMetaProperty("isRendered").setProperty(this, true);
        }
        return super.invokeMethod(o, s, o1); //To change body of overridden methods use File | Settings | File Templates.
    }

    public Object invokeMethod(Object o, String s, Object[] objects) {
        if (s == "render" || s == "redirect")
        {
            metaClass.getMetaProperty("isRendered").setProperty(this, true);
        }
        return super.invokeMethod(o, s, objects); //To change body of overridden methods use File | Settings | File Templates.
    }

}

class MockScriptTest implements Test {
    def name;
    public MockScriptTest(String name) {
        this.name = name;
    }
    public int countTestCases() {
        return 0;
    }
    public void run(TestResult arg0) {
    }

    public String getName() {
        return name;
    }
    public String name() {
        return name;
    }
}

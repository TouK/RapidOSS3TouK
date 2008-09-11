package script
import com.ifountain.rcmdb.scripting.ScriptManager
import com.ifountain.rcmdb.scripting.ScriptScheduler
import com.ifountain.rcmdb.scripting.ScriptingException
import org.quartz.CronTrigger
import datasource.BaseListeningDatasource
import com.ifountain.rcmdb.datasource.ListeningAdapterManager
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 11, 2008
 * Time: 4:28:54 PM
 * To change this template use File | Settings | File Templates.
 */
class CmdbScriptOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{

    def beforeDelete(){
        if(this.type == LISTENING ){
            def scriptInCompass = CmdbScript.get(this.id);
            if(scriptInCompass.listeningDatasource){
                ListeningAdapterManager.getInstance().stopAdapter(scriptInCompass.listeningDatasource);
            }

        }
    }

     def beforeUpdate(){
        if(this.type == LISTENING ){
            def scriptInCompass = CmdbScript.get(this.id);
            if(scriptInCompass.listeningDatasource){
                ListeningAdapterManager.getInstance().stopAdapter(scriptInCompass.listeningDatasource);
            }
        }
    }

    def reload() throws ScriptingException
    {
        ScriptManager.getInstance().reloadScript(scriptFile);
    }
    
    static def addScript(Map params, boolean fromController) throws Exception {
        if(!params.get("scriptFile") || params.get("scriptFile").trim() == "")
        {
            params["scriptFile"] = params.name;
        }
        def script = CmdbScript.add(params)
        if (!script.hasErrors()) {
            if(ScriptManager.getInstance().getScript(script.scriptFile) == null)
            {
                ScriptManager.getInstance().addScript(script.scriptFile);
            }
            if (script.type == CmdbScript.SCHEDULED && script.enabled) {
                if (script.scheduleType == CmdbScript.CRON) {
                    ScriptScheduler.getInstance().scheduleScript(script.name, script.startDelay, script.cronExpression)
                }
                else {
                    ScriptScheduler.getInstance().scheduleScript(script.name, script.startDelay, script.period)
                }
            }
        }
        if (!fromController && script.hasErrors()) {
            throw new Exception(script.messageService.getMessage(script.errors.allErrors[0]))
        }
        return script;
    }
    static def addScript(Map params) throws Exception {
        return addScript(params, false);
    }

    static def deleteScript(CmdbScript script) throws Exception {
        def scriptName = script.name;
        script.remove()
        if(CmdbScript.countHits("scriptFile:"+script.scriptFile) == 0)
        {
            ScriptManager.getInstance().removeScript(script.scriptFile);
        }
        ScriptScheduler.getInstance().unscheduleScript(scriptName)
    }

    static def deleteScript(String scriptName) throws Exception {
        CmdbScript script = CmdbScript.findByName(scriptName)
        if (script) {
            deleteScript(script)
        }
        else {
            throw new Exception("Script with name ${scriptName} does not exist")
        }
    }

    static def updateScript(CmdbScript script, Map params, boolean fromController) throws Exception {
        def scriptFileBeforeUpdate = script.scriptFile;
        script.update(params);

        if (!script.hasErrors()) {

            if(scriptFileBeforeUpdate != script.scriptFile && ScriptManager.getInstance().getScript(script.scriptFile) == null)
            {
                if(CmdbScript.countHits("scriptFile:"+scriptFileBeforeUpdate) == 0)
                {
                    ScriptManager.getInstance().removeScript(scriptFileBeforeUpdate);
                }
                ScriptManager.getInstance().addScript(script.scriptFile);
            }
            ScriptScheduler.getInstance().unscheduleScript(script.name)
            if (script.type == CmdbScript.SCHEDULED && script.enabled) {
                if (script.scheduleType == CmdbScript.CRON) {
                    ScriptScheduler.getInstance().scheduleScript(script.name, script.startDelay, script.cronExpression)
                }
                else {
                    ScriptScheduler.getInstance().scheduleScript(script.name, script.startDelay, script.period)
                }
            }
        }
        if (!fromController && script.hasErrors()) {
            throw new Exception(script.messageService.getMessage(script.errors.allErrors[0]))
        }
        return script;
    }

    static def updateScript(Map params) throws Exception {
        CmdbScript script = CmdbScript.findByName(params.name)
        if (script) {
            updateScript(script, params, false);
        }
        else {
            throw new Exception("Script with name ${params.name} does not exist")
        }
    }

    static def runScript(String scriptName, Map params) throws Exception {
        CmdbScript script = CmdbScript.findByName(scriptName)
        if (script) {
            runScript(script, params);
        }
        else {
            throw new Exception("Script with name ${scriptName} does not exist")
        }
    }

    static def runScript(String scriptName) throws Exception {
        runScript(scriptName, [:])
    }

    static def runScript(CmdbScript script, Map params) throws Exception {
        return ScriptManager.getInstance().runScript(script.scriptFile, params);
    }

    static def startListening(scriptName) throws Exception{
        def script = CmdbScript.get(name:scriptName);
        if(script){
             startListening(script);
        }
        else{
            throw new Exception("Script ${scriptName} does not exist")
        }
    }
    static def startListening(CmdbScript script) throws Exception{
         if(script.listeningDatasource){
             ListeningAdapterManager.getInstance().startAdapter(script.listeningDatasource);
             script.listeningDatasource.update(isSubscribed:true);
         }
         else{
             throw new Exception("No listening datasource defined");
         }
    }

     static def stopListening(scriptName) throws Exception{
        def script = CmdbScript.get(name:scriptName);
        if(script){
             stopListening(script);
        }
        else{
            throw new Exception("Script ${scriptName} does not exist")
        }
    }
    static def stopListening(CmdbScript script) throws Exception{
         if(script.listeningDatasource){
             ListeningAdapterManager.getInstance().stopAdapter(script.listeningDatasource);
             script.listeningDatasource.update(isSubscribed:false);
         }
         else{
             throw new Exception("No listening datasource defined");
         }
    }
}
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
import com.ifountain.rcmdb.scripting.ScriptManager
import com.ifountain.rcmdb.scripting.ScriptScheduler
import com.ifountain.rcmdb.scripting.ScriptingException
import org.quartz.CronTrigger
import datasource.BaseListeningDatasource
import com.ifountain.rcmdb.datasource.ListeningAdapterManager
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.log4j.DailyRollingFileAppender;
import com.ifountain.comp.utils.LoggerUtils;
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 11, 2008
 * Time: 4:28:54 PM
 * To change this template use File | Settings | File Templates.
 */
class CmdbScriptOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{
    static Logger logger = Logger.getLogger(CmdbScriptOperations.class)
    /*
    def beforeDelete(){
        if(this.type == LISTENING ){
            if(listeningDatasource != null){
                try
                {
                    ListeningAdapterManager.getInstance().removeAdapter(listeningDatasource);
                }catch(Exception e)
                {
                    logger.info ("Exception occurred while removing adapter for ${listeningDatasource.name} datasource", e);
                }
            }

        }
    }

   def beforeUpdate(){
       def scriptInCompass = CmdbScript.get(this.id);
        if(scriptInCompass.type == LISTENING ){
            if(scriptInCompass.listeningDatasource != null ){
                try
                {
                    ListeningAdapterManager.getInstance().stopAdapter(scriptInCompass.listeningDatasource);
                }catch(Exception e)
                {
                    logger.info ("Exception occurred while stopping adapter for ${listeningDatasource.name} datasource", e);
                }
            }
        }
    }

    def afterInsert(){
       if(this.type == LISTENING ){
            if(listeningDatasource != null){
                try
                {
                    ListeningAdapterManager.getInstance().addAdapterIfNotExists (listeningDatasource);
                }catch(Exception e)
                {
                    logger.info ("Exception occurred while adding adapter for ${listeningDatasource.name} datasource", e);
                }
            }

        }
    }
     */

    def reload() throws ScriptingException
    {
        ScriptManager.getInstance().reloadScript(scriptFile);
    }
    static def scheduleScript(CmdbScript script)     {
        if (script.type == CmdbScript.SCHEDULED && script.enabled) {
            if (script.scheduleType == CmdbScript.CRON) {
                ScriptScheduler.getInstance().scheduleScript(script.name, script.startDelay, script.cronExpression)
            }
            else {
                ScriptScheduler.getInstance().scheduleScript(script.name, script.startDelay, script.period)
            }
        }
    }
    static def addScript(Map params, boolean fromController) throws Exception {
        if(!params.get("scriptFile") || params.get("scriptFile").trim() == "")
        {
            params["scriptFile"] = params.name;
        }
        if(!params.get("logFile") || params.get("logFile").trim() == "")
        {
            params["logFile"] = params.name;
        }
        def script = CmdbScript.add(params)
        if (!script.hasErrors()) {
            ScriptManager.getInstance().addScript(script.scriptFile);
            scheduleScript(script);
            configureScriptLogger(script);
        }
        if (!fromController && script.hasErrors()) {
            throw  createScriptException(script);
        }
        return script;
    }
    static def addScript(Map params) throws Exception {
        return addScript(params, false);
    }
    protected static Exception createScriptException(CmdbScript script){
        return new Exception(script.messageService.getMessage(script.errors.allErrors[0]))
    }
    static def deleteScript(CmdbScript script) throws Exception {
        def scriptName = script.name;
        if(script.listeningDatasource){
            try
            {
                stopListening(script);
            }
            catch(Exception e)
            {
                logger.info ("Exception occurred while stopListening for ${script.name} script", e);
            }
        }
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
        def scriptNameBeforeUpdate = script.name;


        script.update(params);

        if (!script.hasErrors()) {

            if(scriptFileBeforeUpdate != script.scriptFile )
            {
                if(CmdbScript.countHits("scriptFile:"+scriptFileBeforeUpdate) == 0)
                {
                    ScriptManager.getInstance().removeScript(scriptFileBeforeUpdate);
                }
                ScriptManager.getInstance().addScript(script.scriptFile);
            }
            ScriptScheduler.getInstance().unscheduleScript(scriptNameBeforeUpdate)
            scheduleScript(script)
            configureScriptLogger(script);
        }
        if (!fromController && script.hasErrors()) {
            throw  createScriptException(script);
        }
        return script;
    }

    static def updateScript(Map params) throws Exception {
        CmdbScript script = CmdbScript.get(name:params.name)
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
            return runScript(script, params);
        }
        else {
            throw new Exception("Script with name ${scriptName} does not exist")
        }
    }

    static def runScript(String scriptName) throws Exception {
        return runScript(scriptName, [:])
    }

    static def runScript(CmdbScript script, Map params) throws Exception {
        params=getScriptObjectParams(script,params);
        return ScriptManager.getInstance().runScript(script.scriptFile, params,getScriptLogger(script),script.operationClass);
    }
    static def getScriptObject(CmdbScript script, Map params)
    {
        params=getScriptObjectParams(script,params);
        return ScriptManager.getInstance().getScriptObject(script.scriptFile, params,getScriptLogger(script),script.operationClass)
    }
    private static def getScriptObjectParams(CmdbScript script,Map params)
    {
        params.staticParam=script.staticParam;
        params.staticParamMap=CmdbScript.getStaticParamMap(script);
        return params;
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
            script.listeningDatasource.startListening();
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
             script.listeningDatasource.stopListening();
         }
         else{
             throw new Exception("No listening datasource defined");
         }
    }

    static def configureScriptLogger(CmdbScript script)
    {
        def logger=getScriptLogger(script);
        LoggerUtils.configureLogger(logger,Level.toLevel(script.logLevel),script.logFile,script.logFileOwn);

    }
    static def stopScriptLogger(CmdbScript script)
    {
        //getScriptLogger(script).removeAllAppenders();
    }
    static def getScriptLogger(CmdbScript script)
    {
        return Logger.getLogger("scripting.${script.type}.${script.logFile}");
    }
    static def getStaticParamMap(CmdbScript script)
    {
         def map=[:];
         if(script.staticParam)
         {
             def regex="([^,]+):([^,]+)";
             def matcher = ( script.staticParam =~ regex );
             while (matcher.find()) {
                map.put(matcher.group(1),matcher.group(2));
            }
         }
         return map;
    }
}
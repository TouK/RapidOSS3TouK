package script;
import com.ifountain.rcmdb.exception.scripting.ScriptingException
import com.ifountain.rcmdb.scripting.ScriptManager
import org.quartz.CronExpression
import com.ifountain.rcmdb.scripting.ScriptScheduler

class CmdbScript {
    def messageService;
    static transients = ["messageService"];
    public static String CRON = "Cron";
    public static String PERIODIC = "Periodic";
    String name;
    boolean scheduled = false;
    boolean enabled = false;
    long startDelay = 0;
    String scheduleType = PERIODIC;
    String cronExpression = "* * * * * ?";
    long period = 1;
    static constraints = {
        name(blank: false, unique: true, validator: {val, obj ->
            try
            {
                ScriptManager.getInstance().checkScript(obj.name);
            }
            catch (Throwable t)
            {
                return ['script.compilation.error', t.toString()];
            }
        });

        scheduleType(inList: [PERIODIC, CRON])
        cronExpression(validator: {val, obj ->
            try {
                new CronExpression(val);
            }
            catch (Throwable t) {
                return ['script.cron.doesnt.match', val, t.getMessage()];
            }
        })
    }

    def reload() throws ScriptingException
    {
        ScriptManager.getInstance().reloadScript(name);
    }

    String toString()
    {
        return "$name";
    }

    static def addScript(Map params, boolean fromController) throws Exception {
        def script = new CmdbScript(params)
        if (script.validate() && !script.hasErrors()) {
            ScriptManager.getInstance().addScript(script.name);
            if (script.scheduled && script.enabled) {
                try {
                    if (script.scheduleType == CmdbScript.CRON) {
                        ScriptScheduler.getInstance().scheduleScript(script.name, script.startDelay, script.cronExpression)
                    }
                    else {
                        ScriptScheduler.getInstance().scheduleScript(script.name, script.startDelay, script.period)
                    }
                }
                catch (e) {
                    throw new Exception(script.messageService.getMessage("script.cannot.schedule", [script.name, e.getMessage()], Locale.ENGLISH));
                }
            }
            script.save();
        }
        if (!fromController && script.hasErrors()) {
            throw new Exception(script.messageService.getMessage(script.errors.allErrors[0], Locale.ENGLISH))
        }
        return script;
    }
    static def addScript(Map params) throws Exception {
        return addScript(params, false);
    }

    static def deleteScript(CmdbScript script) throws Exception {
        def scriptName = script.name;
        script.delete()
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
        script.properties = params
        if (!script.hasErrors() && script.validate()) {
            ScriptScheduler.getInstance().unscheduleScript(script.name)
            if (script.scheduled && script.enabled) {
                try {
                    if (script.scheduleType == CmdbScript.CRON) {
                        ScriptScheduler.getInstance().scheduleScript(script.name, script.startDelay, script.cronExpression)
                    }
                    else {
                        ScriptScheduler.getInstance().scheduleScript(script.name, script.startDelay, script.period)
                    }
                }
                catch (e) {
                    throw new Exception(script.messageService.getMessage("script.cannot.schedule", [script.name, e.getMessage()], Locale.ENGLISH));
                }
            }
            script.save();
        }
        if (!fromController && script.hasErrors()) {
            throw new Exception(script.messageService.getMessage(script.errors.allErrors[0], Locale.ENGLISH))
        }
        return script;
    }
    
    static def updateScript(Map params) throws Exception {
        CmdbScript script = CmdbScript.findByName(params.name)
        if (script) {
            updateScript(script, params, false);
        }
        else {
            throw new Exception("Script with name ${scriptName} does not exist")
        }
    }

    static def runScript(String scriptName, Map params) throws Exception{
        CmdbScript script = CmdbScript.findByName(scriptName)
        if (script) {
            runScript(script, params);
        }
        else {
            throw new Exception("Script with name ${scriptName} does not exist")
        }
    }

    static def runScript(String scriptName) throws Exception{
         runScript(scriptName, [:])
    }

    static def runScript(CmdbScript script, Map params) throws Exception{
         def bindings = ["params": params]
         return ScriptManager.getInstance().runScript(script.name, bindings);   
    }
}

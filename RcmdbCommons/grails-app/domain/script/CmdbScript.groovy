package script

import com.ifountain.rcmdb.scripting.ScriptManager
import com.ifountain.rcmdb.scripting.ScriptScheduler
import com.ifountain.rcmdb.scripting.ScriptingException
import org.quartz.CronTrigger
import datasource.BaseListeningDatasource
import com.ifountain.rcmdb.datasource.ListeningAdapterManager

class CmdbScript {
    def messageService;

    public static String CRON = "Cron";
    public static String PERIODIC = "Periodic";
    public static String ONDEMAND = "OnDemand";
    public static String SCHEDULED = "Scheduled";
    public static String LISTENING = "Listening";
    static searchable = {
        except = [];
    };
    static datasources = ["RCMDB": ["master": true, "keys": ["name": ["nameInDs": "name"]]]]
    Long startDelay = 0;
    String name = "";
    String type = ONDEMAND;
    boolean enabled = false;
    String scheduleType = PERIODIC;
    String cronExpression = "* * * * * ?";
    Long period = 1;
    BaseListeningDatasource listeningDatasource;
    static mappedBy = ["listeningDatasource":"listeningScript"]
    static belongsTo = []
    static transients = ["messageService"];

    static constraints = {
        name(blank: false, key: [], validator: {val, obj ->
            try
            {
                ScriptManager.getInstance().checkScript(obj.name);
            }
            catch (Throwable t)
            {
                return ['script.compilation.error', t.toString()];
            }
        });
        type(inList:[ONDEMAND, SCHEDULED, LISTENING]);
        scheduleType(inList: [PERIODIC, CRON])
        listeningDatasource(nullable:true)
        cronExpression(validator: {val, obj ->
            try {
                def trigger = new CronTrigger(obj.name, null, val);
                trigger.getFireTimeAfter(new Date());
            }
            catch (Throwable t) {
                return ['script.cron.doesnt.match', t.toString()];
            }
        })
    }

    def beforeDelete = {
        if(this.type == LISTENING ){
            def scriptInCompass = CmdbScript.get(this.id);
            if(scriptInCompass.listeningDatasource){
                ListeningAdapterManager.getInstance().stopAdapter(scriptInCompass.listeningDatasource);    
            }

        }
    }

     def beforeUpdate = {
        if(this.type == LISTENING ){
            def scriptInCompass = CmdbScript.get(this.id);
            if(scriptInCompass.listeningDatasource){
                ListeningAdapterManager.getInstance().stopAdapter(scriptInCompass.listeningDatasource);
            }
        }
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
        def script = CmdbScript.add(params)
        if (!script.hasErrors()) {
            ScriptManager.getInstance().addScript(script.name);
            if (script.type == SCHEDULED && script.enabled) {
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
        script.update(params);
        if (!script.hasErrors()) {
            ScriptScheduler.getInstance().unscheduleScript(script.name)
            if (script.type == SCHEDULED && script.enabled) {
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
        return ScriptManager.getInstance().runScript(script.name, params);
    }
}

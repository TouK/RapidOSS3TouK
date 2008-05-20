package script

import com.ifountain.rcmdb.scripting.ScriptManager;

class ScriptController {
    public static final String SUCCESSFULLY_CREATED =  "Script created";
    public static final String SCRIPT_DOESNOT_EXIST =  "Script does not exist";
    def scaffold = CmdbScript;
    def save = {
        println "1"
        def script = new CmdbScript(params)
        println "2"
        if(script.save() && !script.hasErrors()) {

            ScriptManager.getInstance().addScript(script.name);
            println "4"
            flash.message = SUCCESSFULLY_CREATED
            println "5"
            redirect(action:show, controller:'script', id:script.id)
            println "6"
        }
        else {
            println script.errors;
            println script.properties;
            println "7"
            render(view:'create', controller:'script', model:[cmdbScript:script])
        }
    }

    def reload = {
        def script = CmdbScript.findByName(params.id);
        if(script)
        {
            try
            {
                script.reload();
                flash.message = "Script reloaded successfully.";
                redirect(action:show,controller:'script', id:script.id);
            }
            catch(t)
            {
                flash.message = "Exception occurred while reloading. Reason : ${t.getMessage()}";
                redirect(action:show,controller:'script', id:script.id);
            }

        }
        else
        {
            flash.message = SCRIPT_DOESNOT_EXIST
            redirect(action:list,controller:'script');
        }
    }
    def run =
    {
        def script = CmdbScript.findByName(params.id);
        if(script)
        {
            def bindings = ["params":params]
            try
            {
                def current = System.currentTimeMillis();
                def result = ScriptManager.getInstance().runScript(script.name,  bindings);
                println "Elapsed: " + (System.currentTimeMillis() - current);
                render(text:String.valueOf(result),contentType:"text/html",encoding:"UTF-8");
            }
            catch(t)
            {
                log.warn("", org.codehaus.groovy.runtime.StackTraceUtils.deepSanitize(t));
                render(text:t.toString(),contentType:"text/html",encoding:"UTF-8");
            }

        }
        else
        {
            flash.message = SCRIPT_DOESNOT_EXIST
            redirect(action:list,controller:'script');
        }
    }
}

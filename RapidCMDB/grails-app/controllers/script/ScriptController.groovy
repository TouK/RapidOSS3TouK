package script;

class ScriptController {
    def scaffold = CmdbScript;
    def scriptingService;
    def save = {
        def script = new CmdbScript(params)
        if(script.save() && !script.hasErrors()) {
            scriptingService.addScript(script.name);
            flash.message = "Script ${script.name} created"
            redirect(action:show,id:script.id)
        }
        else {
            render(view:'create',model:[cmdbScript:script])
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
            flash.message = "Script doesnot exist"
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
                def result = scriptingService.runScript(script.name,  bindings);
                render(text:result,contentType:"text/html",encoding:"UTF-8");
            }
            catch(t)
            {
                render(text:t.getMessage(),contentType:"text/html",encoding:"UTF-8");
            }

        }
        else
        {
            return render(text:"Script doesnot exist",contentType:"text/html",encoding:"UTF-8");
        }
    }
}

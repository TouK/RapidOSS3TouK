class ScriptController {
    def scaffold = CmdbScript;

    def run =
    {
        def script = CmdbScript.findByName(params.id);
        if(script)
        {
            Class scriptClass = null;
            try
            {
                scriptClass = grailsApplication.classLoader.loadClass(script.name);
            }
            catch(java.lang.ClassNotFoundException exception)
            {
                render(text:"Script file doesnot exist",contentType:"text/html",encoding:"UTF-8");
                return;
            }
            if(scriptClass != null)
            {
                def scriptObject = scriptClass.newInstance();
                scriptObject.setProperty("params", params)
                try
                {
                    def res = String.valueOf(scriptObject.run())
                    render(text:res, contentType:"text/html",encoding:"UTF-8");
                }catch(Throwable exception)
                {
                    render(text:"Exception occurred while executing script. Reason :$exception",contentType:"text/html",encoding:"UTF-8");
                }
            }
            else
            {
                render(text:"Script file doesnot exist",contentType:"text/html",encoding:"UTF-8");
            }

        }
        else
        {
            return render(text:"Script doesnot exist",contentType:"text/html",encoding:"UTF-8");
        }
    }
}

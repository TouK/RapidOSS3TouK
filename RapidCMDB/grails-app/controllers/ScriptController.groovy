class ScriptController {
    def scaffold = CmdbScript;

    def runScript =
    {
        def script = CmdbScript.findByName(params.id);
        if(script)
        {
            def scriptClass = null;
            try
            {
                scriptClass = grailsApplication.classLoader.loadClass(script.name);
                println getClass().getClassLoader().getParent();
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
                }catch(Throwable t)
                {
                    render(text:"Exception occurred while executing script. Reason :$t.getMessage()",contentType:"text/html",encoding:"UTF-8");
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

package script;
import com.ifountain.rcmdb.exception.scripting.ScriptingException
class CmdbScript {
    String name;
    def scriptingService;
    static transients = ["scriptingService"];
    static constraints = {
        name(blank:false, unique:true, validator:{val, obj ->
            try
            {
                obj.scriptingService.checkScript(obj.name);
            }
            catch(Throwable t)
            {
                return ['script.compilation.error', t.toString()];
            }
        });
    }

    def reload() throws ScriptingException
    {
        scriptingService.reloadScript(name);
    }

    String toString()
    {
        return "$name";    
    }
}

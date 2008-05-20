package script;
import com.ifountain.rcmdb.exception.scripting.ScriptingException
import com.ifountain.rcmdb.scripting.ScriptManager

class CmdbScript {
    String name;
    static constraints = {
        name(blank:false, unique:true, validator:{val, obj ->
            try
            {
                ScriptManager.getInstance().checkScript(obj.name);
            }
            catch(Throwable t)
            {
                return ['script.compilation.error', t.toString()];
            }
        });
    }

    def reload() throws ScriptingException
    {
        ScriptManager.getInstance().reloadScript(name);
    }

    String toString()
    {
        return "$name";    
    }
}

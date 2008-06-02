package script;
import com.ifountain.rcmdb.exception.scripting.ScriptingException
import com.ifountain.rcmdb.scripting.ScriptManager
import org.quartz.CronExpression

class CmdbScript {
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

        scheduleType(inList:[PERIODIC, CRON])
        cronExpression(validator:{val, obj ->
            try{
                new CronExpression(val);
            }
            catch(Throwable t){
               return ['script.cron.doesnt.match',val, t.getMessage()]; 
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
}

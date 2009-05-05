public class StatisticsOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{
    static String GLOBAL_ENABLE_KEY="RIStatistics.enable";
    
    static def record(String param, value) {
        if(isEnabledGlobally())
        {
            if (recordStats(param)) {//checks whether the param should be recorded at this time
                def t = Date.now()
                def user = getCurrentUserName() // get the name of the user
                Statistics.add([timestamp: t, user: user, parameter: param, value: value])
            }
        }
    }

    static def recordStats(String param) {
        def p = InstrumentationParameters.get(name: param)
        if (p) {
            if (p.enabled) return true
        }
        return false
    }

    static def enableGlobally()
    {
        System.setProperty (GLOBAL_ENABLE_KEY,"true");
    }
    static def disableGlobally()
    {
        System.setProperty (GLOBAL_ENABLE_KEY,"false");
    }

    static boolean isEnabledGlobally()
    {
       return System.getProperty(GLOBAL_ENABLE_KEY) == "true";
    }
    
}

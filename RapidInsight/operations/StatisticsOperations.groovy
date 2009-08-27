public class StatisticsOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{
    static String GLOBAL_ENABLE_KEY="RIStatistics.enable";
    
    static def record(String param, value, String description = "") {
        if (recordStats(param)) {//checks whether the param should be recorded at this time
            def t = Date.now()
            def user = auth.RsUser.getCurrentUserName() // get the name of the user
            Statistics.add([timestamp: t, user: user, parameter: param, value: value, description:description])
        }
    }

    static boolean recordStats(String param) {
        if(isEnabledGlobally())
        {
            return InstrumentationParameters.countHits("name:${param.exactQuery()} AND enabled:true")>0;
        }
        return false;
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

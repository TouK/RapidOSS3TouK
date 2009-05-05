public class StatisticsOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{
    long timerDuration = 0;
    long startingTime = -1;

    static def record(String param, value) {
        if (recordStats(param)) {//checks whether the param should be recorded at this time
            def t = Date.now()
            def user = getCurrentUserName() // get the name of the user            
            Statistics.add([timestamp: t, user: user, parameter: param, value: value])
        }
    }

    static def recordStats(String param) {
        def p = InstrumentationParameters.get(name: param)
        if (p) {
            if (p.enabled) return true
        }
        return false
    }
}

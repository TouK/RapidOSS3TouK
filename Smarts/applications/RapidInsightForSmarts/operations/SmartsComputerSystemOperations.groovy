import com.ifountain.rcmdb.util.RCMDBDataStore

class SmartsComputerSystemOperations extends RsComputerSystemOperations
{
    def calculateStateInformation()
    {
        def propSummary = SmartsNotification.propertySummary("instanceName:\"${name}\"", "severity");
        def minValue = 5;
        propSummary.severity.each {propValue, numberOfObjects ->
            if (propValue >= 0 && minValue > propValue)
            {
                minValue = propValue;
            }
        }
        RCMDBDataStore.get(STATEINFORMATION_KEY)[name] = minValue;
        return minValue;
    }
}
    
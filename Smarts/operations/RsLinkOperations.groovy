import com.ifountain.rcmdb.util.RCMDBDataStore
class RsLinkOperations extends RsSmartsObjectOperations
{
    public static STATEINFORMATION_KEY = "stateInformationforlink"
    int getState()
    {
        def stateInformation = getStateInformation();
        if(stateInformation == null)
        {
            stateInformation = calculateStateInformation();
        }
        return stateInformation;
    }

    def calculateStateInformation()
    {
        def propSummary = RsSmartsNotification.propertySummary("instanceName:\"${name}\"", "severity");
        def minValue = 5;
        propSummary.severity.each{propValue, numberOfObjects->
            if(propValue >= 0 && minValue > propValue)
            {
                minValue = propValue;
            }
        }
        RCMDBDataStore.get (STATEINFORMATION_KEY)[name] = minValue;
        return minValue;
    }

    def getStateInformation()
    {
        def stateInformation = RCMDBDataStore.get(STATEINFORMATION_KEY)
        if(stateInformation == null)
        {
            stateInformation = [:]
            RCMDBDataStore.put (STATEINFORMATION_KEY, stateInformation);
        }
        return stateInformation[name];
    }

    int setState(state)
    {
        def stateInformation = getStateInformation();
        if(stateInformation == null || state < stateInformation)
        {
            stateInformation = calculateStateInformation();
        }
        return stateInformation;
    }
}
    

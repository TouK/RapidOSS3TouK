
    
public class RsCustomerOperations extends RsGroupOperations
{
	 def calculateState(currentState, oldPropagatedState, newPropagatedState)
	    {
	        return findMaxSeverity(currentState,  oldPropagatedState, newPropagatedState);
//    	    	return criticalPercent(currentState,  oldPropagatedState, newPropagatedState);
	    }
}

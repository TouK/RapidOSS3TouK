/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Apr 14, 2009
 * Time: 3:41:16 PM
 * To change this template use File | Settings | File Templates.
 */
class StateCalculator {
    static String calculateMethod="findMaxSeverity";

    static setToDefault()
    {
        calculateMethod="findMaxSeverity";
    }

    static def eventIsAdded(event){
        def objects=getObjectsOfEvent(event);
        objects.each{ object ->
            setObjectState(object,event.severity,Constants.NOTSET);
        }

    }
    static def eventIsUpdated(event,changedProps){
        if(changedProps.containsKey("severity"))
        {
            def objects=getObjectsOfEvent(event);
            objects.each{ object ->
                    setObjectState(object,event.severity,changedProps.severity);
            }
        }
    }
    static def eventIsDeleted(event){
        def objects=getObjectsOfEvent(event);
        objects.each{ object ->
            setObjectState(object,Constants.NORMAL,event.severity);
        }
    }

    static def objectIsAdded(object){


    }
    static def objectIsUpdated(object,changedProps){

    }
    static def objectIsDeleted(object){
        def stateObject = RsObjectState.get(objectId:object.id);
        if (stateObject != null)
        {
            stateObject.remove();
        }
    }

    static def getObjectsOfEvent(event)
    {
        def objects=[];

        def object=RsTopologyObject.get(name:event.elementName);
        if(object)
        {
            objects.add(object);
        }
        return objects;
    }
    static def setObjectState(object,newPropagatedState, oldPropagatedState)
    {
        def currentState = loadObjectState(object);
        def calculatedState = calculateObjectState(object,currentState, oldPropagatedState, newPropagatedState);
        if (calculatedState != currentState)
        {
            saveObjectState(object,calculatedState);
            propagateObjectState(object,currentState, calculatedState);
        }
        return calculatedState;
    }

    static int getObjectState(object)
    {
        def currentState = loadObjectState(object);
        if (currentState == Constants.NOTSET)
        {
            currentState = calculateObjectState(object,currentState, Constants.NOTSET, Constants.NOTSET);
            saveObjectState(object,currentState);
        }
        return currentState;
    }

    static def saveObjectState(object,currentState) {
        RsObjectState.add(objectId: object.id, state: currentState);
    }
    //should only return the value of state
    static def loadObjectState(object) {
        def state = RsObjectState.get(objectId: object.id)?.state;
        if (state == null)
            return Constants.NOTSET
        return state
    }

    static def propagateObjectState(object,oldState, newState)
    {
        object.parentObjects.each {
            setObjectState(it,newState, oldState);
        }
    }

    static def calculateObjectState(object,currentState, oldPropagatedState, newPropagatedState)
    {           
        return "${calculateMethod}"(object,currentState, oldPropagatedState, newPropagatedState)
    }
    static int findMaxSeverity(object,currentState, oldPropagatedState, newPropagatedState)
    {
       if(object instanceof RsGroup)
       {
           return findMaxSeverityForRsGroup(object,currentState, oldPropagatedState, newPropagatedState);
       }
       else
       {
           return findMaxSeverityForRsTopologyObject(object,currentState, oldPropagatedState, newPropagatedState);
       }
    }
    static int findMaxSeverityForRsTopologyObject(object,currentState, oldPropagatedState, newPropagatedState)
    {
        if (needToCalculate(currentState, oldPropagatedState, newPropagatedState))
        {
            def maxValue = Constants.NORMAL;
            def severityResults = RsEvent.getPropertyValues("elementName:${object.name.exactQuery()}", ["severity"], [sort: "severity", order: "desc", max: 1]).severity;
            if (severityResults.size() > 0)
            {
                maxValue = severityResults[0];
            }
            return maxValue;
        }
        return currentState;
    }
     static int findMaxSeverityForRsGroup(object,currentState,  oldPropagatedState, newPropagatedState)
    {
    	if (needToCalculate(currentState,  oldPropagatedState, newPropagatedState))
    	{
            def maxValue = Constants.NORMAL;
            object.childObjects.each {
                def childState = getObjectState(it);
                if (childState >= 0 && maxValue < childState)
                {
                    maxValue = childState;
                }
            }
            return maxValue;
        }
        return currentState;
    }

    static int criticalPercent(object,currentState, oldPropagatedState, newPropagatedState)
    {
       if(object instanceof RsGroup)
       {
           return criticalPercentForRsGroup(object,currentState, oldPropagatedState, newPropagatedState);
       }
       else
       {
           return criticalPercentForRsTopologyObject(object,currentState, oldPropagatedState, newPropagatedState);
       }
    }

    static int criticalPercentForRsTopologyObject(object,currentState, oldPropagatedState, newPropagatedState)
    {
        if (needToCalculate(currentState, oldPropagatedState, newPropagatedState))
        {
            currentState = Constants.NORMAL
            def totalEventCount = RsEvent.countHits("elementName:${object.name.exactQuery()}");
            if (totalEventCount == 0)
                return currentState

            def criticalEventCount = RsEvent.countHits("elementName:${object.name.exactQuery()} AND severity:${Constants.CRITICAL}");
            def percent = (criticalEventCount / totalEventCount) * 100
            switch (percent) {
                case {it > Constants.CRITICAL_PERCENTAGE}: currentState = Constants.CRITICAL; break
                case {it > Constants.MAJOR_PERCENTAGE}: currentState = Constants.MAJOR; break
                default: currentState = Constants.NORMAL
            }
        }
        return currentState;
    }

    static int criticalPercentForRsGroup(object,currentState,  oldPropagatedState, newPropagatedState)
	{
		if (needToCalculate(currentState,  oldPropagatedState, newPropagatedState))
		{
			def stateList = [];
			object.childObjects.each{child->
				stateList.add(getObjectState(child))
			}
			def percent = (stateList.findAll{it == Constants.CRITICAL}.size()/stateList.size())*100
			switch(percent) {
				case {it > Constants.CRITICAL_PERCENTAGE}: currentState = Constants.CRITICAL;break
				case {it >Constants.MAJOR_PERCENTAGE}: currentState = Constants.MAJOR;break
				default: currentState = Constants.NORMAL
			}
		}
		return currentState;
	}

    static boolean needToCalculate(currentState, oldPropagatedState, newPropagatedState)
    {
        // check if state calculation is triggered because of first getState call.
        def condition1 = newPropagatedState == Constants.NOTSET && oldPropagatedState == Constants.NOTSET
        // more severe event is received
        def condition2 = newPropagatedState > currentState
        // this event might have determined the current state (was the event with the max severity)
        def condition3 = currentState == oldPropagatedState && newPropagatedState < currentState
        
        if (condition1 || condition2 || condition3)
            return true;
        else
            return false;
    }
}
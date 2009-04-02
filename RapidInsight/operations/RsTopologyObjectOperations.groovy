/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111NOTSET307
* USA.
*/
public class RsTopologyObjectOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {

    int getState()
    {
        def currentState = currentState();
        if (currentState == Constants.NOTSET)
        {
            currentState = calculateState(currentState, Constants.NOTSET, Constants.NOTSET);
            saveState(currentState);
        }
        return currentState;
    }

    def currentState()
    {
        return loadState();
    }

    int setState(newPropagatedState, oldPropagatedState = Constants.NOTSET)
    {
        def currentState = currentState();
        def calculatedState = calculateState(currentState, oldPropagatedState, newPropagatedState);
        if (calculatedState != currentState)
        {
            saveState(calculatedState);
            propagateState(currentState, calculatedState);
        }
        return calculatedState;
    }

    /*=====================================================================================================================================
    - The way to save the data of state may change for child domains,
    - its enough for a domain to implement save & load state  to have its own way of saving state
    - Note that child classes must not have a property state , they should use other name , stateValue or savedState etc
    - If a child have property state , calling getState over child does not call the getState function here.
    =====================================================================================================================================*/
    def saveState(currentState) {
        RsObjectState.add(objectId: id, state: currentState);
    }
    //should only return the value of state
    def loadState() {
        def state = RsObjectState.get(objectId: id)?.state;
        if (state == null)
            return Constants.NOTSET
        return state
    }
    def afterDelete()
    {
        def stateObject = RsObjectState.get(objectId: id);
        if (stateObject != null)
        {
            stateObject.remove();
        }
    }

    /*=====================================================================================================================================
    - It is expected that users will change only calculateState, propagateState methods according to their needs.
    - In current implementation default state calculation strategy is specified as finding max severity of events.
    - calculateState will be called for each setState call. oldPropagatedState and newPropagatedState refers the states of triggering objects.
    - oldPropagatedState and newPropagatedState will be passed as NOTSET if state calculation is triggered because of first getState call.
    =====================================================================================================================================*/
    def calculateState(currentState, oldPropagatedState, newPropagatedState)
    {
        return findMaxSeverity(currentState, oldPropagatedState, newPropagatedState);
//        return criticalPercent(currentState,  oldPropagatedState, newPropagatedState);
    }

    def propagateState(oldState, newState)
    {
        parentObjects.each {
            it.setState(newState, oldState);
        }
    }

    public int findMaxSeverity(currentState, oldPropagatedState, newPropagatedState)
    {
        if (needToCalculate(currentState, oldPropagatedState, newPropagatedState))
        {
            def maxValue = Constants.NORMAL;
            def severityResults = RsEvent.getPropertyValues("elementName:${name.exactQuery()}", ["severity"], [sort: "severity", order: "desc", max: 1]).severity;
            if (severityResults.size() > 0)
            {
                maxValue = severityResults[0];
            }
            return maxValue;
        }
        return currentState;
    }
    public int criticalPercent(currentState, oldPropagatedState, newPropagatedState)
    {
        if (needToCalculate(currentState, oldPropagatedState, newPropagatedState))
        {
            currentState = Constants.NORMAL
            def totalEventCount = RsEvent.countHits("elementName:${name.exactQuery()}");
            if (totalEventCount == 0)
                return currentState

            def criticalEventCount = RsEvent.countHits("elementName:${name.exactQuery()} AND severity:${Constants.CRITICAL}");
            def percent = (criticalEventCount / totalEventCount) * 100
            switch (percent) {
                case {it > Constants.CRITICAL_PERCENTAGE}: currentState = Constants.CRITICAL; break
                case {it > Constants.MAJOR_PERCENTAGE}: currentState = Constants.MAJOR; break
                default: currentState = Constants.NORMAL
            }
        }
        return currentState;
    }

    def needToCalculate(currentState, oldPropagatedState, newPropagatedState)
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

    public int calculateWeight() {
        int w = 1

        parentObjects.each {
            w = w + it.calculateWeight()
        }
        return w
    }
    
	def getInMaintenance() {
		def o = RsInMaintenance.get(objectName:name)
		if (o?.active) 
		  return true 
		else 
		  return false
	}

	def putInMaintenance() {
		RsInMaintenance.add(objectName:name, active:true)
		eventsInMaintenance(true)
	}

	def putInMaintenance(endDate) {
		RsInMaintenance.add(objectName:name,ending:endDate, active:true)
		eventsInMaintenance(true)
	}

	def putInMaintenance(startDate, endDate) {
		RsInMaintenance.add(objectName:name,starting:startDate, ending:endDate)
	}

	def takeOutOfMaintenance() {
		def maintObj = RsInMaintenance.get(objectName:name)
		maintObj?.remove()
		eventsInMaintenance(false)
	}

	def eventsInMaintenance(boolean maint) {
		def events = RsEvent.search("elementName:${name}")
		events.results.each{
			if (it.inMaintenance != maint)
				it.inMaintenance = maint	
		}
	}     
}
    

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
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
public class RsTopologyObjectOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {
    //the way to save the data of state may change for child domains,
    //its enough for a domain to implement save & load state  to have its own way of saving state
    //Note that child classes must not have a property state , they should use other name , stateValue or savedState etc
    //If a child have property state , calling getState over child does not call the getState function here.
    def saveState(currentState){
        RsObjectState.add(objectId:id, state:currentState);
    }
    //should only return the value of state
    def loadState(){
        return  RsObjectState.get(objectId:id)?.state;
    }

    int getState()
    {
        def currentState = currentState();
        if(currentState == null)
        {
            currentState = calculateState(currentState, -1, -1);
            saveState(currentState);
        }
        return currentState;
    }

    def currentState()
    {
        return loadState();
    }
    
    int setState(newPropagatedState, oldPropagatedState = -1)
    {
        def currentState = getState();
        def calculatedState = calculateState(currentState, oldPropagatedState, newPropagatedState);
        if(calculatedState != currentState)
        {
            saveState(calculatedState);
            propagateState(currentState, calculatedState);
        }
        return calculatedState;
    }


    /*=====================================================================================================================================
    - It is expected that users will change only calculateState, propagateState methods according to their needs.
    - In current implementation default state calculation strategy is specified as finding max severity of events.
    - calculateState will be called for each setState call. oldPropagatedState and newPropagatedState refers the states of triggering objects.
    - oldPropagatedState and newPropagatedState will be passed as -1 if state calculation is triggered because of first getState call.
    =====================================================================================================================================*/
    def calculateState(currentState, oldPropagatedState, newPropagatedState)
    {
        return findMaxSeverity(currentState,  oldPropagatedState, newPropagatedState);
    }

    def propagateState(oldState, newState)
    {
        parentObjects.each{
            it.setState(newState, oldState);    
        }
    }

    public int findMaxSeverity(currentState,  oldPropagatedState, newPropagatedState)
    {
        if(newPropagatedState == -1 && oldPropagatedState == -1 || newPropagatedState > currentState
                || currentState == oldPropagatedState && newPropagatedState < currentState)
        {
            def propSummary = RsEvent.propertySummary("elementName:\"${name}\"", "severity");
            def maxValue = 0;
            propSummary.severity.each{propValue, numberOfObjects->
                if(propValue >= 0 && maxValue < propValue)
                {
                    maxValue = propValue;
                }
            }
            return maxValue;
        }
        return currentState;
    }
}
    

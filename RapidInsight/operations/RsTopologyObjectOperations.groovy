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
    int getState()
    {
        def currentState = currentState();
        if(currentState == null)
        {
            currentState = calculateState(currentState, -1);
            RsObjectState.add(objectId:id, state:currentState);
        }
        return currentState;
    }

    def currentState()
    {
        def stateObj = RsObjectState.get(objectId:id);
        return stateObj?stateObj.state:5;
    }

    int setState(newState)
    {
        def currentState = getState();
        def calculatedState = calculateState(currentState, newState);
        if(calculatedState != currentState)
        {
            RsObjectState.add(objectId:id, state:calculatedState);
            propagateState(calculatedState);
        }
        return calculatedState;
    }


    //=====================================================================================================================================
    def calculateState(currentState, newState)
    {
        return findMaxSeverity(currentState, newState);
    }

    def propagateState(newStateInformation)
    {
        parentObjects.each{
            it.setState(newStateInformation);    
        }
    }

    public int findMaxSeverity(currentState, newState)
    {
        if(newState != -1 && newState <= currentState) return currentState;
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
}
    

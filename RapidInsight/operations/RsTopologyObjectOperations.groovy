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
        def stateInformation = stateInformation();
        if(stateInformation == null)
        {
            stateInformation = calculateStateInformation();
            RsObjectState.add(objectId:id, state:stateInformation);
        }
        return stateInformation;
    }

    def stateInformation()
    {
        def stateObj = RsObjectState.get(objectId:id);
        return stateObj?stateObj.state:null;
    }

    int setState(state)
    {
        def stateInformation = getState();
        if(willRecalculateState(stateInformation, state))
        {
            stateInformation = calculateStateInformation();
            RsObjectState.add(objectId:id, state:stateInformation);
        }
        return stateInformation;
    }


    def calculateStateInformation()
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

    protected boolean willRecalculateState(oldState, newState)
    {
        return newState > oldState;
    }
}
    

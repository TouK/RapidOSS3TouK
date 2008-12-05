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
import com.ifountain.rcmdb.util.RCMDBDataStore
class RsLinkOperations extends RsTopologyObjectOperations
{
    public static STATEINFORMATION_KEY = "stateInformationforlink"
    int getState()
    {
        def stateInformation = stateInformation();
        if(stateInformation == null)
        {
            stateInformation = calculateStateInformation();
        }
        return stateInformation;
    }

    def calculateStateInformation()
    {
        def propSummary = RsRiEvent.propertySummary("node:\"${name}\"", "severity");
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

    def stateInformation()
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
        def stateInformation = stateInformation();
        if(stateInformation == null || state < stateInformation)
        {
            stateInformation = calculateStateInformation();
        }
        return stateInformation;
    }
}
    

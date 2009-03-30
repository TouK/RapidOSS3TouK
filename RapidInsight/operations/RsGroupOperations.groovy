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
public class RsGroupOperations extends RsTopologyObjectOperations {
    
  def calculateState(currentState, oldPropagatedState, newPropagatedState)
    {
        return findMaxSeverity(currentState,  oldPropagatedState, newPropagatedState);
//	    	return criticalPercent(currentState,  oldPropagatedState, newPropagatedState);
    }
  
	public int findMaxSeverity(currentState,  oldPropagatedState, newPropagatedState)
    {
    	if (needToCalculate(currentState,  oldPropagatedState, newPropagatedState))
    	{
            def maxValue = Constants.INDETERMINATE;
            childObjects.each {
                def childState = it.getState();
                if (childState >= 0 && maxValue < childState)
                {
                    maxValue = childState;
                }
            }
            return maxValue;
        }
        return currentState;
    }
    
    public int criticalPercent(currentState,  oldPropagatedState, newPropagatedState)
	{
		if (needToCalculate(currentState,  oldPropagatedState, newPropagatedState))
		{
			def stateList = []; 
			childObjects.each{child->
				stateList.add(child.currentState())
			}
			def percent = (stateList.findAll{it == Constants.CRITICAL}.size()/stateList.size())*100
			switch(percent) {
				case {it > Constants.CRITICAL_PERCENTAGE}: currentState = Constants.CRITICAL;break
				case {it >Constants.MAJOR_PERCENTAGE}: currentState = Constants.MAJOR;break
				default: currentState = Constants.INDETERMINATE
			}
		}
		return currentState;
	}
}
    

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be 
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
/**
 * Created on Feb 20, 2008
 *
 * Author Sezgin
 */
package com.ifountain.smarts.test.mocks;

import com.ifountain.core.datasource.Action;
import com.ifountain.smarts.datasource.BaseSmartsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaseSmartsAdapterMock extends BaseSmartsAdapter {

    public List<Action> executedActions = new ArrayList<Action>();
    public BaseSmartsAdapterMock(String datasourceName, long reconnectInterval) {
        super(datasourceName, reconnectInterval, null);
    }

    @Override
    public void executeAction(Action action) throws Exception {
        executedActions.add(action);
    }

	@Override
	public Map<String, Object> getObject(Map<String, String> ids, List<String> fieldsToBeRetrieved) throws Exception
	{
		return null;
	}
}

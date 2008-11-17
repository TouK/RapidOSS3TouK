/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
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

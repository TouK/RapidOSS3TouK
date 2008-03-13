/*
 * Created on Jan 21, 2008
 *
 */
package com.ifountain.core.datasource.mocks;

import java.util.List;
import java.util.Map;

import com.ifountain.core.datasource.BaseAdapter;

public class MockBaseAdapterImpl extends BaseAdapter
{
    public MockBaseAdapterImpl(String connConfigName, int reconnectInterval)
    {
        super(connConfigName, reconnectInterval, null);
    }

    public MockBaseAdapterImpl(String connConfigName)
    {
        this(connConfigName, 0);
    }
    
    public Map<String, Object> getObject(Map<String, String> ids, List<String> fieldsToBeRetrieved) throws Exception{
    	return null;
    }
}
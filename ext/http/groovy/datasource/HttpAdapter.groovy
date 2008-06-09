package datasource

import com.ifountain.core.datasource.BaseAdapter
import org.apache.log4j.Logger;

public class HttpAdapter extends BaseAdapter{
    
	public HttpAdapter() {
		super();
    }
	
    public HttpAdapter(connectionName, logger){
    	super(connectionName, 0, logger);
    }
    
    public HttpAdapter(connectionName, reconnectInterval, logger){
    	super(connectionName, reconnectInterval, logger);
    }    
    
	public static getInstance(){
        def adapter = new HttpAdapter();
        adapter.setLogger(Logger.getRootLogger());
        return adapter;
	}
	public static getInstance(connectionName){
	    return new HttpAdapter(connectionName, Logger.getRootLogger());
	}   

    public String doRequest(String url, Map params, int type) throws Exception{
        DoRequestAction action = new DoRequestAction(logger, url, params, type);
        executeAction(action);
        return action.getResponse();
    }
    
    public String doRequest(String url, Map params) throws Exception{
        return doGetRequest(url, params);
    }
    
    public String doGetRequest(String url, Map params) throws Exception{
        return doRequest(url, params, DoRequestAction.GET);
    }
    
    public String doPostRequest(String url, Map params) throws Exception{
        return doRequest(url, params, DoRequestAction.POST);
    }
	
    public Map<String, Object> getObject(Map<String, String> ids, List<String> fieldsToBeRetrieved) {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }
}

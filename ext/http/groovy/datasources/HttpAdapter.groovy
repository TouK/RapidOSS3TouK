package datasources;

import org.apache.log4j.Logger;
import com.ifountain.core.datasource.BaseAdapter;
import java.util.List;
import java.util.Map;
import api.RS;

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
	    return new HttpAdapter();
	    setLogger(RS.getSession().logger);
	}
	public static getInstance(connectionName){
	    return new HttpAdapter(connectionName, RS.getSession().logger);
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

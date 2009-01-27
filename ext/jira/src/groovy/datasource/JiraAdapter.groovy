package datasource

import com.ifountain.core.datasource.BaseAdapter
import org.apache.log4j.Logger


public class JiraAdapter extends BaseAdapter{

    public JiraAdapter(connectionName, reconnectInterval, logger){
        super(connectionName, reconnectInterval, logger);
    }
    
    public Map<String, Object> getObject(Map<String, String> ids, List<String> fieldsToBeRetrieved) {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    public openIssue(Map params) throws Exception{
        OpenIssueAction action = new OpenIssueAction(logger,params);
        executeAction(action);
        return action.getIssue();
    }
     
	public void closeIssue(String issueId, String resolution) throws Exception{
	    CloseIssueAction action = new CloseIssueAction(logger,issueId, resolution);
	    executeAction(action);        
	} 
	
	public retrieveDetails(String issueId) throws Exception{
	    RetrieveDetailsAction action = new RetrieveDetailsAction(logger,issueId);
	    executeAction(action);        
	    return action.getIssue();
	} 
	
	public getProp(String issueId, List props){
		RetrievePropsAction action = new RetrievePropsAction(logger,issueId);
	    executeAction(action);        
	    return action.getProps();
	}
}
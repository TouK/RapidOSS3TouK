package datasource

import com.ifountain.core.datasource.BaseAdapter
import org.apache.log4j.Logger


public class JiraAdapter extends BaseAdapter{

    public JiraAdapter(connectionName, reconnectInterval, logger){
        super(connectionName, reconnectInterval, logger);
    }
    
    public Map<String, Object> getObject(Map<String, String> ids, List<String> fieldsToBeRetrieved) {
    	RetrievePropsAction action = new RetrievePropsAction(logger,ids.values().toArray()[0],fieldsToBeRetrieved);
	    executeAction(action);        
	    return action.getProps();
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
	
	public getProps(String issueId, List props){
		RetrievePropsAction action = new RetrievePropsAction(logger,issueId,props);
	    executeAction(action);        
	    return action.getProps();
	}
	
	public resolveIssue(String issueId, String resolution){
		ResolveIssueAction action = new ResolveIssueAction(logger,issueId, resolution);
	    executeAction(action);        
	}
	
	public updateIssue(String issueId, Map props){
		UpdateIssueAction action = new UpdateIssueAction(logger,issueId, props);
	    executeAction(action);        
	}
	
	public addCommentToIssue(String issueId, String comment){
		AddCommentAction action = new AddCommentAction(logger,issueId, comment);
	    executeAction(action);        
	}
	
	public getComments(String issueId){
		GetCommentsAction action = new GetCommentsAction(logger,issueId);
	    executeAction(action);        
	    return action.getComments();
	}
	
	public reopenIssue(String issueId){
		ReopenIssueAction action = new ReopenIssueAction(logger,issueId);
	    executeAction(action);        
	}
}
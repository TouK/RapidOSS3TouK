package datasource

import com.ifountain.core.connection.IConnection
import com.ifountain.core.datasource.Action
import org.apache.log4j.Logger;
import connection.JiraConnectionImpl;

import org.ifountain.www.jira.rpc.soap.jirasoapservice_v2.*;
import com.atlassian.jira.rpc.soap.beans.*;

public class RetrieveDetailsAction implements Action {
	private Logger logger;
    private issueId;
    private detailMap = [:];
    
    public CloseIssueAction(Logger logger, String issueId, String resolution) {
        this.logger = logger;
        this.issueId = issueId;
    }

    public void execute(IConnection conn) throws Exception {
    	String token = ((JiraConnectionImpl)conn).getToken();
    	JiraSoapService jiraSoapService = ((JiraConnectionImpl)conn).getJiraSoapService();
		RemoteIssue issue = jiraSoapService.getIssue(token, issueId);
		
	    detailMap.name = issue["key"];//.getKey();
	    detailMap.assignee = issue["assignee"];//.getAssignee();
	    detailMap.created = issue["create"];//.getCreated();
	    detailMap.description = issue["description"];//.getDescription();
	    detailMap.dueDate = issue["duedate"];//.getDuedate();
	    detailMap.priority = issue["priority"];//.getPriority();
	    detailMap.project = issue["project"];//.getProject();
	    detailMap.reporter = issue["reporter"];//.getReporter();
//	     detailMap.resolution = issue.getResolution();
	    detailMap.status = issue["status"];//.getStatus();
	    detailMap.summary = issue["summary"];//.getSummary();
	    detailMap.type = issue["type"];//.getType();
	    detailMap.updated = issue["updated"];//.getUpdated();
	}
    public Map getIssue() {
        return detailMap;
    }
}
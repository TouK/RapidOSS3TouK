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
		
	    detailMap.name = issue.getKey();
	    detailMap.assignee = issue.getAssignee();
	    detailMap.created = issue.getCreated();
	    detailMap.description = issue.getDescription();
	    detailMap.dueDate = issue.getDuedate();
	    detailMap.priority = issue.getPriority();
	    detailMap.project = issue.getProject();
	    detailMap.reporter = issue.getReporter();
//	     detailMap.resolution = issue.getResolution();
	    detailMap.status = issue.getStatus();
	    detailMap.summary = issue.getSummary();
	    detailMap.type = issue.getType();
	    detailMap.updated = issue.getUpdated();
	}
    public Map getIssue() {
        return detailMap;
    }
}
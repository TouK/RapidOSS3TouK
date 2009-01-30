package datasource

import com.ifountain.core.connection.IConnection
import com.ifountain.core.datasource.Action
import org.apache.log4j.Logger;
import connection.JiraConnectionImpl;

import org.ifountain.www.jira.rpc.soap.jirasoapservice_v2.*;
import com.atlassian.jira.rpc.soap.beans.*;

public class ReopenIssueAction implements Action {

    private Logger logger;
    private issueId;
    
    public ReopenIssueAction(Logger logger, String issueId) {
        this.logger = logger;
        this.issueId = issueId;
    }

    public void execute(IConnection conn) throws Exception {
    	String token = ((JiraConnectionImpl)conn).getToken();
    	JiraSoapService jiraSoapService = ((JiraConnectionImpl)conn).getJiraSoapService(); 
    	
    	String[] value = new String[1];
    	value[0] = "4";
    	RemoteFieldValue[] actionParams = new RemoteFieldValue[1];
    	RemoteFieldValue jirastatus = new RemoteFieldValue("status", value);
		actionParams[0] = jirastatus;
		RemoteIssue remoteIssue = jiraSoapService.progressWorkflowAction(token, issueId, "3", actionParams); 
    }
}

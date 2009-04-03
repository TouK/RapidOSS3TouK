package datasource

import com.ifountain.core.connection.IConnection
import com.ifountain.core.datasource.Action
import org.apache.log4j.Logger;
import connection.JiraConnectionImpl;

import org.ifountain.www.jira.rpc.soap.jirasoapservice_v2.*;
import com.atlassian.jira.rpc.soap.beans.*;

public class ResolveIssueAction implements Action {

    private Logger logger;
    private issueId;
    private resolution;
    
    public ResolveIssueAction(Logger logger, String issueId, String resolution) {
        this.logger = logger;
        this.issueId = issueId;
        this.resolution = resolution;
    }

    public void execute(IConnection conn) throws Exception {
    	def jiraConn = (JiraConnectionImpl)conn;
    	jiraConn.connect();
    	String token = jiraConn.getToken();
    	JiraSoapService jiraSoapService = jiraConn.getJiraSoapService(); 
    	
    	String[] value = new String[1];
		value[0] = resolution;
		RemoteFieldValue res = new RemoteFieldValue("resolution", value);
		RemoteFieldValue[] actionParams = new RemoteFieldValue[1];
		actionParams[0] = res;
		RemoteIssue remoteIssue = jiraSoapService.progressWorkflowAction(token, issueId, "5", actionParams); 
    }
}

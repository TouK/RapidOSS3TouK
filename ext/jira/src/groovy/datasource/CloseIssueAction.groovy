package datasource

import com.ifountain.core.connection.IConnection
import com.ifountain.core.datasource.Action
import org.apache.log4j.Logger;
import connection.JiraConnectionImpl;

import org.ifountain.www.jira.rpc.soap.jirasoapservice_v2.*;
import com.atlassian.jira.rpc.soap.beans.*;

public class CloseIssueAction implements Action {

    private Logger logger;
    private issueId;
    private resolution;
    
    public CloseIssueAction(Logger logger, String issueId, String resolution) {
        this.logger = logger;
        this.issueId = issueId;
        this.resolution = resolution;
    }

    public void execute(IConnection conn) throws Exception {
    	String token = ((JiraConnectionImpl)conn).getToken();
    	JiraSoapService jiraSoapService = ((JiraConnectionImpl)conn).getJiraSoapService(); 
    	
    	String[] value = new String[1];
		value[0] = resolution;
		RemoteFieldValue res = new RemoteFieldValue("resolution", value);
		RemoteFieldValue[] actionParams = new RemoteFieldValue[1];
		actionParams[0] = res;
		RemoteIssue remoteIssue = jiraSoapService.progressWorkflowAction(token, issueId, "5", actionParams);
		
		value[0] = "6";
		RemoteFieldValue jirastatus = new RemoteFieldValue("status", value);
		actionParams[0] = jirastatus;
		remoteIssue = jiraSoapService.progressWorkflowAction(token, issueId, "701", actionParams); 
    }
}

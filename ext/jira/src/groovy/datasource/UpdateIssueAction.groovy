package datasource

import com.ifountain.core.connection.IConnection
import com.ifountain.core.datasource.Action
import org.apache.log4j.Logger;
import connection.JiraConnectionImpl;

import org.ifountain.www.jira.rpc.soap.jirasoapservice_v2.*;
import com.atlassian.jira.rpc.soap.beans.*;

public class UpdateIssueAction implements Action {
	private Logger logger;
    private issueId;
    private params = [:];
    
    public UpdateIssueAction(Logger logger, String issueId, Map params) {
        this.logger = logger;
        this.issueId = issueId;
        this.params.putAll(params);
    }

    public void execute(IConnection conn) throws Exception {
    	String token = ((JiraConnectionImpl)conn).getToken();
    	JiraSoapService jiraSoapService = ((JiraConnectionImpl)conn).getJiraSoapService();
		RemoteIssue issue = jiraSoapService.getIssue(token, issueId);
		
		params.each{key,value->
			issue[key] = value;
		}
	}
}

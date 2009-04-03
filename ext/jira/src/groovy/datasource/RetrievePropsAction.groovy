package datasource

import com.ifountain.core.connection.IConnection
import com.ifountain.core.datasource.Action
import org.apache.log4j.Logger;
import connection.JiraConnectionImpl;

import org.ifountain.www.jira.rpc.soap.jirasoapservice_v2.*;
import com.atlassian.jira.rpc.soap.beans.*;

public class RetrievePropsAction implements Action {
	private Logger logger;
    private issueId;
    private props = []
    private propsMap = [:];
    
    public RetrievePropsAction(Logger logger, String issueId, List props) {
        this.logger = logger;
        this.issueId = issueId;
        this.props = props;
    }

    public void execute(IConnection conn) throws Exception {
    	def jiraConn = (JiraConnectionImpl)conn;
    	jiraConn.connect();
    	String token = jiraConn.getToken();
    	JiraSoapService jiraSoapService = jiraConn.getJiraSoapService();
    	
		RemoteIssue issue = jiraSoapService.getIssue(token, issueId);
		
		props.each{
			props.put(it,issue[it])
		}
	}
    public Map getProps() {
        return propsMap;
    }
}
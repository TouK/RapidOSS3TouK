package datasource

import com.ifountain.core.connection.IConnection
import com.ifountain.core.datasource.Action
import org.apache.log4j.Logger;
import connection.JiraConnectionImpl;

import org.ifountain.www.jira.rpc.soap.jirasoapservice_v2.*;
import com.atlassian.jira.rpc.soap.beans.*;

public class GetCommentsAction implements Action {
	private Logger logger;
    private issueId;
    private comments = [];
    
    public GetCommentsAction(Logger logger, String issueId) {
        this.logger = logger;
        this.issueId = issueId;
    }

    public void execute(IConnection conn) throws Exception {
    	String token = ((JiraConnectionImpl)conn).getToken();
    	JiraSoapService jiraSoapService = ((JiraConnectionImpl)conn).getJiraSoapService();
		def remoteComments = jiraSoapService.getComments(token, issueId);
		remoteComments.each{
			comments.add(it.getBody());
		}
	}
    
    public getComments(){
    	return comments;
    }
}


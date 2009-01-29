package datasource

import com.ifountain.core.connection.IConnection
import com.ifountain.core.datasource.Action
import org.apache.log4j.Logger;
import connection.JiraConnectionImpl;

import org.ifountain.www.jira.rpc.soap.jirasoapservice_v2.*;
import com.atlassian.jira.rpc.soap.beans.*;

public class AddCommentAction implements Action {
	private Logger logger;
    private issueId;
    private comment;
    
    public AddCommentAction(Logger logger, String issueId, String comment) {
        this.logger = logger;
        this.issueId = issueId;
        this.comment = comment;
    }

    public void execute(IConnection conn) throws Exception {
    	String token = ((JiraConnectionImpl)conn).getToken();
    	JiraSoapService jiraSoapService = ((JiraConnectionImpl)conn).getJiraSoapService();
    	RemoteComment remoteComment = new RemoteComment();
    	remoteComment.setBody(comment);
    	jiraSoapService.addComment(token, issueId, remoteComment);
	}
}


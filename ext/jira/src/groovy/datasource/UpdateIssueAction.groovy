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
    private project;
    
    public UpdateIssueAction(Logger logger, String issueId, Map params) {
        this.logger = logger;
        this.issueId = issueId;
        this.project = params.project;
        params.remove("project");
        this.params.putAll(params);
        
    }

    public void execute(IConnection conn) throws Exception {
    	String token = ((JiraConnectionImpl)conn).getToken();
    	JiraSoapService jiraSoapService = ((JiraConnectionImpl)conn).getJiraSoapService();
    	def componentsFromJira = jiraSoapService.getComponents(token,project);
    	
    	def componentMap = [:];
    	componentsFromJira.each{
    		componentMap.put(it.getName(), it.getId())
    	}
    	def versionsFromJira = jiraSoapService.getVersions(token,project);
    	def versionMap = [:];
    	versionsFromJira.each{
    		versionMap.put(it.getName(), it.getId())
    	}
    	RemoteFieldValue[] remoteFields = new RemoteFieldValue[params.size()];	
    	def cntr = 0;
    	params.each{key,value->
    		if (key == "component"){
    			if (componentMap.containsKey(value)){
    				key = "components";
    				value = componentMap[value];
    			}
    			else{
    				throw new Exception("There is no such component: $value!")
    			}
    		}
    		else {
    			if (key == "affectsVersion" || key == "fixVersion"){
    				if (versionMap.containsKey(value)){
    					key = key+"s";
    					value = versionMap[value];
    					println "key:$key value:$value"
    				}
    				else{
    					throw new Exception("There is no such version: $value!")
    				}
    			}
    		}

    		def propToBeUpdated = new RemoteFieldValue(key, value);
    		remoteFields[cntr] = propToBeUpdated;
    		cntr++;
    	}
    	jiraSoapService.updateIssue(token, issueId,remoteFields);
	}
}

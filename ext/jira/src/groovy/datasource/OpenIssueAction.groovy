package datasource

import com.ifountain.core.connection.IConnection
import com.ifountain.core.datasource.Action
import org.apache.log4j.Logger;

import org.ifountain.www.jira.rpc.soap.jirasoapservice_v2.*;
import com.atlassian.jira.rpc.soap.beans.*;
import connection.JiraConnectionImpl;

public class OpenIssueAction implements Action {

    private Logger logger;
    private issuePropsMap = [:];
    private RemoteIssue issue;
    private localIssue = [:];
    
    public OpenIssueAction(Logger logger, Map issuePropsMap) {
        this.logger = logger;
        this.issuePropsMap.putAll(issuePropsMap);
    }

    private checkAndReturnIssueProp(Map issuePropsMap, String propKey) throws Exception{
    	if (issuePropsMap.containsKey(propKey)){
    		def value = issuePropsMap.get(propKey);
    		return value;
    	}
    	else{
    		throw new Exception("$propKey is required to create an issue!")
    	}
    }
    
    public void execute(IConnection conn) throws Exception {
    	String token = ((JiraConnectionImpl)conn).getToken();
    	JiraSoapService jiraSoapService = ((JiraConnectionImpl)conn).getJiraSoapService(); 
    	
    	RemoteIssue issue = new RemoteIssue();
    	localIssue.project = checkAndReturnIssueProp(issuePropsMap, "project"); 
    	issue.setProject(localIssue.project);

    	localIssue.type = checkAndReturnIssueProp(issuePropsMap, "type"); 
    	issue.setType(localIssue.type);
    	
    	localIssue.summary = checkAndReturnIssueProp(issuePropsMap, "summary");
    	issue.setSummary(localIssue.summary)
    	
    	if (issuePropsMap.containsKey("priority")){
    		localIssue.priority = issuePropsMap.priority;
    		issue.setPriority(issuePropsMap.priority);
    	}

    	if (issuePropsMap.containsKey("reporter")){
    		localIssue.reporter = issuePropsMap.reporter;
    		issue.setReporter(issuePropsMap.reporter);
    	}
    	else{
    		localIssue.reporter = ((JiraConnectionImpl)conn).getUsername();
    	}
    	if (issuePropsMap.containsKey("assignee")){
    		localIssue.assignee = issuePropsMap.assignee;
    		issue.setAssignee(issuePropsMap.assignee);
    	}
    	if (issuePropsMap.containsKey("description")){
    		localIssue.description = issuePropsMap.description;
    		issue.setDescription(issuePropsMap.description);
    	}
    	
    	if (issuePropsMap.containsKey("affectsVersions")){
    		def versions = issuePropsMap.affectedVersions;
    		def versionsFromJira = jiraSoapService.getVersions(token,project);
    		RemoteVersion[] remoteVersions = new RemoteVersion[versions.size()];
    		def cntr = 0; 
    		versionsFromJira.each{
    			def version = it.getName();
    			if (versions.contains(version)){
    				remoteVersions[cntr] = it;
    				localIssue.affectedVersions(version);
    				cntr++;
    				versions.remove(version);
    			}
    		}
    		if (versions.size()>0){
    			throw new Exception("Non-existent affectsVersion: ${versions.toString()}!")
    		}
    		issue.setAffectsVersions(remoteVersions);
    	}
    	
    	if (issuePropsMap.containsKey("fixVersions")){
    		def versions = issuePropsMap.fixVersions;
    		def versionsFromJira = jiraSoapService.getVersions(token,project);
    		RemoteVersion[] remoteVersions = new RemoteVersion[versions.size()];
    		def cntr = 0; 
    		versionsFromJira.each{
    			def version = it.getName();
    			if (versions.contains(version)){
    				remoteVersions[cntr] = it;
    				localIssue.fixVersions.add(version);
    				cntr++;
    				versions.remove(version);
    			}
    		}
    		if (versions.size()>0){
    			throw new Exception("Non-existent fixVersion: ${versions.toString()}!")
    		}
    		issue.setFixVersions(remoteVersions);
    	}
    	
    	if (issuePropsMap.containsKey("components")){
    		def components = issuePropsMap.components;
    		def componentsFromJira = jiraSoapService.getComponents(token,project);
    		RemoteComponent[] remoteComponents = new RemoteComponent[components.size()];
    		def cntr = 0; 
    		componentsFromJira.each{
    			def component = it.getName();
    			if (components.contains(component)){
    				remoteComponents[cntr] = it;
    				localIssue.components.add(component);
    				cntr++;
    				components.remove(version);
    			}
    		}
    		if (components.size()>0){
    			throw new Exception("Non-existent component: ${components.toString()}!")
    		}
    		issue.setComponents(remoteComponents);
    	}
    	if (issuePropsMap.containsKey("duedate")){
    		localIssue.duedate = issuePropsMap.duedate;
    		issue.setDueDate(issuePropsMap.duedate);
    	}
    	if (issuePropsMap.containsKey("updated")){
    		localIssue.updated = issuePropsMap.updated;
    		issue.setUpdated(issuePropsMap.updated);
    	}
//    	 Run the create issue in Jira
		RemoteIssue returnedIssue = jiraSoapService.createIssue(token, issue);
		final String issueKey = returnedIssue["key"];
		localIssue.status = returnedIssue["status"];  // 1: open
		localIssue.name = issueKey;
        logger.debug("Opened issue");
    }
    
    public Map getIssue() {
        return localIssue;
    }

}

package datasource

import datasource.JiraAdapter
import org.apache.log4j.Logger

class JiraDatasourceOperations extends BaseDatasourceOperations {
    JiraAdapter adapter;
    def onLoad() {
       def ownConnection=getProperty("connection")
       if(ownConnection != null)
       {
            this.adapter = new JiraAdapter(ownConnection.name, reconnectInterval * 1000, getLogger());
       }
    }

    def getAdapters()
    {
        return [adapter];
    }

    def getProperty(Map keys, String propName){
        def props = adapter.getObject(keys, [propName]);
        return convert(props[propName]);
    }

    def getProperties(Map keys, List properties){
       def props = adapter.getObject(keys, properties);
       return convert(props);
    }
    
    def openIssue(Map params) {
    	def issue = this.adapter.openIssue(params)
        return issue;
    }

    def retrieveDetails(String issueId) {
    	def issue = this.adapter.retrieveDetails(issueId)
    	return issue;
    }
    
    def resolveIssue(String issueId, String resolution) {
    	def issue = this.adapter.resolveIssue(issueId, resolution)
    }
    
    def closeIssue(String issueId) {
    	def issue = this.adapter.closeIssue(issueId)
    }
    
    def reopenIssue(String issueId) {
    	def issue = this.adapter.reopenIssue(issueId)
    }
    
    def updateIssue(String issueId, Map params) {
    	def issue = this.adapter.updateIssue(issueId, params)
    }

    def addComment(String issueId, String comment) {
    	def issue = this.adapter.addCommentToIssue(issueId, comment)
    }
    
    def getComments(String issueId) {
    	return this.adapter.getComments(issueId)
    }
}

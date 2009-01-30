package datasource

import datasource.JiraAdapter
import org.apache.log4j.Logger

class JiraDatasourceOperations extends BaseDatasourceOperations {
    def adapter;
    def onLoad() {
        this.adapter = new JiraAdapter(getProperty("connection").name, reconnectInterval * 1000, Logger.getRootLogger());
    }

    def getProperty(Map keys, String propName){
        def props = adapter.getProps(keys.name, [propName]);
        return convert(props[propName]);
    }

    def getProperties(Map keys, List properties){
       def props = adapter.getProps(keys.name, properties);
       return convert(props);
    }
    
    def openIssue(Map params) {
    	def issue = this.adapter.openIssue(params)
        return issue;
    }

    def closeIssue(String issueId, String resolution) {
    	def issue = this.adapter.closeIssue(issueId, resolution)
    }
    
    def retrieveDetails(String issueId) {
    	def issue = this.adapter.retrieveDetails(issueId)
    	return issue;
    }
    
    def resolveIssue(String issueId, String resolution) {
    	def issue = this.adapter.resolveIssue(issueId, resolution)
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
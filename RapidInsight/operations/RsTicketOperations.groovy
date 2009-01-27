    import java.util.Calendar;
    import java.util.Date;
    import connector.JiraConnector;
    import datasource.JiraDatasource;

    /*
    Status codes
    1: Open
    3: In Progress
    4: Reopened
    5: Resolved
    6: Closed
    Priority codes
    1: Blocker
    2: Critical 
    3: Major
    4: Minor
    5: Trivial
    					Severity From RI: Priority in Jira
    					case '5' : return "Critical"; => 1
                        case '4' : return "Major"; => 2
                        case '3' : return "Minor"; =>3
                        case '2' : return "Warning"; =>4
                        case '1' : return "Indeterminate"; =>5
                        case '0' : return "Clear";
                        
                        
    Issue Type codes
    1: Bug
    8: Comp Time
    4: Improvement
    2: New Feature
    3: Task
    6: Time Off
    7: Vacation
    Resolution codes:
    1: Fixed
    2: Won't Fix
    3: Duplicate
    4: Incomplete
    5: Cannot Reproduce
    Actions
    5: Resolve Issue -> 701: Close Issue
    2: Close Issue

    */		
    public class RsTicketOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {
    	
    	static createTicket(Map ticketProps){ 
    		def connectorName = ticketProps.connectorName;
    		if (connectorName!=null && ticketProps.eventName!=null){
    			def jiraConnector = JiraConnector.get(name:connectorName);
    			if (jiraConnector!=null){
    				def event = RsEvent.get(name:ticketProps.eventName);
    				def jiraDs = jiraConnector.ds;
    			
    				// Run the create issue code in Jira
    				def returnedIssue = jiraDs.openIssue(ticketProps);
    				returnedIssue.rsDatasource = jiraDs.name;
    				def ticket = RsTicket.add(returnedIssue);
    				
    				ticket.addRelation("relatedEvent":event);
    				
    				if (ticketProps.elementName!="" && ticketProps.elementName!=null){
    					def topoObj = RsTopologyObject.get(name:ticketProps.elementName);
    					ticket.addRelation("relatedObjects":topoObj);
    				}
    				println "Successfully created issue: ${ticket.asMap()}"
    				return ticket;
    			}
    		}
    	}

    	public closeTicket(String eventName, String elementName){
    		def jiraDs = JiraDatasource.get(name:rsDatasource);
    		jiraDs.closeIssue(name, "1"); // Default resolution is "Fixed"
    		def event = RsEvent.get(name:eventName);
    		removeRelation("relatedEvent":event);
    		
    		if (elementName!=""){
    			def topoObj = RsTopologyObject.get(name:elementName);
    			removeRelation("relatedObjects":topoObj);
    		}
    		
    		status = "6"; //CLOSED;
    	}
    	
    	public retrieveDetails(){
    		def jiraDs = JiraDatasource.get(name:rsDatasource);
    	    return jiraDs.retrieveDetails(name);
    	}

    }
        

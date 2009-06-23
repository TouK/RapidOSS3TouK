/*
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
	import java.util.Calendar;
    import java.util.Date;
    import connector.JiraConnector;
    import datasource.JiraDatasource;

/*
    Status codes			Priority Codes		Severity From RI
    1: Open					1: Blocker			5: Critical
    3: In Progress			2: Critical 		4: Major
    4: Reopened				3: Major			3: Minor
    5: Resolved				4: Minor			2: Warning
    6: Closed				5: Trivial			1: Indeterminate
    											0: Clear (NOT MAPPED IN JIRA)
    Issue Type codes		Resolution codes	Actions
    1: Bug					1: Fixed			5: Resolve Issue -> 701: Close Issue
    2: New Feature			2: Won't Fix		2: Close Issue
    3: Task					3: Duplicate
    4: Improvement			4: Incomplete
    						5: Cannot Reproduce
*/
class RsTicketOperations  extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {

    	static openTicket(Map ticketProps){
   		// project, type and summary are required to open a ticket.
   		// project is hard coded as DEMO in this method.
   		// summary is formed from the event description
   		// type is assumed to be provided in the ticketProps
    		def severityToPriority = ["5":"1","4":"2","3":"3","2":"4","1":"5"];
    		def connectorName = ticketProps.connectorName;
    		if (connectorName!=null && ticketProps.eventName!=null){
    			def jiraConnector = JiraConnector.get(name:connectorName);
    			if (jiraConnector!=null){
    				def event = RsEvent.get(name:ticketProps.eventName);
    				def jiraDs = jiraConnector.ds;
    				// Run the create issue code in Jira
    				ticketProps.project = "DEMO";
    				ticketProps.summary = event.name;
    				ticketProps.priority = severityToPriority.get(event.severity.toString())
    				def returnedIssue = jiraDs.openIssue(ticketProps);
    				returnedIssue.rsDatasource = jiraDs.name;
    				def ticket = RsTicket.add(returnedIssue);

    				ticket.addRelation("relatedEvents":event);

    				if (ticketProps.elementName!="" && ticketProps.elementName!=null){
    					def topoObj = RsTopologyObject.get(name:ticketProps.elementName);
    					ticket.addRelation("relatedObjects":topoObj);
    				}
    				println "Successfully created issue: ${ticket.asMap()}"
    				return ticket;
    			}
    		}
    	}

    	public resolveTicket(){
    		resolveTicket("1"); // Default resolution is "Fixed"

    		status = "5"; //RESOLVED;
    	}

    	public resolveTicket(resolution){
    		if (status!=5){
	    		def jiraDs = JiraDatasource.get(name:rsDatasource);
	    		jiraDs.resolveIssue(name, resolution);

	    		status = "5"; //RESOLVED;
    		}
    	}

    	public closeTicket(String eventName, String elementName){
    		if (status!=6){
	    		def jiraDs = JiraDatasource.get(name:rsDatasource);
	    		if (status != "5") resolveTicket("1") // Default resolution is "Fixed"
	    		jiraDs.closeIssue(name);
	    		def event = RsEvent.get(name:eventName);
	    		removeRelation("relatedEvents":event);

	    		if (elementName!=""){
	    			def topoObj = RsTopologyObject.get(name:elementName);
	    			removeRelation("relatedObjects":topoObj);
	    		}

	    		status = "6"; //CLOSED;
    		}
    	}

    	public closeTicket(String eventName, String elementName, resolution){
    		if (status!=6){
	    		def jiraDs = JiraDatasource.get(name:rsDatasource);
	    		if (status != "5") resolveTicket(resolution)
	    		jiraDs.closeIssue(name);
	    		def event = RsEvent.get(name:eventName);
	    		removeRelation("relatedEvents":event);

	    		if (elementName!=""){
	    			def topoObj = RsTopologyObject.get(name:elementName);
	    			removeRelation("relatedObjects":topoObj);
	    		}

	    		status = "6"; //CLOSED;
    		}
    	}

    	public updateTicket(Map props){
    		def tempProps = [:];
    		tempProps.putAll(props);
    		tempProps.project = "DEMO"
    		def jiraDs = JiraDatasource.get(name:rsDatasource);

    		jiraDs.updateIssue(name, tempProps);
    		update(props)
    	}

    	public retrieveDetails(){
    		def jiraDs = JiraDatasource.get(name:rsDatasource);
    	    return jiraDs.retrieveDetails(name);
    	}

    	public addToLog(String comment){
    		def jiraDs = JiraDatasource.get(name:rsDatasource);
    	    jiraDs.addComment(name, comment);
    	}

    	public getLogEntries(){
    		def jiraDs = JiraDatasource.get(name:rsDatasource);
    	    return jiraDs.getComments(name);
    	}

    	public reopenTicket(){
    		def jiraDs = JiraDatasource.get(name:rsDatasource);
    	    jiraDs.reopenIssue(name);
    	    status = "4";
    	}
    }


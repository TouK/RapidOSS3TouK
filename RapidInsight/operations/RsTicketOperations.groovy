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

class RsTicketOperations  extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {
//	Creates a ticket both in RI and in its datasource 
	static openTicket(Map ticketProps){ 
   	}
//  Resolves as fixed, but keeps the ticket open	
	public resolveTicket(){
	}
//	Resolves with a given value, but keeps the ticket open
	public resolveTicket(resolution){
	}
//	Closes the ticket as fixed	
	public closeTicket(String eventName, String elementName){
	}
//	Closes the ticket with a given resolution value	
	public closeTicket(String eventName, String elementName, resolution){
	}
//	Updates the ticket both in datasource and RI	
	public updateTicket(Map props){
	}
//	Retrieves the ticket details from its datasource	
	public retrieveDetails(){
	}
//	Adds logs for the ticket in its datasource
	public addToLog(String comment){
	}
//	Gets the logs for the ticket from its datasource	
	public getLogEntries(){
	}
//  Reopens a closed ticket	
	public reopenTicket(){
	}
}
        

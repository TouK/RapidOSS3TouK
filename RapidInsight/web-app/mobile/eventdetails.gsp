<%@ page import="search.SearchQuery; auth.RsUser; java.text.SimpleDateFormat"%>

<%
	def name = params.name
	def event = RsEvent.get(name:name)
	def user = RsUser.findByUsername(session.username);
	
	def link = "eventdetails.gsp?name=${name}"
		
	def errorMessage;
	def errorCaught = false;
	
	def actionSuccessful = false;
	def actionMesssage
	
	if(params.acknowledge != null) 
	{
		if(event) {
			if (params.acknowledge == "true") {
        		event.acknowledge(true, user.username);
        		actionMessage = "RsEvent with name: ${name} is acknowledged" 		
        	}
    		else if (params.acknowledge == "false") {
    			event.acknowledge(false, user.username);
        		actionMessage = "RsEvent with name: ${name} is unacknowledged"
    		}
        	actionSuccessful = true;
        }
        else {
        	errorCaught = true
        	errorMessage = "RsEvent with name: ${name} does not exist";
        }
    }
    
    if(params.ownership != null)
    {
    	if(event) {
			if (params.ownership == "true") {
        		event.setOwnership(true, user.username);
        		actionMessage = "User: ${user.username} has taken the ownership of RsEvent with name: ${name}" 		
        	}
    		else if (params.ownership == "false") {
    			event.setOwnership(false, user.username);
        		actionMessage = "User: ${user.username} has released the ownership of RsEvent with name: ${name}"
    		}
        	actionSuccessful = true;
        }
        else {
        	errorCaught = true
        	errorMessage = "RsEvent with name: ${name} does not exist";
        }
    }
	
%>

<body>

	<%-------------------------------------------------------------------------------
	 								<Event Details>									
	 -------------------------------------------------------------------------------%>
	<div id="eventdetails" title="Details of ${event.name}:Details">
		
		<g:if test="${errorCaught == true}">
			<div id="messageArea" class="error">
				Error has occured <br> ${errorMessage}
			</div>
		</g:if>
		
		<g:if test="${actionSuccessful== true}">
			<div id="messageArea" class="success">
				Action is successful <br> ${actionMessage}
			</div>
		</g:if>
		
			
		<div id="event${event.id}-menu" style="position: static; ">
			<div id="menu${event.id}-header">
				<div id="menu${event.id}-link" class="menu-closed"><a href="home.gsp#_${event.id}" onclick="expand('menu${event.id}-link', 'menu${event.id}-list'); return false;">Event Actions</a></div>
			</div>
			<div id="menu${event.id}-list" style="display: none; ">
				<ul class="items">
					<li><a href="${link}&acknowledge=${!event.acknowledged}" target="_temp"> <g:if test="${event.acknowledged}">Unacknowledge</g:if><g:else>Acknowledge</g:else> </a></li>
					<li><a href="${link}&ownership=true" target="_temp">Take Ownership</a></li>
					<li><a href="${link}&ownership=false" target="_temp">Release Ownership</a></li>
				</ul>
			</div>
		</div>
		
		<table class="itable" width="100%" border="0" cellspacing="0" cellpadding="3">
			<tr class="header">
				<th width="30%">  </th>
				<th> Event </th>
			</tr>
			<tr class="reg">
				<td><b>name</b></td>
				<td>${event.name}</td>
			</tr>
			<tr class="alt">
				<td><b>id</b></td>
				<td>${event.id}</td>
			</tr>
			<tr class="reg">
				<td><b>owner</b></td>
				<td>${event.owner}</td>
			</tr>
			<tr class="alt">
				<td><b>acknowledged</b></td>
				<td>${event.acknowledged}</td>
			</tr>
			<tr class="reg">
				<td><b>inMaintenance</b></td>
				<td>${event.inMaintenance}</td>
			</tr>
			<tr class="alt">
				<td><b>severity</b></td>
				<td>${event.severity}</td>
			</tr>
			<tr class="reg">
				<td><b>source</b></td>
				<td>${event.source}</td>
			</tr>
			
			<%
				def format = new SimpleDateFormat("d MMM HH:mm:ss");
				def created = (event.createdAt == 0)?'never': format.format(new Date(event.createdAt)) 
				def changed = (event.changedAt == 0)?'never': format.format(new Date(event.changedAt))
				def cleared = (event.clearedAt == 0)?'never': format.format(new Date(event.changedAt))
				def expired = (event.willExpireAt == 0)?'never': format.format(new Date(event.willExpireAt))
			%>	
					
			<tr class="alt">
				<td><b>createdAt</b></td>
				<td>${created}</td>
			</tr>
			<tr class="reg">
				<td><b>changedAt</b></td>
				<td>${changed}</td>
			</tr>
			<tr class="alt">
				<td><b>clearedAt</b></td>
				<td>${cleared}</td>
			</tr>
			<tr class="reg">
				<td><b>willExpireAt</b></td>
				<td>${expired}</td>
			</tr>
			<tr class="alt">
				<td><b>rsDatasource</b></td>
				<td>${event.rsDatasource}</td>
			</tr>
			<tr class="reg">
				<td><b>state</b></td>
				<td>${event.state}</td>
			</tr>
			<tr class="alt">
				<td><b>elementName</b></td>
				<td>${event.elementName}</td>
			</tr>
			<tr class="reg">
				<td><b>elementDisplayName</b></td>
				<td>${event.elementDisplayName}</td>
			</tr>
		</table>
	</div>
	<%-------------------------------------------------------------------------------
	 								</Event Details>								
	 -------------------------------------------------------------------------------%>
</body>

<%@ page import="search.SearchQuery; auth.RsUser; java.text.SimpleDateFormat"%>
<%
	def name = params.name
	def event = RsEvent.get(name:name)
	def user = RsUser.findByUsername(session.username);
	
	def link = "eventdetails.gsp?name=${name}"
%>

<body>
	<%-------------------------------------------------------------------------------
	 								<Event Details>									
	 -------------------------------------------------------------------------------%>
	<div id="eventdetails" title="Details of ${name}:Details">
	
		<g:if test="${!event}">
			<div id="messageArea" class="error">
				RsEvent with name: ${name} does not exist
			</div>
		</g:if>
		<g:else>
			<%----------------------------------------------------------------
		 						<Event Action Menu>						
		 	----------------------------------------------------------------%>
			<div id="event${event.id}-menu" style="position: static; ">
				<div id="menu${event.id}-header">
					<div id="menu${event.id}-link" class="menu-closed"><a href="home.gsp#_${event.id}" onclick="expandEventActionMenu('menu${event.id}-link', 'menu${event.id}-list'); return false">Event Actions</a></div>
				</div>
				<div id="menu${event.id}-list" style="display: none; ">
					<ul class="items">
						<li><a href="scriptter.gsp?name=${event.name}&acknowledge=${!event.acknowledged}" target="_open"> <g:if test="${event.acknowledged}">Unacknowledge</g:if><g:else>Acknowledge</g:else> </a></li>
						<li><a href="scriptter.gsp?name=${event.name}&ownership=true" target="_open">Take Ownership</a></li>
						<li><a href="scriptter.gsp?name=${event.name}&ownership=false" target="_open">Release Ownership</a></li>
					</ul>
				</div>
			</div>
			<%----------------------------------------------------------------
		 						</Event Action Menu>						
		 	----------------------------------------------------------------%>
		
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
	</g:else>
	<%-------------------------------------------------------------------------------
	 								</Event Details>								
	 -------------------------------------------------------------------------------%>
</body>
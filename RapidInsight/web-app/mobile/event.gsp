<%@ page import="search.SearchQuery; java.text.SimpleDateFormat"%>

<%
	def eventList
	def queryName
	def extractQuery
	def page = java.lang.Integer.parseInt(params.page)
	def total;
	def totalPage
	def errorMessage
	def errorCaught = false
	
	if(params.sort == '' || params.sort == null) {
		params.sort = 'name'
	}
	if(params.order == '' || params.order == null) {
		params.order = 'asc'
	}
		
	if(params.search == 'null' || params.search == null) {
		def id = params.id
		def query = SearchQuery.searchEvery("( id:${params.id.exactQuery()} )")[0]
		if(query != null)
		{
			extractQuery = query.query
			queryName = query.name
		}
		else
		{
			errorCaught = true;
			errorMessage = "Query with id: ${id} does not exist. It may have been deleted"
		}
	}
	else {
		extractQuery = params.search
		queryName = "Custom Query"
	}
	
	if(!errorCaught)
	{
		try
		{
			searchResults = RsEvent.search( extractQuery, [max:10, offset:10*page, sort:params.sort,order:params.order]);
			eventList = searchResults.results
			total = searchResults.total
			totalPage = (int)((total + 9) / 10)
			if(eventList.size() == 0) {
				if(total > 0) {	
					page = totalPage - 1
					eventList = RsEvent.search( extractQuery, [max:10, offset:10*page, sort:params.sort,order:params.order]).results
				}
				else {
					eventList = []
					page = 0;
					totalPage = 1;
				}
			}
		}
		catch(Exception e) {
			errorMessage = e.getMessage()
			errorCaught = true
		}
	}
	
	if(errorCaught) {
		queryName = "Error";
		eventList = [] 
		total=0 
		page=0 
		totalPage=1 
	}
	
	def i = 0
	def j = 0
	
	def link = "event.gsp?id=${params.id}&search=${params.search}&sort=${params.sort}&order=${params.order}"
	def shortLink = "event.gsp?id=${params.id}&search=${params.search}&page=${params.page}"
%>

<body>


	<%-------------------------------------------------------------------------------
										<Events>									
	 -------------------------------------------------------------------------------%>
	<div id="event" title="${queryName}:Events"selected="true">
	
		<%----------------------------------------------------------------
		 						<Event Pagination>						
		 ----------------------------------------------------------------%>
		<g:if test="${errorCaught == true}">
			<div id="messageArea" class="error">
				Error has occured <br> ${errorMessage}
			</div>
		</g:if>
		
		<div class="paginate">
		
			<g:if test="${page-1>=0}">
				<a href="${link}&page=${page-1}" target="_temp"> < </a>
			</g:if>
			&nbsp &nbsp
			<g:if test="${page-2 >= 0}">
				<g:if test="${page-2 > 0}">
					...
				</g:if>
				<a href="${link}&page=${page-2}" target="_temp"> ${page-1}</a>
			 	&nbsp &nbsp
	    		<a href="${link}&page=${page-1}" target="_temp"> ${page} </a>
	    	</g:if>
			<g:elseif test="${page-1>=0}">
				<a href="${link}&page=${page-1}" target="_temp"> ${page} </a>
			</g:elseif>
			&nbsp &nbsp
			<b> ${page+1} </b> 
			&nbsp &nbsp
			<g:if test="${page+2 < totalPage}">
				<a href="${link}&page=${page+1}" target="_temp"> ${page+2}</a>
			 	&nbsp &nbsp
	    		<a href="${link}&page=${page+2}" target="_temp"> ${page+3} </a>
				<g:if test="${page+2 < totalPage-1}">
					...
				</g:if>
	    	</g:if>
			<g:elseif test="${page+1 < totalPage}">
				<a href="${link}&page=${page+1}" target="_temp"> ${page+2} </a>
			</g:elseif>
			&nbsp &nbsp
			<g:if test="${page+1 < totalPage}">
				<a href="${link}&page=${page+1}" target="_temp"> > </a>
			</g:if>
		</div>
		<%----------------------------------------------------------------
		 						</Pagination>							
		 ----------------------------------------------------------------%>
		
		<%----------------------------------------------------------------
		 						<Events Table>							
		 ----------------------------------------------------------------%>
		<div class = "table">
		<table class="itable" height="100%" width="100%" border="0" cellspacing="0" cellpadding="3">
				<tr class="header">
					<th width="1%"></th>
					<th id="name" onclick="redirectEvent('name', '${shortLink}', '${params.sort}', '${params.order}'); return false">Name</th>
					<th id="acknowledged" onclick="redirectEvent('acknowledged', '${shortLink}', '${params.sort}', '${params.order}'); return false">Ack</th>
					<th id="owner" onclick="redirectEvent('owner', '${shortLink}', '${params.sort}', '${params.order}'); return false">Owner</th>
					<th id="source" onclick="redirectEvent('source', '${shortLink}', '${params.sort}', '${params.order}'); return false">Source</th>
				</tr>

				<g:each var="event" in="${eventList}">
				<tr class="${(i++ % 2) == 0 ? 'reg' : 'alt'}" onclick="showPageByHref('eventdetails.gsp?name=${event.name}')">
					<td><img src="../images/mobile/${event.severity}.png" height="25px" width="19px"/></td>
					<td>${event.name}</td>
					<td>${event.acknowledged}</td>
					<td>${event.owner}</td>
					<td>${event.source}</td>
				</tr>
				</g:each>	
			
			</table>
		</div>
		<%----------------------------------------------------------------
		 						</Events Table>							
		 ----------------------------------------------------------------%>
	</div>
	<%-------------------------------------------------------------------------------
	 								</Events>										
	 -------------------------------------------------------------------------------%>
	
</body>


<%@ page import="search.SearchQuery; search.SearchQueryGroup; auth.RsUser" %>

<%
	def filterType = 'event';
	def user = RsUser.findByUsername(session.username);
	def queryGroups = SearchQueryGroup.searchEvery("( type:${filterType.exactQuery()} OR type:${SearchQueryGroup.DEFAULT_TYPE.exactQuery()} ) AND  ( ( username:${RsUser.RSADMIN.exactQuery()} AND isPublic:true) OR (username:${user.username.exactQuery()}) )");
%>

<body onload="alert('3');" selected="true">

	<%-------------------------------------------------------------------------------
										<Query Page>									
	 -------------------------------------------------------------------------------%>
	<ul id="query" title="Saved Queries:Query">
	    <g:each var="group" in="${queryGroups}">
	    	<li class="group"> ${group.name} </li>
	    	<g:each var="query" in="${group.queries}">
	    		<g:if test="${query.type == filterType || query.type == ''}">
	    			<li><a href="event.gsp?id=${query.id}&page=0" target="_open" > ${query.name}</a></li>
	    		</g:if>
	    	</g:each>
	    </g:each>
	</ul>
	<%-------------------------------------------------------------------------------
										</Query Page>									
	 -------------------------------------------------------------------------------%>
		
</body>

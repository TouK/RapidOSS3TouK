<%@ page import="search.SearchQuery; search.SearchQueryGroup; auth.RsUser" %>
<%
	def filterType = params.filterType;
	def listURI = params.listURI ? params.listURI : "mobile/event.gsp";
	def user = RsUser.findByUsername(session.username);
	def queryGroups = SearchQueryGroup.searchEvery("( type:${filterType.exactQuery()} OR type:${SearchQueryGroup.DEFAULT_TYPE.exactQuery()} ) AND  ( ( username:${RsUser.RSADMIN.exactQuery()} AND isPublic:true) OR (username:${user.username.exactQuery()}) )");
%>

<body>
	<%-------------------------------------------------------------------------------
										<Query Page>									
	 -------------------------------------------------------------------------------%>
	<ul id="query" title="Saved Queries:Query">
	    <g:each var="group" in="${queryGroups}">
	    	<li class="group"> ${group.name} </li>
	    	<g:each var="query" in="${group.queries}">
	    		<g:if test="${query.type == filterType || query.type == ''}">
	    			<li><rui:link url="${listURI}" params="${[query:query.query, searchIn:query.searchClass]}" target="_open"> ${query.name}</rui:link></li>
	    		</g:if>
	    	</g:each>
	    </g:each>
	</ul>
	<%-------------------------------------------------------------------------------
										</Query Page>									
	 -------------------------------------------------------------------------------%>
</body>

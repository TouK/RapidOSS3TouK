<%@ page import="com.ifountain.rcmdb.mobile.MobileUtils; search.SearchQuery; search.SearchQueryGroup; auth.RsUser" %>
<%
    def gspFolder = "simple";
    if(MobileUtils.isIphone(request)){
        gspFolder = "iphone";
    }
    def filterType = params.filterType;
    def listURI = params.listURI ? params.listURI : "mobile/${gspFolder}/event.gsp";
    def username=session.username;
    def queryGroups = SearchQueryGroup.searchEvery("( type:${filterType.exactQuery()} OR type:${SearchQueryGroup.DEFAULT_TYPE.exactQuery()} ) AND  ( ( username:${RsUser.RSADMIN.exactQuery()} AND isPublic:true) OR (username:${username.exactQuery()}) )");
%>
<ul id="query" class="list">
    <g:each var="group" in="${queryGroups}">
        <li class="group">${group.name}</li>
        <g:each var="query" in="${group.queries}">
            <g:if test="${query.type == filterType || query.type == ''}">
                <li><rui:link url="${listURI}" params="${[query:query.query, searchIn:query.searchClass]}">${query.name}</rui:link></li>
            </g:if>
        </g:each>
    </g:each>
</ul>

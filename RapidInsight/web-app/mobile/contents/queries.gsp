<%@ page import="com.ifountain.rcmdb.mobile.MobileUtils; search.SearchQuery; search.SearchQueryGroup; auth.RsUser" %>
<%
    def gspFolder = "simple";
    if (MobileUtils.isIphone(request)) {
        gspFolder = "iphone";
    }
    def filterType = params.filterType;
    def listURI = params.listURI ? params.listURI : "mobile/${gspFolder}/event.gsp";
    def username = session.username;
    def queryGroups = SearchQueryGroup.getVisibleGroups(username, filterType)
    def queryHierarchy = [:]
    def populateQueryHierarchy;
    populateQueryHierarchy = {parentQueryId, hierarchyMap, queries ->
        def currentLevelQueries = queries.findAll {it.parentQueryId == parentQueryId};
        currentLevelQueries.each {query ->
            def currentHierarchyMap = [:]
            hierarchyMap[query] = currentHierarchyMap;
            populateQueryHierarchy(query.id, currentHierarchyMap, queries);
        }
    }
    queryGroups.each {group ->
        def currentHierarchyMap = [:]
        queryHierarchy[group] = currentHierarchyMap;
        def queries = SearchQuery.getVisibleQueries(group, username, filterType)
        populateQueryHierarchy(0, currentHierarchyMap, queries);
    }

    def drawQueries;
    drawQueries = {subqueries, groupName, level ->
        subqueries.each {subquery, squeries ->
            def padding = level * 5
            println "<li>${rui.link(url:listURI, params:[query:subquery.query, searchIn:subquery.searchClass], style:'padding-left:'+ padding, subquery.name)}</li>"
            drawQueries(squeries, groupName, level+1);
        }
    }
%>
<ul id="query" class="list">
    <g:each var="groupEntry" in="${queryHierarchy}">
        <g:set var="group" value="${groupEntry.key}"></g:set>
        <g:set var="subQueries" value="${groupEntry.value}"></g:set>
        <li class="group">${group.name}</li>
        <%
            drawQueries(subQueries, group.name, 0);
        %>
    </g:each>
</ul>

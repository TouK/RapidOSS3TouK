<%@ page import="search.SearchQuery; search.SearchQueryGroup; auth.RsUser" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: Feb 10, 2009
  Time: 6:30:54 PM
  To change this template use File | Settings | File Templates.
--%>

<%
    def mode = params.mode;
    def userName = session.username;
    def queryType = params.type;
    def searchComponentType = params.searchComponentType;
    def searchQuery;
%>
<g:if test="${mode == 'edit' && (searchQuery = SearchQuery.get([id: params.queryId])) == null}">
    <div style="height:100%; background-color:#fff3f3;color:#cc0000">
        SearchQuery with id ${params.queryId} does not exist.
    </div>
</g:if>
<g:else>
    <%
        def url = mode == 'create' ? "searchQuery/save?format=xml&type=${queryType}" : "searchQuery/update?format=xml&type=${queryType}";
        def gridViews = ui.GridView.searchEvery("username:${userName.exactQuery()} OR (username:${RsUser.RSADMIN.exactQuery()} AND isPublic:true)", [sort: "name"]);
        def className;
        switch (queryType) {
            case 'event': className = 'RsEvent'; break;
            case 'historicalEvent': className = 'RsHistoricalEvent'; break;
            case 'topology': className = 'RsTopologyObject'; break;
        }
        if(className == null){
            className = params.rootClass;
        }
        def allProps = [];
        def classes = [];
        def domainClass = grailsApplication.getDomainClass(className);
        classes.add(domainClass);
        allProps.addAll(domainClass.clazz.getNonFederatedPropertyList());
        domainClass.getSubClasses().each {
            classes.add(it);
            allProps.addAll(it.clazz.getNonFederatedPropertyList());
        }
        classes = classes.sort{it.fullName};
        def sortedProps = allProps.sort {it.name}
        def searchQueryGroups = SearchQueryGroup.list().findAll {queryGroup ->
            queryGroup.username == userName && queryGroup.isPublic == false && (queryGroup.type == queryType || queryGroup.type == "default")
        };
        def propertyMap = [:]
        def group = params.group ? params.group : mode == 'edit' ? searchQuery.group.name : '';
        def queryName = params.name ? params.name : mode == 'edit' ? searchQuery.name : '';
        def query = params.query ? params.query : mode == 'edit' ? searchQuery.query : '';
        def viewName = params.viewName ? params.viewName : mode == 'edit' ? searchQuery.viewName : '';
        def sortProperty = params.sortProperty ? params.sortProperty : mode == 'edit' ? searchQuery.sortProperty : '';
        def sortOrder = params.sortOrder ? params.sortOrder : mode == 'edit' ? searchQuery.sortOrder : 'asc';
        def searchClass = params.searchClass ? params.searchClass : mode == 'edit' ? searchQuery.searchClass : '';
    %>
    <script type="text/javascript">
    window.refreshFilterTree = function(){
    var filterTree = YAHOO.rapidjs.Components['filterTree'];
    filterTree.poll();
    }
    </script>
    <rui:formRemote method="POST" action="${url}" componentId="${params.componentId}" onSuccess="window.refreshFilterTree">
        <table>
            <tr><td width="50%"><label>Group Name:</label></td><td width="50%"><select name="group" style="width:175px">
                <g:each var="searchQueryGroup" in="${searchQueryGroups}">
                    <g:if test="${group == searchQueryGroup.name}">
                        <option name="${searchQueryGroup.name}" selected="true">${searchQueryGroup.name}</option>
                    </g:if>
                    <g:else>
                        <option name="${searchQueryGroup.name}">${searchQueryGroup.name}</option>
                    </g:else>
                </g:each>
            </select></td></tr>
            <tr><td width="50%"><label>Query Name:</label></td><td width="50%">
                <input type="textbox" name="name" style="width:175px" value="${queryName.encodeAsHTML()}">
            </td></tr>
             <tr><td width="50%"><label>Search Class:</label></td><td width="50%"><select name="searchClass" style="width:175px">
                <g:each var="currentClass" in="${classes}">
                    <g:if test="${searchClass == currentClass.fullName}">
                        <option name="${currentClass.fullName}" selected="true">${currentClass.fullName}</option>
                    </g:if>
                    <g:else>
                        <option name="${currentClass.fullName}">${currentClass.fullName}</option>
                    </g:else>
                </g:each>
            </select></td></tr>
            <tr><td width="50%"><label>Query:</label></td><td width="50%">
                <input type="textbox" name="query" style="width:175px" value="${query.encodeAsHTML()}">
            </td></tr>
            <g:if test="${searchComponentType == 'grid'}">
                <tr><td width="50%"><label>View Name:</label></td><td width="50%">
                    <select name="viewName" style="width:175px">
                        <option name="default" ${viewName == 'default' ? 'selected="true"' : ''}>default</option>
                        <g:each var="view" in="${gridViews}">
                            <g:if test="${viewName == view.name}">
                                <option name="${view.name}" selected="true">${view.name}</option>
                            </g:if>
                            <g:else>
                                <option name="${view.name}">${view.name}</option>
                            </g:else>
                        </g:each>
                    </select>
                </td></tr>
                <input type="hidden" name="sortProperty" value="${sortProperty.encodeAsHTML()}">
                <input type="hidden" name="sortOrder" value="${sortOrder.encodeAsHTML()}">
            </g:if>
            <g:else>
                <tr><td width="50%"><label>Sort Property:</label></td><td width="50%">
                    <select name="sortProperty" style="width:175px">
                        <g:each var="prop" in="${sortedProps}">
                            <g:if test="${prop.name != 'rsDatasource'}">
                                <g:if test="${!propertyMap[prop.name]}">
                                    <g:if test="${sortProperty == prop.name}">
                                        <option name="${prop.name}" selected="true">${prop.name}</option>
                                    </g:if>
                                    <g:else>
                                        <option name="${prop.name}">${prop.name}</option>
                                    </g:else>
                                    <%
                                        propertyMap.put(prop.name, "true");
                                    %>
                                </g:if>
                            </g:if>
                        </g:each>
                    </select></td></tr>
                <tr><td width="50%"><label>Sort Order:</label></td><td width="50%">
                    <select name="sortOrder" style="width:175px">
                        <option value="asc" ${sortOrder == 'asc' ? 'selected="true"' : ''}>asc</option>
                        <option value="desc" ${sortOrder == 'desc' ? 'selected="true"' : ''}>desc</option>
                    </select>
                </td></tr>
                <input type="hidden" name="viewName" value="${viewName.encodeAsHTML()}">
            </g:else>
        </table>
        <input type="hidden" name="id" value="${searchQuery != null ? searchQuery.id : ''}">
    </rui:formRemote>
</g:else>
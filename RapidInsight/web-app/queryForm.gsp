<%@ page import="auth.Role; search.SearchQuery; search.SearchQueryGroup; auth.RsUser" %>
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
        if (className == null) {
            className = params.rootClass;
        }
        def allProps = [];
        def classes = [];
        def searchInEnabled = params.searchInEnabled != 'false' ? true : false;
        def domainClass = grailsApplication.getDomainClass(className);
        classes.add(domainClass);
        allProps.addAll(domainClass.clazz.getNonFederatedPropertyList());
        domainClass.getSubClasses().each {
            classes.add(it);
            allProps.addAll(it.clazz.getNonFederatedPropertyList());
        }
        if (searchInEnabled) {
            classes = classes.sort {it.fullName};
        }
        def sortedProps = allProps.sort {it.name}
        def searchQueryGroups = SearchQueryGroup.getEditableGroups(userName, queryType)
        def parentQueryCandidates = [];
        def propertyMap = [:]
        def group = params.group ? params.group : mode == 'edit' ? searchQuery.group.name : '';
        if (group == '' && searchQueryGroups.size() > 0) {
            parentQueryCandidates = searchQueryGroups[0].queries;
        }
        def parentQueryId = params.parentQueryId ? params.parentQueryId : mode == 'edit' ? searchQuery.parentQueryId.toString() : "0";
        def queryName = params.name ? params.name : mode == 'edit' ? searchQuery.name : '';
        def query = params.query ? params.query : mode == 'edit' ? searchQuery.query : '';
        def viewName = params.viewName ? params.viewName : mode == 'edit' ? searchQuery.viewName : '';
        def sortProperty = params.sortProperty ? params.sortProperty : mode == 'edit' ? searchQuery.sortProperty : '';
        def sortOrder = params.sortOrder ? params.sortOrder : mode == 'edit' ? searchQuery.sortOrder : 'asc';
        def searchClass = params.searchClass ? params.searchClass : mode == 'edit' ? searchQuery.searchClass : '';
        def isPublic = params.isPublic ? Boolean.parseBoolean(params.isPublic) : mode == 'edit' ? searchQuery.isPublic : false;
        def expanded = params.expanded ? Boolean.parseBoolean(params.expanded) : mode == 'edit' ? searchQuery.expanded : false;
    %>
    <script type="text/javascript">
        QueryFormHelper = function(htmlCompId){
            this.compId = htmlCompId;
            this.requester = new YAHOO.rapidjs.Requester(this.processSuccess, this.processFailure, this);
        };
        QueryFormHelper.prototype = {
            getHtmlComp : function(){
                return YAHOO.rapidjs.Components[this.compId]
            },
            refreshFilterTree : function(){
                var filterTree = YAHOO.rapidjs.Components['filterTree'];
                filterTree.poll();
            },
            queryGroupChanged : function(groupSelectEl){
               var queryGroup = groupSelectEl.options[groupSelectEl.selectedIndex].text;
               this.getHtmlComp().showMask();
               this.requester.doRequest(getUrlPrefix() + 'script/run/queryFormHelper', {name:queryGroup, type:'${queryType}'})
            },
            processSuccess: function(response){
               this.getHtmlComp().hideMask();
               var parentQuerySelectEl = document.getElementById('parentQuerySelect');
               var queries = response.responseXML.getElementsByTagName('Query');
               var currentQueryId = document.getElementById('searchQueryId').value;
               SelectUtils.clear(parentQuerySelectEl)
               SelectUtils.addOption(parentQuerySelectEl, '', '0');
               for(var i=0; i<queries.length; i++){
                   var queryNode = queries[i];
                   var queryId = queryNode.getAttribute('id');
                   if(queryId != currentQueryId){
                      SelectUtils.addOption(parentQuerySelectEl, queryNode.getAttribute('name'), queryId);
                   }
               }
            },
            processFailure: function(errors){
                var htmlComp = this.getHtmlComp()
                htmlComp.hideMask();
                htmlComp.events["error"].fireDirect(htmlComp,  errors);
            }
        }
        window.queryFormHelper = new QueryFormHelper('${params.componentId}');
    </script>
    <rui:formRemote method="POST" action="${url}" componentId="${params.componentId}" onSuccess="window.queryFormHelper.refreshFilterTree">
        <table>
            <tr><td width="50%"><label>Group Name:</label></td><td width="50%"><select name="group" style="width:175px" onchange="window.queryFormHelper.queryGroupChanged(this)">
                <g:each var="searchQueryGroup" in="${searchQueryGroups}">
                    <g:if test="${group == searchQueryGroup.name}">
                        <%
                             parentQueryCandidates = searchQueryGroup.queries;
                        %>
                        <option name="${searchQueryGroup.name}" selected="true">${searchQueryGroup.name}</option>
                    </g:if>
                    <g:else>
                        <option name="${searchQueryGroup.name}">${searchQueryGroup.name}</option>
                    </g:else>
                </g:each>
            </select></td></tr>
            <tr><td width="50%"><label>Parent Query:</label></td><td width="50%"><select name="parentQueryId" style="width:175px" id="parentQuerySelect">
                <option value="0"></option>
                <%
                    parentQueryCandidates = SearchQuery.getEditableQueries(parentQueryCandidates, userName, queryType)
                %>
                <g:each var="parentQuery" in="${parentQueryCandidates}">
                    <g:if test="${parentQuery.id.toString() != params.queryId}">
                        <g:if test="${parentQueryId == parentQuery.id.toString()}">
                            <option value="${parentQuery.id}" selected="true">${parentQuery.name}</option>
                        </g:if>
                        <g:else>
                            <option value="${parentQuery.id}">${parentQuery.name}</option>
                        </g:else>
                    </g:if>
                </g:each>
            </select></td></tr>
            <tr><td width="50%"><label>Query Name:</label></td><td width="50%">
                <input type="textbox" name="name" style="width:175px" value="${queryName.encodeAsHTML()}">
            </td></tr>
            <g:if test="${searchInEnabled}">
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
            </g:if>
             <g:else>
            	<input type="hidden" name="searchClass" value="${searchClass}">
            </g:else>
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
            <tr><td width="50%"><label>Expanded:</label></td><td width="50%"><g:checkBox name="expanded" value="${expanded}"></g:checkBox></td></tr>
            <jsec:hasRole name="${Role.ADMINISTRATOR}">
                <tr><td width="50%"><label>Public:</label></td><td width="50%"><g:checkBox name="isPublic" value="${isPublic}"></g:checkBox></td></tr>
            </jsec:hasRole>
        </table>
        <input type="hidden" name="id" value="${searchQuery != null ? searchQuery.id : ''}" id="searchQueryId">
    </rui:formRemote>
</g:else>
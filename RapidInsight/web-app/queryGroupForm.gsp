<%@ page import="auth.Role; search.SearchQueryGroup" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: Feb 11, 2009
  Time: 11:22:38 AM
  To change this template use File | Settings | File Templates.
--%>

<%
    def mode = params.mode;
    def queryType = params.type;
    def searchQueryGroup;
%>
<g:if test="${mode == 'edit' && (searchQueryGroup = SearchQueryGroup.get([id: params.queryGroupId])) == null}">
    <div style="height:100%; background-color:#fff3f3;color:#cc0000">
        SearchQueryGroup with id ${params.queryGroupId} does not exist.
    </div>
</g:if>
<g:else>
    <%
        def url = mode == 'edit' ? "searchQueryGroup/update?format=xml&type=${queryType}":"searchQueryGroup/save?format=xml&type=${queryType}"
        def groupName = mode == 'edit'? searchQueryGroup.name: '';
        def isPublic = mode == 'edit'? searchQueryGroup.isPublic: false;
        def expanded = mode == 'edit'? searchQueryGroup.expanded: false;
    %>
    <script type="text/javascript">
         window.refreshFilterTree = function(){
            var filterTree = YAHOO.rapidjs.Components['filterTree'];
            filterTree.poll();
        }
    </script>
    <rui:formRemote method="POST" action="${url}" componentId="${params.componentId}" onSuccess="window.refreshFilterTree">
        <table>
            <tr><td width="50%"><label>Group Name:</label></td><td width="50%"><input type="textbox" name="name" style="width:175px" value="${groupName.encodeAsHTML()}"/></td></tr>
            <jsec:hasRole name="${Role.ADMINISTRATOR}">
                <tr><td width="50%"><label>Public:</label></td><td width="50%"><g:checkBox name="isPublic" value="${isPublic}"></g:checkBox></td></tr>
            </jsec:hasRole>
            <tr><td width="50%"><label>Expanded:</label></td><td width="50%"><g:checkBox name="expanded" value="${expanded}"></g:checkBox></td></tr>
        </table>
        <input type="hidden" name="id" value="${mode == 'edit'? searchQueryGroup.id : ''}">
    </rui:formRemote>
</g:else>
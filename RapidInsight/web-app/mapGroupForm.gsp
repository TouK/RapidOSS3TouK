<%@ page import="auth.Role; ui.map.MapGroup" %>
<%
    def mode = params.mode;
    def mapGroup;
%>
<g:if test="${mode == 'edit' && (mapGroup = MapGroup.get([id: params.mapGroupId])) == null}">
    <div style="height:100%; background-color:#fff3f3;color:#cc0000">
        MapGroup with id ${params.mapGroupId} does not exist.
    </div>
</g:if>
<g:else>
    <%
        def url = mode == 'edit' ? "mapGroup/update?format=xml":"mapGroup/save?format=xml"
        def groupName = mode == 'edit'? mapGroup.groupName: '';
        def isPublic = mode == 'edit'? mapGroup.isPublic: false;
        def expanded = mode == 'edit'? mapGroup.expanded: false;
    %>
    <script type="text/javascript">
         window.refreshMapTree = function(){
            var mapTree = YAHOO.rapidjs.Components['mapTree'];
            mapTree.poll();
        }
    </script>
    <rui:formRemote method="POST" action="${url}" componentId="${params.componentId}" onSuccess="window.refreshMapTree">
        <table>
            <tr><td width="50%"><label>Group Name:</label></td><td width="50%"><input type="textbox" name="groupName" style="width:175px" value="${groupName.encodeAsHTML()}"/></td></tr>
            <jsec:hasRole name="${Role.ADMINISTRATOR}">
                <tr><td width="50%"><label>Public:</label></td><td width="50%"><g:checkBox name="isPublic" value="${isPublic}"></g:checkBox></td></tr>
            </jsec:hasRole>
            <tr><td width="50%"><label>Expanded:</label></td><td width="50%"><g:checkBox name="expanded" value="${expanded}"></g:checkBox></td></tr>
        </table>
        <input type="hidden" name="id" value="${mode == 'edit'? mapGroup.id : ''}">
    </rui:formRemote>
</g:else>
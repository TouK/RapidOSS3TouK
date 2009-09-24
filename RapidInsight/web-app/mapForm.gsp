<%@ page import="ui.map.TopoMap; ui.map.MapGroup" %>
<%
    def mode = params.mode;
    def userName = session.username;
    def topoMap;
%>
<g:if test="${mode == 'edit' && (topoMap = TopoMap.get([id: params.id])) == null}">
    <div style="height:100%; background-color:#fff3f3;color:#cc0000">
        TopoMap with id ${params.id} does not exist.
    </div>
</g:if>
<g:else>
    <script type="text/javascript">
    window.refreshMapTree = function(){
    var mapTree = YAHOO.rapidjs.Components['mapTree'];
    mapTree.poll();
    }
    </script>
    <%
        def mapGroups = MapGroup.getSaveGroupsForUser(userName);
        def groupName = params.groupName ? params.groupName : mode == 'edit' ? topoMap.group.groupName : '';
        def mapName = params.mapName ? params.mapName : mode == 'edit' ? topoMap.mapName : '';
        def nodes = params.nodes ? params.nodes : ''
        def nodePropertyList=params.nodePropertyList? params.nodePropertyList:''
        def mapProperties=params.mapProperties? params.mapProperties:''
        def mapPropertyList=params.mapPropertyList? params.mapPropertyList:''


        def layout = params.layout ? params.layout : mode == 'edit' ? topoMap.layout : '0'

        def url = mode == 'edit' ? "topoMap/update?format=xml":"topoMap/save?format=xml"
    %>

    <rui:formRemote method="POST" action="${url}" componentId="${params.componentId}" onSuccess="window.refreshMapTree">
        <table>
            <tr><td width="50%"><label>Group Name:</label></td><td width="50%"><select name="groupName" style="width:175px">
                <g:each var="mapGroup" in="${mapGroups}">
                    <g:if test="${groupName == mapGroup.groupName}">
                        <option name="${mapGroup.groupName}" selected="true">${mapGroup.groupName}</option>
                    </g:if>
                    <g:else>
                        <option name="${mapGroup.groupName}">${mapGroup.groupName}</option>
                    </g:else>
                </g:each>
            </select>
            </td></tr>
            <tr><td width="50%"><label>Map Name:</label></td><td width="50%"><input type="textbox" name="mapName" style="width:175px" value="${mapName.encodeAsHTML()}"/></td></tr>
        </table>
        <input type="hidden" name="nodes" value="${nodes.encodeAsHTML()}"/>
        <input type="hidden" name="layout" value="${layout.encodeAsHTML()}"/>
        <input type="hidden" name="id" value="${mode == 'edit'? topoMap.id : ''}"/>
        <input type="hidden" name="mapProperties" value="${mapProperties.encodeAsHTML()}"/>
        <input type="hidden" name="mapPropertyList" value="${mapPropertyList.encodeAsHTML()}"/>
        <input type="hidden" name="nodePropertyList" value="${nodePropertyList.encodeAsHTML()}"/>
    </rui:formRemote>
</g:else>
<%@ page import="auth.Group; auth.Role" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout" />
    <title>Edit Group</title>
</head>
<script type="text/javascript">
      function render(){
        segmentTypeChanged();
      }
      function segmentTypeChanged(){
           var segmentFilterTypeSelect = document.getElementById('segmentFilterType')
           var segmentFiltersEl = document.getElementById('segmentFilters')
           var segmentFilterRow = document.getElementById('segmentFilterRow')
           var segmentFilterType = segmentFilterTypeSelect.options[segmentFilterTypeSelect.selectedIndex].value;
           if(segmentFilterType == '${Group.GLOBAL_FILTER}'){
                segmentFiltersEl.style.display = 'none'
                segmentFilterRow.style.display = ''
           }
           else{
               segmentFiltersEl.style.display = ''
               segmentFilterRow.style.display = 'none'
           }
      }

</script>
<body onload="render()">
<div class="nav">
    <span class="menuButton"><g:link class="list" action="list">Group List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New Group</g:link></span>
</div>
<div class="body">
    <h1>Edit Group</h1>
    <g:render template="/common/messages" model="[flash:flash, beans:[group]]"></g:render>
    <g:form method="post" >
        <input type="hidden" name="id" value="${group?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">Name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:group,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:group,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="role">Role:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:group,field:'role','errors')}">
                            <g:select optionKey="id" from="${Role.list()}" name="role.id" value="${group?.role?.id}" noSelection="['null':'']"></g:select>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="segmentFilterType">Segment Filter Type:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: group, field: 'segmentFilterType', 'errors')}">
                            <g:select id="segmentFilterType" name="segmentFilterType" from="${group.constraints.segmentFilterType.inList.collect{it.encodeAsHTML()}}" value="${fieldValue(bean:group,field:'segmentFilterType')}" onchange="segmentTypeChanged()"></g:select>
                        </td>
                    </tr>
                    
                    <tr class="prop" id="segmentFilterRow">
                        <td valign="top" class="name">
                            <label for="segmentFilter">Segment Filter:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:group,field:'segmentFilter','errors')}">
                            <input type="text" id="segmentFilter" name="segmentFilter" value="${fieldValue(bean:group,field:'segmentFilter')}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name" colspan="2">
                            Users:
                        </td>
                    </tr>
                    <tr>
                        <td valign="top" class="name" colspan="2">
                            <g:render template="/common/listToList" model="[id:'users', inputName:'users.id', valueProperty:'id', displayProperty:'username', fromListTitle:'Available Users', toListTitle:'Group Users', fromListContent:availableUsers, toListContent:group?.users]"></g:render>
                        </td>
                    </tr>
                    
                </tbody>
            </table>
        </div>
        <div style="margin-top:20px;" id="segmentFilters">
            <%
                def currentUrl = "/group/edit/${group.id}"
            %>
        <table>
            <tr>
                <td>
                    <span style="color:#006DBA;font-size:14px;font-weight:bold;margin:0.8em 0pt 0.3em;">SegmentFilter List</span>
                    <span class="menuButton"><g:link class="create" controller="segmentFilter" params="${['groupId':group?.id, 'targetURI':currentUrl]}" action="create">New SegmentFilter</g:link></span>
                    <div class="list">
                        <table><br>
                            <thead>
                                <tr>
                                    <th>Class Name</th>
                                    <th>Filter</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <g:each in="${group.filters}" status="i" var="segmentFilter">
                                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                        <td><g:link action="show" controller="segmentFilter" id="${segmentFilter.id}" params="${[groupId:group.id, targetURI:currentUrl]}">${segmentFilter.className?.encodeAsHTML()}</g:link></td>
                                        <td>${segmentFilter.filter.encodeAsHTML()}</td>
                                        <td><g:link action="edit" controller="segmentFilter" id="${segmentFilter.id}" class="edit" params="${[groupId:group.id, targetURI:currentUrl]}">Edit</g:link></td>
                                    </tr>
                                </g:each>
                            </tbody>
                        </table>
                    </div>
                </td>
            </tr>
        </table>
    </div>
        <div class="buttons">
            <span class="button"><g:actionSubmit class="save" value="Update"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </div>
    </g:form>
</div>
</body>
</html>

<%@ page import="auth.Group; auth.Role" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout" />
    <title>Create Group</title>
</head>
<script type="text/javascript">
      function render(){
        segmentTypeChanged();
      }
      function segmentTypeChanged(){
           var segmentFilterTypeSelect = document.getElementById('segmentFilterType')
           var segmentFilterRow = document.getElementById('segmentFilterRow')
           var segmentFilterType = segmentFilterTypeSelect.options[segmentFilterTypeSelect.selectedIndex].value;
           if(segmentFilterType == '${Group.GLOBAL_FILTER}'){
                segmentFilterRow.style.display = ''
           }
           else{
               segmentFilterRow.style.display = 'none'
           }
      }

</script>
<body onload="render()">
<div class="nav">
    <span class="menuButton"><g:link class="list" action="list">Group List</g:link></span>
</div>
<div class="body">
    <h1>Create Group</h1>
    <g:render template="/common/messages" model="[flash:flash, beans:[group]]"></g:render>
    <g:form action="save" method="post" >
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
                            <g:select optionKey="id" from="${Role.list()}" name="role.id" value="${group?.role?.id}" ></g:select>
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
                            <g:render template="/common/listToList" model="[id:'users', inputName:'users.id', valueProperty:'id', displayProperty:'username', fromListTitle:'Available Users', toListTitle:'Group Users', fromListContent:availableUsers, toListContent:groupUsers]"></g:render>
                        </td>
                    </tr>
                    
                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><input class="save" type="submit" value="Create"/></span>
        </div>
    </g:form>
</div>
</body>
</html>

<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Nov 19, 2008
  Time: 3:52:05 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout"/>
    <title>Edit Group</title>
</head>
<body>
<script>
    function addUsers() {
       moveAllSelectedsFromSelectToSelect(document.getElementById('availableUsers'), document.getElementById('groupUsers'))
       updateUsersInput();
    }
    function removeUsers() {
      moveAllSelectedsFromSelectToSelect(document.getElementById('groupUsers'), document.getElementById('availableUsers'))
      updateUsersInput();
    }
    function updateUsersInput(){
        var groupUsersSelect = document.getElementById('groupUsers');
        var users = [];
        for(var index=0; index< groupUsersSelect.options.length; index ++){
            users.push(groupUsersSelect.options[index].value);
        }
        document.getElementById("usersInput").value = users.join(",");
    }
    function collectSelectedIndicesFromSelect(aSelect)
    {
        var selectedIndices = new Array();
        for (var i = 0; i < aSelect.options.length; i++)
        {
            if (aSelect.options[i].selected == true)
            {
                selectedIndices[selectedIndices.length] = i;
            }
        }
        return selectedIndices;
    }


    function moveFromSelectToSelect(index, fromSelect, toSelect)
    {
        if (index > -1)
        {
            var associatedOption = fromSelect.options[index];
            fromSelect.remove(index);
            try
            {
                toSelect.add(associatedOption, null);//to the end of the select
            }
            catch(ex)
            {
                toSelect.add(associatedOption);//IE only
            }

        }
    }


    function moveAllSelectedsFromSelectToSelect(fromSelect, toSelect)
    {
        var arrayOfSelectedIndices = collectSelectedIndicesFromSelect(fromSelect);
        for (var i = arrayOfSelectedIndices.length - 1; i >= 0; i--)
        {
            moveFromSelectToSelect(arrayOfSelectedIndices[i], fromSelect, toSelect);
        }
    }

</script>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir: 'admin.gsp')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">User List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New User</g:link></span>
</div>
<div class="body">
    <h1>Edit Group Users</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${flash.errors}">
        <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post">
        <input type="hidden" name="id" value="${group?.id}"/>
        <%
          def usersStringArray = [];
          group.users.each{
              usersStringArray.add(it.username);
          }
        %>
        <input type="hidden" name="users" id="usersInput" value="${usersStringArray.join(',')}"/>
        <div class="dialog" style="border:1px solid #A8B9CF">
            <table style="border:none">
                <tbody>
                    <tr class="prop">
                        <td valign="top" class="name">Group Name:</td>
                        <td valign="top" class="value">${group.name}</td>
                    </tr>
                </tbody>
            </table>
            <table style="border:none;width:auto">
                <tbody>
                    <tr>
                        <td><div>Available Users</div></td>
                        <td></td>
                        <td><div>Group's Users</div></td>
                    </tr>
                    <tr>
                        <td>
                            <div>
                                <select style="overflow:auto;width:200px;border:1px solid #A8B9CF" size="15" multiple="true" name="availableUsers" id="availableUsers">
                                    <g:each in="${availableUsers}" status="i" var="user">
                                        <option value="${user}">${user}</option>
                                    </g:each>
                                </select>
                            </div>
                        </td>
                        <td>
                            <div>
                                <span><span><button type="button" onclick="addUsers()">>></button></span></span>
                            </div>
                            <div>
                                <span><span><button type="button" onclick="removeUsers()"><<</button></span></span>
                            </div>
                        </td>
                        <td>
                            <div>
                                <select style="overflow:auto;width:200px;border:1px solid #A8B9CF" size="15" multiple="true" name="groupUsers" id="groupUsers">
                                    <g:each in="${group.users}" status="i" var="user">
                                        <option value="${user}">${user}</option>
                                    </g:each>
                                </select>
                            </div>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="buttons" style="margin-top:20px;">
            <span class="button"><g:actionSubmit class="save" value="Update" action="UpdateUsers"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
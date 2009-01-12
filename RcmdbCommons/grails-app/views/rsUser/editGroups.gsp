<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Nov 19, 2008
  Time: 1:59:00 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout"/>
    <title>Edit User</title>
</head>
<body>
<script>
    function addGroups() {
       moveAllSelectedsFromSelectToSelect(document.getElementById('availableGroups'), document.getElementById('userGroups'))
       updateGroupsInput();
    }
    function removeGroups() {
      moveAllSelectedsFromSelectToSelect(document.getElementById('userGroups'), document.getElementById('availableGroups'))
      updateGroupsInput();
    }
    function updateGroupsInput(){
        var userGroupsSelect = document.getElementById('userGroups');
        var groups = [];
        for(var index=0; index< userGroupsSelect.options.length; index ++){
            groups.push(userGroupsSelect.options[index].value);
        }
        document.getElementById("groupsInput").value = groups.join(",");
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
    <span class="menuButton"><g:link class="list" action="list">User List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New User</g:link></span>
</div>
<div class="body">
    <h1>Edit User Groups</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${flash.errors}">
        <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post">
        <input type="hidden" name="id" value="${rsUser?.id}"/>
        <%
          def groupsStringArray = [];
          rsUser.groups.each{
              groupsStringArray.add(it.name);
          }
        %>
        <input type="hidden" name="groups" id="groupsInput" value="${groupsStringArray.join(',')}"/>
        <div class="dialog" style="border:1px solid #A8B9CF">
            <table style="border:none">
                <tbody>
                    <tr class="prop">
                        <td valign="top" class="name">Username:</td>
                        <td valign="top" class="value">${rsUser.username}</td>
                    </tr>
                </tbody>
            </table>
            <table style="border:none;width:auto">
                <tbody>
                    <tr>
                        <td><div>Available Groups</div></td>
                        <td></td>
                        <td><div>User's Groups</div></td>
                    </tr>
                    <tr>
                        <td>
                            <div>
                                <select style="overflow:auto;width:200px;border:1px solid #A8B9CF" size="15" multiple="true" name="availableGroups" id="availableGroups">
                                    <g:each in="${availableGroups}" status="i" var="group">
                                        <option value="${group}">${group}</option>
                                    </g:each>
                                </select>
                            </div>
                        </td>
                        <td>
                            <div>
                                <span><span><button type="button" onclick="addGroups()">>></button></span></span>
                            </div>
                            <div>
                                <span><span><button type="button" onclick="removeGroups()"><<</button></span></span>
                            </div>
                        </td>
                        <td>
                            <div>
                                <select style="overflow:auto;width:200px;border:1px solid #A8B9CF" size="15" multiple="true" name="userGroups" id="userGroups">
                                    <g:each in="${rsUser.groups}" status="i" var="group">
                                        <option value="${group}">${group}</option>
                                    </g:each>
                                </select>
                            </div>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="buttons" style="margin-top:20px;">
            <span class="button"><g:actionSubmit class="save" value="Update" action="UpdateGroups"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
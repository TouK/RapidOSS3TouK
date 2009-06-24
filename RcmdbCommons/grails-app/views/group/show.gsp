

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout" />
    <title>Show Group</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><g:link class="list" action="list">Group List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New Group</g:link></span>
</div>
<div class="body">
    <h1>Show Group</h1>
    <g:if test="${flash.message}">
        <div class="message" id="pageMessage">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors" id="pageFlashErrors">>
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <div class="dialog">
        <table>
            <tbody>


                <tr class="prop">
                    <td valign="top" class="name" id="idLabel" >id:</td>

                    <td valign="top" class="value" id="id" >${group.id}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name"  id="nameLabel" >name:</td>

                    <td valign="top" class="value" id="name" >${group.name}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name" id="roleLabel" >role:</td>

                    <td valign="top" class="value"  id="role" >${group?.role}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name" id="segmentFilterLabel" >segmentFilter:</td>

                    <td valign="top" class="value" id= "segmentFilter">${group.segmentFilter}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">users:</td>

                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="u" in="${group.users}">
                                <li><g:link controller="rsUser" action="show" id="${u.id}">${u}</g:link></li>
                            </g:each>
                        </ul>
                    </td>

                </tr>

            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${group?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>



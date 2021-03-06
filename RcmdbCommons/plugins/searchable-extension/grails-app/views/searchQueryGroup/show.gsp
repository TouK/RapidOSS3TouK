<%@ page import="search.SearchQueryGroup" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show SearchQueryGroup</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><g:link class="list" action="list">SearchQueryGroup List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New SearchQueryGroup</g:link></span>
</div>
<div class="body">
    <h1>Show SearchQueryGroup</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors>
        <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
            <g:renderErrors bean="${searchQueryGroup}"/>
        </div>
    </g:hasErrors>
    <div class="dialog">
        <table>
            <tbody>

                <tr class="prop">
                    <td valign="top" class="name">Name:</td>

                    <td valign="top" class="value">${searchQueryGroup?.name}</td>

                </tr>

              </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${searchQueryGroup?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>

<%@ page import="search.SearchQuery" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show SearchQuery</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><g:link class="list" action="list">SearchQuery List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New SearchQuery</g:link></span>
</div>
<div class="body">
    <h1>Show SearchQuery</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors>
        <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
            <g:renderErrors bean="${searchQuery}"/>
        </div>
    </g:hasErrors>
    <div class="dialog">
        <table>
            <tbody>

                <tr class="prop">
                    <td valign="top" class="name">Name:</td>

                    <td valign="top" class="value">${searchQuery?.name}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Group:</td>

                    <td valign="top" class="value">${searchQuery?.group?.name}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Query:</td>

                    <td valign="top" class="value">${searchQuery?.query}</td>

                </tr>

              </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${searchQuery?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>

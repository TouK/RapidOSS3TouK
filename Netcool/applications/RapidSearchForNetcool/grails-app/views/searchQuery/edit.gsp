
<%@ page import="search.SearchQuery" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Edit SearchQuery</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">SearchQuery List</g:link></span>
            <span class="menuButton"><g:link class="create" action="create">New SearchQuery</g:link></span>
             <span class="menuButton"><a class="logout" href="${createLinkTo(dir: 'auth/logout')}">Logout</a></span>
        </div>
        <div class="body">
            <h1>Edit SearchQuery</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${searchQuery}">
            <div class="errors">
                <g:renderErrors bean="${searchQuery}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <input type="hidden" name="id" value="${searchQuery?.id}" />
                <div class="dialog">
                     <table>
                        <tbody>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name">Name:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:searchQuery,field:'name','errors')}">
                                    <input type="text" class="inputtextfield" id="name" name="name" value="${fieldValue(bean:searchQuery,field:'name')}"/>
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="query">Query:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:searchQuery,field:'query','errors')}">
                                    <input type="text"  class="inputtextfieldl" id="query" name="query" value="${fieldValue(bean:searchQuery,field:'query')}"/>
                                </td>
                            </tr>
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="group">SearchQueryGroup:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:searchQuery,field:'group','errors')}">
                                    <g:select class="inputtextfield" optionKey="id" from="${search.SearchQueryGroup.list()}" name="group.id" value="${searchQuery?.group?.id}" ></g:select>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:actionSubmit class="save" value="Update" /></span>
                    <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>

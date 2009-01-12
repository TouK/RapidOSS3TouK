
<%@ page import="search.SearchQuery" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Create SearchQuery</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><g:link class="list" action="list">SearchQuery List</g:link></span>
        </div>
        <div class="body">
            <h1>Create SearchQuery</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors>
                <div class="errors">
                    <g:renderErrors bean="${flash.errors}"/>
                    <g:renderErrors bean="${searchQuery}"/>
                </div>
            </g:hasErrors>
            <g:form action="save.html" method="post" >
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
                    <span class="button"><input class="save" type="submit" value="Create" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>


<%@ page import="search.SearchQueryGroup" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Edit SearchQueryGroup</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">SearchQueryGroup List</g:link></span>
            <span class="menuButton"><g:link class="create" action="create">New SearchQueryGroup</g:link></span>
        </div>
        <div class="body">
            <h1>Edit SearchQueryGroup</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors>
                <div class="errors">
                    <g:renderErrors bean="${flash.errors}"/>
                    <g:renderErrors bean="${searchQueryGroup}"/>
                </div>
            </g:hasErrors>
            <g:form method="post" >
                <input type="hidden" name="id" value="${searchQueryGroup?.id}" />
                <div class="dialog">
                     <table>
                        <tbody>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name">Name:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:searchQueryGroup,field:'name','errors')}">
                                    <input type="text" class="inputtextfield" id="name" name="name" value="${fieldValue(bean:searchQueryGroup,field:'name')}"/>
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

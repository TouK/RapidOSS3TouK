
<%@ page import="search.SearchQueryGroup" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>SearchQueryGroup List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><g:link class="create" action="create">New SearchQueryGroup</g:link></span>
        </div>
        <div class="body">
            <h1>SearchQueryGroup List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors>
                <div class="errors">
                    <g:renderErrors bean="${flash.errors}"/>
                    <g:renderErrors bean="${searchQueryGroup}"/>
                </div>
            </g:hasErrors>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="name" title="Name" />
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${searchQueryGroupList}" status="i" var="searchQueryGroup">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${searchQueryGroup.id}">${searchQueryGroup.name?.encodeAsHTML()}</g:link></td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${SearchQueryGroup.count()}" />
            </div>
        </div>
    </body>
</html>


<%@ page import="search.SearchQuery" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>SearchQuery List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New SearchQuery</g:link></span>
        </div>
        <div class="body">
            <h1>SearchQuery List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors>
                <div class="errors">
                    <g:renderErrors bean="${flash.errors}"/>
                    <g:renderErrors bean="${searchQuery}"/>
                </div>
            </g:hasErrors>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="name" title="Name" />
                        
                   	        <g:sortableColumn property="group" title="Group" />
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${searchQueryList}" status="i" var="searchQuery">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${searchQuery.id}">${searchQuery.name?.encodeAsHTML()}</g:link></td>

                            <td>${searchQuery.group?.name.encodeAsHTML()}</td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${SearchQuery.count()}" />
            </div>
        </div>
    </body>
</html>

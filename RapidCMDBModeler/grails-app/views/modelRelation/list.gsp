<%@ page import="model.*" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>ModelRelation List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New ModelRelation</g:link></span>
        </div>
        <div class="body">
            <h1>ModelRelation List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="id" title="Id" />
                   	        <g:sortableColumn property="firstCardinality" title="Cardinality" />
                           <th>From Model</th>
                           <g:sortableColumn property="firstModel" title="Relation Name" />
                   	        <th>To Model</th>
                   	        <g:sortableColumn property="secondModel" title="Reverse Relation Name" />
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${modelRelationList}" status="i" var="modelRelation">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${modelRelation.id}">${modelRelation.id?.encodeAsHTML()}</g:link></td>
                        
                            <td>${modelRelation.firstCardinality?.encodeAsHTML()}</td>
                            <td>${modelRelation.firstModel?.encodeAsHTML()}</td>
                            <td>${modelRelation.firstName?.encodeAsHTML()}</td>
                            <td>${modelRelation.secondModel?.encodeAsHTML()}</td>
                            <td>${modelRelation.secondName?.encodeAsHTML()}</td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total='${ModelRelation.countHits("id:*")}' />
            </div>
        </div>
    </body>
</html>

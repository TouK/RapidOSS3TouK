<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>${domainName} List</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><g:link class="home" action="classes">Home</g:link></span>
</div>
<div class="body">
    <h1>${domainName} List</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${flash.errors}">
        <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <div class="list">
        <table>
            <thead>
                <tr>
                    <g:sortableColumn property="id" title="id" action="${params.domain}" params="${[id:'search', query:params.query, searchQuery:params.searchQuery]}"/>
                    <g:each in="${propertyList}" var="p">
                        <g:if test="${p.name != 'id'}">
                            <g:if test="${!p.isRelation}">
                                <g:sortableColumn property="${p.name}" title="${p.name}" action="${params.domain}" params="${[id:'search', query:params.query, searchQuery:params.searchQuery]}"/>
                            </g:if>
                            <g:else>
                                <th>${p.name}</th>
                            </g:else>
                        </g:if>
                    </g:each>
                </tr>
            </thead>
            <tbody>
                <g:each in="${objectList}" status="i" var="object">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        <td><g:link action="${params.domain}" id="${object.id}">${object.id?.encodeAsHTML()}</g:link></td>
                        <g:each in="${propertyList}" var="p">
                            <g:if test="${p.name != 'id'}">
                                <g:if test="true">
                                    <td>${object[p.name]?.encodeAsHTML()}</td>
                                </g:if>
                                <g:else>
                                    <td>${object[p.name]?.encodeAsHTML()}</td>
                                </g:else>
                            </g:if>
                        </g:each>
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
     <div class="paginateButtons">
         <%
             def paginateParams = [searchQuery:params.searchQuery];
             if(params.query){
                 paginateParams.put("query", params.query)
             }
         %>
        <g:paginate total="${count}" action="${params.domain}" id="search" params="${paginateParams}"/>
    </div>
</div>
</body>
</html>

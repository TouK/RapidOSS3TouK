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
    <g:render template="/common/messages" model="[flash:flash]"></g:render>
    <div class="list">
        <table>
            <thead>
                <tr>
                    <g:sortableColumn property="id" title="id" action="${params.domain}"/>
                    <g:each in="${propertyList}" var="p">
                        <g:if test="${p.name != 'id'}">
                            <g:if test="${!p.isRelation}">
                                <g:sortableColumn property="${p.name}" title="${p.name}" action="${params.domain}"/>
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
        <g:paginate total="${count}" action="${params.domain}"/>
    </div>
</div>
</body>
</html>

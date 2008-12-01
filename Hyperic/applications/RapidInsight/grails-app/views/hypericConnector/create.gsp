<%@ page import="connection.HypericConnection" %><head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout"/>
    <title>Create HypericConnector</title>

</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir: '/admin.gsp')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">HypericConnector List</g:link></span>
</div>
<div class="body">
    <h1>Create HypericConnector</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${hypericConnector}">
        <div class="errors">
            <g:renderErrors bean="${hypericConnector}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
        <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>

    <g:hasErrors bean="${script.errors}">
        <div class="errors">
            <g:renderErrors bean="${script.errors}"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${datasource.errors}">
        <div class="errors">
            <g:renderErrors bean="${datasource.errors}"/>
        </div>
    </g:hasErrors>
    <g:form action="save" method="post">
        <div class="dialog">
            <table>
                <tbody>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">Name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: hypericConnector, field: 'name', 'errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean: hypericConnector, field: 'name')}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="connection">Hyperic Connection:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: hypericConnector, field: 'connection', 'errors')}">
                            <g:select optionKey="id" from="${HypericConnection.list()}" name="connection.id" value="${hypericConnector?.connection?.id}"></g:select>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="type">Type:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: hypericConnector, field: 'type', 'errors')}">
                            <g:select id="type" name="type" from="${hypericConnector.constraints.type.inList.collect{it.encodeAsHTML()}}" value="${fieldValue(bean:hypericConnector,field:'type')}"></g:select>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="period">Period:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: script, field: 'period', 'errors')}">
                            <input type="text" id="period" name="period" value="${fieldValue(bean: script, field: 'period')}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="logLevel">Log Level:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: script, field: 'logLevel', 'errors')}">
                            <g:select id="logLevel" name="logLevel" from="${script.constraints.logLevel.inList.collect{it.encodeAsHTML()}}" value="${fieldValue(bean:script,field:'logLevel')}"></g:select>
                        </td>
                    </tr>

                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><input class="save" type="submit" value="Create"/></span>
        </div>
    </g:form>
</div>
</body>
</html>

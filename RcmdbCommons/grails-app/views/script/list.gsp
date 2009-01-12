<%@ page import="script.CmdbScript" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout" />
    <title>Script List</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><g:link class="create" action="create">New Script</g:link></span>
</div>
<div class="body">
    <h1>Script List</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <div class="list">
        <table>
            <thead>
                <tr>

                    <g:sortableColumn property="name" title="Name"/>
                    <g:sortableColumn property="scriptFile" title="File"/>
                    <g:sortableColumn property="type" title="Type"/>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <g:each in="${cmdbScriptList}" status="i" var="cmdbScript">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        <td><g:link action="show" id="${cmdbScript.id}">${cmdbScript.name?.encodeAsHTML()}</g:link></td>
                        <td>${cmdbScript.scriptFile?.encodeAsHTML()}</td>
                        <td>${cmdbScript.type?.encodeAsHTML()}</td>
                        <td>
                            <%
                                if (cmdbScript.type != CmdbScript.LISTENING) {
                            %>
                            <g:link action="run" id="${cmdbScript.name}">Run</g:link>
                            <%
                                }
                            %>
                        </td>
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <g:paginate total="${CmdbScript.count()}"/>
    </div>
</div>
</body>
</html>

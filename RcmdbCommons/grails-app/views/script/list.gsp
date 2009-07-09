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
        <%
            def currentUrl=request.request.uri.toString().replace("/RapidSuite","");
            def startIndex=currentUrl.indexOf("script")
            if(script>=0)
            {
                currentUrl=currentUrl.substring(startIndex)
            }
            
        %>
        <table>
            <thead>
                <tr>

                    <g:sortableColumn property="name" title="Name"/>
                    <g:sortableColumn property="scriptFile" title="File"/>
                    <g:sortableColumn property="type" title="Type"/>
                    <th></th>
                    <th></th>
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
                            <g:link action="reload" id="${cmdbScript.name}" targetURI="cmdbScript" params="[id:cmdbScript.name,targetURI:currentUrl]">Reload</g:link>
                        </td>
                        <td>
                            <%
                                if (cmdbScript.type != CmdbScript.LISTENING) {
                            %>
                            <g:link url="run?id=${cmdbScript.name}">Run</g:link>
                            <%
                                }
                            %>
                        </td>
                        <td>
                           <g:link action="edit" id="${cmdbScript.id}" class="edit">Edit</g:link> 
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

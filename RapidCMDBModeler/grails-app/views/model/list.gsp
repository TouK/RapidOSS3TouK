<%@ page import="model.*" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout" />
    <title>Model List</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir: '/admin.gsp')}">Home</a></span>
    <span class="menuButton"><g:link class="create" action="create">New Model</g:link></span>
    <span class="menuButton"><g:link class="generate" action="generate" onclick="return confirm('Are you sure?');">Generate</g:link></span>
</div>
<div class="body">
    <h1>Model List</h1>
    <g:render template="/common/messages" model="[flash:flash]"></g:render>
    <div class="list">
        <table>
            <thead>
                <tr>

                    <g:sortableColumn property="name" title="Name"/>

                    <th>Inherits from Model</th>

                </tr>
            </thead>
            <tbody>
                <g:each in="${modelList}" status="i" var="model">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                        <td><g:link action="show" id="${model.id}">${model.name?.encodeAsHTML()}</g:link></td>

                        <td>${model.parentModel?.encodeAsHTML()}</td>

                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <g:paginate total='${Model.countHits("id:*")}'/>
    </div>
</div>
</body>
</html>

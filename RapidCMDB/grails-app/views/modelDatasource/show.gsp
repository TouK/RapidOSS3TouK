<%@ page import="model.*" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show ModelDatasource</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir: 'model/show/' + modelDatasource?.model?.id)}">${modelDatasource?.model}</a></span>
</div>
<div class="body">
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <h1>Show ModelDatasource</h1>
    <div class="dialog">
        <table>
            <tbody>

                <tr class="prop">
                    <td valign="top" class="name">Id:</td>

                    <td valign="top" class="value">${modelDatasource.id}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Datasource:</td>

                    <td valign="top" class="value"><g:link controller="baseDatasource" action="show" id="${modelDatasource?.datasource?.id}">${modelDatasource?.datasource}</g:link></td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Master:</td>

                    <td valign="top" class="value">${modelDatasource.master}</td>

                </tr>
            </tbody>
        </table>
    </div>


    <div style="margin-top:20px;">
        <span style="color:#006DBA;font-size:16px;font-weight:normal;margin:0.8em 0pt 0.3em;">KeyMapping List</span>
        <span class="menuButton"><g:link controller="modelDatasourceKeyMapping" params="['modelDatasource.id':modelDatasource?.id]" class="create" action="create">New KeyMapping</g:link></span>
        <div class="list">
            <table>
                <thead>
                    <tr>
                        <g:sortableColumn property="property" title="Property"/>
                        <g:sortableColumn property="nameInDatasource" title="Name In Datasource"/>
                    </tr>
                </thead>
                <tbody>
                    <g:each in="${modelDatasource.keyMappings}" status="i" var="modelDatasourceKeyMapping">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                            <td><g:link action="show" controller="modelDatasourceKeyMapping" id="${modelDatasourceKeyMapping.id}">${modelDatasourceKeyMapping.property?.encodeAsHTML()}</g:link></td>

                            <td>${modelDatasourceKeyMapping.nameInDatasource?.encodeAsHTML()}</td>

                        </tr>
                    </g:each>
                </tbody>
            </table>
        </div>
    </div>
    <div class="buttons" style="margin-top:30px;">
        <g:form>
            <input type="hidden" name="id" value="${modelDatasource?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>

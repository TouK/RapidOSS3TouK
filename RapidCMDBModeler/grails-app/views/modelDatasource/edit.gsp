<%@ page import="model.*" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout" />
    <title>Edit ModelDatasource</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir: 'model/show/' + modelDatasource.model?.id)}">${modelDatasource.model?.name}</a></span>
</div>
<div class="body">
    <h1>Edit ModelDatasource</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${modelDatasource}">
        <div class="errors">
            <g:renderErrors bean="${modelDatasource}" as="list"/>
        </div>
    </g:hasErrors>

    <p>Every property of the modeled class is retrieved from a datasource. There is always one master datasource for CRUD operations. Therefore id properties are defined for the master datasource. A modeled class may use multiple datasources and for each datasource, keys are listed, mapping properties to datasource fields. This defines how the datasource will be queried using the attributes of the modeled class.</p>
    <ul>
        <li><b>Datasource:</b>&nbsp; Name of the datasource instance as defined in the admin UI Datasources tab.</li>
        <li><b>Model:</b> Preselected. No need to change.</li>
    </ul>

    <g:form method="post">
        <input type="hidden" name="id" value="${modelDatasource?.id}"/>
        <div class="dialog">
            <table>
                <tbody>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="datasource">Datasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: modelDatasource, field: 'datasource', 'errors')}">
                            <g:select class="inputtextfield" optionKey="id" from="${DatasourceName.list()}" name="datasource.id" value="${modelDatasource?.datasource?.id}"></g:select>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="model">Model:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: modelDatasource, field: 'model', 'errors')}">
                            <g:select class="inputtextfield" optionKey="id" from="${Model.list()}" name="model.id" value="${modelDatasource?.model?.id}"></g:select>
                        </td>
                    </tr>

                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><g:actionSubmit class="save" value="Update"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </div>
    </g:form>
</div>
</body>
</html>

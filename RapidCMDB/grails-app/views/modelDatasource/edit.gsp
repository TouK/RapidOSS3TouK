<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit ModelDatasource</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir: '')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">ModelDatasource List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New ModelDatasource</g:link></span>
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
        <li><b>Master:</b> true/false. Default value is <b>false</b>. Declares which datasource will be used for CRUD operations.</li>
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
                            <g:select optionKey="id" from="${BaseDatasource.list()}" name="datasource.id" value="${modelDatasource?.datasource?.id}"></g:select>
                        </td>
                    </tr>

                    %{--<tr class="prop">--}%
                        %{--<td valign="top" class="name">--}%
                            %{--<label for="keyMappings">Key Mappings:</label>--}%
                        %{--</td>--}%
                        %{--<td valign="top" class="value ${hasErrors(bean: modelDatasource, field: 'keyMappings', 'errors')}">--}%

                            %{--<ul>--}%
                                %{--<g:each var="k" in="${modelDatasource?.keyMappings?}">--}%
                                    %{--<li><g:link controller="modelDatasourceKeyMapping" action="show" id="${k.id}">${k}</g:link></li>--}%
                                %{--</g:each>--}%
                            %{--</ul>--}%
                            %{--<g:link controller="modelDatasourceKeyMapping" params="['modelDatasource.id':modelDatasource?.id]" action="create">Add ModelDatasourceKeyMapping</g:link>--}%

                        %{--</td>--}%
                    %{--</tr>--}%

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="master">Master:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: modelDatasource, field: 'master', 'errors')}">
                            <g:checkBox name="master" value="${modelDatasource?.master}"></g:checkBox>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="model">Model:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: modelDatasource, field: 'model', 'errors')}">
                            <g:select optionKey="id" from="${Model.list()}" name="model.id" value="${modelDatasource?.model?.id}"></g:select>
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

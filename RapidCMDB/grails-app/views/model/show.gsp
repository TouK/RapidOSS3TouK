<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show Model</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir: '')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">Model List</g:link></span>
</div>
<div class="body">
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <h1>Show Model</h1>
    <div class="dialog">
        <table>
            <tbody>
                <tr class="prop">
                    <td valign="top" class="name">Id:</td>

                    <td valign="top" class="value">${model.id}</td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Name:</td>

                    <td valign="top" class="value">${model.name}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Parent Model:</td>

                    <td valign="top" class="value"><g:link controller="model" action="show" id="${model?.parentModel?.id}">${model?.parentModel}</g:link></td>

                </tr>
            </tbody>
        </table>
    </div>
    <div style="margin-top:20px;">
        <span style="color:#006DBA;font-size:16px;font-weight:normal;margin:0.8em 0pt 0.3em;">Datasource List</span>
        <span class="menuButton" ><g:link class="create" controller="modelDatasource" params="['model.id':model?.id]" action="create">New Datasource</g:link></span>
        <div class="list">
            <table>
                <thead>
                    <tr>
                        <g:sortableColumn property="datasource" title="Datasource"/>
                        <g:sortableColumn property="master" title="Master"/>
                    </tr>
                </thead>
                <tbody>
                    <g:each in="${model.datasources}" status="i" var="modelDatasource">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                            <td><g:link action="show" controller="modelDatasource" id="${modelDatasource.id}">${modelDatasource.datasource?.encodeAsHTML()}</g:link></td>
                            <td>${modelDatasource.master?.encodeAsHTML()}</td>
                        </tr>
                    </g:each>
                </tbody>
            </table>
        </div>
    </div>
    <div style="margin-top:20px;">
       <span style="color:#006DBA;font-size:16px;font-weight:normal;margin:0.8em 0pt 0.3em;">Property List</span>
       <span class="menuButton"><g:link class="create" controller="modelProperty" params="['model.id':model?.id]" action="create">New Property</g:link></span>
        <div class="list">
            <table>
                <thead>
                    <tr>

                        <g:sortableColumn property="name" title="Name"/>
                        <g:sortableColumn property="type" title="Type"/>

                        <g:sortableColumn property="blank" title="Blank"/>

                        <th>Datasource Name</th>

                        <th>Property Specifying Datasource</th>



                    </tr>
                </thead>
                <tbody>
                    <g:each in="${model.modelProperties}" status="i" var="modelProperty">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                            <td><g:link action="show" id="${modelProperty.id}" controller="modelProperty">${modelProperty.name?.encodeAsHTML()}</g:link></td>
                            <td>${modelProperty.type?.encodeAsHTML()}</td>

                            <td>${modelProperty.blank?.encodeAsHTML()}</td>

                            <td>${modelProperty.datasourceName?.encodeAsHTML()}</td>

                            <td>${modelProperty.propertySpecifyingDatasource?.encodeAsHTML()}</td>



                        </tr>
                    </g:each>
                </tbody>
            </table>
        </div>
    </div>
    <div class="buttons" style="margin-top:30px;">
        <g:form>
            <input type="hidden" name="id" value="${model?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>

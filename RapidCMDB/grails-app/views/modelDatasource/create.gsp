<%@ page import="datasource.*; model.*" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create ModelDatasource</title>
</head>
<body>
<div class="nav">
    <%
        if (params["model.id"] != null) {
    %>
    <span class="menuButton"><a class="home" href="${createLinkTo(dir: 'model/show/' + params["model.id"])}">${Model.get(params["model.id"])?.name}</a></span>
    <%
        }
    %>

</div>
<div class="body">
    <h1>Create ModelDatasource</h1>
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

    <g:form action="save" method="post">
        <div class="dialog">
            <table>
                <tbody>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="datasource">Datasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: modelDatasource, field: 'datasource', 'errors')}">
                            <g:select class="inputtextfield" optionKey="id" from="${BaseDatasource.list()}" name="datasource.id" value="${modelDatasource?.datasource?.id}"></g:select>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="master">Master:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: modelDatasource, field: 'master', 'errors')}">
                            <g:checkBox name="master" value="${modelDatasource?.master}"></g:checkBox>
                        </td>
                    </tr>

                    <%
                        def selectValue;
                        def modelList;
                        if (params["model.id"] != null) {
                            selectValue = params["model.id"];
                    %>
                    <input type="hidden" name="model.id" value="${selectValue}"/>
                    <%
                        }
                        else {
                            selectValue = modelDatasource?.model?.id;
                            modelList = Model.list();
                    %>
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="model">Model:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: modelDatasource, field: 'model', 'errors')}">

                            <g:select optionKey="id" from="${modelList}" name="model.id" value="${selectValue}"></g:select>
                        </td>
                    </tr>

                    <% } %>
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

<%@ page import="model.*" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create ModelDatasourceKeyMapping</title>
</head>
<body>
<div class="nav">
     <%
        if (params["modelDatasource.id"] != null) {
    %>
        <span class="menuButton"><a class="home" href="${createLinkTo(dir: 'modelDatasource/show/' + params["modelDatasource.id"])}">${ModelDatasource.get(params["modelDatasource.id"])?.datasource?.name}</a></span>
    <%
        }
    %>
</div>
<div class="body">
    <h1>Create ModelDatasourceKeyMapping</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${modelDatasourceKeyMapping}">
        <div class="errors">
            <g:renderErrors bean="${modelDatasourceKeyMapping}" as="list"/>
        </div>
    </g:hasErrors>

    <ul>
        <li><b>Property:</b> Name of the property whose value will be used while querying the datasource. This can be a transient attribute whose value is calculated.</li>
        <li><b>Name In Datasource:</b> name of the field in datasource corresponding to the property in the modeled class.</li>
    </ul>

    <g:form action="save" method="post">
        <div class="dialog">
            <table>
                <tbody>

                    <%
                        def datasourceList;
                        def datasourceValue;
                        if (params["modelDatasource.id"] != null) {
                            datasourceValue = params["modelDatasource.id"];

                    %>
                    <input type="hidden" name="datasource.id" value="${datasourceValue}"/>
                    <%
                        }
                        else {
                            datasourceList = ModelDatasource.list();
                            datasourceValue = modelDatasourceKeyMapping?.datasource?.id;

                    %>
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="datasource">Datasource:</label>
                        </td>

                        <td valign="top" class="value ${hasErrors(bean: modelDatasourceKeyMapping, field: 'datasource', 'errors')}">
                            <g:select optionKey="id" from="${datasourceList}" name="datasource.id" value="${datasourceValue}"></g:select>
                        </td>
                    </tr>

                    <%
                        }
                    %>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="property">Property:</label>
                        </td>
                        <%
                            def modelPropertyList;
                            if (params["modelDatasource.id"] != null) {
                                def modelDatasource = ModelDatasource.get(params["modelDatasource.id"]);
                                def mdl = modelDatasource?.model;
                                def modelPropertyMap = [:];
                                modelPropertyList = mdl?.modelProperties;
                                for(modelProp in modelPropertyList){
                                    modelPropertyMap.put(modelProp.name, modelProp);
                                }
                                def tempModel = mdl.parentModel;
                                while(tempModel != null){
                                    for(prop in tempModel.modelProperties){
                                        if(!modelPropertyMap.containsKey(prop.name)){
                                            modelPropertyMap.put(prop.name, prop);
                                            modelPropertyList.add(prop);
                                        }

                                    }
                                    tempModel = tempModel.parentModel;
                                }
                            }
                            else {
                                modelPropertyList = ModelProperty.list();
                            }
                        %>
                        <td valign="top" class="value ${hasErrors(bean: modelDatasourceKeyMapping, field: 'property', 'errors')}">
                            <g:select optionKey="id" from="${modelPropertyList}" name="property.id" value="${modelDatasourceKeyMapping?.property?.id}"></g:select>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="nameInDatasource">Name In Datasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: modelDatasourceKeyMapping, field: 'nameInDatasource', 'errors')}">
                            <input type="text" id="nameInDatasource" name="nameInDatasource" value="${fieldValue(bean: modelDatasourceKeyMapping, field: 'nameInDatasource')}"/>
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

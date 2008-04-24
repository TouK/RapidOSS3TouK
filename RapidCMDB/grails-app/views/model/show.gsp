<%@ page import="model.*" %>
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
    <g:if test="${flash.errors}">
        <div class="errors">
            <ul>
                <g:each var="error" in="${flash?.errors}">
                    <li>${error}</li>
                </g:each>
            </ul>
        </div>
    </g:if>
    <h1>Show Model</h1>
    <div class="dialog">
        <table style="width:900;">
            <tbody>

                <tr class="prop">
                    <td valign="top" class="name">Name:</td>

                    <td valign="top" class="value">${model.name}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Extends Model:</td>

                    <td valign="top" class="value"><g:link controller="model" action="show" id="${model?.parentModel?.id}">${model?.parentModel}</g:link></td>

                </tr>
            </tbody>
        </table>
    </div>

    <div style="margin-top:20px;">
        <table style="width:900;">
            <tr>
                <td>
                    <span style="color:#006DBA;font-size:14px;font-weight:bold;margin:0.8em 0pt 0.3em;">Property List</span>
                    <span class="menuButton"><g:link class="create" controller="modelProperty" params="['model.id':model?.id]" action="create">New Property</g:link></span>
                    <%
                        def modelPropertyList = model.modelProperties.sort {p1, p2 ->
                            def val1 = p1."${modelPropertySortProp}";
                            def val2 = p2."${modelPropertySortProp}";
                            return modelPropertySortOrder == "asc" ? (val2 < val1 ? 1 : -1) : (val2 > val1 ? 1 : -1);
                        }
                        def modelPropertySortLinkParams = [
                                'modelDatasourceSortProp': modelDatasourceSortProp, 'modelDatasourceSortOrder': modelDatasourceSortOrder,
                                'modelRelationSortProp': modelRelationSortProp, 'modelRelationSortOrder': modelRelationSortOrder,
                                'modelOpertionSortProp': modelOpertionSortProp, 'modelOpertionSortOrder': modelOpertionSortOrder
                        ];

                        def mpNameSortLinkParams = ['modelPropertySortProp': 'name',
                                'modelPropertySortOrder': modelPropertySortProp == 'name' && modelPropertySortOrder == 'asc' ? 'desc' : 'asc'];
                        def mpTypeSortLinkParams = ['modelPropertySortProp': 'type',
                                'modelPropertySortOrder': modelPropertySortProp == 'type' && modelPropertySortOrder == 'asc' ? 'desc' : 'asc'];
                        def mpBlankSortLinkParams = ['modelPropertySortProp': 'blank',
                                'modelPropertySortOrder': modelPropertySortProp == 'blank' && modelPropertySortOrder == 'asc' ? 'desc' : 'asc'];
                        def mpDsSortLinkParams = ['modelPropertySortProp': 'propertyDatasource',
                                'modelPropertySortOrder': modelPropertySortProp == 'propertyDatasource' && modelPropertySortOrder == 'asc' ? 'desc' : 'asc'];
                        def mpDynamicDsSortLinkParams = ['modelPropertySortProp': 'propertySpecifyingDatasource',
                                'modelPropertySortOrder': modelPropertySortProp == 'propertySpecifyingDatasource' && modelPropertySortOrder == 'asc' ? 'desc' : 'asc'];

                        mpNameSortLinkParams.putAll(modelPropertySortLinkParams);
                        mpTypeSortLinkParams.putAll(modelPropertySortLinkParams);
                        mpBlankSortLinkParams.putAll(modelPropertySortLinkParams);
                        mpDsSortLinkParams.putAll(modelPropertySortLinkParams);
                        mpDynamicDsSortLinkParams.putAll(modelPropertySortLinkParams);

                    %>
                    <div class="list">
                        <table>
                            <thead>
                                <tr>
                                    <br>
                                    <th class="${modelPropertySortProp == 'name' ? 'sorted ' + modelPropertySortOrder : ''}">
                                        <g:link action="show" id="${model.id}" params="${mpNameSortLinkParams}">Name</g:link>
                                    </th>
                                    <th class="${modelPropertySortProp == 'type' ? 'sorted ' + modelPropertySortOrder : ''}">
                                        <g:link action="show" id="${model.id}" params="${mpTypeSortLinkParams}">Type</g:link>
                                    </th>
                                    <th class="${modelPropertySortProp == 'blank' ? 'sorted ' + modelPropertySortOrder : ''}">
                                        <g:link action="show" id="${model.id}" params="${mpBlankSortLinkParams}">Blank</g:link>
                                    </th>
                                    <th class="${modelPropertySortProp == 'propertyDatasource' ? 'sorted ' + modelPropertySortOrder : ''}">
                                        <g:link action="show" id="${model.id}" params="${mpDsSortLinkParams}">Static Datasource Name</g:link>
                                    </th>
                                    <th class="${modelPropertySortProp == 'propertySpecifyingDatasource' ? 'sorted ' + modelPropertySortOrder : ''}">
                                        <g:link action="show" id="${model.id}" params="${mpDynamicDsSortLinkParams}">Dynamic Datasource (specified in property)</g:link>
                                    </th>
                                </tr>
                            </thead>
                            <tbody>
                                <g:each in="${modelPropertyList}" status="i" var="modelProperty">
                                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                        <td><g:link action="show" id="${modelProperty.id}" controller="modelProperty">${modelProperty.name?.encodeAsHTML()}</g:link></td>
                                        <td>${modelProperty.type?.encodeAsHTML()}</td>
                                        <td>${modelProperty.blank?.encodeAsHTML()}</td>
                                        <td>${modelProperty?.propertyDatasource?.datasource?.toString()?.encodeAsHTML()}</td>
                                        <td>${modelProperty.propertySpecifyingDatasource?.encodeAsHTML()}</td>
                                    </tr>
                                </g:each>
                            </tbody>
                        </table>
                    </div>
                </td>
            </tr>
        </table></div>
    <div style="margin-top:20px;">
        <table style="width:900;">
            <tr>
                <td>
                    <span style="color:#006DBA;font-size:14px;font-weight:bold;margin:0.8em 0pt 0.3em;">Relation List</span>
                    <span class="menuButton"><g:link class="create" controller="modelRelation" params="['firstModel.id':model?.id]" action="create">New Relation</g:link></span>
                    <div class="list">
                        <%
                            def modelRelationList = [];
                            model.fromRelations.each{
                                modelRelationList.add(["id":it.id, "name":it.firstName, "to":it.secondModel, "type":it.firstCardinality + "To" + it.secondCardinality])
                            }
                            model.toRelations.each{
                                modelRelationList.add(["id":it.id, "name":it.secondName, "to":it.firstModel, "type":it.secondCardinality + "To" + it.firstCardinality])
                            }
                            modelRelationList.sort {p1, p2 ->
                                def val1 = p1."${modelRelationSortProp}".toString();
                                def val2 = p2."${modelRelationSortProp}".toString();
                                return modelRelationSortOrder == "asc" ? (val2 < val1 ? 1 : -1) : (val2 > val1 ? 1 : -1);
                            }
                            def modelRelationSortLinkParams = [
                                    'modelPropertySortProp': modelPropertySortProp, 'modelPropertySortOrder': modelPropertySortOrder,
                                    'modelDatasourceSortProp': modelDatasourceSortProp, 'modelDatasourceSortOrder': modelDatasourceSortOrder,
                                    'modelOpertionSortProp': modelOpertionSortProp, 'modelOpertionSortOrder': modelOpertionSortOrder
                            ];

                            def mrNameSortLinkParams = ['modelRelationSortProp': 'name',
                                    'modelRelationSortOrder': modelRelationSortProp == 'name' && modelRelationSortOrder == 'asc' ? 'desc' : 'asc'];
                            def mrToSortLinkParams = ['modelRelationSortProp': 'to',
                                    'modelRelationSortOrder': modelRelationSortProp == 'to' && modelRelationSortOrder == 'asc' ? 'desc' : 'asc'];
                            def mrTypeSortLinkParams = ['modelRelationSortProp': 'type',
                                    'modelRelationSortOrder': modelRelationSortProp == 'type' && modelRelationSortOrder == 'asc' ? 'desc' : 'asc'];
                                    
                            mrNameSortLinkParams.putAll(modelRelationSortLinkParams);
                            mrToSortLinkParams.putAll(modelRelationSortLinkParams);
                            mrTypeSortLinkParams.putAll(modelRelationSortLinkParams);
                        %>
                        <table><br>
                            <thead>
                                <tr>
                                    <th class="${modelRelationSortProp == 'name' ? 'sorted ' + modelRelationSortOrder : ''}">
                                        <g:link action="show" id="${model.id}" params="${mrNameSortLinkParams}">Name</g:link>
                                    </th>
                                    <th class="${modelRelationSortProp == 'to' ? 'sorted ' + modelRelationSortOrder : ''}">
                                        <g:link action="show" id="${model.id}" params="${mrToSortLinkParams}">To</g:link>
                                    </th>
                                    <th class="${modelRelationSortProp == 'type' ? 'sorted ' + modelRelationSortOrder : ''}">
                                        <g:link action="show" id="${model.id}" params="${mrTypeSortLinkParams}">Type</g:link>
                                    </th>
                                </tr>
                            </thead>
                            <tbody>
                                <g:each in="${modelRelationList}" status="i" var="modelRelation">
                                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                        <td><g:link action="show" id="${modelRelation.id}" controller="modelRelation">${modelRelation.name?.encodeAsHTML()}</g:link></td>
                                        <td><g:link action="show" id="${modelRelation.to.id}" controller="model">${modelRelation.to?.encodeAsHTML()}</g:link></td>
                                        <td>${modelRelation.type?.encodeAsHTML()}</td>
                                    </tr>
                                </g:each>
                            </tbody>
                        </table>
                    </div>
                </td>
            </tr>
        </table>
        <div style="margin-top:20px;">
            <table style="width:900;">
                <tr>
                    <td>
                        <span style="color:#006DBA;font-size:14px;font-weight:bold;margin:0.8em 0pt 0.3em;">Datasource List</span>
                        <span class="menuButton"><g:link class="create" controller="modelDatasource" params="['model.id':model?.id]" action="create">New Datasource</g:link></span>
                        <div class="list">
                            <%
                                def modelDatasourceList = model.datasources.sort {p1, p2 ->
                                    def val1 = p1."${modelDatasourceSortProp}".toString();
                                    def val2 = p2."${modelDatasourceSortProp}".toString();
                                    return modelDatasourceSortOrder == "asc" ? (val2 < val1 ? 1 : -1) : (val2 > val1 ? 1 : -1);
                                }
                                def modelDatasourceSortLinkParams = [
                                        'modelPropertySortProp': modelPropertySortProp, 'modelPropertySortOrder': modelPropertySortOrder,
                                        'modelRelationSortProp': modelRelationSortProp, 'modelRelationSortOrder': modelRelationSortOrder,
                                        'modelOpertionSortProp': modelOpertionSortProp, 'modelOpertionSortOrder': modelOpertionSortOrder
                                ];

                                def mdDatasourceSortLinkParams = ['modelDatasourceSortProp': 'datasource',
                                        'modelDatasourceSortOrder': modelDatasourceSortProp == 'datasource' && modelDatasourceSortOrder == 'asc' ? 'desc' : 'asc'];
                                def mdMasterSortLinkParams = ['modelDatasourceSortProp': 'master',
                                        'modelDatasourceSortOrder': modelDatasourceSortProp == 'master' && modelDatasourceSortOrder == 'asc' ? 'desc' : 'asc'];

                                mdDatasourceSortLinkParams.putAll(modelDatasourceSortLinkParams);
                                mdMasterSortLinkParams.putAll(modelDatasourceSortLinkParams);
                            %>
                            <table><br>
                                <thead>
                                    <tr>
                                        <th class="${modelDatasourceSortProp == 'datasource' ? 'sorted ' + modelDatasourceSortOrder : ''}">
                                            <g:link action="show" id="${model.id}" params="${mdDatasourceSortLinkParams}">Datasource</g:link>
                                        </th>
                                        <th class="${modelDatasourceSortProp == 'master' ? 'sorted ' + modelDatasourceSortOrder : ''}">
                                            <g:link action="show" id="${model.id}" params="${mdMasterSortLinkParams}">Master</g:link>
                                        </th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <g:each in="${modelDatasourceList}" status="i" var="modelDatasource">
                                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                            <td><g:link action="show" controller="modelDatasource" id="${modelDatasource.id}">${modelDatasource.datasource?.encodeAsHTML()}</g:link></td>
                                            <td>${modelDatasource.master?.encodeAsHTML()}</td>
                                        </tr>
                                    </g:each>
                                </tbody>
                            </table>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
        <div style="margin-top:20px;">
            <table style="width:900;">
                <tr>
                    <td>
                        <span style="color:#006DBA;font-size:14px;font-weight:bold;margin:0.8em 0pt 0.3em;">Operation List</span>
                        <div class="list">
                            <%
                                def modelOperationList = model.getOperations().sort {p1, p2 ->
                                    def val1 = p1."${modelDatasourceSortProp}".toString();
                                    def val2 = p2."${modelDatasourceSortProp}".toString();
                                    return modelDatasourceSortOrder == "asc" ? (val2 < val1 ? 1 : -1) : (val2 > val1 ? 1 : -1);
                                }
                                def modelOperationSortLinkParams = [
                                        'modelPropertySortProp': modelPropertySortProp, 'modelPropertySortOrder': modelPropertySortOrder,
                                        'modelRelationSortProp': modelRelationSortProp, 'modelRelationSortOrder': modelRelationSortOrder,
                                        'modelDatasourceSortProp': modelDatasourceSortProp, 'modelDatasourceSortOrder': modelDatasourceSortOrder
                                ];

                                def moNameSortLinkParams = ['modelOperationSortProp': 'name',
                                        'modelOperationSortOrder': modelOperationSortProp == 'name' && modelOperationSortOrder == 'asc' ? 'desc' : 'asc'];
                                def moDescriptionSortLinkParams = ['modelOperationSortProp': 'description',
                                        'modelOperationSortOrder': modelOperationSortProp == 'description' && modelOperationSortOrder == 'asc' ? 'desc' : 'asc'];
                                moNameSortLinkParams.putAll(modelOperationSortLinkParams);
                                moDescriptionSortLinkParams.putAll(modelOperationSortLinkParams);
                            %>
                            <table><br>
                                <thead>
                                    <tr>
                                        <th class="${modelOperationSortProp == 'name' ? 'sorted ' + modelOperationSortOrder : ''}">
                                            <g:link action="show" id="${model.id}" params="${moNameSortLinkParams}">Name</g:link>
                                        </th>
                                        <th class="${modelOperationSortProp == 'description' ? 'sorted ' + modelOperationSortOrder : ''}">
                                            <g:link action="show" id="${model.id}" params="${moDescriptionSortLinkParams}">Name</g:link>
                                        </th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <g:each in="${modelOperationList}" status="i" var="operation">
                                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                            <td>${operation.name?.encodeAsHTML()}</td>
                                            <td>${operation.description?.encodeAsHTML()}</td>
                                        </tr>
                                    </g:each>
                                </tbody>
                            </table>
                        </div>
                    </td>
                </tr>
            </table>
        </div>

        <div class="buttons" style="margin-top:30px;">
            <g:form>
                <input type="hidden" name="id" value="${model?.id}"/>
                <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
                <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
                <span class="button">
                    <%
                        if (!model.isGenerated()) {
                    %>
                    <g:actionSubmit class="generate" onclick="return confirm('Are you sure?');" value="Generate"/>
                    <%
                        }
                        else
                        {
                    %>
                    <g:actionSubmit class="generate" onclick="return confirm('Model already exists. All of the changes will be lost. Are you sure?');" value="Generate"/>
                    <%
                        }
                    %>
                </span>
            </g:form>
        </div>
    </div>
</div>
</body>
</html>

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
        <div class="list">
            <table>
                <thead>
                   <tr>
                   <br>

                        <g:sortableColumn property="name" title="Name"/>
                        <g:sortableColumn property="type" title="Type"/>

                        <g:sortableColumn property="blank" title="Blank"/>

                        <th>Static Datasource Name</th>

                        <th>Dynamic Datasource (specified in property)</th>

                    </tr>
                </thead>
                <tbody>
                    <g:each in="${model.modelProperties}" status="i" var="modelProperty">
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
</table> </div>
    <div style="margin-top:20px;">
    <table style="width:900;">
   		<tr>
   			<td>
       <span style="color:#006DBA;font-size:14px;font-weight:bold;margin:0.8em 0pt 0.3em;">Relation List</span>
       <span class="menuButton"><g:link class="create" controller="modelRelation" params="['firstModel.id':model?.id]" action="create">New Relation</g:link></span>
        <div class="list">
            <table><br>
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>To</th>
                        <th>Type</th>
                    </tr>
                </thead>
                <tbody>
                    <g:each in="${model.fromRelations}" status="i" var="modelRelation">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                            <td><g:link action="show" id="${modelRelation.id}" controller="modelRelation">${modelRelation.firstName?.encodeAsHTML()}</g:link></td>
                            <td><g:link action="show" id="${modelRelation.secondModel.id}" controller="model">${modelRelation.secondModel?.encodeAsHTML()}</g:link></td>
                            <td>${modelRelation.firstCardinality?.encodeAsHTML() + "To" + modelRelation.secondCardinality?.encodeAsHTML()}</td>
                        </tr>
                    </g:each>
                    <g:each in="${model.toRelations}" status="i" var="modelRelation">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                            <td><g:link action="show" id="${modelRelation.id}" controller="modelRelation" params ="['reverse':'true']">${modelRelation.secondName?.encodeAsHTML()}</g:link></td>
                            <td><g:link action="show" id="${modelRelation.firstModel.id}" controller="model">${modelRelation.firstModel?.encodeAsHTML()}</g:link></td>
                            <td>${modelRelation.secondCardinality?.encodeAsHTML() + "To" + modelRelation.firstCardinality?.encodeAsHTML()}</td>
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
        <span class="menuButton" ><g:link class="create" controller="modelDatasource" params="['model.id':model?.id]" action="create">New Datasource</g:link></span>
        <div class="list">
            <table ><br>
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
           <table><br>
                <thead>
                    <tr>
                        <g:sortableColumn property="name" title="Name"/>
                        <g:sortableColumn property="name" title="Description"/>
                    </tr>
                </thead>
                <tbody>
                    <g:each in="${model.getOperations()}" status="i" var="operation">
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
                if(!model.isGenerated()){
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

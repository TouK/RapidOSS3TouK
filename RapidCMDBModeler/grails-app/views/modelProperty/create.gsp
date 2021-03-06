<%@ page import="model.*" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout" />
    <title>Create ModelProperty</title>
</head>
<body>
<div class="nav">
    <%
        if (params["model.id"] != null) {
    %>
    <span class="menuButton"><a class="home" href="${createLinkTo(dir: 'model/show/' + params["model.id"])}">${Model.get(id:params["model.id"])?.name}</a></span>
    <%
        }
    %>
</div>
<div class="body">
    <h1>Create ModelProperty</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${modelProperty}">
        <div class="errors">
            <g:renderErrors bean="${modelProperty}" as="list"/>
        </div>
    </g:hasErrors>

    <h3>Adding property to a Modeled Class</h3>
    <ul>
        <li>Set a series of constraints on the property</li>
        <li>select the static datasource or the property that has the name of the dynamic datasource that will be used to retrieve the value for the property</li>
    </ul>

    <g:form action="save" method="post">
        <div class="dialog">
            <table>
                <tbody>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">Name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: modelProperty, field: 'name', 'errors')}">
                            <input type="text"  class="inputtextfield" id="name" name="name" value="${fieldValue(bean: modelProperty, field: 'name')}"/>
                        </td>
                    </tr>

                    <%
                        def modelPropertyList;
                        if (params["model.id"] != null) {
                            def mdl = Model.get(id:params["model.id"]);
                            if(!mdl)
                            {
                                modelPropertyList = [];
                            }
                            else
                            {
                                modelPropertyList = mdl.modelProperties;    
                            }
                            def modelPropertyMap = [:];
                            def modelDatasourceMap = [:];

                            for(modelProp in modelPropertyList){
                                modelPropertyMap.put(modelProp.name, modelProp);
                            }
                            def tempModel = mdl?.parentModel;
                            while(tempModel != null){
                                def parentModelProperties = tempModel.modelProperties;
                                for(prop in parentModelProperties){
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

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="propertyDatasource">Static Datasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: modelProperty, field: 'propertyDatasource', 'errors')}">
                            <g:select  class="inputtextfield" optionKey="id" from="${DatasourceName.list()}" name="datasource.id" value="${modelProperty?.propertyDatasource?.datasource?.id}" noSelection="['null':'']"></g:select>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="propertySpecifyingDatasource">Dynamic Datasource (specified in property):</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: modelProperty, field: 'propertySpecifyingDatasource', 'errors')}">
                            <g:select  class="inputtextfield" optionKey="id" from="${modelPropertyList}" name="propertySpecifyingDatasource.id" value="${modelProperty?.propertySpecifyingDatasource?.id}" noSelection="['null':'']"></g:select>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="type">Type:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: modelProperty, field: 'type', 'errors')}">
                            <g:select  class="inputtextfield" id="type" name="type" from="${modelProperty.constraints.type.inList.collect{it.encodeAsHTML()}}" value="${fieldValue(bean:modelProperty,field:'type')}"></g:select>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="defaultValue">Default Value:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: modelProperty, field: 'defaultValue', 'errors')}">
                            <input type="text"  class="inputtextfield" id="defaultValue" name="defaultValue" value="${fieldValue(bean: modelProperty, field: 'defaultValue')}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="lazy">Lazy:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: modelProperty, field: 'lazy', 'errors')}">
                            <g:checkBox name="lazy" value="${modelProperty?.lazy}"></g:checkBox>
                        </td>
                    </tr>

                    <%
                        if (params["model.id"] != null) {
                    %>
                    <input type="hidden" name="model.id" value="${params["model.id"]}"/>
                    <%
                        }
                        else {
                    %>
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="model">Model:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: modelProperty, field: 'model', 'errors')}">
                            <g:select optionKey="id" from="${Model.list()}" name="model.id" value="${modelProperty?.model?.id}"></g:select>
                        </td>
                    </tr>
                    <%
                        }
                    %>


                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="nameInDatasource">Name In Datasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: modelProperty, field: 'nameInDatasource', 'errors')}">
                            <input type="text"  class="inputtextfield" id="nameInDatasource" name="nameInDatasource" value="${fieldValue(bean: modelProperty, field: 'nameInDatasource')}"/>
                        </td>
                    </tr>

                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><input class="save" type="submit" value="Create"/></span>
        </div>
    </g:form>

    <h3>Property Constraints and Rules</h3>
    <p>Model definition starts with property definition along with constraints placed on the properties. Each property supports the following attributes:</p>
    <ul>
        <li><b>name:</b>&nbsp; Name of the property.</li>

        <li><b>type:</b> Supported types are string, number, date, boolean, float</li>
        <li><b>nameInDatasource:</b> Required if the modeled name and the name of the property in the datasource that is used to retrieve the property are not the same.</li>
        <li><b>blank:</b> true/false. Default value is <b>true</b>. When false, a value must be supplied while adding an instance of this modeled class unless a default value is supplied for the property. CRUD operations are performed on the master datasource therefore this property applies to properties defined for the master datasource.</li>
        <li><b>default:</b> Default value for a property. When defined for a property with blank=false, this property will no longer be mandatory during an add operation but can be supplied optionally.</li>
        <li><b>lazy:</b> true/false. Default value is true. When true, the property's value will NOT be retrieved from the corresponding datasource during a getObject invocation. It will be retrieved only when the attribute is accessed. When false, the value will be retrieved during a getObject invocation.</li>
    </ul>

    <p><b>NOTE1:</b> <b>blank</b> attribute applies to properties in the master datasource only. Ids cannot be blank.</p>

    <p><b>NOTE2:</b> <b>default</b> attribute applies to properties in the master datasource only. If supplied for a property with blank=false, it will no longer be required in an add operation</p>

    <p><b>NOTE3:</b> <b>lazy</b> attribute does not apply to properties listed as ids in the master datasource.</p>

</div>
</body>
</html>

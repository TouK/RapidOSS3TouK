

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Create ModelProperty</title>         
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">ModelProperty List</g:link></span>
        </div>
        <div class="body">
            <h1>Create ModelProperty</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${modelProperty}">
            <div class="errors">
                <g:renderErrors bean="${modelProperty}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" method="post" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name">Name:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:modelProperty,field:'name','errors')}">
                                    <input type="text" id="name" name="name" value="${fieldValue(bean:modelProperty,field:'name')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="datasourceName">Datasource Name:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:modelProperty,field:'datasourceName','errors')}">
                                    <%
                                        def modelDatasourceList;
                                        if(params["model.id"] != null){
                                            def mdl = Model.get(params["model.id"]);
                                            modelDatasourceList = mdl?.datasources;
                                        }
                                        else{
                                            modelDatasourceList = ModelDatasource.list();
                                        }
                                    %>
                                    <g:select optionKey="id" from="${modelDatasourceList}" name="datasourceName.id" value="${modelProperty?.datasourceName?.id}" noSelection="['null':'']"></g:select>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="propertySpecifyingDatasource">Property Specifying Datasource:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:modelProperty,field:'propertySpecifyingDatasource','errors')}">
                                    <%
                                        def modelPropertyList;
                                        if(params["model.id"] != null){
                                            def mdl = Model.get(params["model.id"]);
                                            modelPropertyList = mdl?.modelProperties;
                                        }
                                        else{
                                            modelPropertyList = ModelProperty.list();
                                        }
                                    %>
                                    <g:select optionKey="id" from="${modelPropertyList}" name="propertySpecifyingDatasource.id" value="${modelProperty?.propertySpecifyingDatasource?.id}" noSelection="['null':'']"></g:select>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="type">Type:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:modelProperty,field:'type','errors')}">
                                    <g:select id="type" name="type" from="${modelProperty.constraints.type.inList.collect{it.encodeAsHTML()}}" value="${fieldValue(bean:modelProperty,field:'type')}" ></g:select>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="blank">Blank:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:modelProperty,field:'blank','errors')}">
                                    <g:checkBox name="blank" value="${modelProperty?.blank}" ></g:checkBox>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="defaultValue">Default Value:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:modelProperty,field:'defaultValue','errors')}">
                                    <input type="text" id="defaultValue" name="defaultValue" value="${fieldValue(bean:modelProperty,field:'defaultValue')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="lazy">Lazy:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:modelProperty,field:'lazy','errors')}">
                                    <g:checkBox name="lazy" value="${modelProperty?.lazy}" ></g:checkBox>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="model">Model:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:modelProperty,field:'model','errors')}">
                                    <g:select optionKey="id" from="${Model.list()}" name="model.id" value="${modelProperty?.model?.id}" ></g:select>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="nameInDatasource">Name In Datasource:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:modelProperty,field:'nameInDatasource','errors')}">
                                    <input type="text" id="nameInDatasource" name="nameInDatasource" value="${fieldValue(bean:modelProperty,field:'nameInDatasource')}"/>
                                </td>
                            </tr> 
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><input class="save" type="submit" value="Create" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>

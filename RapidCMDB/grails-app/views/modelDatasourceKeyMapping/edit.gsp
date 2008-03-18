

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Edit ModelDatasourceKeyMapping</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">ModelDatasourceKeyMapping List</g:link></span>
            <span class="menuButton"><g:link class="create" action="create">New ModelDatasourceKeyMapping</g:link></span>
        </div>
        <div class="body">
            <h1>Edit ModelDatasourceKeyMapping</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${modelDatasourceKeyMapping}">
            <div class="errors">
                <g:renderErrors bean="${modelDatasourceKeyMapping}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <input type="hidden" name="id" value="${modelDatasourceKeyMapping?.id}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="datasource">Datasource:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:modelDatasourceKeyMapping,field:'datasource','errors')}">
                                    <g:select optionKey="id" from="${ModelDatasource.list()}" name="datasource.id" value="${modelDatasourceKeyMapping?.datasource?.id}" ></g:select>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="nameInDatasource">Name In Datasource:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:modelDatasourceKeyMapping,field:'nameInDatasource','errors')}">
                                    <input type="text" id="nameInDatasource" name="nameInDatasource" value="${fieldValue(bean:modelDatasourceKeyMapping,field:'nameInDatasource')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="property">Property:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:modelDatasourceKeyMapping,field:'property','errors')}">
                                    <g:select optionKey="id" from="${ModelProperty.findAllByModel(modelDatasourceKeyMapping?.datasource?.model)}" name="property.id" value="${modelDatasourceKeyMapping?.property?.id}" ></g:select>
                                </td>
                            </tr> 
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:actionSubmit class="save" value="Update" /></span>
                    <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>

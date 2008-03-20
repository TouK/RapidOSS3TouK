

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Create ModelDatasourceKeyMapping</title>         
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">ModelDatasourceKeyMapping List</g:link></span>
        </div>
        <div class="body">
            <h1>Create ModelDatasourceKeyMapping</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${modelDatasourceKeyMapping}">
            <div class="errors">
                <g:renderErrors bean="${modelDatasourceKeyMapping}" as="list" />
            </div>
            </g:hasErrors>

            <ul>
                <li><b>Datasource:</b>Preselected value for the datasource for which the mapping is done. No need to change.</li>
                <li><b>Property:</b> Name of the property whose value will be used while querying the datasource. This can be a transient attribute whose value is calculated.</li>
                <li><b>Name In Datasource:</b> name of the field in datasource corresponding to the property in the modeled class.</li>
            </ul>

            <g:form action="save" method="post" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="datasource">Datasource:</label>
                                </td>
                                <%
                                  def datasourceList;
                                  def datasourceValue;
                                  if(params["modelDatasource.id"] != null){
                                      datasourceList = ModelDatasource.get(params["modelDatasource.id"]);
                                      datasourceValue = params["modelDatasource.id"];
                                  }
                                  else{
                                      datasourceList = ModelDatasource.list();
                                      datasourceValue = modelDatasourceKeyMapping?.datasource?.id;
                                  }
                                %>
                                <td valign="top" class="value ${hasErrors(bean:modelDatasourceKeyMapping,field:'datasource','errors')}">
                                    <g:select optionKey="id" from="${datasourceList}" name="datasource.id" value="${datasourceValue}" ></g:select>
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
                                <%
                                    def modelPropertyList;
                                    if(params["modelDatasource.id"] != null){
                                        def modelDatasource = ModelDatasource.get(params["modelDatasource.id"]);
                                        def model = modelDatasource?.model;
                                        modelPropertyList = model?.modelProperties;
                                    }
                                    else{
                                        modelPropertyList = ModelProperty.list();
                                    }
                                %>
                                <td valign="top" class="value ${hasErrors(bean:modelDatasourceKeyMapping,field:'property','errors')}">
                                    <g:select optionKey="id" from="${modelPropertyList}" name="property.id" value="${modelDatasourceKeyMapping?.property?.id}" ></g:select>
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

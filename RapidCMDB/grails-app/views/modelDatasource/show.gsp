

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Show ModelDatasource</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">ModelDatasource List</g:link></span>
            <span class="menuButton"><g:link class="create" action="create">New ModelDatasource</g:link></span>
        </div>
        <div class="body">
            <h1>Show ModelDatasource</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
                <table>
                    <tbody>

                    
                        <tr class="prop">
                            <td valign="top" class="name">Id:</td>
                            
                            <td valign="top" class="value">${modelDatasource.id}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name">Datasource:</td>
                            
                            <td valign="top" class="value"><g:link controller="baseDatasource" action="show" id="${modelDatasource?.datasource?.id}">${modelDatasource?.datasource}</g:link></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name">Key Mappings:</td>
                            
                            <td  valign="top" style="text-align:left;" class="value">
                                <ul>
                                <g:each var="k" in="${modelDatasource.keyMappings}">
                                    <li><g:link controller="modelDatasourceKeyMapping" action="show" id="${k.id}">${k}</g:link></li>
                                </g:each>
                                </ul>
                            </td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name">Master:</td>
                            
                            <td valign="top" class="value">${modelDatasource.master}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name">Model:</td>
                            
                            <td valign="top" class="value"><g:link controller="model" action="show" id="${modelDatasource?.model?.id}">${modelDatasource?.model}</g:link></td>
                            
                        </tr>
                    
                    </tbody>
                </table>
            </div>
            <div class="buttons">
                <g:form>
                    <input type="hidden" name="id" value="${modelDatasource?.id}" />
                    <span class="button"><g:actionSubmit class="edit" value="Edit" /></span>
                    <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete" /></span>
                </g:form>
            </div>
        </div>
    </body>
</html>

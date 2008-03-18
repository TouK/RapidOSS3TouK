

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Show Model</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">Model List</g:link></span>
            <span class="menuButton"><g:link class="create" action="create">New Model</g:link></span>
        </div>
        <div class="body">
            <h1>Show Model</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
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
                    
                        <tr class="prop">
                            <td valign="top" class="name">Datasources:</td>
                            
                            <td  valign="top" style="text-align:left;" class="value">
                                <ul>
                                <g:each var="d" in="${model.datasources}">
                                    <li><g:link controller="modelDatasource" action="show" id="${d.id}">${d}</g:link></li>
                                </g:each>
                                </ul>
                            </td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name">Model Properties:</td>
                            
                            <td  valign="top" style="text-align:left;" class="value">
                                <ul>
                                <g:each var="m" in="${model.modelProperties}">
                                    <li><g:link controller="modelProperty" action="show" id="${m.id}">${m}</g:link></li>
                                </g:each>
                                </ul>
                            </td>
                            
                        </tr>
                    
                    </tbody>
                </table>
            </div>
            <div class="buttons">
                <g:form>
                    <input type="hidden" name="id" value="${model?.id}" />
                    <span class="button"><g:actionSubmit class="edit" value="Edit" /></span>
                    <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete" /></span>
                </g:form>
            </div>
        </div>
    </body>
</html>

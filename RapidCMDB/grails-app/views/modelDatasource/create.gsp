

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Create ModelDatasource</title>         
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">ModelDatasource List</g:link></span>
        </div>
        <div class="body">
            <h1>Create ModelDatasource</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${modelDatasource}">
            <div class="errors">
                <g:renderErrors bean="${modelDatasource}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" method="post" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="datasource">Datasource:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:modelDatasource,field:'datasource','errors')}">
                                    <g:select optionKey="id" from="${BaseDatasource.list()}" name="datasource.id" value="${modelDatasource?.datasource?.id}" ></g:select>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="master">Master:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:modelDatasource,field:'master','errors')}">
                                    <g:checkBox name="master" value="${modelDatasource?.master}" ></g:checkBox>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="model">Model:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:modelDatasource,field:'model','errors')}">
                                    <%
                                        def selectValue;
                                        def modelList;
                                        if(params["model.id"] != null){
                                            selectValue = params["model.id"];
                                            modelList = Model.get(params["model.id"]);
                                        }
                                        else{
                                            selectValue = modelDatasource?.model?.id;
                                            modelList = Model.list();
                                        }
                                    %>
                                    <g:select optionKey="id" from="${modelList}" name="model.id" value="${selectValue}" ></g:select>
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

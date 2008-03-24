<%@ page import="model.*" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Create Model</title>         
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">Model List</g:link></span>
        </div>
        <div class="body">
            <h1>Create Model</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${model}">
            <div class="errors">
                <g:renderErrors bean="${model}" as="list" />
            </div>
            </g:hasErrors>

            <h3>Steps for Defining a Modeled Object</h3>
            <ol>
                <li>Create Modeled Object</li>
                <li>Edit the Modeled Object just created to link to Datasource(s) that are used as sources for properties. Repeat for all datasources referenced.</li>
                <li>Edit the Modeled Object to add properties. Repeat until all properties are added</li>
                <li>Edit each referenced datasource and add key mappings</li>
            </ol>

            <g:form action="save" method="post" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name">Name:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:model,field:'name','errors')}">
                                    <input type="text" id="name" name="name" value="${fieldValue(bean:model,field:'name')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="parentModel">Parent Model:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:model,field:'parentModel','errors')}">
                                    <g:select optionKey="id" from="${Model.list()}" name="parentModel.id" value="${model?.parentModel?.id}" noSelection="['null':'']"></g:select>
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

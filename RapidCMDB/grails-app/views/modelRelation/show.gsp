<%@ page import="model.*" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Show ModelRelation</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton">From <a class="home" href="${createLinkTo(dir: 'model/show/' + modelRelation?.fromModel?.id)}">${modelRelation?.fromModel}</a> To <a class="home" href="${createLinkTo(dir: 'model/show/' + modelRelation?.toModel?.id)}">${modelRelation?.toModel}</a></span>
        </div>
        <div class="body">
            <h1>Show ModelRelation</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
                <table>
                    <tbody>

                    
                        <tr class="prop">
                            <td valign="top" class="name">Id:</td>
                            
                            <td valign="top" class="value">${modelRelation.id}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name">Cardinality:</td>
                            
                            <td valign="top" class="value">${modelRelation.cardinality}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name">Relation Name:</td>
                            
                            <td valign="top" class="value">${modelRelation.fromName}</td>
                            
                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name">Reverse Relation Name:</td>

                            <td valign="top" class="value">${modelRelation.toName}</td>

                        </tr>
                    
                    </tbody>
                </table>
            </div>
            <div class="buttons">
                <g:form>
                    <input type="hidden" name="id" value="${modelRelation?.id}" />
                    <span class="button"><g:actionSubmit class="edit" value="Edit" /></span>
                    <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete" /></span>
                </g:form>
            </div>
        </div>
    </body>
</html>

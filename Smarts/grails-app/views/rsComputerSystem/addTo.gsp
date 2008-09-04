

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Add To RsComputerSystem</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">RsComputerSystem List</g:link></span>
            <span class="menuButton"><g:link class="create" action="create">New RsComputerSystem</g:link></span>
        </div>
        <div class="body">
            <h1>Add To RsComputerSystem</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${rsComputerSystem}">
                <div class="errors">
                    <g:renderErrors bean="${rsComputerSystem}" as="list" />
                </div>
            </g:hasErrors>
            <g:hasErrors bean="${flash.errors}">
               <div class="errors">
                    <g:renderErrors bean="${flash.errors}"/>
                </div>
            </g:hasErrors>
            <g:form method="post" >
                <div class="dialog">
                    <table>
                        <tbody>
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="${relationName}">${rsComputerSystem.relations[relationName]?.type.getName()}:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: rsComputerSystem, field: relationName, 'errors')}">
                                    <g:select optionKey="id" from="${relatedObjectList}" name="relatedObjectId"></g:select>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <input type="hidden" name="id" value="${rsComputerSystem?.id}"/>
                    <input type="hidden" name="relationName" value="${relationName}"/>
                    <span class="button"><g:actionSubmit class="edit" value="AddRelation"/></span>
                </div>
            </g:form>
        </div>
    </body>
</html>

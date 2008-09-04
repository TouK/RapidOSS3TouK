

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Add To RsComputerSystemComponent</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">RsComputerSystemComponent List</g:link></span>
            <span class="menuButton"><g:link class="create" action="create">New RsComputerSystemComponent</g:link></span>
        </div>
        <div class="body">
            <h1>Add To RsComputerSystemComponent</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${rsComputerSystemComponent}">
                <div class="errors">
                    <g:renderErrors bean="${rsComputerSystemComponent}" as="list" />
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
                                    <label for="${relationName}">${rsComputerSystemComponent.relations[relationName]?.type.getName()}:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: rsComputerSystemComponent, field: relationName, 'errors')}">
                                    <g:select optionKey="id" from="${relatedObjectList}" name="relatedObjectId"></g:select>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <input type="hidden" name="id" value="${rsComputerSystemComponent?.id}"/>
                    <input type="hidden" name="relationName" value="${relationName}"/>
                    <span class="button"><g:actionSubmit class="edit" value="AddRelation"/></span>
                </div>
            </g:form>
        </div>
    </body>
</html>

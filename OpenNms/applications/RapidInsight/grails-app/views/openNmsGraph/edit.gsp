

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit OpenNmsGraph</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">OpenNmsGraph List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New OpenNmsGraph</g:link></span>
</div>
<div class="body">
    <h1>Edit OpenNmsGraph</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${openNmsGraph}">
        <div class="errors">
            <g:renderErrors bean="${openNmsGraph}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${openNmsGraph?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="url">url:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsGraph,field:'url','errors')}">
                            <input type="text" id="url" name="url" value="${fieldValue(bean:openNmsGraph,field:'url')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="graphOf">graphOf:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsGraph,field:'graphOf','errors')}">
                            <g:select optionKey="id" from="${OpenNmsObject.list()}" name="graphOf.id" value="${openNmsGraph?.graphOf?.id}" noSelection="['null':'']"></g:select>
                        </td>
                    </tr>
                    
                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><g:actionSubmit class="save" value="Update"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </div>
    </g:form>
</div>
</body>
</html>

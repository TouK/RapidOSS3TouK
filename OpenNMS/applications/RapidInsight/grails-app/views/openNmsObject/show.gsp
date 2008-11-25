

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show OpenNmsObject</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">OpenNmsObject List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New OpenNmsObject</g:link></span>
</div>
<div class="body">
    <h1>Show OpenNmsObject</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <div class="dialog">
        <table>
            <tbody>

                
                <tr class="prop">
                    <td valign="top" class="name">id:</td>
                    
                    <td valign="top" class="value">${openNmsObject.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">openNmsId:</td>
                    
                    <td valign="top" class="value">${openNmsObject.openNmsId}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">graphs:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="g" in="${openNmsObject.graphs}">
                                <li><g:link controller="openNmsGraph" action="show" id="${g.id}">${g}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${openNmsObject?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>

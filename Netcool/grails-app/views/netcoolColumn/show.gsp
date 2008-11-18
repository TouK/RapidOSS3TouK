
<%@ page import="datasource.NetcoolColumn" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout" />
    <title>Show NetcoolColumn</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">NetcoolColumn List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New NetcoolColumn</g:link></span>
</div>
<div class="body">
    <h1>Show NetcoolColumn</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:if test="${flash.errors}">
        <div class="errors">
            <ul>
                <g:each var="error" in="${flash?.errors}">
                    <li>${error}</li>
                </g:each>
            </ul>
        </div>
    </g:if>
    <div class="dialog">
        <table>
            <tbody>

                
                <tr class="prop">
                    <td valign="top" class="name">id:</td>
                    
                    <td valign="top" class="value">${netcoolColumn.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">netcoolName:</td>
                    
                    <td valign="top" class="value">${netcoolColumn.netcoolName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isDeleteMarker:</td>
                    
                    <td valign="top" class="value">${netcoolColumn.isDeleteMarker}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">localName:</td>
                    
                    <td valign="top" class="value">${netcoolColumn.localName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">type:</td>
                    
                    <td valign="top" class="value">${netcoolColumn.type}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${netcoolColumn?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>

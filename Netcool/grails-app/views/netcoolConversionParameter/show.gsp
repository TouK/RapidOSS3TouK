
<%@ page import="datasource.NetcoolConversionParameter" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show NetcoolConversionParameter</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">NetcoolConversionParameter List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New NetcoolConversionParameter</g:link></span>
</div>
<div class="body">
    <h1>Show NetcoolConversionParameter</h1>
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
                    
                    <td valign="top" class="value">${netcoolConversionParameter.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">keyField:</td>
                    
                    <td valign="top" class="value">${netcoolConversionParameter.keyField}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">columnName:</td>
                    
                    <td valign="top" class="value">${netcoolConversionParameter.columnName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">conversion:</td>
                    
                    <td valign="top" class="value">${netcoolConversionParameter.conversion}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">value:</td>
                    
                    <td valign="top" class="value">${netcoolConversionParameter.value}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${netcoolConversionParameter?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>

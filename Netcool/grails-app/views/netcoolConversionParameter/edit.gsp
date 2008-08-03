
<%@ page import="datasource.NetcoolConversionParameter" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Edit NetcoolConversionParameter</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">NetcoolConversionParameter List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New NetcoolConversionParameter</g:link></span>
</div>
<div class="body">
    <h1>Edit NetcoolConversionParameter</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${netcoolConversionParameter}">
        <div class="errors">
            <g:renderErrors bean="${netcoolConversionParameter}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${netcoolConversionParameter?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="keyField">keyField:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolConversionParameter,field:'keyField','errors')}">
                            <input type="text" id="keyField" name="keyField" value="${fieldValue(bean:netcoolConversionParameter,field:'keyField')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="columnName">columnName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolConversionParameter,field:'columnName','errors')}">
                            <input type="text" id="columnName" name="columnName" value="${fieldValue(bean:netcoolConversionParameter,field:'columnName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="conversion">conversion:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolConversionParameter,field:'conversion','errors')}">
                            <input type="text" id="conversion" name="conversion" value="${fieldValue(bean:netcoolConversionParameter,field:'conversion')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="value">value:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolConversionParameter,field:'value','errors')}">
                            <input type="text" id="value" name="value" value="${fieldValue(bean:netcoolConversionParameter,field:'value')}" />
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

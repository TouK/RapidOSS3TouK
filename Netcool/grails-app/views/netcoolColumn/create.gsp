
<%@ page import="datasource.NetcoolColumn" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create NetcoolColumn</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">NetcoolColumn List</g:link></span>
</div>
<div class="body">
    <h1>Create NetcoolColumn</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${netcoolColumn}">
        <div class="errors">
            <g:renderErrors bean="${netcoolColumn}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form action="save" method="post" >
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="netcoolName">netcoolName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolColumn,field:'netcoolName','errors')}">
                            <input type="text" id="netcoolName" name="netcoolName" value="${fieldValue(bean:netcoolColumn,field:'netcoolName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isDeleteMarker">isDeleteMarker:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolColumn,field:'isDeleteMarker','errors')}">
                            <g:checkBox name="isDeleteMarker" value="${netcoolColumn?.isDeleteMarker}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="localName">localName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolColumn,field:'localName','errors')}">
                            <input type="text" id="localName" name="localName" value="${fieldValue(bean:netcoolColumn,field:'localName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="type">type:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolColumn,field:'type','errors')}">
                            <input type="text" id="type" name="type" value="${fieldValue(bean:netcoolColumn,field:'type')}"/>
                        </td>
                    </tr>
                    
                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><input class="save" type="submit" value="Create"/></span>
        </div>
    </g:form>
</div>
</body>
</html>

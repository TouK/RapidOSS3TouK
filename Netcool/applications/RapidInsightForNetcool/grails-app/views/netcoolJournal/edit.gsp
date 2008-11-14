

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit NetcoolJournal</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">NetcoolJournal List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New NetcoolJournal</g:link></span>
</div>
<div class="body">
    <h1>Edit NetcoolJournal</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${netcoolJournal}">
        <div class="errors">
            <g:renderErrors bean="${netcoolJournal}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${netcoolJournal?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="keyfield">keyfield:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolJournal,field:'keyfield','errors')}">
                            <input type="text" id="keyfield" name="keyfield" value="${fieldValue(bean:netcoolJournal,field:'keyfield')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="servername">servername:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolJournal,field:'servername','errors')}">
                            <input type="text" id="servername" name="servername" value="${fieldValue(bean:netcoolJournal,field:'servername')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="chrono">chrono:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolJournal,field:'chrono','errors')}">
                            <input type="text" id="chrono" name="chrono" value="${fieldValue(bean:netcoolJournal,field:'chrono')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsdatasource">rsdatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolJournal,field:'rsdatasource','errors')}">
                            <input type="text" id="rsdatasource" name="rsdatasource" value="${fieldValue(bean:netcoolJournal,field:'rsdatasource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="serverserial">serverserial:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolJournal,field:'serverserial','errors')}">
                            <input type="text" id="serverserial" name="serverserial" value="${fieldValue(bean:netcoolJournal,field:'serverserial')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="text">text:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolJournal,field:'text','errors')}">
                            <input type="text" id="text" name="text" value="${fieldValue(bean:netcoolJournal,field:'text')}"/>
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

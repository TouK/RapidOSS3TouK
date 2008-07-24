

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create NetcoolJournal</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">NetcoolJournal List</g:link></span>
</div>
<div class="body">
    <h1>Create NetcoolJournal</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${netcoolJournal}">
        <div class="errors">
            <g:renderErrors bean="${netcoolJournal}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form action="save" method="post" >
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
                            <label for="connectorname">connectorname:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolJournal,field:'connectorname','errors')}">
                            <input type="text" id="connectorname" name="connectorname" value="${fieldValue(bean:netcoolJournal,field:'connectorname')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="serverserial">serverserial:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolJournal,field:'serverserial','errors')}">
                            <input type="text" id="serverserial" name="serverserial" value="${fieldValue(bean:netcoolJournal,field:'serverserial')}"/>
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
            <span class="button"><input class="save" type="submit" value="Create"/></span>
        </div>
    </g:form>
</div>
</body>
</html>

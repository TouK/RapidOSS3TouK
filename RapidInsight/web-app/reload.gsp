<html>
<head>
    <meta name="layout" content="adminLayout"/>
</head>
<body>
<div class="nav">
    <span class="menuButton"><g:link class="refresh" action="reload" controller="application" params="['targetURI':'/reload.gsp']" onclick="return confirm('Are you sure?');">Reload App.</g:link></span>
    <span class="menuButton"><g:link class="refresh" action="reloadViewsAndControllers" controller="application" params="['targetURI':'/reload.gsp']">Reload Web UI</g:link></span>
</div>
<g:render template="/common/messages" model="[flash:flash]"></g:render>
<div class="body">
	<br>
    <g:form method="post" controller="script">
        <input type="hidden" name="id" value="reloadOperations"/>
        <input type="hidden" name="targetURI" value="/reload.gsp"/>
        <table>
            <tr class="prop">
                <td valign="top" width="0%">
                    <%
                        def sortedModels = org.codehaus.groovy.grails.commons.ApplicationHolder.application.getDomainClasses().clazz;
                        sortedModels = sortedModels.name;
                        java.util.Collections.sort(sortedModels);
                    %>
                    <g:select class="inputtextfield" name="domainClassName" from="${sortedModels}" noSelection="['':'-Select A Model To Reload Operation-']" style="width:300px" size="25"></g:select>
                </td>
                <td width="0%" align="center" valign="top">
                    <span class="button"><g:actionSubmit class="refresh" value="Reload" action="run"/></span>
                </td>
                <td width="100%">
                </td>
            </tr>
        </table>
    </g:form>
</div>
</body>
</html>




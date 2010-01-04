<%@ page import="auth.Role; java.text.SimpleDateFormat" %><html>
<jsec:lacksRole name="${Role.ADMINISTRATOR}">
     <%
         response.sendRedirect("/RapidSuite/auth/unauthorized");
     %>
</jsec:lacksRole>
<head>
    <meta name="layout" content="adminLayout"/>
</head>
<body>
<div class="nav">
        <span class="menuButton"><rui:link class="list" url="versionControl.gsp">Modification List</rui:link></span>
</div>
<g:render template="/common/messages" model="[flash:flash]"></g:render>
<%
    def utility = application.RapidApplication.getUtility("VersionControlUtility");
    def changesAsMap = utility.getChangesAsMap(utility.getChangeSetDir(params.changeSet))
    def df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
%>
<div class="body">
    <table>
        <tbody>
                <tr class="prop">
                    <td valign="top" class="name">Name:</td>
                    <td valign="top" class="value">${changesAsMap.name}</td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name">Comment:</td>
                    <td valign="top" class="value">${changesAsMap.comment}</td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name">IsValid:</td>
                    <td valign="top" class="value">${changesAsMap.isValid}</td>
                </tr>
        </tbody>
    </table>

    <div>
        <span style="color:#006DBA;font-size:14px;font-weight:bold;margin:0.8em 0pt 0.3em;">Modified Files</span>
    </div>

    <table>
        <thead>
            <tr>
                <th>Path</th>
                <th>Operation</th>
                <th>LastModifiedAt</th>
            </tr>
        </thead>
        <tbody>
            <g:each in="${changesAsMap.changes}" status="i" var="changedFile">
                <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                    <td>${changedFile.path}</td>
                    <td>${changedFile.operation}</td>
                    <td>${df.format(changedFile.modifiedAt)}</td>
                </tr>
            </g:each>
        </tbody>
    </table>
</div>
</body>
</html>




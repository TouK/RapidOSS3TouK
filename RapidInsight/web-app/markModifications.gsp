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
    def utility = application.RsApplication.getUtility("VersionControlUtility");
%>
<div class="body">
    <g:form method="post" controller="script">
        <input type="hidden" id="id" name="id" value="markModifications"/>
        <input type="hidden" id="forceMark" name="forceMark" value="true"/>
        <div class="dialog">
            <table>
                <tbody>
                     <tr class="prop">
                        <td valign="top" class="name">
                            <label for="comment">Comment:</label>
                        </td>
                        <td valign="top" class="value">
                            <textarea cols="200" rows="40" id="comment" name="comment"></textarea>
                        </td>
                     </tr>

                </tbody>
            </table>
        </div>

        <div class="buttons">
            <span class="button"><g:actionSubmit class="Mark" value="Mark"  action="run"/></span>
        </div>
    </g:form>
</div>
</body>
</html>




<%@ page import="java.text.SimpleDateFormat" %><html>
<head>
    <meta name="layout" content="adminLayout"/>
</head>
<body>
<div class="nav">
    <span class="menuButton"><rui:link url="markModifications.gsp" params="[:]">Mark Modifications</rui:link></span>
</div>
<g:render template="/common/messages" model="[flash:flash]"></g:render>
<%
    def utility = application.RsApplication.getUtility("VersionControlUtility");
    def changeSets = utility.getChangeSetList();
    def df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
%>
<div class="body">
	<table>
        <thead>
            <tr>
                <th>Date</th>
                <th>Comment</th>
                <th>IsValid</th>
            </tr>
        </thead>
        <tbody>
            <g:each in="${changeSets}" status="i" var="changeSet">
                <tr class="${(i % 2) == 0 ? 'odd' : 'even'}" style="${!changeSet.isValid?"color:red;":""}">
                    <td style="${!changeSet.isValid?"color:red;font-weight:bolder;":""}">
                        <%
                            if(changeSet.isValid){
                        %>
                        <rui:link style="text-decoration:underline" url="getModificationDetails.gsp" params="[changeSet:changeSet.file.name]">${df.format(changeSet.date)}</rui:link>
                        <%
                            }else{
                        %>
                        ${df.format(changeSet.date)}
                        <%
                            }
                        %>
                    </td>
                    <td  style="${!changeSet.isValid?"color:red;font-weight:bolder;":""}">${changeSet.isValid?utility.getChangesAsMap(changeSet.file).comment:""}</td>
                    <td  style="${!changeSet.isValid?"color:red;font-weight:bolder;":""}">${changeSet.isValid}</td>
                </tr>
            </g:each>
        </tbody>
    </table>
</div>
</body>
</html>




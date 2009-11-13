<%@ page import="auth.Role; java.text.SimpleDateFormat" %><html>
<jsec:lacksRole name="${Role.ADMINISTRATOR}">
     <%
         response.sendRedirect("/RapidSuite/auth/unauthorized");
     %>
</jsec:lacksRole>
<head>
    <meta name="layout" content="adminLayout"/>
    <style type="text/css">
        .body .export, .body .import{
            padding-top:5px;
            padding-bottom:5px;
            font-size: 11px;
        }
        .invalidRow{
            color:red;
            font-weight:bolder;
        }
        .invalidRow td{
            color:red;
            font-weight:bolder;                
        }
    </style>
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
                <th></th>
                <th></th>
            </tr>
        </thead>
        <tbody>
            <g:each in="${changeSets}" status="i" var="changeSet">
                <tr class="${(i % 2) == 0 ? 'odd' : 'even'} ${!changeSet.isValid?"invalidRow":""}" style="">
                    <td>
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
                    <td>${changeSet.isValid?utility.getChangesAsMap(changeSet.file).comment:""}</td>
                    <td>${changeSet.isValid}</td>
                    <td><span class="button"><rui:link class="export" url="script/run/getModifications" params="[date:changeSet.file.name]">Changes Since</rui:link></span></td>
                    <td><span class="button"><rui:link class="import" url="script/run/getModifications" params="[date:changeSet.file.name, direction:"till"]">Changes Till</rui:link></span></td>
                </tr>
            </g:each>
        </tbody>
    </table>
</div>
</body>
</html>




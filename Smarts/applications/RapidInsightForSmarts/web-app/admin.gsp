<%@ page import="com.ifountain.rcmdb.datasource.ListeningAdapterManager; connector.SmartsListeningTopologyConnector" %><html>
<head>
    <title>RapidInsight For Smarts Admin UI</title>
    <rui:stylesheet dir="js/yui/assets/skins/sam" file="skin.css"></rui:stylesheet>
    <link rel="stylesheet" href="${createLinkTo(dir: 'css', file: 'main.css')}"/>
    <link rel="stylesheet" href="${createLinkTo(file: 'admin.css')}"/>
    <jsec:isNotLoggedIn>
        <g:javascript>window.location='auth/login?targetUri=/admin.gsp'</g:javascript>
    </jsec:isNotLoggedIn>
    <jsec:lacksRole name="Administrator">
    	<meta http-equiv="REFRESH" content="0;url=${createLinkTo(dir: 'auth/unauthorized')}">
    </jsec:lacksRole>
</head>
<body class="yui-skin-sam admin">
<div style="padding-left:10px;padding-top:5px;"><img src="images/RapidInsight-white.png"></div>
<div class="nav"><h1 style="display:inline">RapidInsight For Smarts</h1><span class="menuButton"><a href="auth/logout?targetUri=/admin.gsp" class="logout">Logout</a></span></div>
<p style="margin-left:20px;width:80%">This is the basic administration UI where you can define your connections ........</p>
<br>
<p/>
<p/>
<div class="yui-navset">
    <ul class="yui-nav">
        <li class="selected"><a href="${createLinkTo(file: 'admin.gsp')}"><em>Connectors</em></a></li>
        <li><g:link action="list" controller="script"><em>Scripts</em></g:link></li>
        <li><a href="${createLinkTo(file: 'reload.gsp')}"><em>Reload</em></a></li>
        <li><g:link action="list" controller="rsUser"><em>Users</em></g:link></li>
    </ul>
    <div style="margin:20px 15px 10px;">
        <div class="nav">
         &nbsp
        </div>
        <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
        </g:if>
        <g:hasErrors bean="${flash.errors}">
           <div class="errors">
                <g:renderErrors bean="${flash.errors}"/>
            </div>
        </g:hasErrors>
        <div class="body">
            <br><br>
            <div style="margin-top:20px;">
                <table style="width:900;">
                    <tr>
                        <td>
                            <span style="color:#006DBA;font-size:14px;font-weight:bold;margin:0.8em 0pt 0.3em;">Smarts Connection Configuration Data List</span>
                            <span class="menuButton"><g:link class="create" controller="smartsConnectionTemplate" action="create">New ConnectionTemplate</g:link></span>
                            <div class="list">
                                <table>
                                    <thead>
                                        <tr>
                                            <th>Name</th>
                                            <th>Broker</th>
                                            <th></th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <g:each in="${connection.SmartsConnectionTemplate.list()}" status="i" var="smartsConnectionTemplate">
                                            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                                <td><g:link action="edit" controller="smartsConnectionTemplate" id="${smartsConnectionTemplate.id}">${smartsConnectionTemplate.name?.encodeAsHTML()}</g:link></td>
                                                <td>${smartsConnectionTemplate?.broker?.encodeAsHTML()}</td>
                                            </tr>
                                        </g:each>
                                    </tbody>
                                </table>
                            </div>
                        </td>
                    </tr>
                </table>
            </div>
            <div style="margin-top:20px;">
                <table style="width:900;">
                    <tr>
                        <td>
                            <span style="color:#006DBA;font-size:14px;font-weight:bold;margin:0.8em 0pt 0.3em;">Property List</span>
                            <span class="menuButton"><g:link class="create" controller="smartsConnector" action="create">New Connector</g:link></span>
                            <div class="list">
                                <table>
                                    <thead>
                                        <tr>
                                            <th>Name</th>
                                            <th>Domain</th>
                                            <th>ConnectionTemplate</th>
                                            <th>Type</th>
                                            <th></th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <g:each in="${connector.SmartsConnector.list()}" status="i" var="smartsConnector">
                                            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                                <td><g:link action="show" controller="smartsConnector" id="${smartsConnector.id}">${smartsConnector.name?.encodeAsHTML()}</g:link></td>
                                                <td>${smartsConnector?.ds?.connection?.domain?.encodeAsHTML()}</td>
                                                <td><g:link action="show" controller="smartsConnectionTemplate" id="${smartsConnector.connectionTemplate.id}">${smartsConnector.connectionTemplate.name?.encodeAsHTML()}</g:link></td>
                                                <td>${smartsConnector instanceof SmartsListeningTopologyConnector?"Topology":"Notification"}</td>
                                                <%
                                                    def isSubscribed = ListeningAdapterManager.getInstance().isSubscribed(smartsConnector.ds);
                                                    if (isSubscribed) {
                                                %>
                                                <td><g:link action="stopConnector" controller="smartsConnector" id="${smartsConnector.id}" class="stop">Stop</g:link></td>
                                                <%
                                                    }
                                                    else {
                                                %>
                                                <td><g:link action="startConnector" controller="smartsConnector" id="${smartsConnector.id}" class="start">Start</g:link></td>
                                                <%
                                                    }
                                                %>
                                            </tr>
                                        </g:each>
                                    </tbody>
                                </table>
                            </div>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</div>
</body>
</html>
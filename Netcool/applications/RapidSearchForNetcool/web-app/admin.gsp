<%@ page import="script.CmdbScript; connection.NetcoolConnection" %><html>
<head>
    <title>RapidInsight For Netcool Admin UI</title>
    <rui:stylesheet dir="js/yui/assets/skins/sam" file="skin.css"></rui:stylesheet>
    <link rel="stylesheet" href="${createLinkTo(dir: 'css', file: 'main.css')}"/>
    <link rel="stylesheet" href="${createLinkTo(file: 'admin.css')}"/>
    <jsec:isNotLoggedIn>
        <g:javascript>window.location='auth/login?targetUri=/admin.gsp'</g:javascript>
    </jsec:isNotLoggedIn>
</head>
<body class="yui-skin-sam admin">
<div><img src="images/RapidInsight.png"></div>
<div class="nav"><h1 style="display:inline">RapidInsight For Netcool</h1><span class="menuButton"><a href="auth/logout?targetUri=/admin.gsp" class="logout">Logout</a></span></div>
<p style="margin-left:20px;width:80%">This is the basic administration UI where you can define your connections, create NetcoolEvent and NetcoolJournal models, import conversion parameters.</p>
<br>
<p/>
<p/>
<div class="yui-navset">
    <ul class="yui-nav">
        <li class="selected"><a href="${createLinkTo(file: 'admin.gsp')}"><em>Connectors</em></a></li>
        <li><a href="${createLinkTo(file: 'synchronize.gsp')}"><em>Configuration Sync.</em></a></li>
        <li><g:link action="list" controller="script"><em>Scripts</em></g:link></li>
        <li><g:link action="list" controller="netcoolConversionParameter"><em>Conversion Parameters</em></g:link></li>
        <li><a href="${createLinkTo(file: 'reload.gsp')}"><em>Reload</em></a></li>
    </ul>
    <div style="margin:20px 15px 10px;">
        <div class="nav">
            <span class="menuButton"><g:link class="create" action="create" controller="netcoolConnection">New Connector</g:link></span>
        </div>
        <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
        </g:if>
        <g:hasErrors>
            <div class="errors">
                <g:renderErrors bean="${flash.errors}"/>
            </div>
        </g:hasErrors>
        <div class="body">
        	<br><br>
	        <div class="list">
	            <table>
	                <thead>
	                    <tr>
	                        <th>Name</th>
	                        <th>Host</th>
	                        <th>Port</th>
	                        <th></th>
	                    </tr>
	                </thead>
	                <tbody>

	                    <g:each in="${NetcoolConnection.list()}" status="i" var="netcoolConnection">
	                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

	                            <td><g:link action="show" controller="netcoolConnection" id="${netcoolConnection.id}">${netcoolConnection.name?.encodeAsHTML()}Connector</g:link></td>

	                            <td>${netcoolConnection.host?.encodeAsHTML()}</td>

	                            <td>${netcoolConnection.port?.encodeAsHTML()}</td>
	                            <%
	                                def connName = "${netcoolConnection.name}Connector";
	                                def connScript = CmdbScript.get(name: connName);
	                                if (connScript?.enabled) {
	                            %>
	                            <td><g:link action="stopConnector" controller="netcoolConnection" id="${netcoolConnection.id}" class="stop">Stop</g:link></td>
	                            <%
	                                }
	                                else {
	                            %>
	                            <td><g:link action="startConnector" controller="netcoolConnection" id="${netcoolConnection.id}" class="start">Start</g:link></td>
	                            <%
	                                }
	                            %>

	                        </tr>
	                    </g:each>
	                </tbody>
	            </table>
	        </div>
        </div>
    </div>
</div>
</body>
</html>
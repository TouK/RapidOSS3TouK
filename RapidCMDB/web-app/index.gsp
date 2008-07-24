<%@ page import="model.*" %>
<%@ page import="connection.*" %>
<%@ page import="datasource.*" %>
<%@ page import="script.*" %>
<html>
<head>
    <title>RapidCMDB</title>
    <meta name="layout" content="main"/>
    <jsec:isNotLoggedIn>
	  <g:javascript>window.location='auth/login?targetUri=/index.gsp'</g:javascript>
	</jsec:isNotLoggedIn>
</head>
<body>

<div class="nav"><h1 style="display:inline">RapidCMDB Admin UI</h1><span class="menuButton"><a href="/RapidCMDB/auth/logout?targetUri=/index.gsp" class="logout">Logout</a></span></div>
<p style="margin-left:20px;width:80%">This is the basic user interface through which you can work with your modeled classes.</p>
<br>
<p/>
<p/>
<br>
<br>

<div class="front">
    <table><tr><th width="50%">Modeled Classes</th></tr>
        <tr><td><p style="margin-left:20px;"></p>
            <br>
            <div class="dialog" style="margin-left:20px;">
                <ul style="margin-left:25px;">
                    <%
                        grailsApplication.domainClasses.each {
                            def mc = it.metaClass;
                            if (mc.getMetaProperty("propertyConfiguration") != null) {
                    %>
                    <li class="controller"><g:link controller="${it.logicalPropertyName}">${mc.getTheClass().name}</g:link></li>
                    <%
                            }
                        }
                    %>
                </ul>
            </div>
        </td>
    </table></div>

</body>
</html>
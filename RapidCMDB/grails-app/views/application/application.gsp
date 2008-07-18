<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Application Actions</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir: 'admin.gsp')}">Home</a></span>
    <span class="menuButton"><a class="logout" href="${createLinkTo(dir: 'auth/logout')}">Logout</a></span>
</div>
<div class="body">
    <h1>Application Actions</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:if test="${flash.errors}">
        <div class="errors">
            <ul>
                <g:each var="error" in="${flash?.errors}">
                    <li>${error}</li>
                </g:each>
            </ul>
        </div>
    </g:if>
    <g:form method="post">
	    <div class="buttons">
	        <span class="button"><g:actionSubmit class="refresh" onclick="return confirm('Are you sure?');" value="Reload"/></span>
	        <span class="button"><g:actionSubmit class="export" onclick="return confirm('Are you sure?');" value="ExportConfiguration"/></span>
	        <span class="button"><g:actionSubmit class="import" onclick="return confirm('All configuration data will be replaced and application will restart. Are you sure?');" value="ImportConfiguration"/></span>
	    </div>
    </g:form>
</div>
</body>
</html>

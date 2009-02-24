<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
     <meta name="layout" content="adminLayout" />
    <title>Application Actions</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir: 'admin.gsp')}">Home</a></span>
</div>
<div class="body">
    <h1>Application Actions</h1>
    <g:render template="/common/messages" model="[flash:flash]"></g:render>
    <g:form method="post">
	    <div class="buttons">
	        <span class="button"><g:actionSubmit class="refresh" onclick="return confirm('Are you sure?');" value="ReloadControllers"/></span>
	        <span class="button"><g:actionSubmit class="refresh" value="ReloadViews"/></span>
            <span class="button"><g:actionSubmit class="refresh" onclick="return confirm('Are you sure?');" value="Reload"/></span>
        </div>
    </g:form>
</div>
</body>
</html>

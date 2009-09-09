<html>
<head>
<title>RIMobile</title>
<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;"/>
<link rel="apple-touch-icon" href="../images/mobile/iui-logo-touch-icon.png" />
<meta name="apple-touch-fullscreen" content="YES" />
<style type="text/css" media="screen">@import "../css/mobile/iui.css";</style>
<script type="application/x-javascript" src="../js/mobile/iui.js"></script>
</head>

<body>
	<%-------------------------------------------------------------------------------
										<Toolbar>
	 -------------------------------------------------------------------------------%>
    <div class="toolbar" id ="toolbar" name="toolbar">
        <h1 id="pageTitle"></h1>
        <a id="backButton" class="button" href="#">Back</a>
        <a id="logoutButton" class="button" href="../auth/logout" target="_self">Logout</a>
        <a id="searchButton" class="button" href="#searchForm" style="display: none;" title="Search">Search</a>
    </div>

    <form id="searchForm" name="searchForm" class="dialog" method="post" action="event.gsp">
        <fieldset>
            <h1>Event Search</h1>
            <a class="button leftButton" type="cancel">Cancel</a>
            <a class="button blueButton" type="submit">Search</a>
            <input id="search" type="text" name="query"/>
        </fieldset>
    </form>
    <%-------------------------------------------------------------------------------
										</Toolbar>
	 -------------------------------------------------------------------------------%>

	<%-------------------------------------------------------------------------------
										<Home Page>
	 -------------------------------------------------------------------------------%>
    <ul id="home" title="RI Mobile" selected="true">
		<li><a href="queries.gsp?filterType=event" onclick="outFromMainPage();return false" target="_open"> Events </a></li>
	</ul>
	<%-------------------------------------------------------------------------------
										</Home Page>
	 -------------------------------------------------------------------------------%>

    <rui:include template="mobile/errors.gsp" model="${binding.variables}"></rui:include>
    <rui:include template="mobile/commonScripts.gsp"></rui:include>

</body>
</html>

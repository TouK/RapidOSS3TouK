<html>
<head>
    <title><g:layoutTitle default="Grails"/></title>
    <link rel="stylesheet" href="${createLinkTo(dir: 'css', file: 'main.css')}"/>
    <link rel="shortcut icon" href="${createLinkTo(dir: 'images', file: 'favicon.ico')}" type="image/x-icon"/>
    <g:layoutHead/>
    <g:javascript library="application"/>
</head>
<body onload="${pageProperty(name: 'body.onload')}">
    <div id="spinner" class="spinner" style="display:none;">
        <img src="${createLinkTo(dir: 'images', file: 'spinner.gif')}" alt="Spinner"/>
    </div>
    <div class="logo"><img src="${createLinkTo(dir: 'images', file: 'RapidCMDB.jpg')}" alt="Grails"/></div>
    <div>
        <div id="logoutwrp" class="menuButton" style="position:absolute;top:98px;right:3px"><a class="logout" href="${createLinkTo(dir: 'auth/logout')}">Logout</a></div>
        <g:layoutBody/>
    </div>
</body>
</html>
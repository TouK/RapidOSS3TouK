<%--
  Created by IntelliJ IDEA.
  User: ifountain
  Date: Aug 10, 2009
  Time: 10:36:45 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title>Login</title>
  <meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;"/>
  <meta name="apple-touch-fullscreen" content="YES" />
  <style type="text/css" media="screen">@import"../css/mobile/iui.css";</style>
</head>

<body>

    <div class="toolbar">
        <h1 id="pageTitle">Login</h1>
    </div>

    <g:form id="login" title="Login" action="signIn" controller="auth" class="panel" selected="true">
        <fieldset>
            <div class="row">
                <label>Username</label>
                <input id="login" type="text" name="login" value=""/>
            </div>
            <div class="row">
                <label>Password</label>
                <input id="password" type="password" name="password" value=""/>
            </div>
            <input id="targetUri" type="hidden" name="targetUri" value="/mobile/home.gsp">
        </fieldset>
    </g:form>

</body>
</html>
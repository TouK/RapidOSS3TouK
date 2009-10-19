<%--
  Created by IntelliJ IDEA.
  User: ifountain
  Date: Aug 10, 2009
  Time: 10:36:45 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="com.ifountain.rcmdb.mobile.MobileUtils" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Login</title>
    <g:if test="${MobileUtils.isIphone(request)}">
        <meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;"/>
        <meta name="apple-touch-fullscreen" content="YES"/>
        <style type="text/css" media="screen">@import"${createLinkTo(dir: 'css/mobile', file: 'iui.css')}";</style>
    </g:if>
    <g:else>
        <link rel="stylesheet" type="text/css" href="${createLinkTo(dir: 'css/mobile', file: 'simple.css')}"/>
    </g:else>

</head>

<body>

<div class="toolbar">
    <h1 id="pageTitle">ROSS</h1>
</div>

<g:if test="${MobileUtils.isIphone(request)}">
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
        <a class="whiteButton" href="javascript:{}" onclick="this.parentNode.submit(); return false;">Login</a>
        <g:if test="${params.flash != null}">
            <div style="margin-top: 20px; color:ff0000;">
                ${params.flash}
            </div>
        </g:if>
    </g:form>
</g:if>
<g:else>
    <div class="form-wrp">
        <g:form title="Login" action="signIn" controller="auth">
            <div class="row"><label>Username</label></div>
            <div class="row"><input type="text" name="login" value="" style="width:90%"/></div>
            <div class="row"><label>Password</label></div>
            <div class="row"><input type="password" name="password" value="" style="width:90%"/></div>
            <input type="hidden" name="targetUri" value="/mobile/home.gsp"/>
            <div class="row"><input id="loginButton" type="submit" value="Login"/></div>
        </g:form>
    </div>
    <g:if test="${params.flash != null}">
        <div style="margin-top: 20px; color:ff0000;">${params.flash}</div>
    </g:if>
</g:else>

</body>
</html>
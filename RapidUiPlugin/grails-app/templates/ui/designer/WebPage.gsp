<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <g:render template="/layouts/layoutHeader"></g:render>
    <g:layoutHead/>
</head>
<body class=" yui-skin-sam rimain">
<rui:form id="changeProfileDialog" width="35em" saveUrl="\${createLink(controller:'rsUser', action:'changeProfile', params:[format:'xml'])}" createUrl="\${createLink(controller:'rsUser', action:'changeProfileData', params:[format:'xml', username:session.username])}">
    <div>
        <div class="hd">Change My Profile</div>
        <div class="bd">
            <form method="POST" action="javascript://nothing">
                <table>
                    <tr><td width="50%"><label>Old Password:</label></td><td width="50%"><input type="password" name="oldPassword" style="width:175px"/></td></tr>
                    <tr><td width="50%"><label>New Password:</label></td><td width="50%"><input type="password" name="password1" style="width:175px"/></td></tr>
                    <tr><td width="50%"><label>Confirm Password:</label></td><td width="50%"><input type="password" name="password2" style="width:175px"/></td></tr>
                    <tr><td width="50%"><label>Email:</label></td><td width="50%"><input type="text" name="email" style="width:175px"/></td></tr>
                </table>
                <input type="hidden" name="username">
            </form>

        </div>
    </div>
</rui:form>

<div id="top" style="background-color:#BBD4F6;">
    <table style="height:100%" cellspacing="0" cellpadding="0"><tbody><tr>
        <td width="0%" style="padding-left:10px;padding-top:5px;padding-right:60px;">
            <img src="/RapidSuite/images/RapidInsight-blue.png">
        </td>
        <td width="100%" style="vertical-align: bottom;;">
            <div class="yui-navset">
                <ul class="yui-nav" style="border-style: none">
                    <%
                        url.tabs.each{tab->
                    %>
                    <li class="\${request.uri.toString().indexOf('${url.name}/${tab.name}.gsp') > -1 ? "selected" : ""}"><a href="\${createLinkTo(file: '${url.name}/${tab.name}.gsp')}"><em>${tab.title}</em></a></li>
                    <%
                        }
                    %>
                </ul>
            </div>
        </td>
        <td width="0%"></td>
        <td id="serverDownEl" width="0%" style="display:none">
            <img src="images/network-offline.png"/>
        </td>
        <td width="0%">
            <div style="vertical-align:bottom">
                <span id="rsUser" style="font-size:12px;font-weight:bold;color:#083772;text-align:right;margin-bottom:5px;cursor:pointer">\${session.username}</span>
                <a href="/RapidSuite/auth/logout" style="font-size:13px;font-weight:bold;color:#083772;text-align:right;text-decoration:none">Logout</a>
            </div>
        </td>
    </tr>
    </tbody></table>
</div>
<script>
YAHOO.rapidjs.ErrorManager.serverDownEvent.subscribe(function(){
YAHOO.util.Dom.setStyle(document.getElementById('serverDownEl'), 'display', '');
}, this, true);
YAHOO.rapidjs.ErrorManager.serverUpEvent.subscribe(function(){
YAHOO.util.Dom.setStyle(document.getElementById('serverDownEl'), 'display', 'none');
}, this, true);

var changeProfileDialog = YAHOO.rapidjs.Components['changeProfileDialog']
YAHOO.util.Event.addListener(document.getElementById('rsUser'), 'click', function(){
changeProfileDialog.show(YAHOO.rapidjs.component.Form.CREATE_MODE, null, {username:"\${session.username}"});
},this, true)
</script>
<g:layoutBody/>
</body>
</html>
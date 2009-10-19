<html>
<head>
    <g:render template="/layouts/layoutHeader"></g:render>
    <g:layoutHead/>
</head>
<body class="yui-skin-sam">
<script type="text/javascript">
    window.loadingMask = new YAHOO.rapidjs.component.LoadingMask({});
    window.loadingMask.show();
</script>
<g:render template="/layouts/commonLayout"></g:render>
<div id="top" style="background-color:#BBD4F6;">
    <table style="height:100%" cellspacing="0" cellpadding="0"><tbody><tr>
        <td width="0%" style="padding-left:10px;padding-top:5px;padding-right:60px;">
            <img src="/RapidSuite/images/RapidInsight-blue.png">
        </td>
        <td width="100%" style="vertical-align: bottom;;">
            <div class="yui-navset">
                <ul class="yui-nav" style="border-style: none">
                    
                    <li class="${request.uri.toString().indexOf('browser/browser.gsp') > -1 ? "selected" : ""}"><a href="${createLinkTo(file: 'browser/browser.gsp')}"><em>Repository Browser</em></a></li>
                    
                </ul>
            </div>
        </td>
        <td width="0%"></td>
        <td id="serverDownEl" width="0%" style="display:none">
            <img src="/RapidSuite/images/network-offline.png"/>
        </td>
        <td width="0%">
        	<a href="/RapidSuite/help/browserHelp.gsp" target="_blank">
            <img src="/RapidSuite/images/rapidjs/component/tools/help.png" border="0" style="margin-right:5px"/>
            </a>
        </td>
        <td width="0%">
            <div style="vertical-align:bottom">
                <span id="rsUser" style="font-size:12px;font-weight:bold;color:#083772;text-align:right;margin-bottom:5px;cursor:pointer">${session.username}</span>
                <a href="/RapidSuite/auth/logout" style="font-size:13px;font-weight:bold;color:#083772;text-align:right;text-decoration:none">Logout</a>
            </div>
        </td>
    </tr>
    </tbody></table>
</div>
<rui:html id="changeProfileDialog" iframe="false"></rui:html>
<rui:popupWindow componentId="changeProfileDialog" width="390" height="250" resizable="false" title="Change Profile"></rui:popupWindow>
<script>
YAHOO.rapidjs.ErrorManager.serverDownEvent.subscribe(function(){
YAHOO.util.Dom.setStyle(document.getElementById('serverDownEl'), 'display', '');
}, this, true);
YAHOO.rapidjs.ErrorManager.serverUpEvent.subscribe(function(){
YAHOO.util.Dom.setStyle(document.getElementById('serverDownEl'), 'display', 'none');
}, this, true);

YAHOO.util.Event.addListener(document.getElementById('rsUser'), 'click', function(){
var profileDlg = YAHOO.rapidjs.Components['changeProfileDialog'];
profileDlg.popupWindow.show();
profileDlg.show(createURL("userProfileForm.gsp"));
},this, true)
</script>
<g:layoutBody/>
<script type="text/javascript">
    YAHOO.util.Event.onDOMReady(function(){
        if(window.yuiLayout._rendered){
           window.loadingMask.hide();
        }
        else{
            window.yuiLayout.on('render', function(){
               window.loadingMask.hide();
            })
        }
    })
</script>
</body>
</html>
<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Nov 12, 2008
  Time: 10:00:44 AM
--%>

<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Sep 2, 2008
  Time: 11:42:22 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>iFountain - RapidInsight</title>
    <script type="text/javascript" src="js/yui/utilities/utilities.js"></script>
    <script type="text/javascript" src="js/yui/container/container-min.js"></script>
    <script type="text/javascript" src="js/yui/menu/menu-min.js"></script>
    <script type="text/javascript" src="js/yui/resize/resize-min.js"></script>
    <script type="text/javascript" src="js/yui/layout/layout-min.js"></script>
    <script type="text/javascript" src="js/yui/history/history-min.js"></script>
    <script type="text/javascript" src="js/yui/datasource/datasource-min.js"></script>
    <script type="text/javascript" src="js/yui/datatable/datatable-min.js"></script>
    <script type="text/javascript" src="js/yui/autocomplete/autocomplete-min.js"></script>
    <script type="text/javascript" src="js/ext/ext.js"></script>

    <script type="text/javascript" src="js/rapidjs/component/ComponentContainer.js"></script>
    <script type="text/javascript" src="js/rapidjs/RapidUtil.js"></script>
    <script type="text/javascript" src="js/rapidjs/data/NodeFactory.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/RapidElement.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/PollingComponentContainer.js"></script>

    <script type="text/javascript" src="js/yui/button/button-min.js"></script>
    <script type="text/javascript" src="js/rapidjs/data/RapidXmlDocument.js"></script>
    <script type="text/javascript" src="js/rapidjs/SelectUtils.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/form/Form.js"></script>

    <script type="text/javascript" src="js/rapidjs/component/simplewidgets/Button.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/tools/BasicTool.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/tools/SettingsTool.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/dialog/Dialog.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/PopupWindow.js"></script>


    <script type="text/javascript" src="js/rapidjs/component/search/SearchNode.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/tools/ButtonToolBar.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/tools/SearchListSettingsTool.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/tools/LoadingTool.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/tools/ErrorTool.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/search/ExportTool.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/search/AbstractSearchList.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/search/SearchList.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/search/ViewBuilder.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/search/SearchGrid.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/autocomplete/Autocomplete.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/pieChart/FABridge.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/pieChart/FlexPieChart.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/map/GMap.js"></script>

    <script type="text/javascript" src="js/rapidjs/component/simplewidgets/split.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/tools/Tooltip.js"></script>

    <script type="text/javascript" src="js/rapidjs/component/timeline/Timeline.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/timeline/Timelineext.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/timeline/TimelineWindow.js"></script>

    <script type="text/javascript" src="js/rapidjs/component/treegrid/TreeNode.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/treegrid/TreeHeaderCell.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/treegrid/TreeGridView.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/treegrid/TreeGrid.js"></script>

    <script type="text/javascript" src="js/rapidjs/component/action/Action.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/html/Html.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/topologyMap/TopologyMap.js"></script>


    <link rel="stylesheet" type="text/css" href="js/yui/assets/skins/sam/menu.css"/>
    <link rel="stylesheet" type="text/css" href="js/yui/assets/skins/sam/skin.css"/>
    <link rel="stylesheet" type="text/css" href="js/yui/button/assets/skins/sam/button.css"/>
    <link rel="stylesheet" type="text/css" href="js/yui/container/assets/skins/sam/container.css"/>
    <link rel="stylesheet" type="text/css" href="js/yui/datatable/assets/skins/sam/datatable.css"/>
    <link rel="stylesheet" type="text/css" href="css/rapidjs/yuioverride.css"/>
    <link rel="stylesheet" type="text/css" href="css/rapidjs/timeline/timeline.css"/>
    <link rel="stylesheet" type="text/css" href="css/rapidjs/common.css"/>
    <link rel="stylesheet" type="text/css" href="css/rapidjs/dialog.css"/>
    <link rel="stylesheet" type="text/css" href="css/rapidjs/form.css"/>
    <link rel="stylesheet" type="text/css" href="css/rapidjs/layout.css"/>
    <link rel="stylesheet" type="text/css" href="css/rapidjs/search/search.css"/>
    <link rel="stylesheet" type="text/css" href="css/rapidjs/search/searchlist.css"/>
    <link rel="stylesheet" type="text/css" href="css/rapidjs/search/searchgrid.css"/>
    <link rel="stylesheet" type="text/css" href="css/rapidjs/simplewidgets/button.css"/>
    <link rel="stylesheet" type="text/css" href="css/rapidjs/tools/tools.css"/>
    <link rel="stylesheet" type="text/css" href="css/rapidjs/treegrid/treegrid.css"/>
    <link rel="stylesheet" type="text/css" href="css/rapidjs/autocomplete/autocomplete.css"/>
    <link rel="stylesheet" type="text/css" href="riindex.css"/>

    <jsec:isNotLoggedIn>
        <g:javascript>window.location='auth/login?targetUri=/index.gsp'</g:javascript>
    </jsec:isNotLoggedIn>
    <g:layoutHead/>
</head>
<body class=" yui-skin-sam rimain">
<rui:form  id="changeProfileDialog" width="35em" saveUrl="rsUser/changeProfile?format=xml" createUrl="rsUser/changeProfileData?format=xml&username=${session.username}">
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
                        def currentUrl = request.uri.toString();
                    %>
                    <li class="${currentUrl.indexOf('events.gsp') > -1 ? "selected":""}"><a href="${createLinkTo(file: 'events.gsp')}"><em>Events</em></a></li>
                    <li class="${currentUrl.indexOf('eventSearch.gsp') > -1 ? "selected":""}"><a href="${createLinkTo(file: 'eventSearch.gsp')}"><em>Event Search</em></a></li>
                    <li class="${currentUrl.indexOf('historicalEvents.gsp') > -1 ? "selected":""}"><a href="${createLinkTo(file: 'historicalEvents.gsp')}"><em>Historical Events</em></a></li>
                    <li class="${currentUrl.indexOf('inventory.gsp') > -1 ? "selected":""}"><a href="${createLinkTo(file: 'inventory.gsp')}"><em>Inventory</em></a></li>
                    <li class="${currentUrl.indexOf('maps.gsp') > -1 ? "selected":""}"><a href="${createLinkTo(file: 'maps.gsp')}"><em>Maps</em></a></li>
                    %{--<li class="${currentUrl.indexOf('serviceView.gsp') > -1 ? "selected":""}"><a href="${createLinkTo(file: 'serviceView.gsp')}"><em>Service View</em></a></li>--}%
                    %{--<li class="${currentUrl.indexOf('deviceView.gsp') > -1 ? "selected":""}"><a href="${createLinkTo(file: 'deviceView.gsp')}"><em>Device View</em></a></li>--}%
                </ul>
            </div>
        </td>
        <td width="0%"></td>
        <td id="serverDownEl" width="0%" style="display:none">
            <img src="images/network-offline.png"/>
        </td>
        <td width="0%">
            <div style="vertical-align:bottom">
                <span id="rsUser" style="font-size:12px;font-weight:bold;color:#083772;text-align:right;margin-bottom:5px;cursor:pointer">${session.username}</span>
                <a href="auth/logout" style="font-size:13px;font-weight:bold;color:#083772;text-align:right;text-decoration:none">Logout</a>
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
         changeProfileDialog.show(YAHOO.rapidjs.component.Form.CREATE_MODE, null, {username:"${session.username}"});
    },this, true)
</script>
<g:layoutBody/>
</body>
</html>
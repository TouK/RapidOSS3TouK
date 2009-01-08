<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Nov 11, 2008
  Time: 5:32:40 PM
  To change this template use File | Settings | File Templates.
--%>

<html>
<head>
    <meta name="layout" content="indexLayout" />
</head>
<body>
<%
       def stateMapping = ["0":"../states/green.png",
                    "5":"../states/red.png",
                    "4":"../states/orange.png",
                    "3":"../states/yellow.png",
                    "2":"../states/blue.png",
                    "1":"../states/purple.png",
                    "default":"../states/green.png"]
        def typeMapping = ["Host":"server_icon.png",
                    "Router":"router_icon.png",
                    "Switch":"switch_icon.png"]
        def edgeMapping = ["5" : "0xffde2c26","4":"0xfff79229","3": "0xfffae500", "2":"0xff20b4e0","1": "0xffac6bac", "0":"0xff62b446", "default":"0xff62b446" ]                    
   %>

<rui:maps nodeSize="60" mapPollingInterval="0" savedMapsPollingInterval="0" edgeColors="${edgeMapping}">
    <rui:mpMenus>
        <rui:mpMenu id="objectDetails" label="Browse" location="node" actionType="htmlDialog" width="850" height="700" x="85" y="50"
                url="createURL('getObjectDetails.gsp', {name:params.data.id})" title="'Details of ' + params.data.type + ' ' + params.data.id"></rui:mpMenu>
        <rui:mpMenu id="showEvents" label="Show Events" location="node" actionType="htmlDialog" width="850" height="700" x="100" y="80"
                url="createURL('showEvents.gsp', {name:params.data.id})" title="'Events of ' + params.data.type + ' ' + params.data.id"></rui:mpMenu>
    </rui:mpMenus>
    <rui:mpImages>
        <rui:mpImage id="status" x="70" y="40" width="30" height="30" dataKey="state" mapping="${stateMapping}"></rui:mpImage>
        <rui:mpImage id="icon" x="70" y="0" width="20" height="20" dataKey="type" mapping="${typeMapping}"></rui:mpImage>
    </rui:mpImages>
     <rui:mpTexts>
        <rui:mpText id="name" x="15" y="20" width="70" height="30" dataKey="id"></rui:mpText>
     </rui:mpTexts>
</rui:maps>
<rui:html id="eventDetails" iframe="false"></rui:html>
<rui:popupWindow componentId="eventDetails" width="850" height="500"></rui:popupWindow>
</body>
</html>
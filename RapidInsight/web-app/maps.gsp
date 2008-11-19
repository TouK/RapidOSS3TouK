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
                    "1":"../states/red.png",
                    "2":"../states/orange.png",
                    "3":"../states/yellow.png",
                    "4":"../states/blue.png",
                    "5":"../states/green.png",
                    "default":"../states/green.png"]
        def typeMapping = ["Host":"server_icon.png",
                    "Router":"router_icon.png",
                    "Switch":"switch_icon.png"]
   %>

<rui:maps nodeSize="60" mapPollingInterval="0" savedMapsPollingInterval="0">
    <rui:mpMenus>
        <rui:mpMenu id="browse" label="Browse" location="node" actionType="htmlDialog" width="850" height="700" x="85" y="50"
                url="'getObjectDetails.gsp?name=' + params.data.id" title="'Details of ' + params.data.type + ' ' + params.data.id"></rui:mpMenu>
    </rui:mpMenus>
    <rui:mpImages>
        <rui:mpImage id="status" x="70" y="40" width="30" height="30" dataKey="state" mapping="${stateMapping}"></rui:mpImage>
        <rui:mpImage id="icon" x="70" y="0" width="20" height="20" dataKey="type" mapping="${typeMapping}"></rui:mpImage>
    </rui:mpImages>
     <rui:mpTexts>
        <rui:mpText id="name" x="15" y="20" width="70" height="30" dataKey="id"></rui:mpText>
     </rui:mpTexts>
</rui:maps>

</body>
</html>
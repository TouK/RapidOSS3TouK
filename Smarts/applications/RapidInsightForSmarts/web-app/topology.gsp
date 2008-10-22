<html>
<head>
    <meta name="layout" content="indexLayout" />
</head>
<body>
<%
       def stateMapping = ["0":"green.png",
                    "1":"red.png",
                    "2":"orange.png",
                    "3":"yellow.png",
                    "4":"blue.png",
                    "5":"green.png",
                    "default":"green.png"]
        def typeMapping = ["Host":"server_icon.png",
                    "Router":"router_icon.png",
                    "Switch":"switch_icon.png"]
   %>

<rui:topologyMap nodeSize="60" mapPollingInterval="0" savedMapsPollingInterval="0">
    <rui:tmMenus>
        <rui:tmMenu id="browse" label="Browse" location="node" actionType="htmlDialog" width="850" height="700" x="85" y="50" 
                url="'getObjectDetails.gsp?name=' + params.data.id" title="'Details of ' + params.data.type + ' ' + params.data.id"></rui:tmMenu>
    </rui:tmMenus>
    <rui:tmImages>
        <rui:tmImage id="status" x="70" y="40" width="30" height="30" dataKey="state" mapping="${stateMapping}"></rui:tmImage>
        <rui:tmImage id="icon" x="70" y="0" width="20" height="20" dataKey="type" mapping="${typeMapping}"></rui:tmImage>
    </rui:tmImages>
     <rui:tmTexts>
        <rui:tmText id="name" x="15" y="20" width="70" height="30" dataKey="id"></rui:tmText>
     </rui:tmTexts>
</rui:topologyMap>

</body>
</html>
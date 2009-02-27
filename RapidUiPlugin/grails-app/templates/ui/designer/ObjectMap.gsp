<%
    def getMap = {stringMap->
        def mapArray = [];
        stringMap.split(",").each{entry->
            if(entry != "")
            {
                def parts = entry.split(":");
                def key = parts[0]
                def value = parts[1]
                mapArray.add("${key}:${value}")
            }
        }
        def mapString = mapArray.join(",");
        if(mapString == "") mapString = ":"
        return "\${["+mapString+"]}";
    }
    def edgeColorString = getMap(uiElement.edgeColors);
    def contents = uiElement.nodeContents;
%>
<rui:objectMap id="${uiElement.name.encodeAsHTML()}" expandURL="../${uiElement.expandURL.encodeAsHTML()}" dataURL="../${uiElement.dataURL.encodeAsHTML()}" nodeSize="${uiElement.nodeSize}" edgeColorDataKey="${uiElement.edgeColorDataKey.encodeAsHTML()}" edgeColors="${edgeColorString}"
<%
    uiElement.getActionTrigers().each{eventName, actionTriggers->
         def actionString = uiElement.getActionsString(actionTriggers);
    %>
        on${eventName.substring(0,1).toUpperCase().encodeAsHTML()}${eventName.substring(1).encodeAsHTML()}="${actionString}"
    <%
    }
    %>
>
    <rui:omMenuItems>
        <%
            uiElement.getNodeMenuItems().each{menuItem->
                 if(menuItem.parentMenuItem == null){
                    def menuActionString = menuItem.getActionString();
                    def actionString = menuActionString ? "action=\"${menuActionString}\"": "";
        %>
        <rui:omMenuItem id="${menuItem.name.encodeAsHTML()}" label="${menuItem.label.encodeAsHTML()}" ${actionString}>
        </rui:omMenuItem>
        <%
            }
            }
        %>
    </rui:omMenuItems>

    <rui:omToolbarMenus>
        <%
            uiElement.toolbarMenus.each{toolbarMenu->
        %>
        <rui:omToolbarMenu label="${toolbarMenu.label.encodeAsHTML()}">
            <%
                toolbarMenu.menuItems.each{menuItem->
                    def menuActionString = menuItem.getActionString();
                    def actionString = menuActionString ? "action=\"${menuActionString}\"": "";
            %>
            <rui:omMenuItem id="${menuItem.name.encodeAsHTML()}" label="${menuItem.label.encodeAsHTML()}" ${actionString}>
            </rui:omMenuItem>
            <%
                }
            %>

        </rui:omToolbarMenu>
        <%
            }
        %>
    </rui:omToolbarMenus>
    <rui:omNodeContent>
        <rui:omImages>
            <%
                contents.each{imageNodeContent->
                    if(imageNodeContent.type == "image")
                    {
                        def mapString = getMap(imageNodeContent.mapping);
            %>
            <rui:omImage id="${imageNodeContent.name.encodeAsHTML()}" x="${imageNodeContent.x}" y="${imageNodeContent.y}" width="${imageNodeContent.width}" height="${imageNodeContent.height}" dataKey="${imageNodeContent.dataKey.encodeAsHTML()}" mapping="${mapString}"></rui:omImage>
            <%
                    }
                }
            %>
        </rui:omImages>
        <rui:omTexts>
            <%
                contents.each{textNodeContent->
                    if(textNodeContent.type == "text")
                    {
            %>
            <rui:omText id="${textNodeContent.name.encodeAsHTML()}" x="${textNodeContent.x}" y="${textNodeContent.y}" width="${textNodeContent.width}" height="${textNodeContent.height}" dataKey="${textNodeContent.dataKey.encodeAsHTML()}"></rui:omText>
            <%
                    }
                }
            %>
        </rui:omTexts>
        <rui:omGauges>
            <%
                contents.each{gaugeNodeContent->
                    if(gaugeNodeContent.type == "gauge")
                    {
            %>
            <rui:omGauge id="${gaugeNodeContent.name.encodeAsHTML()}" x="${gaugeNodeContent.x}" y="${gaugeNodeContent.y}" width="${gaugeNodeContent.width}" height="${gaugeNodeContent.height}" dataKey="${gaugeNodeContent.dataKey.encodeAsHTML()}"></rui:omGauge>
            <%
                    }
                }
            %>
        </rui:omGauges>
    </rui:omNodeContent>
</rui:objectMap>
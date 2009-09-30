<%
    def getMap = {stringMap, correctPath->
        def mapArray = [];
        stringMap.split(",").each{entry->
            if(entry != "")
            {
                def parts = entry.split(":");
                def key = parts[0]
                def value = parts[1].trim();
                if(correctPath)
                {
                    if(value.startsWith("'") || value.startsWith("\""))
                    {
                        value = value.substring(0,1) +"../"+value.substring(1)
                    }
                    else
                    {
                        value = "../"+value;
                    }

                }
                mapArray.add("${key}:${value}")
            }
        }
        def mapString = mapArray.join(",");
        if(mapString == "") mapString = ":"
        return "\${["+mapString+"]}";
    }
    def edgeColorString = getMap(uiElement.edgeColors, false);
    def contents = uiElement.nodeContents;
%>
<rui:objectMap id="${uiElement.name}" expandURL="../${uiElement.expandURL}" dataURL="../${uiElement.dataURL}" nodePropertyList="${uiElement.nodePropertyList}" mapPropertyList="${uiElement.mapPropertyList}" nodeSize="${uiElement.nodeSize}" edgeColorDataKey="${uiElement.edgeColorDataKey}" edgeColors="${edgeColorString}" timeout="${uiElement.timeout}"
<%
    uiElement.getActionTrigers().each{eventName, actionTriggers->
         def actionString = uiElement.getActionsString(actionTriggers);
    %>
        on${eventName.substring(0,1).toUpperCase()}${eventName.substring(1)}="${actionString}"
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
                    def menuItemVisiblePropertyName = menuItem.name+ "Visible";
                    println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(menuItemVisiblePropertyName, menuItem.visible, true);
        %>
        <rui:omMenuItem id="${menuItem.name}" label="${menuItem.label}" visible="\${${menuItemVisiblePropertyName}}" ${actionString}>
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
        <rui:omToolbarMenu label="${toolbarMenu.label}">
            <%
                toolbarMenu.menuItems.each{menuItem->
                    def menuActionString = menuItem.getActionString();
                    def actionString = menuActionString ? "action=\"${menuActionString}\"": "";
            %>
            <rui:omMenuItem id="${menuItem.name}" label="${menuItem.label}" ${actionString}>
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
                        def mapString = getMap(imageNodeContent.mapping, true);
            %>
            <rui:omImage id="${imageNodeContent.name}" x="${imageNodeContent.x}" y="${imageNodeContent.y}" width="${imageNodeContent.width}" height="${imageNodeContent.height}" dataKey="${imageNodeContent.dataKey}" mapping="${mapString}"></rui:omImage>
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
            <rui:omText id="${textNodeContent.name}" x="${textNodeContent.x}" y="${textNodeContent.y}" width="${textNodeContent.width}" height="${textNodeContent.height}" dataKey="${textNodeContent.dataKey}"></rui:omText>
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
            <rui:omGauge id="${gaugeNodeContent.name}" x="${gaugeNodeContent.x}" y="${gaugeNodeContent.y}" width="${gaugeNodeContent.width}" height="${gaugeNodeContent.height}" dataKey="${gaugeNodeContent.dataKey}"></rui:omGauge>
            <%
                    }
                }
            %>
        </rui:omGauges>
    </rui:omNodeContent>
</rui:objectMap>
<%
    def getMap = {stringMap->
        def map = [:];
        stringMap.split(",").each{entry->
            if(entry != "")
            {
                def parts = entry.split(":");
                def key = parts[0]
                def value = parts[1]
                map[key] = value;
            }
        }
        def mapString = map.toString();
        mapString = mapString.substring(1, mapString.length()-1);
        if(mapString == "") mapString = ":"
        return "\${["+mapString+"]}";
    }
    def edgeColorString = getMap(uiElement.edgeColors);
    def contents = uiElement.nodeContents;
%>
<rui:objectMap id="${uiElement.name}" dataTag="${uiElement.dataTag}" expandURL="${uiElement.expandURL}" dataURL="${uiElement.dataURL}" nodeSize="${uiElement.nodeSize}" edgeColorDataKey="${uiElement.edgeColorDataKey}" edgeColors="${edgeColorString}">
    <rui:omMenuItems>
        <%
            uiElement.menuItems.each{menuItem->
                def menuAction = menuItem.getAction();
        %>
        <rui:omMenuItem id="${menuItem.name}" label="${menuItem.label}" ${menuAction?"action='"+menuAction.name+"'":""}>
        </rui:omMenuItem>
        <%
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
                    def menuAction = menuItem.getAction();
            %>
            <rui:omMenuItem id="${menuItem.name}" label="${menuItem.label}" ${menuAction?"action='"+menuAction.name+"'":""}>
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
<rui:searchGrid id="${uiElement.name}" url="../${uiElement.url}" queryParameter="${uiElement.queryParameter}" rootTag="${uiElement.rootTag}" contentPath="${uiElement.contentPath}"
        keyAttribute="${uiElement.keyAttribute}" totalCountAttribute="${uiElement.totalCountAttribute}" offsetAttribute="${uiElement.offsetAttribute}" sortOrderAttribute="${uiElement.sortOrderAttribute}" title="${uiElement.title}"
        pollingInterval="${uiElement.pollingInterval}" fieldsUrl="../${uiElement.fieldsUrl}" queryEnabled="${uiElement.queryEnabled}"
    <%
    uiElement.getActionTrigers().each{eventName, actionTriggers->
         def actionString = uiElement.getActionsString(actionTriggers);
    %>
        on${eventName.substring(0,1).toUpperCase()}${eventName.substring(1)}="${actionString}"
    <%
    }
%>
>
    <rui:sgMenuItems>
    <%
        uiElement.menuItems.each{menuItem->
            def menuActionString = menuItem.getActionString();
            def actionString = menuActionString ? "action=\"${menuActionString}\"": "";
    %>
        <rui:sgMenuItem id="${menuItem.name}" label="${menuItem.label}" visible="${menuItem.visible}" ${actionString}>
            <%
                if(!menuItem.childMenuItems.isEmpty())
                {
            %>
                <rui:sgSubmenuItems>
                    <%
                        menuItem.childMenuItems.each{subMenuItem->
                            def subMenuActionString = subMenuItem.getActionString();
                            def subActionString = subMenuActionString ? "action=\"${subMenuActionString}\"": "";
                    %>
                        <rui:sgMenuItem id="${subMenuItem.name}" label="${subMenuItem.label}" ${subActionString} visible="${subMenuItem.visible}"></rui:sgMenuItem>
                    <%
                            }
                    %>
                </rui:sgSubmenuItems>
            <%
                    }
            %>
        </rui:sgMenuItem>
    <%
        }
    %>
    </rui:sgMenuItems>
    <rui:sgImages>
    <%
        uiElement.images.each{image->
    %>
        <rui:sgImage visible="${image.visible}" src="../${image.src}"></rui:sgImage>
    <%
        }
    %>
    </rui:sgImages>
    <rui:sgColumns>
    <%
        uiElement.columns.each{column->
        def sortByString = column.sortBy ? "sortBy=\"${column.sortBy}\"":""
        def sortOrderString = column.sortBy ? "sortOrder=\"${column.sortOrder}\"":""
        System.out.println("" + column.sortBy + " " + sortByString + " " + sortOrderString)
    %>
        <rui:sgColumn attributeName="${column.attributeName}" colLabel="${column.colLabel}" width="${column.width}" ${sortByString} ${sortOrderString}></rui:sgColumn>
    <%
        }
    %>
    </rui:sgColumns>
    <rui:sgRowColors>
    <%
        uiElement.rowColors.each{rowColor->
            def textColor = rowColor.textColor;
    %>
        <rui:sgRowColor color="${rowColor.color}" visible="${rowColor.visible}" ${textColor?"textColor='"+textColor+"'":""}></rui:sgRowColor>
    <%
        }
    %>
    </rui:sgRowColors>
</rui:searchGrid>
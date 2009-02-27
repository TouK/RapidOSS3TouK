<rui:searchGrid id="${uiElement.name.encodeAsHTML()}" url="../${uiElement.url.encodeAsHTML()}" queryParameter="${uiElement.queryParameter.encodeAsHTML()}" rootTag="${uiElement.rootTag.encodeAsHTML()}" contentPath="${uiElement.contentPath.encodeAsHTML()}"
        keyAttribute="${uiElement.keyAttribute.encodeAsHTML()}" totalCountAttribute="${uiElement.totalCountAttribute.encodeAsHTML()}" offsetAttribute="${uiElement.offsetAttribute.encodeAsHTML()}" sortOrderAttribute="${uiElement.sortOrderAttribute.encodeAsHTML()}" title="${uiElement.title.encodeAsHTML()}"
        pollingInterval="${uiElement.pollingInterval}" fieldsUrl="../${uiElement.fieldsUrl.encodeAsHTML()}" queryEnabled="${uiElement.queryEnabled}"
    <%
    uiElement.getActionTrigers().each{eventName, actionTriggers->
         def actionString = uiElement.getActionsString(actionTriggers);
    %>
        on${eventName.substring(0,1).toUpperCase().encodeAsHTML()}${eventName.substring(1).encodeAsHTML()}="${actionString}"
    <%
    }
%>
>
    <rui:sgMenuItems>
    <%
        uiElement.menuItems.each{menuItem->
            if(menuItem.parentMenuItem == null){
                def menuActionString = menuItem.getActionString();
                def actionString = menuActionString ? "action=\"${menuActionString}\"": "";
    %>
        <rui:sgMenuItem id="${menuItem.name.encodeAsHTML()}" label="${menuItem.label.encodeAsHTML()}" visible="${menuItem.visible}" ${actionString}>
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
                        <rui:sgMenuItem id="${subMenuItem.name.encodeAsHTML()}" label="${subMenuItem.label.encodeAsHTML()}" ${subActionString} visible="${subMenuItem.visible}"></rui:sgMenuItem>
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
        }
    %>
    </rui:sgMenuItems>
    <rui:sgImages>
    <%
        uiElement.images.each{image->
    %>
        <rui:sgImage visible="${image.visible.encodeAsHTML()}" src="../${image.src.encodeAsHTML()}"></rui:sgImage>
    <%
        }
    %>
    </rui:sgImages>
    <rui:sgColumns>
    <%
        def columns = uiElement.columns.sort{it.columnIndex};
        columns.each{column->
        def sortByString = column.sortBy ? "sortBy=\"${column.sortBy.encodeAsHTML()}\"":""
        def sortOrderString = column.sortBy ? "sortOrder=\"${column.sortOrder.encodeAsHTML()}\"":""
    %>
        <rui:sgColumn attributeName="${column.attributeName.encodeAsHTML()}" colLabel="${column.colLabel.encodeAsHTML()}" width="${column.width}" ${sortByString} ${sortOrderString} type="${column.type.encodeAsHTML()}"></rui:sgColumn>
    <%
        }
    %>
    </rui:sgColumns>
    <rui:sgRowColors>
    <%
        uiElement.rowColors.each{rowColor->
            def textColor = rowColor.textColor;
    %>
        <rui:sgRowColor color="${rowColor.color.encodeAsHTML()}" visible="${rowColor.visible.encodeAsHTML()}" ${textColor?"textColor='"+textColor.encodeAsHTML()+"'":""}></rui:sgRowColor>
    <%
        }
    %>
    </rui:sgRowColors>
</rui:searchGrid>
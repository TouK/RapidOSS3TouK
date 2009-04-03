<rui:searchGrid id="${uiElement.name}" url="../${uiElement.url}" queryParameter="${uiElement.queryParameter}" rootTag="${uiElement.rootTag}" contentPath="${uiElement.contentPath}"
        keyAttribute="${uiElement.keyAttribute}" totalCountAttribute="${uiElement.totalCountAttribute}" offsetAttribute="${uiElement.offsetAttribute}" sortOrderAttribute="${uiElement.sortOrderAttribute}" title="${uiElement.title}"
        pollingInterval="${uiElement.pollingInterval}" fieldsUrl="../${uiElement.fieldsUrl}" queryEnabled="${uiElement.queryEnabled}" defaultQuery="${uiElement.defaultQuery}" timeout="${uiElement.timeout}"
        defaultSearchClass="${uiElement.defaultSearchClass}" ${uiElement.queryEnabled?"searchClassesUrl='../"+uiElement.searchClassesUrl+"'":""}
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
            if(menuItem.parentMenuItem == null){
                def menuActionString = menuItem.getActionString();
                def actionString = menuActionString ? "action=\"${menuActionString}\"": "";
                def visiblePropertyName = menuItem.name+ "Visible";
                println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(visiblePropertyName, menuItem.visible, true);
    %>
        <rui:sgMenuItem id="${menuItem.name}" label="${menuItem.label}" visible="\${${visiblePropertyName}}" ${actionString}>
            <%
                if(!menuItem.childMenuItems.isEmpty())
                {
            %>
                <rui:sgSubmenuItems>
                    <%
                        menuItem.childMenuItems.each{subMenuItem->
                            def subMenuActionString = subMenuItem.getActionString();
                            def subActionString = subMenuActionString ? "action=\"${subMenuActionString}\"": "";
                            def subMenuVisiblePropertyName = subMenuItem.name+ "Visible";
                            println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(subMenuVisiblePropertyName, subMenuItem.visible, true);
                    %>
                        <rui:sgMenuItem id="${subMenuItem.name}" label="${subMenuItem.label}" ${subActionString} visible="\${${subMenuVisiblePropertyName}}"></rui:sgMenuItem>
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
            def imageVisiblePropertyName = "image"+image.id+ "Visible";
            println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(imageVisiblePropertyName, image.visible, true);
    %>
        <rui:sgImage visible="\${${imageVisiblePropertyName}}" src="../${image.src}"></rui:sgImage>
    <%
        }
    %>
    </rui:sgImages>
    <rui:sgColumns>
    <%
        def columns = uiElement.columns.sort{it.columnIndex};
        columns.each{column->
        def sortByString = column.sortBy ? "sortBy=\"${column.sortBy}\"":""
        def sortOrderString = column.sortBy ? "sortOrder=\"${column.sortOrder}\"":""
    %>
        <rui:sgColumn attributeName="${column.attributeName}" colLabel="${column.colLabel}" width="${column.width}" ${sortByString} ${sortOrderString} type="${column.type}"></rui:sgColumn>
    <%
        }
    %>
    </rui:sgColumns>
    <rui:sgRowColors>
    <%
        uiElement.rowColors.each{rowColor->
            def textColor = rowColor.textColor;
            def rowColorVisiblePropertyName = "rowColor"+rowColor.id+ "Visible";
            println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(rowColorVisiblePropertyName, rowColor.visible, true);
    %>
        <rui:sgRowColor color="${rowColor.color}" visible="\${${rowColorVisiblePropertyName}}" ${textColor?"textColor='"+textColor+"'":""}></rui:sgRowColor>
    <%
        }
    %>
    </rui:sgRowColors>
</rui:searchGrid>
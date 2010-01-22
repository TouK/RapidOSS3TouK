<rui:treeGrid id="${uiElement.name}" url="../${uiElement.url}" rootTag="${uiElement.rootTag}" pollingInterval="${uiElement.pollingInterval}" timeout="${uiElement.timeout}"
        keyAttribute="${uiElement.keyAttribute}" expandAttribute="${uiElement.expandAttribute}" contentPath="${uiElement.contentPath}" title="${uiElement.title}" expanded="${uiElement.expanded}"
<%
    uiElement.getActionTrigers().each{eventName, actionTriggers->
         def actionString = uiElement.getActionsString(actionTriggers);
    %>
        on${eventName.substring(0,1).toUpperCase()}${eventName.substring(1)}="${actionString}"
    <%
    }
    %>
>
    <rui:tgColumns>
<%

    def columns = uiElement.columns.sort{it.columnIndex};
    columns.each{column->
        def sortByString = column.sortBy ? "sortBy=\"${column.sortBy}\"":""
        def sortOrderString = column.sortBy ? "sortOrder=\"${column.sortOrder}\"":""
%>
        <rui:tgColumn type="${column.type}" attributeName="${column.attributeName}" colLabel="${column.colLabel}" width="${column.width}" ${sortByString} ${sortOrderString} sortType="${column.sortType}">
            <%
                if(!column.images.isEmpty())
                {
            %>
            <rui:tgImages>
                <%
                    column.images.each{colImage->
                        def colImageMenuItemVisiblePropertyName = "image"+colImage._designerKey+ "Visible";
                        println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(colImageMenuItemVisiblePropertyName, colImage.visible, true);
                %>
                <rui:tgImage src="../${colImage.src}" visible="\${${colImageMenuItemVisiblePropertyName}}" align="${colImage.align}"></rui:tgImage>
                <%
                    }
                %>
            </rui:tgImages>
            <%
                }
            %>
        </rui:tgColumn>
<%
    }
%>
    </rui:tgColumns>    
    <rui:tgMenuItems>
        <%
            uiElement.getRowMenuItems().each{menuItem->
                 if(menuItem.parentMenuItem == null){
                    def menuActionString = menuItem.getActionString();
                    def actionString = menuActionString ? "action=\"${menuActionString}\"": "";
                    def menuItemVisiblePropertyName = menuItem.name+ "Visible";
                    println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(menuItemVisiblePropertyName, menuItem.visible, true);
        %>
        <rui:tgMenuItem id="${menuItem.name}" label="${menuItem.label}" visible="\${${menuItemVisiblePropertyName}}" ${actionString}>
               <%
                    if(!menuItem.childMenuItems.isEmpty())
                    {
                %>
                    <rui:tgSubmenuItems>
                        <%
                            menuItem.childMenuItems.each{subMenuItem->
                                def subMenuActionString = subMenuItem.getActionString();
                                def subActionString = subMenuActionString ? "action=\"${subMenuActionString}\"": "";
                                def subMenuItemVisiblePropertyName = subMenuItem.name+ "Visible";
                                println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(subMenuItemVisiblePropertyName, subMenuItem.visible, true);
                        %>
                            <rui:tgMenuItem id="${subMenuItem.name}" label="${subMenuItem.label}" ${subActionString} visible="\${${subMenuItemVisiblePropertyName}}"></rui:tgMenuItem>
                        <%
                                }
                        %>
                    </rui:tgSubmenuItems>
                <%
                        }
                %>
        </rui:tgMenuItem>
        <%
            }
            }
        %>
    </rui:tgMenuItems>
     <rui:tgMultiSelectionMenuItems>
    <%
        uiElement.getMultiSelectionMenuItems().each{menuItem->
            if(menuItem.parentMenuItemId == null || menuItem.parentMenuItemId == ""){
                def menuActionString = menuItem.getActionString();
                def actionString = menuActionString ? "action=\"${menuActionString}\"": "";
                def visiblePropertyName = menuItem.name+ "Visible";
                println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(visiblePropertyName, menuItem.visible, true);
    %>
        <rui:tgMenuItem id="${menuItem.name}" label="${menuItem.label}" visible="\${${visiblePropertyName}}" ${actionString}>
            <%
                if(!menuItem.childMenuItems.isEmpty())
                {
            %>
                <rui:tgSubmenuItems>
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
                </rui:tgSubmenuItems>
            <%
                    }
            %>
        </rui:tgMenuItem>
    <%

          }
        }
    %>
    </rui:tgMultiSelectionMenuItems>
    <rui:tgRootImages>
        <%
            uiElement.rootImages.each{rootImage->
                def rootImageVisiblePropertyName = "rootImage"+rootImage._designerKey+ "Visible";
                println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(rootImageVisiblePropertyName, rootImage.visible, true);
        %>
        <rui:tgRootImage visible="\${${rootImageVisiblePropertyName}}" expanded="../${rootImage.expanded}" collapsed="../${rootImage.collapsed}"></rui:tgRootImage>
        <%
            }
        %>
    </rui:tgRootImages>
</rui:treeGrid>
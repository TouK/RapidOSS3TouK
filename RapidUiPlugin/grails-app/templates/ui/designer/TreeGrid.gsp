<rui:treeGrid id="${uiElement.name}" url="../${uiElement.url}" rootTag="${uiElement.rootTag}" pollingInterval="${uiElement.pollingInterval}"
        keyAttribute="${uiElement.keyAttribute}" contentPath="${uiElement.contentPath}" title="${uiElement.title}"
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
        <rui:tgColumn type="${column.type}" attributeName="${column.attributeName}" colLabel="${column.colLabel}" width="${column.width}" ${sortByString} ${sortOrderString}>
            <%
                if(!column.images.isEmpty())
                {
            %>
            <rui:tgImages>
                <%
                    column.images.each{colImage->
                %>
                <rui:tgImage src="../${colImage.src}" visible="${colImage.visible}" align="${colImage.align}"></rui:tgImage>
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
            uiElement.menuItems.each{menuItem->
                def menuActionString = menuItem.getActionString();
                def actionString = menuActionString ? "action=\"${menuActionString}\"": "";
        %>
        <rui:tgMenuItem id="${menuItem.name}" label="${menuItem.label}" visible="${menuItem.visible}" ${actionString}>
               <%
                    if(!menuItem.childMenuItems.isEmpty())
                    {
                %>
                    <rui:tgSubmenuItems>
                        <%
                            menuItem.childMenuItems.each{subMenuItem->
                                def subMenuAction = subMenuItem.getAction();
                        %>
                            <rui:tgMenuItem id="${subMenuItem.name}" label="${subMenuItem.label}" ${subMenuAction?"action='"+subMenuAction.name+"'":""} visible="${subMenuItem.visible}"></rui:tgMenuItem>
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
        %>
    </rui:tgMenuItems>
    <rui:tgRootImages>
        <%
            uiElement.rootImages.each{rootImage->
        %>
        <rui:tgRootImage visible="${rootImage.visible}" expanded="../${rootImage.expanded}" collapsed="../${rootImage.collapsed}"></rui:tgRootImage>
        <%
            }
        %>
    </rui:tgRootImages>
</rui:treeGrid>
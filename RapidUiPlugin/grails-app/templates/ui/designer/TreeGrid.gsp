<rui:treeGrid id="${uiElement.name.encodeAsHTML()}" url="../${uiElement.url.encodeAsHTML()}" rootTag="${uiElement.rootTag.encodeAsHTML()}" pollingInterval="${uiElement.pollingInterval}"
        keyAttribute="${uiElement.keyAttribute.encodeAsHTML()}" contentPath="${uiElement.contentPath.encodeAsHTML()}" title="${uiElement.title.encodeAsHTML()}" expanded="${uiElement.expanded}"
<%
    uiElement.getActionTrigers().each{eventName, actionTriggers->
         def actionString = uiElement.getActionsString(actionTriggers);
    %>
        on${eventName.substring(0,1).toUpperCase().encodeAsHTML()}${eventName.substring(1).encodeAsHTML()}="${actionString}"
    <%
    }
    %>
>
    <rui:tgColumns>
<%

    def columns = uiElement.columns.sort{it.columnIndex};
    columns.each{column->
        def sortByString = column.sortBy ? "sortBy=\"${column.sortBy.encodeAsHTML()}\"":""
        def sortOrderString = column.sortBy ? "sortOrder=\"${column.sortOrder}\"":""
%>
        <rui:tgColumn type="${column.type.encodeAsHTML()}" attributeName="${column.attributeName.encodeAsHTML()}" colLabel="${column.colLabel.encodeAsHTML()}" width="${column.width}" ${sortByString} ${sortOrderString}>
            <%
                if(!column.images.isEmpty())
                {
            %>
            <rui:tgImages>
                <%
                    column.images.each{colImage->
                %>
                <rui:tgImage src="../${colImage.src.encodeAsHTML()}" visible="${colImage.visible.encodeAsHTML()}" align="${colImage.align}"></rui:tgImage>
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
                 if(menuItem.parentMenuItem == null){
                    def menuActionString = menuItem.getActionString();
                    def actionString = menuActionString ? "action=\"${menuActionString}\"": "";
        %>
        <rui:tgMenuItem id="${menuItem.name.encodeAsHTML()}" label="${menuItem.label.encodeAsHTML()}" visible="${menuItem.visible.encodeAsHTML()}" ${actionString}>
               <%
                    if(!menuItem.childMenuItems.isEmpty())
                    {
                %>
                    <rui:tgSubmenuItems>
                        <%
                            menuItem.childMenuItems.each{subMenuItem->
                                def subMenuAction = subMenuItem.getAction();
                        %>
                            <rui:tgMenuItem id="${subMenuItem.name.encodeAsHTML()}" label="${subMenuItem.label.encodeAsHTML()}" ${subMenuAction?"action='"+subMenuAction.name.encodeAsHTML()+"'":""} visible="${subMenuItem.visible.encodeAsHTML()}"></rui:tgMenuItem>
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
    <rui:tgRootImages>
        <%
            uiElement.rootImages.each{rootImage->
        %>
        <rui:tgRootImage visible="${rootImage.visible.encodeAsHTML()}" expanded="../${rootImage.expanded}" collapsed="../${rootImage.collapsed}"></rui:tgRootImage>
        <%
            }
        %>
    </rui:tgRootImages>
</rui:treeGrid>
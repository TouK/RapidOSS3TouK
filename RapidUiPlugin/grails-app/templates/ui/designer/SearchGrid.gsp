<rui:searchGrid id="${uiElement.name}" url="${uiElement.url}" queryParameter="${uiElement.queryParameter}" rootTag="${uiElement.rootTag}" contentPath="${uiElement.contentPath}"
        keyAttribute="${uiElement.keyAttribute}" totalCountAttribute="${uiElement.totalCountAttribute}" offsetAttribute="${uiElement.offsetAttribute}" sortOrderAttribute="${uiElement.sortOrderAttribute}" title="${uiElement.title}"
        pollingInterval="${uiElement.pollingInterval}" fieldsUrl="${uiElement.fieldsUrl}"
    <%
    uiElement.getActionTrigers().each{actionTrigger->
    %>
        on${actionTrigger.name.substring(0,1).toUpperCase()}${actionTrigger.name.substring(1)}="${actionTrigger.action.name}"
    <%
    }
%>
>
    <rui:sgMenuItems>
    <%
        uiElement.menuItems.each{menuItem->
            def menuAction = menuItem.getAction();
    %>
        <rui:sgMenuItem id="${menuItem.name}" label="${menuItem.label}" visible="${menuItem.visible}" ${menuAction?"action='"+menuAction.name+"'":""}>
            <%
                if(!menuItem.childMenuItems.isEmpty())
                {
            %>
                <rui:sgSubmenuItems>
                    <%
                        menuItem.childMenuItems.each{subMenuItem->
                            def subMenuAction = subMenuItem.getAction();
                    %>
                        <rui:sgMenuItem id="${subMenuItem.name}" label="${subMenuItem.label}" ${subMenuAction?"action='"+subMenuAction.name+"'":""} visible="${subMenuItem.visible}"></rui:sgMenuItem>
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
        <rui:sgImage visible="${image.visible}" src="${image.src}"></rui:sgImage>
    <%
        }
    %>
    </rui:sgImages>
    <rui:sgColumns>
    <%
        uiElement.columns.each{column->
    %>
        <rui:sgColumn attributeName="${column.attributeName}" colLabel="${column.colLabel}" width="${column.width}"></rui:sgColumn>
    <%
        }
    %>
    </rui:sgColumns>
</rui:searchGrid>
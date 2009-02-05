<rui:treeGrid id="${uiElement.name}" url="${uiElement.url}" rootTag="${uiElement.rootTag}" pollingInterval="${uiElement.pollingInterval}"
        keyAttribute="${uiElement.keyAttribute}" contentPath="${uiElement.contentPath}" title="${uiElement.title}"
<%
    ui.designer.UiActionTrigger.list().each{actionTrigger->
        if(actionTrigger.component.name == uiElement.name && !actionTrigger.isMenuItem)
        {
    %>
        on${actionTrigger.name.substring(0,1).toUpperCase()}${actionTrigger.name.substring(1)}="${event.action.name}"
    <%
        }
    }
%>
>
    <rui:tgColumns>
<%
    uiElement.columns.each{column->
%>
        <rui:tgColumn type="${column.type}" attributeName="${column.attributeName}" colLabel="${column.colLabel}" width="${column.width}" sortBy="${column.sortBy}" sortOrder="${column.sortOrder}">
            <%
                if(!column.images.isEmpty())
                {
            %>
            <rui:tgImages>
                <%
                    column.images.each{colImage->
                %>
                <rui:tgImage src="${colImage.src}" visible="${colImage.visible}" align="${colImage.align}"></rui:tgImage>
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
%>
        <rui:tgMenuItem id="${menuItem.name}" label="${menuItem.label}" ${menuItem.action != null?"action='"+menuItem.action.name+"'":""}></rui:tgMenuItem>
<%
    }
%>
    </rui:tgMenuItems>
    <rui:tgRootImages>
        <%
            uiElement.rootImages.each{rootImage->
        %>
        <rui:tgRootImage visible="${rootImage.visible}" expanded="${rootImage.expanded}" collapsed="${rootImage.collapsed}"></rui:tgRootImage>
        <%
            }
        %>
    </rui:tgRootImages>
</rui:treeGrid>
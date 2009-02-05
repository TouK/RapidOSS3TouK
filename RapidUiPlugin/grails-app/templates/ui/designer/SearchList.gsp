<rui:searchList id="${uiElement.name}" url="${uiElement.url}" rootTag="${uiElement.rootTag}" contentPath="${uiElement.contentPath}" keyAttribute="${uiElement.keyAttribute}"
    lineSize="${uiElement.lineSize}" title="${uiElement.title}" queryParameter="${uiElement.queryParameter}" totalCountAttribute="${uiElement.totalCountAttribute}" offsetAttribute="${uiElement.offsetAttribute}" sortOrderAttribute="${uiElement.sortOrderAttribute}"
    pollingInterval="${uiElement.pollingInterval}" defaultFields="${uiElement.defaultFields}"
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
    <rui:slMenuItems>
        <%
            uiElement.menuItems.each{menuItem->
        %>
        <rui:slMenuItem id="${menuItem.name}" label="${menuItem.label}" ${menuItem.action?"action='"+menuItem.action.name+"'":""}></rui:slMenuItem>
               <%
                    if(!menuItem.childMenuItems.isEmpty())
                    {
                %>
                    <rui:slSubmenuItems>
                        <%
                            menuItem.childMenuItems.each{subMenuItem->
                        %>
                            <rui:slMenuItem id="${subMenuItem.name}" label="${subMenuItem.label}" ${subMenuItem.action?"action='"+subMenuItem.action.name+"'":""} visible="${subMenuItem.visible}"></rui:sgMenuItem>
                        <%
                                }
                        %>
                    </rui:slSubmenuItems>
                <%
                        }
                %>
        </rui:slMenuItem>
        <%
            }
        %>
    </rui:slMenuItems>
    <rui:slPropertyMenuItems>
        <%
            uiElement.propertyMenuItems.each{menuItem->
        %>
        <rui:slMenuItem id="${menuItem.name}" label="${menuItem.label}" ${menuItem.action?"action='"+menuItem.action.name+"'":""}></rui:slMenuItem>
               <%
                    if(!menuItem.childMenuItems.isEmpty())
                    {
                %>
                    <rui:slSubmenuItems>
                        <%
                            menuItem.childMenuItems.each{subMenuItem->
                        %>
                            <rui:slMenuItem id="${subMenuItem.name}" label="${subMenuItem.label}" ${subMenuItem.action?"action='"+subMenuItem.action.name+"'":""} visible="${subMenuItem.visible}"></rui:sgMenuItem>
                        <%
                                }
                        %>
                    </rui:slSubmenuItems>
                <%
                        }
                %>
        </rui:slMenuItem>
        <%
            }
        %>
    </rui:slPropertyMenuItems>
     <rui:slFields>
    <%
        uiElement.fields.each{field->
    %>
        <rui:slField exp="${field.exp}" fields="${field.fields}"></rui:slField>
    <%
        }
    %>
    </rui:slFields>
    <rui:slImages>
    <%
        uiElement.images.each{image->
    %>
        <rui:slImage visible="${image.visible}" src="${image.src}"></rui:slImage>
    <%
        }
    %>
    </rui:slImages>
</rui:searchList>
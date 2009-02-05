<rui:searchList id="${uiElement.name}" url="${uiElement.url}" rootTag="${uiElement.rootTag}" contentPath="${uiElement.contentPath}" keyAttribute="${uiElement.keyAttribute}"
    lineSize="${uiElement.lineSize}" title="${uiElement.title}" queryParameter="${uiElement.queryParameter}" totalCountAttribute="${uiElement.totalCountAttribute}" offsetAttribute="${uiElement.offsetAttribute}" sortOrderAttribute="${uiElement.sortOrderAttribute}"
    pollingInterval="${uiElement.pollingInterval}" defaultFields="${uiElement.defaultFields}"
    <%
    uiElement.getActionTrigers().each{actionTrigger->
    %>
        on${actionTrigger.name.substring(0,1).toUpperCase()}${actionTrigger.name.substring(1)}="${actionTrigger.action.name}"
    <%
    }
    %>
>
    <rui:slMenuItems>
        <%
            uiElement.menuItems.each{menuItem->
                def menuAction = menuItem.getAction();
        %>
        <rui:slMenuItem id="${menuItem.name}" label="${menuItem.label}" ${menuAction?"action='"+menuAction.name+"'":""}></rui:slMenuItem>
               <%
                    if(!menuItem.childMenuItems.isEmpty())
                    {
                %>
                    <rui:slSubmenuItems>
                        <%
                            menuItem.childMenuItems.each{subMenuItem->
                                def subMenuAction = subMenuItem.getAction();
                        %>
                            <rui:slMenuItem id="${subMenuItem.name}" label="${subMenuItem.label}" ${subMenuAction?"action='"+subMenuAction.name+"'":""} visible="${subMenuItem.visible}"></rui:sgMenuItem>
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
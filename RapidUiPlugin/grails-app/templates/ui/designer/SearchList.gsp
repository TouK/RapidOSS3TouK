<rui:searchList id="${uiElement.name}" url="../${uiElement.url}" rootTag="${uiElement.rootTag}" contentPath="${uiElement.contentPath}" keyAttribute="${uiElement.keyAttribute}"
    lineSize="${uiElement.lineSize}" title="${uiElement.title}" queryParameter="${uiElement.queryParameter}" totalCountAttribute="${uiElement.totalCountAttribute}" offsetAttribute="${uiElement.offsetAttribute}" sortOrderAttribute="${uiElement.sortOrderAttribute}"
    pollingInterval="${uiElement.pollingInterval}" defaultFields="${uiElement.defaultFields}" ${uiElement.showMax !=0?"showMax='"+uiElement.showMax+"'":""}
    <%
    uiElement.getActionTrigers().each{eventName, actionTriggers->
         def actionString = uiElement.getActionsString(actionTriggers);
    %>
        on${eventName.substring(0,1).toUpperCase()}${eventName.substring(1)}="${actionString}"
    <%
    }
    %>
>
    <rui:slMenuItems>
        <%
            uiElement.menuItems.each{menuItem->
                def menuActionString = menuItem.getActionString();
                def actionString = menuActionString ? "action=\"${menuActionString}\"": "";
        %>
        <rui:slMenuItem id="${menuItem.name}" label="${menuItem.label}" ${actionString}>
               <%
                    if(!menuItem.childMenuItems.isEmpty())
                    {
                %>
                    <rui:slSubmenuItems>
                        <%
                            menuItem.childMenuItems.each{subMenuItem->
                                def subMenuActionString = subMenuItem.getActionString();
                                def subActionString = subMenuActionString ? "action=\"${subMenuActionString}\"": "";
                        %>
                            <rui:slMenuItem id="${subMenuItem.name}" label="${subMenuItem.label}" ${subActionString} visible="${subMenuItem.visible}"></rui:sgMenuItem>
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
                def menuActionString = menuItem.getActionString();
                def actionString = menuActionString ? "action=\"${menuActionString}\"": "";
        %>
        <rui:slMenuItem id="${menuItem.name}" label="${menuItem.label}" ${actionString}>
               <%
                    if(!menuItem.childMenuItems.isEmpty())
                    {
                %>
                    <rui:slSubmenuItems>
                        <%
                            menuItem.childMenuItems.each{subMenuItem->
                                def subMenuActionString = subMenuItem.getActionString();
                                def subActionString = subMenuActionString ? "action=\"${subMenuActionString}\"": "";
                        %>
                            <rui:slMenuItem id="${subMenuItem.name}" label="${subMenuItem.label}" ${subActionString} visible="${subMenuItem.visible}"></rui:sgMenuItem>
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
            def fieldsString = field.fields.split(",").toString();
            fieldsString = fieldsString.substring(1, fieldsString.length()-1);
    %>
        <rui:slField exp="${field.exp}" fields='\${[${fieldsString}]}'></rui:slField>
    <%
        }
    %>
    </rui:slFields>
    <rui:slImages>
    <%
        uiElement.images.each{image->
    %>
        <rui:slImage visible="${image.visible}" src="../${image.src}"></rui:slImage>
    <%
        }
    %>
    </rui:slImages>
</rui:searchList>
<rui:searchList id="${uiElement.name.encodeAsHTML()}" url="../${uiElement.url.encodeAsHTML()}" rootTag="${uiElement.rootTag.encodeAsHTML()}" contentPath="${uiElement.contentPath.encodeAsHTML()}" keyAttribute="${uiElement.keyAttribute.encodeAsHTML()}"
    lineSize="${uiElement.lineSize}" title="${uiElement.title.encodeAsHTML()}" queryParameter="${uiElement.queryParameter.encodeAsHTML()}" totalCountAttribute="${uiElement.totalCountAttribute.encodeAsHTML()}" offsetAttribute="${uiElement.offsetAttribute.encodeAsHTML()}" sortOrderAttribute="${uiElement.sortOrderAttribute.encodeAsHTML()}"
    pollingInterval="${uiElement.pollingInterval}" defaultFields="${uiElement.defaultFields.encodeAsHTML()}" ${uiElement.showMax !=0?"showMax='"+uiElement.showMax+"'":""}
    <%
    uiElement.getActionTrigers().each{eventName, actionTriggers->
         def actionString = uiElement.getActionsString(actionTriggers);
    %>
        on${eventName.substring(0,1).toUpperCase().encodeAsHTML()}${eventName.substring(1).encodeAsHTML()}="${actionString}"
    <%
    }
    %>
>
    <rui:slMenuItems>
        <%
            uiElement.getRowMenuItems().each{menuItem->
                def menuActionString = menuItem.getActionString();
                def actionString = menuActionString ? "action=\"${menuActionString}\"": "";
        %>
        <rui:slMenuItem id="${menuItem.name.encodeAsHTML()}" label="${menuItem.label.encodeAsHTML()}" ${actionString}>
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
                            <rui:slMenuItem id="${subMenuItem.name.encodeAsHTML()}" label="${subMenuItem.label.encodeAsHTML()}" ${subActionString} visible="${subMenuItem.visible.encodeAsHTML()}"></rui:sgMenuItem>
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
        <rui:slMenuItem id="${menuItem.name.encodeAsHTML()}" label="${menuItem.label.encodeAsHTML()}" ${actionString}>
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
                            <rui:slMenuItem id="${subMenuItem.name.encodeAsHTML()}" label="${subMenuItem.label.encodeAsHTML()}" ${subActionString} visible="${subMenuItem.visible.encodeAsHTML()}"></rui:sgMenuItem>
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
            def fieldsString = field.fields.split(",").encodeAsHTML().toString();
            fieldsString = fieldsString.substring(1, fieldsString.length()-1);
    %>
        <rui:slField exp="${field.exp.encodeAsHTML()}" fields='\${[${fieldsString}]}'></rui:slField>
    <%
        }
    %>
    </rui:slFields>
    <rui:slImages>
    <%
        uiElement.images.each{image->
    %>
        <rui:slImage visible="${image.visible.encodeAsHTML()}" src="../${image.src.encodeAsHTML()}"></rui:slImage>
    <%
        }
    %>
    </rui:slImages>
</rui:searchList>
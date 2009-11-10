<%
    def defaultFieldsString = uiElement.defaultFields.split(",").toString();
    defaultFieldsString = defaultFieldsString.substring(1, defaultFieldsString.length()-1);
%>
<rui:searchList id="${uiElement.name}" url="../${uiElement.url}" rootTag="${uiElement.rootTag}" contentPath="${uiElement.contentPath}" keyAttribute="${uiElement.keyAttribute}"
    lineSize="${uiElement.lineSize}" title="${uiElement.title}" queryParameter="${uiElement.queryParameter}" totalCountAttribute="${uiElement.totalCountAttribute}" offsetAttribute="${uiElement.offsetAttribute}" sortOrderAttribute="${uiElement.sortOrderAttribute}"
    pollingInterval="${uiElement.pollingInterval}" defaultFields='\${[${defaultFieldsString}]}' ${uiElement.showMax !=0?"showMax='"+uiElement.showMax+"'":""} defaultQuery="${uiElement.defaultQuery}" extraPropertiesToRequest="${uiElement.extraPropertiesToRequest}" 
    defaultSearchClass="${uiElement.defaultSearchClass}" ${uiElement.searchInEnabled?"searchClassesUrl='../"+uiElement.searchClassesUrl+"'":""} timeout="${uiElement.timeout}" searchInEnabled="${uiElement.searchInEnabled}"
    <%
    uiElement.getActionTrigers().each{eventName, actionTriggers->
         def actionString = uiElement.getActionsString(actionTriggers);
    %>
        on${eventName.substring(0,1).toUpperCase()}${eventName.substring(1)}="${actionString}"
    <%
    }
    %>
>
    <%
        uiElement.subComponents.each{subComponent->
            if(subComponent.class.name == "com.ifountain.rui.designer.model.UiSearchListTimeRangeSelector")
            {
    %>
        <rui:timeRangeSelector url="../${subComponent.url}" buttonConfigurationUrl="../${subComponent.buttonConfigurationUrl}" fromTimeProperty="${subComponent.fromTimeProperty}"
            tooltipProperty="${subComponent.tooltipProperty}" toTimeProperty="${subComponent.toTimeProperty}" stringToTimeProperty="${subComponent.stringToTimeProperty}" stringFromTimeProperty="${subComponent.stringFromTimeProperty}" timeAxisLabelProperty="${subComponent.timeAxisLabelProperty}" valueProperties="${subComponent.valueProperties}">
    </rui:timeRangeSelector>
    <%
            }
        }
    %>
    <rui:slMenuItems>
        <%
            uiElement.getRowMenuItems().each{menuItem->
                if(menuItem.parentMenuItemId == null || menuItem.parentMenuItemId == ""){
                def menuActionString = menuItem.getActionString();
                def actionString = menuActionString ? "action=\"${menuActionString}\"": "";
                def menuItemVisiblePropertyName = menuItem.name+ "Visible";
                println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(menuItemVisiblePropertyName, menuItem.visible, true);
        %>
        <rui:slMenuItem id="${menuItem.name}" label="${menuItem.label}" visible="\${${menuItemVisiblePropertyName}}" ${actionString}>
               <%
                    if(!menuItem.childMenuItems.isEmpty())
                    {
                %>
                    <rui:slSubmenuItems>
                        <%
                            menuItem.childMenuItems.each{subMenuItem->
                                def subMenuActionString = subMenuItem.getActionString();
                                def subActionString = subMenuActionString ? "action=\"${subMenuActionString}\"": "";
                                def subMenuItemVisiblePropertyName = subMenuItem.name+ "Visible";
                                println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(subMenuItemVisiblePropertyName, subMenuItem.visible, true);
                        %>
                            <rui:slMenuItem id="${subMenuItem.name}" label="${subMenuItem.label}" ${subActionString} visible="\${${subMenuItemVisiblePropertyName}}"></rui:slMenuItem>
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
            }
        %>
    </rui:slMenuItems>
    <rui:slPropertyMenuItems>
        <%
            uiElement.propertyMenuItems.each{menuItem->
                if(menuItem.parentMenuItem == null )
                {
                    def menuActionString = menuItem.getActionString();
                    def actionString = menuActionString ? "action=\"${menuActionString}\"": "";
                    def propertyMenuItemVisiblePropertyName = menuItem.name+ "Visible";
                    println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(propertyMenuItemVisiblePropertyName, menuItem.visible, true);
        %>
        <rui:slMenuItem id="${menuItem.name}" label="${menuItem.label}" ${actionString} visible="\${${propertyMenuItemVisiblePropertyName}}">
               <%
                    if(!menuItem.childMenuItems.isEmpty())
                    {
                %>
                    <rui:slSubmenuItems>
                        <%
                            menuItem.childMenuItems.each{subMenuItem->
                                def subMenuActionString = subMenuItem.getActionString();
                                def subActionString = subMenuActionString ? "action=\"${subMenuActionString}\"": "";
                                def propertySubMenuItemVisiblePropertyName = subMenuItem.name+ "Visible";
                                println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(propertySubMenuItemVisiblePropertyName, subMenuItem.visible, true);
                        %>
                            <rui:slMenuItem id="${subMenuItem.name}" label="${subMenuItem.label}" ${subActionString} visible="\${${propertySubMenuItemVisiblePropertyName}}"></rui:slMenuItem>
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
            }
        %>
    </rui:slPropertyMenuItems>
     <rui:slFields>
    <%
        uiElement.fields.each{field->
            def fieldsString = field.fields.split(",").toString();
            fieldsString = fieldsString.substring(1, fieldsString.length()-1);
            def expVisiblePropertyName = "fieldExpression"+field._designerKey+ "Visible";
            println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(expVisiblePropertyName, field.exp, true);
    %>
        <rui:slField exp="\${${expVisiblePropertyName}}" fields='\${[${fieldsString}]}'></rui:slField>
    <%
        }
    %>
    </rui:slFields>
    <rui:slImages>
    <%
        uiElement.images.each{image->
            def imageMenuItemVisiblePropertyName = "image"+image._designerKey+ "Visible";
            println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(imageMenuItemVisiblePropertyName, image.visible, true);
    %>
        <rui:slImage visible="\${${imageMenuItemVisiblePropertyName}}" src="../${image.src}"></rui:slImage>
    <%
        }
    %>
    </rui:slImages>
</rui:searchList>
<rui:searchGrid id="${uiElement.name}" url="../${uiElement.url}" queryParameter="${uiElement.queryParameter}" rootTag="${uiElement.rootTag}" contentPath="${uiElement.contentPath}" bringAllProperties="${uiElement.bringAllProperties}" 
        keyAttribute="${uiElement.keyAttribute}"  title="${uiElement.title}" pollingInterval="${uiElement.pollingInterval}" fieldsUrl="../${uiElement.fieldsUrl}" viewType="${uiElement.viewType}"
        queryEnabled="${uiElement.queryEnabled}" searchInEnabled="${uiElement.searchInEnabled}" defaultQuery="${uiElement.defaultQuery}" timeout="${uiElement.timeout}" multipleFieldSorting="${uiElement.multipleFieldSorting}" maxRowsDisplayed="${uiElement.maxRowsDisplayed}"
        defaultSearchClass="${uiElement.defaultSearchClass}" defaultView="${uiElement.defaultView}" ${uiElement.queryEnabled && uiElement.searchInEnabled?"searchClassesUrl='../"+uiElement.searchClassesUrl+"'":""} extraPropertiesToRequest="${uiElement.extraPropertiesToRequest}"
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
    <rui:sgMenuItems>
    <%
        uiElement.getRowMenuItems().each{menuItem->
            if(menuItem.parentMenuItemId == null || menuItem.parentMenuItemId == ""){
                def menuActionString = menuItem.getActionString();
                def actionString = menuActionString ? "action=\"${menuActionString}\"": "";
                def visiblePropertyName = menuItem.name+ "Visible";
                println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(visiblePropertyName, menuItem.visible, true);
    %>
        <rui:sgMenuItem id="${menuItem.name}" label="${menuItem.label}" visible="\${${visiblePropertyName}}" ${actionString}>
            <%
                if(!menuItem.childMenuItems.isEmpty())
                {
            %>
                <rui:sgSubmenuItems>
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
                </rui:sgSubmenuItems>
            <%
                    }
            %>
        </rui:sgMenuItem>
    <%

          }
        }
    %>
    </rui:sgMenuItems>
     <rui:sgMultiSelectionMenuItems>
    <%
        uiElement.getMultiSelectionMenuItems().each{menuItem->
            if(menuItem.parentMenuItemId == null || menuItem.parentMenuItemId == ""){
                def menuActionString = menuItem.getActionString();
                def actionString = menuActionString ? "action=\"${menuActionString}\"": "";
                def visiblePropertyName = menuItem.name+ "Visible";
                println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(visiblePropertyName, menuItem.visible, true);
    %>
        <rui:sgMenuItem id="${menuItem.name}" label="${menuItem.label}" visible="\${${visiblePropertyName}}" ${actionString}>
            <%
                if(!menuItem.childMenuItems.isEmpty())
                {
            %>
                <rui:sgSubmenuItems>
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
                </rui:sgSubmenuItems>
            <%
                    }
            %>
        </rui:sgMenuItem>
    <%

          }
        }
    %>
    </rui:sgMultiSelectionMenuItems>
    <rui:sgImages>
    <%
        uiElement.images.each{image->
            def imageVisiblePropertyName = "image"+image._designerKey+ "Visible";
            println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(imageVisiblePropertyName, image.visible, true);
    %>
        <rui:sgImage visible="\${${imageVisiblePropertyName}}" src="../${image.src}"></rui:sgImage>
    <%
        }
    %>
    </rui:sgImages>
    <rui:sgColumns>
    <%
        def columns = uiElement.columns.sort{it.columnIndex};
        columns.each{column->
        def sortByString = column.sortBy ? "sortBy=\"${column.sortBy}\"":""
        def sortOrderString = column.sortBy ? "sortOrder=\"${column.sortOrder}\"":""
    %>
        <rui:sgColumn attributeName="${column.attributeName}" colLabel="${column.colLabel}" width="${column.width}" ${sortByString} ${sortOrderString} type="${column.type}">
            <%
                if(column.type == 'image')
                {
            %>
            <rui:sgColumnImages>
                <%
                    column.images.each{colImage->
                        def colImageMenuItemVisiblePropertyName = "image"+colImage._designerKey+ "Visible";
                        println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(colImageMenuItemVisiblePropertyName, colImage.visible, true);
                %>
                <rui:sgColumnImage src="../${colImage.src}" visible="\${${colImageMenuItemVisiblePropertyName}}" align="${colImage.align}"></rui:sgColumnImage>
                <%
                    }
                %>
            </rui:sgColumnImages>
            <%
                }
            %>

        </rui:sgColumn>
    <%
        }
    %>
    </rui:sgColumns>
    <rui:sgRowColors>
    <%
        uiElement.rowColors.each{rowColor->
            def textColor = rowColor.textColor;
            def rowColorVisiblePropertyName = "rowColor"+rowColor._designerKey+ "Visible";
            println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(rowColorVisiblePropertyName, rowColor.visible, true);
    %>
        <rui:sgRowColor color="${rowColor.color}" visible="\${${rowColorVisiblePropertyName}}" ${textColor?"textColor='"+textColor+"'":""}></rui:sgRowColor>
    <%
        }
    %>
    </rui:sgRowColors>
</rui:searchGrid>
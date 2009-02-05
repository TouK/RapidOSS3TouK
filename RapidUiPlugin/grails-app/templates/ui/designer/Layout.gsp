<%
    def layoutPrinter = {layout, parentElement->
%>

    var layout${layout.id} = new YAHOO.widget.Layout(${parentElement!=null?parentElement:"el"}, {
        <%
            if(layout.parentUnit)
            {
                println "parent:layout${layout.parentUnit.parentLayout.id},"
            }
            else
            {
                println "parent:layout,"   
            }
        %>
        units: [
        <%
            def units = layout.units;
            for(int i=0; i < units.size(); i++)
            {
                def layoutUnitBody = null;
                layoutunit = units[i];
                if(layoutunit.component)
                {
                    layoutUnitBody = "YAHOO.rapidjs.Components['${layoutunit.component.name}'].container.id";       
                }
                else if(layoutunit.contentFile != null && layoutunit.contentFile != "")
                {
                    layoutUnitBody = "'${com.ifountain.rui.util.DesignerTemplateUtils.getContentDivId(layoutunit.contentFile)}'";
                }
                if(layoutunit.class.name == "ui.designer.UiTopUnit"){
        %>
                { position: 'top', scroll: ${layoutunit.scroll}, ${layoutUnitBody?"body:"+layoutUnitBody+",":""} height: ${layoutunit.height}, resize: ${layoutunit.resize}, gutter: '${layoutunit.gutter}', ${layoutunit.maxHeight != 0?"maxHeight:"+layoutunit.maxHeight+",":""}  ${layoutunit.minHeight != 0?"minHeight:"+layoutunit.minHeight+",":""} useShim:${layoutunit.useShim}}
        <%
            }else if(layoutunit.class.name == "ui.designer.UiRightUnit")
        {
        %>
                { position: 'right', scroll: ${layoutunit.scroll}, width: ${layoutunit.width}, resize: ${layoutunit.resize}, ${layoutUnitBody?"body:"+layoutUnitBody+",":""} gutter: '${layoutunit.gutter}', ${layoutunit.maxWidth!=0?"maxWidth:"+layoutunit.maxWidth+",":""}  ${layoutunit.minWidth!=0?"minWidth:"+layoutunit.minWidth+",":""} useShim:${layoutunit.useShim} }
        <%
        }else if(layoutunit.class.name == "ui.designer.UiLeftUnit"){
        %>
                { position: 'left', scroll: ${layoutunit.scroll}, width: ${layoutunit.width}, resize: ${layoutunit.resize}, ${layoutUnitBody?"body:"+layoutUnitBody+",":""} gutter: '${layoutunit.gutter}', ${layoutunit.maxWidth!=0?"maxWidth:"+layoutunit.maxWidth+",":""}  ${layoutunit.minWidth!=0?"minWidth:"+layoutunit.minWidth+",":""} useShim:${layoutunit.useShim}}
        <%
        }else if(layoutunit.class.name == "ui.designer.UiBottomUnit"){
        %>
                { position: 'bottom', scroll: ${layoutunit.scroll}, ${layoutUnitBody?"body:"+layoutUnitBody+",":""} height: ${layoutunit.height}, resize: ${layoutunit.resize}, gutter: '${layoutunit.gutter}', ${layoutunit.maxHeight != 0?"maxHeight:"+layoutunit.maxHeight+",":""}  ${layoutunit.minHeight != 0?"minHeight:"+layoutunit.minHeight+",":""} useShim:${layoutunit.useShim}}
        <%
        }else if(layoutunit.class.name == "ui.designer.UiCenterUnit"){
        %>
                { position: 'center', ${layoutUnitBody?"body:"+layoutUnitBody+",":""} gutter: '${layoutunit.gutter}', scroll: ${layoutunit.scroll}, useShim:${layoutunit.useShim} }
        <%
        }
                if(i != units.size()-1)
                print ","
            }
        %>
        ]
    });
    layout${layout.id}.on('render', function() {
    <%
        units.each{layoutunit->
            def className = layoutunit.getClass().simpleName;
            def unitPosition = className.substring(2, className.length()-4).toLowerCase();
            def elName = "el${layout.id}${unitPosition}";
    %>
        var ${elName} = layout${layout.id}.getUnitByPosition('${unitPosition}').get('wrap');
    <%
            if(layoutunit.childLayout)
            {
                layoutPrinter(layoutunit.childLayout, elName);
            }
      }
    %>

    });
    layout${layout.id}.render();
    <%
        layout.units.each{unit->
              if(unit.component != null)  {
                def className = unit.getClass().simpleName;
            def unitPosition = className.substring(2, className.length()-4).toLowerCase();
            def layoutVarName = "layoutUnitJsObjectlayout"+layout.id+unitPosition;
    %>
            var ${layoutVarName} = layout${layout.id}.getUnitByPosition('${unitPosition}');
            var ${layoutVarName}component = YAHOO.rapidjs.Components['${unit.component.name}'];
    ${layoutVarName}component.resize(${layoutVarName}.getSizes().body.w, ${layoutVarName}.getSizes().body.h);
        layout${layout.id}.on('resize', function() {
            ${layoutVarName}component.resize(${layoutVarName}.getSizes().body.w, ${layoutVarName}.getSizes().body.h);
        });
    <%
                }
            }
        }
        layoutPrinter(uiElement, null);
    %>


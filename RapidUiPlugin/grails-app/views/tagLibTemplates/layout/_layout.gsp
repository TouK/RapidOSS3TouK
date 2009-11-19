<script type="text/javascript">
YAHOO.util.Event.onDOMReady(function() {
<%
    layoutPrinter = {layoutConfiguration, parentLayoutUnitName->
        if(parentLayoutUnitName != null){
%>
    var ${parentLayoutUnitName}Wrapper = ${parentLayoutUnitName}.get('wrap');
<%
        }
%>        
    var layout${layoutConfiguration.attributes.id} = new YAHOO.widget.Layout(${parentLayoutUnitName!=null?parentLayoutUnitName+"Wrapper,":""} {
        <%
            if(layoutConfiguration.attributes.parentLayout)
            {
                println "parent:layout${layoutConfiguration.attributes.parentLayout},"
            }
        %>
        units: [
        <%
            def units = layoutConfiguration.units;
            if(units != null)
            {
                for(int i=0; i < units.size(); i++)
                {
                    layoutunit = units[i];
        %>
            ${layoutunit.content}         
        <%
                    if(i != units.size()-1)
                    print ","
                }
            }
        %>
        ]
    });
    <g:if test="${parentLayoutUnitName == null}">
       window.yuiLayout = layout${layoutConfiguration.attributes.id};
    </g:if>
    <g:else>
        ${parentLayoutUnitName}.childLayout = layout${layoutConfiguration.attributes.id}; 
    </g:else>
    layout${layoutConfiguration.attributes.id}.on('render', function() {
    <%
        units.each{layoutunit->
            def unitPosition = layoutunit.attributes.position;
            def elName = "layoutUnit${layoutConfiguration.attributes.id}${layoutunit.attributes.position}";
    %>
        var ${elName} = layout${layoutConfiguration.attributes.id}.getUnitByPosition('${unitPosition}');
    <%
            if(layoutunit.childLayout)
            {
                layoutPrinter(layoutunit.childLayout, elName);
            }
        }
    %>

    });
    layout${layoutConfiguration.attributes.id}.render();
    <%
        layoutConfiguration.units.each{unit->
                def unitPosition = unit.attributes.position;
                def layoutVarName = "layoutUnitJsObjectlayout"+layoutConfiguration.attributes.id+unitPosition;
    %>
            var ${layoutVarName} = layout${layoutConfiguration.attributes.id}.getUnitByPosition('${unitPosition}');
            <%
                if(unit.attributes.component)
                {
            %>
            var ${layoutVarName}component = YAHOO.rapidjs.Components["${unit.attributes.component}"]
            if(${layoutVarName}component != null)
            {
                ${layoutVarName}component.resize(${layoutVarName}.getSizes().body.w, ${layoutVarName}.getSizes().body.h);
                layout${layoutConfiguration.attributes.id}.on('resize', function() {
                    ${layoutVarName}component.resize(${layoutVarName}.getSizes().body.w, ${layoutVarName}.getSizes().body.h);
                });
            }
            <%
                }
            %>
    <%
        }
    }
    layoutPrinter(layout, null);
    %>
});
</script>

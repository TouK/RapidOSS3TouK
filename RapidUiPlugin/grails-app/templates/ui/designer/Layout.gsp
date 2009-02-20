<%
    layoutPrinter = {layout->
        def tagName = "innerLayout";
        def units = layout.units;
%>
    <rui:${tagName} id="${layout.id}">
        <%
            for(int i=0; i < units.size(); i++)
            {
                def layoutUnit = units[i];
                def className = layoutUnit.getClass().simpleName;
                def unitPosition = className.substring(2, className.length()-4).toLowerCase();
                def attributes = ["position='${unitPosition}'"];
                layoutUnit.getPropertiesList().each{prop->
                    if(!prop.isRelation && !prop.isOperationProperty)
                    {
                        def propValue = layoutUnit[prop.name]
                        if(propValue != "" && propValue != "0" && propValue != 0 && propValue != null)
                        {
                            attributes.add("${prop.name}='${layoutUnit[prop.name]}'");
                        }
                    }
                }
                if(layoutUnit.component != null)
                {
                    attributes.add("component='${layoutUnit.component.name}'");
                }
                else if(layoutUnit.contentFile != "" && layoutUnit.contentFile != null)
                {
                    attributes.add("body='${layoutUnit.getContentFileDivId()}'");
                }
        %>
            <rui:layoutUnit ${attributes.join(" ")}>
        <%
                if(layoutUnit.childLayout != null)
                {
                    layoutPrinter(layoutUnit.childLayout);
                }
        %>
            </rui:layoutUnit>
        <%
                }
        %>
        </rui:${tagName}>
        <%
            }
        %>

<%
    layoutPrinter(uiElement);
%>


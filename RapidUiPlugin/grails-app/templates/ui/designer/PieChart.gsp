<%
    def colors = uiElement.colors.split(",").findAll {it !=""}
    def colorsString =colors.join(",");
    if(colorsString != "")
    {
        colorsString = "\${["+colorsString.substring(1, colorsString.length()-1)+"]}";
    }
%>
<rui:pieChart id="${uiElement.name.encodeAsHTML()}" url="../${uiElement.url.encodeAsHTML()}" dataField="${uiElement.dataField.encodeAsHTML()}" contentPath="${uiElement.contentPath.encodeAsHTML()}" title="${uiElement.title.encodeAsHTML()}"
        categoryField="${uiElement.categoryField.encodeAsHTML()}" legend="${uiElement.legend.encodeAsHTML()}" swfURL="../js/yui/charts/assets/charts.swf"
            ${colorsString != ""?"colors=\""+colorsString+"\"":""}></rui:pieChart>
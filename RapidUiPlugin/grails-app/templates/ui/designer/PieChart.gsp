<%
    def colors = uiElement.colors.split(",").findAll {it !=""}
    def colorsString =colors.join(",");
    if(colorsString != "")
    {
        colorsString = "\${["+colorsString.substring(1, colorsString.length()-1)+"]}";
    }
%>
<rui:pieChart id="${uiElement.name}" url="../${uiElement.url}" dataField="${uiElement.dataField}" contentPath="${uiElement.contentPath}" title="${uiElement.title}"
        categoryField="${uiElement.categoryField}" legend="${uiElement.legend}" swfURL="../js/yui/charts/assets/charts.swf" pollingInterval="${uiElement.pollingInterval}"
            ${colorsString != ""?"colors=\""+colorsString+"\"":""}></rui:pieChart>
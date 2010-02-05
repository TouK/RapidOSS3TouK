<%
    def urlString = uiElement.url != "" ? uiElement.url.startsWith("http") ? 'url="' + uiElement.url + '"' : 'url="../' + uiElement.url + '"' : '';
%>
<rui:html id="${uiElement.name}" iframe="${uiElement.iframe}"  timeout="${uiElement.timeout}" ${urlString} pollingInterval="${uiElement.pollingInterval}" title="${uiElement.title}"></rui:html>
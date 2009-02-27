<html>
<head>
    <meta name="layout" content="${tab.webPage.name.encodeAsHTML()}Layout" />
</head>
<body>
<%
    print tabContent;
    def layoutUnitsHavingContentFile = ui.designer.UiLayoutUnit.getLayoutUnitHavingContentFile(tab.layout);
%>

<%
    layoutUnitsHavingContentFile.each{layoutUnit->
%>
    <div id="${layoutUnit.getContentFileDivId().encodeAsHTML()}">
        <rui:include template="${layoutUnit.contentFile.encodeAsHTML()}"></rui:include>
    </div>
<%
    }
%>
<%
    if(tab.contentFile != null && tab.contentFile != "")
    {
%>
       <rui:include template="${tab.contentFile.encodeAsHTML()}"></rui:include>
<%
    }
%>
<%
    tab.getGlobalActionTrigers().each{String triggerName, triggers->
        def eventName = triggerName.substring(0,1).toUpperCase()+triggerName.substring(1);
        def actionString = tab.getActionsString(triggers); 
%>
       <rui:globalEvent on${eventName.encodeAsHTML()}="${actionString}"></rui:globalEvent>
<%
    }
%>
<rui:layout id="layout">
    <rui:layoutUnit position="top" body="top" resize="false" height="45"></rui:layoutUnit>
    <rui:layoutUnit position="center" gutter="1px">
        ${layoutContent}
    </rui:layoutUnit>
</rui:layout>
</body>
</html>
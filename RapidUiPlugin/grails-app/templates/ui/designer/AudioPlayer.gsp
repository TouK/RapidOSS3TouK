<%
    def playConditionString = "";
    if(uiElement.playCondition != null && uiElement.playCondition.trim() != ""){
        def playConditionPropertyName = uiElement.name+ "PlayCondition";
        println com.ifountain.rui.util.DesignerTemplateUtils.declareVariable(playConditionPropertyName, uiElement.playCondition, true);
        playConditionString = "playCondition=\"\$${playConditionPropertyName}\"";
        
    }
%>
<rui:audioPlayer id="${uiElement.name}" url="../${uiElement.url}" volume="${uiElement.volume}" pollingInterval="${uiElement.pollingInterval}"
        title="${uiElement.title}" soundFile="../${uiElement.soundFile}" timeout="${uiElement.timeout}" ${playConditionString}>
</rui:audioPlayer>
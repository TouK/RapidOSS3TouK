package com.ifountain.rui.util


/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 2, 2009
* Time: 6:20:36 PM
* To change this template use File | Settings | File Templates.
*/
class DesignerTemplateUtils {
    public static getContentDivId(String contentFilePath)
    {
        def contentFile = new File("web-app/"+contentFilePath);
        return getContentDivId(contentFile);
    }
    public static getContentDivId(File contentFile)
    {
        def divId = contentFile.path.replaceAll("/", ".")
        divId = divId.replaceAll("\\\\", ".")
        return divId;
    }

    public static declareVariable(String variableName, String variableValue, boolean isString)
    {
        if(isString)
        {
            variableValue = "\"${variableValue.escape()}\"";
        }
        return "<%\n"+
        "${variableName}=${variableValue}\n" + 
        "%>";
    }

    public static def getActionsString(actionTriggers){
        def actionNames = actionTriggers.action.name;
        def actionsString;
        if(actionNames.size() > 0){
            return "\${['" + actionNames.join("','") + "']}"
        }
        else{
            return "\${[]}"
        }
        return actionsString
    }
}
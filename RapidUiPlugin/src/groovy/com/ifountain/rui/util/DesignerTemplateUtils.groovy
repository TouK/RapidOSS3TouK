package com.ifountain.rui.util

import ui.designer.UiLayoutUnit

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

    public static List getLayoutContentFiles(ui.designer.UiLayout layout)
    {
        def fileList = [];
        if(layout)
        {
            _getLayoutContentFiles(layout, fileList);
        }
        return fileList;
    }
    private static void _getLayoutContentFiles(ui.designer.UiLayout layout, List listOfFiles)
    {
        layout.units.each{UiLayoutUnit unit->
            if(unit.contentFile != null && unit.contentFile != "")
            {
                listOfFiles.add(new File("web-app/${unit.contentFile}"));
            }
            else if(unit.childLayout != null)
            {
                _getLayoutContentFiles (unit.childLayout, listOfFiles);
            }
        }
    }
}
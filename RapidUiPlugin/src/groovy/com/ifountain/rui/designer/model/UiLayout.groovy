package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.UiElmnt
import com.ifountain.rui.designer.DesignerSpace

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 20, 2009
* Time: 6:26:14 PM
*/
class UiLayout extends UiElmnt {

    String tabId = "";
    String parentUnitId = "";

    public static Map metaData()
    {
        Map metaData = [
                help: "Layout.html",
                designerType: "Layout",
                canBeDeleted: true,
                display: "Layout",
                imageExpanded: 'images/rapidjs/component/tools/folder_open.gif',
                imageCollapsed: 'images/rapidjs/component/tools/folder.gif',
                propertyConfiguration: [:],
                childrenConfiguration: [
                        [designerType: "CenterUnit"],
                        [designerType: "TopUnit"],
                        [designerType: "BottomUnit"],
                        [designerType: "LeftUnit"],
                        [designerType: "RightUnit"]
                ]
        ];
        return metaData;
    }

    public List getUnits() {
        return DesignerSpace.getInstance().getUiElements(UiLayoutUnit).values().findAll {it.parentLayoutId == _designerKey};
    }
}
package ui.designer

import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 3, 2009
* Time: 10:51:47 AM
* To change this template use File | Settings | File Templates.
*/
class UiColumnOperations extends AbstractDomainOperation
{

    public static Map metaData()
    {
        Map metaData = [
                help:"Column.html",
                designerType: "Column",
                canBeDeleted: true,
                displayFromProperty: "attributeName",

                propertyConfiguration: [
                        attributeName: [descr: "The data node attribute which will be shown in the column"],
                        colLabel: [descr: "Title of the column"],
                        width: [descr: "Width of the column"],
                        sortBy: [descr: "Parameter to render component whether sorted according to this column or not"],
                        sortOrder: [descr: "The order of the sort when \"sortBy\" property is \"true\"", required:false],
                        columnIndex: [descr: "The order of the column in the grid."]

                ],
                childrenConfiguration: []
        ];
        return metaData;
    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = xmlNode.attributes();
        attributes.component = parentElement;
        return DesignerUtils.addUiObject(UiColumn, attributes, xmlNode);
    }

}
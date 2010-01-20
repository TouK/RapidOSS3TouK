package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.DesignerSpace
import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 1:48:05 PM
*/
class UiRequestAction extends UiAction {
    public static String GET = "GET"
    public static String POST = "POST"

    String url;
    String submitType = GET;
    String components = "";
    Long timeout = 60;

    public static Map metaData()
    {
        Map metaData = [
                help: "Request Action.html",
                designerType: "RequestAction",
                canBeDeleted: true,
                displayFromProperty: "name",
                imageExpanded: "images/rapidjs/designer/arrow_refresh_small.png",
                imageCollapsed: "images/rapidjs/designer/arrow_refresh_small.png",
                propertyConfiguration: [
                        url: [descr: "The URL to be used for requests to the server", validators: [blank: false, nullable: false]],
                        timeout: [descr: "The time interval in seconds to wait the server request completes successfully before aborting.", required: true],
                        components: [descr: "The list of component names which are related with this action (For example which components' error dialog should show if an error occurred during request)", required: true],
                        submitType: [descr: "HTTP method used to send the request to the server. Possible values are \"GET\" and \"POST\".", validators: [blank: false, inList: [GET, POST]]]
                ],
                childrenConfiguration: [
                        [
                                designerType: "RequestParameters",
                                metaData: [
                                        help: "RequestAction RequestParameters.html",
                                        designerType: "RequestParameters",
                                        display: "RequestParameters",
                                        canBeDeleted: false,
                                        imageExpanded: "images/rapidjs/designer/link_go.png",
                                        imageCollapsed: "images/rapidjs/designer/link_go.png",
                                        propertyConfiguration: [
                                        ],
                                        childrenConfiguration: [
                                                [designerType: "RequestParameter", isMultiple: true]
                                        ]
                                ],
                                isMultiple: false
                        ]
                ]
        ];
        def parentMetaData = UiAction.metaData();
        def childConfiguration = [];
        childConfiguration.addAll(parentMetaData.childrenConfiguration)
        childConfiguration.addAll(metaData.childrenConfiguration)
        def propConfig = [:]
        propConfig.put("tabId", parentMetaData.propertyConfiguration.remove("tabId"))
        propConfig.put("name", parentMetaData.propertyConfiguration.remove("name"))
        propConfig.putAll(metaData.propertyConfiguration)
        propConfig.putAll(parentMetaData.propertyConfiguration)
        metaData.propertyConfiguration = propConfig;
        metaData.childrenConfiguration = childConfiguration;
        return metaData;
    }


    protected void addChildElements(GPathResult node, UiElmnt parent) {
        super.addChildElements(node, parent);
        def requestParamsNode = node."${UIELEMENT_TAG}".find {it.@"${DESIGNER_TYPE}".text() == "RequestParameters"}
        removeUnneccessaryAttributes(requestParamsNode)
        requestParamsNode."${UIELEMENT_TAG}".each {
            create(it, this)
        }
    }

    protected void populateStringAttributes(GPathResult node, UiElmnt parent) {
        super.populateStringAttributes(node, parent);
        def comps = [];
        def componentsAtt = node.@components.toString();
        if (componentsAtt != null && componentsAtt != "")
        {
            componentsAtt.split(",").each {componentName ->
                if (componentName != "")
                {
                    UiComponent comp = (UiComponent) DesignerSpace.getInstance().getUiElement(UiComponent, "${parent._designerKey}_${componentName.trim()}")
                    if (comp == null)
                    {
                        throw new Exception("Component ${componentName} could not found for request action ${node.@name}".toString());
                    }
                }
            }
        }
    }


    public List getParameters() {
        return DesignerSpace.getInstance().getUiElements(UiRequestParameter).values().findAll {it.actionId == _designerKey};
    }
}
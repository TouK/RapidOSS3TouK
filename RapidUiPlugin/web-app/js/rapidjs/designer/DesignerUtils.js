YAHOO.namespace("rapidjs", "rapidjs.designer");

YAHOO.rapidjs.designer.DesignerUtils = new function() {
    this.getItemType = function(designer, xmlData) {
        return xmlData.getAttribute(designer.treeTypeAttribute);
    };
    this.getTabNodeFromNode = function(designer, xmlData) {
        var currentTab = xmlData.getAttribute(designer.itemTabAtt);
        if (currentTab) {
            var tabNodes = designer.data.findAllObjects(designer.keyAttribute, currentTab, designer.contentPath);
            if (tabNodes.length > 0) {
                return tabNodes[0];
            }
        }
        return null;
    };
    this.getCurrentLayoutNode = function(designer, xmlData) {
        var currentTabNode = this.getTabNodeFromNode(designer, xmlData);
        if (currentTabNode) {
            var childNodes = currentTabNode.childNodes();
            for (var i = 0; i < childNodes.length; i++) {
                if (childNodes[i].getAttribute(designer.treeTypeAttribute) == "Layout") {
                    return childNodes[i]
                }
            }
        }
        return null;
    };
    this.getComponentNodesOfCurrentTab = function(designer, xmlData) {
        var currentTabNode = this.getTabNodeFromNode(designer, xmlData);
        if (currentTabNode) {
            var componentsNode;
            var childNodes = currentTabNode.childNodes();
            for (var i = 0; i < childNodes.length; i++) {
                if (this.getItemType(designer, childNodes[i]) == "Components") {
                    componentsNode = childNodes[i];
                    break;
                }
            }
            return componentsNode.childNodes()
        }
        return [];
    };
    this.getActionNodesOfCurrentTab = function(designer, xmlData) {
        var currentTabNode = this.getTabNodeFromNode(designer, xmlData);
        if (currentTabNode) {
            var actionsNode;
            var childNodes = currentTabNode.childNodes();
            for (var i = 0; i < childNodes.length; i++) {
                if (this.getItemType(designer, childNodes[i]) == "Actions") {
                    actionsNode = childNodes[i];
                    break;
                }
            }
            return actionsNode.childNodes()
        }
        return [];
    };
    this.getActionNamesOfCurrentTab = function(designer, xmlData) {
        var actionNames = [];
        var actionNodes = this.getActionNodesOfCurrentTab(designer, xmlData);
        for (var i = 0; i < actionNodes.length; i++) {
            actionNames[actionNames.length] = actionNodes[i].getAttribute("name");
        }
        return actionNames;
    };
    this.getComponentNamesOfCurrentTab = function(designer, xmlData) {
        var compNames = [];
        var componentNodes = this.getComponentNodesOfCurrentTab(designer, xmlData);
        for (var i = 0; i < componentNodes.length; i++) {
            compNames[compNames.length] = componentNodes[i].getAttribute("name");
        }
        return compNames;
    };
    this.getComponentsOfCurrentTab = function(designer, xmlData) {
        var comps = {};
        var componentNodes = this.getComponentNodesOfCurrentTab(designer, xmlData);
        for (var i = 0; i < componentNodes.length; i++) {
            var compName = componentNodes[i].getAttribute("name");
            var compType = componentNodes[i].getAttribute(designer.treeTypeAttribute);
            comps[compName] = compType;
        }
        return comps;
    };

}
var DesignerUtils = YAHOO.rapidjs.designer.DesignerUtils;
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

    this.getComponentsWithMenuItems = function(designer, xmlData) {
        var getMenuNames = function(xmlData, menuNamesArray) {
            var subMenuNodes = xmlData.childNodes();
            for (var n = 0; n < subMenuNodes.length; n++) {
                menuNamesArray.push(subMenuNodes[n].getAttribute('name'));
                getMenuNames(subMenuNodes[n], menuNamesArray)
            }

        }
        var menuConfig = {};
        var menuTypes = {'MenuItems':'node', 'PropertyMenuItems':'property', 'ToolbarMenus':'toolbar'}
        var componentNodes = this.getComponentNodesOfCurrentTab(designer, xmlData);
        for (var i = 0; i < componentNodes.length; i++) {
            var hasMenu = false;
            var compMenuConfig = {};
            var compChildNodes = componentNodes[i].childNodes();
            for (var j = 0; j < compChildNodes.length; j++) {
                var childDisplay = compChildNodes[j].getAttribute(designer.treeDisplayAttribute);
                if (childDisplay == "MenuItems" || childDisplay == "PropertyMenuItems" || childDisplay == "ToolbarMenus") {
                    var menuType = menuTypes[childDisplay];
                    var menuNames = [];
                    if (childDisplay == 'ToolbarMenus') {
                        var toolbarMenus = compChildNodes[j].childNodes();
                        for (var k = 0; k < toolbarMenus.length; k++) {
                            var menuNodes = toolbarMenus[k].childNodes();
                            if (menuNodes.length > 0) {
                                hasMenu = true;
                            }

                            for (var n = 0; n < menuNodes.length; n++) {
                                menuNames.push(menuNodes[n].getAttribute('name'));
                                getMenuNames(menuNodes[n], menuNames);
                            }
                        }
                    }
                    else {
                        var menuNodes = compChildNodes[j].childNodes();
                        if (menuNodes.length > 0) {
                            hasMenu = true;
                            for (var k = 0; k < menuNodes.length; k++) {
                                menuNames.push(menuNodes[k].getAttribute('name'));
                                getMenuNames(menuNodes[k], menuNames);
                            }
                        }
                    }
                    compMenuConfig[menuType] = menuNames;
                }
            }
            if (hasMenu) {
                menuConfig[componentNodes[i].getAttribute('name')] = compMenuConfig;
            }
        }
        return menuConfig
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
    this.getActionsOfCurrentTab = function(designer, xmlData) {
        var actions = {};
        var actionNodes = this.getActionNodesOfCurrentTab(designer, xmlData);
        for (var i = 0; i < actionNodes.length; i++) {
            var actionName = actionNodes[i].getAttribute("name");
            var actionType = actionNodes[i].getAttribute(designer.treeTypeAttribute);
            actions[actionName] = actionType
        }
        return actions;
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
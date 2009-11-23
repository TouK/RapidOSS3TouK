YAHOO.namespace("rapidjs", "rapidjs.designer");

YAHOO.rapidjs.designer.DesignerUtils = new function() {
    this.getItemType = function(xmlData) {
        return xmlData.getAttribute(window.designer.treeTypeAttribute);
    };
    this.getWebPageNodes = function() {
        return  window.designer.data.firstChild().firstChild().childNodes();
    };
    this.getWebPageNames = function() {
        var webPageNodes = this.getWebPageNodes();
        var webPageNames = [];
        for (var i = 0; i < webPageNodes.length; i++) {
            webPageNames.push(webPageNodes[i].getAttribute('name'))
        }
        return webPageNames;
    };
    this.getTabNodes = function(webPageName) {
        var webPageNodes = this.getWebPageNodes();
        var webPageNode = null;
        for (var i = 0; i < webPageNodes.length; i++) {
            if (webPageNodes[i].getAttribute('name') == webPageName) {
                webPageNode = webPageNodes[i];
                break;
            }
        }
        if (webPageNode) {
            return webPageNode.firstChild().childNodes();
        }
        else {
            return [];
        }
    }
    this.getTabNames = function(webPageName) {
        var tabNames = [];
        var tabNodes = this.getTabNodes(webPageName);
        for (var i = 0; i < tabNodes.length; i++) {
            tabNames.push(tabNodes[i].getAttribute('name'))
        }
        return tabNames;
    };
    this.findTabNode = function(webPageName, tabName) {
        var tabNodes = this.getTabNodes(webPageName);
        for (var i = 0; i < tabNodes.length; i++) {
            if (tabNodes[i].getAttribute('name') == tabName) {
                return tabNodes[i]
            }
        }
        return null;
    }
    this.getTabNodeFromNode = function(xmlData) {
        var designer = window.designer;
        var currentTab = xmlData.getAttribute(designer.itemTabAtt);
        if (currentTab) {
            var tabNodes = designer.data.findAllObjects(designer.keyAttribute, currentTab, designer.contentPath);
            if (tabNodes.length > 0) {
                return tabNodes[0];
            }
        }
        return null;
    };
    this.getCurrentLayoutNode = function(xmlData) {
        var designer = window.designer;
        var currentTabNode = this.getTabNodeFromNode(xmlData);
        if (currentTabNode) {
            return this.findChildNode(currentTabNode, "Layout");
        }
        return null;
    };
    this.getComponentNodesOfCurrentTab = function(xmlData) {
        var currentTabNode = this.getTabNodeFromNode(xmlData);
        if (currentTabNode) {
            var componentsNode = this.findChildNode(currentTabNode, "Components");
            return componentsNode.childNodes()
        }
        return [];
    };

    this.getComponentsWithMenuItems = function(xmlData) {
        var getMenuNames = function(xmlData, menuNamesArray) {
            var subMenuNodes = xmlData.childNodes();
            for (var n = 0; n < subMenuNodes.length; n++) {
                menuNamesArray.push(subMenuNodes[n].getAttribute('name'));
                getMenuNames(subMenuNodes[n], menuNamesArray)
            }

        }
        var menuConfig = {};
        var menuTypes = {'MenuItems':'node', 'PropertyMenuItems':'property', 'ToolbarMenus':'toolbar', "MultiSelectionMenuItems":"multiple"}
        var componentNodes = this.getComponentNodesOfCurrentTab(xmlData);
        for (var i = 0; i < componentNodes.length; i++) {
            var hasMenu = false;
            var compMenuConfig = {};
            var compChildNodes = componentNodes[i].childNodes();
            for (var j = 0; j < compChildNodes.length; j++) {
                var childDisplay = compChildNodes[j].getAttribute(window.designer.treeDisplayAttribute);
                if (childDisplay == "MenuItems" || childDisplay == "PropertyMenuItems" || childDisplay == "ToolbarMenus" || childDisplay == "MultiSelectionMenuItems") {
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
    this.getActionNodesOfCurrentTab = function(xmlData) {
        var currentTabNode = this.getTabNodeFromNode(xmlData);
        if (currentTabNode) {
            var actionsNode = this.findChildNode(currentTabNode, "Actions");
            return actionsNode.childNodes()
        }
        return [];
    };
    this.getActionNamesOfCurrentTab = function(xmlData) {
        var actionNames = [];
        var actionNodes = this.getActionNodesOfCurrentTab(xmlData);
        for (var i = 0; i < actionNodes.length; i++) {
            actionNames[actionNames.length] = actionNodes[i].getAttribute("name");
        }
        return actionNames;
    };
    this.getActionsOfCurrentTab = function(xmlData) {
        var actions = {};
        var actionNodes = this.getActionNodesOfCurrentTab(xmlData);
        for (var i = 0; i < actionNodes.length; i++) {
            var actionName = actionNodes[i].getAttribute("name");
            var actionType = actionNodes[i].getAttribute(window.designer.treeTypeAttribute);
            actions[actionName] = actionType
        }
        return actions;
    };
    this.getComponentNamesOfCurrentTab = function(xmlData) {
        var compNames = [];
        var componentNodes = this.getComponentNodesOfCurrentTab(xmlData);
        for (var i = 0; i < componentNodes.length; i++) {
            compNames[compNames.length] = componentNodes[i].getAttribute("name");
        }
        return compNames;
    };
    this.getComponentsOfCurrentTab = function(xmlData) {
        var comps = {};
        var componentNodes = this.getComponentNodesOfCurrentTab(xmlData);
        for (var i = 0; i < componentNodes.length; i++) {
            var compName = componentNodes[i].getAttribute("name");
            var compType = componentNodes[i].getAttribute(window.designer.treeTypeAttribute);
            comps[compName] = compType;
        }
        return comps;
    };
    this.findChildNode = function(node, childItemType) {
        var childNodes = node.childNodes();
        for (var i = 0; i < childNodes.length; i++) {
            var itemType = DesignerUtils.getItemType(childNodes[i])
            if (itemType == childItemType) {
                return childNodes[i];
            }
        }
        return null;
    };
    this.findComponentsNode = function(componentNode) {
        return componentNode.parentNode();
    };
    this.findActionsNode = function(componentNode) {
        var tabNode = this.findComponentsNode(componentNode).parentNode();
        return this.findChildNode(tabNode, "Actions");
    };
    this.findDialogsNode = function(componentNode) {
        var tabNode = this.findComponentsNode(componentNode).parentNode();
        return this.findChildNode(tabNode, "Dialogs");
    };
    this.findMenuItemsNode = function(componentNode) {
        var childNodes = componentNode.childNodes();
        for (var i = 0; i < childNodes.length; i++) {
            var childNode = childNodes[i]
            if (UIConfig.getDisplayName(this.getItemType(childNode), childNode) == 'MenuItems') {
                return childNode;
            }
        }
        return null;
    };

    this.findColumnsNode = function(componentNode) {
        var childNodes = componentNode.childNodes();
        for (var i = 0; i < childNodes.length; i++) {
            var childNode = childNodes[i]
            if (UIConfig.getDisplayName(this.getItemType(childNode), childNode) == 'Columns') {
                return childNode;
            }
        }
        return null;
    };

    this.getColumnNames = function(componentNode) {
        var columnNames = [];
        var columnsNode = this.findColumnsNode(componentNode);
        if(columnsNode){
            var columns = columnsNode.childNodes();
            for (var i = 0; i < columns.length; i++) {
                columnNames.push(columns[i].getAttribute('attributeName'));
            }
        }
        return columnNames
    };

    this.findFunctionArgumentsNode = function(functionActionNode) {
        return this.findChildNode(functionActionNode, "FunctionArguments")
    };
    this.findActionTriggersNode = function(actionNode) {
        return this.findChildNode(actionNode, "ActionTriggers")
    }

    this.getMenuItemNames = function(componentNode) {
        var menuItemsNode = this.findMenuItemsNode(componentNode);
        var menuItems = menuItemsNode.childNodes();
        var menuItemNames = [];
        for (var i = 0; i < menuItems.length; i++) {
            menuItemNames.push(menuItems[i].getAttribute("name"))
        }
        return menuItemNames;
    }

}
var DesignerUtils = YAHOO.rapidjs.designer.DesignerUtils;
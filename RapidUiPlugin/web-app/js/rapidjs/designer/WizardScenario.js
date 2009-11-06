YAHOO.rapidjs.designer.WizardScenario = function(container) {
    this.container = container;
    this.componentNode = null
    this.currentStep = -1;
    this.steps = null;
    this.name = null;
    this.htmlEl = null;
    this.currentStepEl = null;
    this.itemNames = null;
};

YAHOO.rapidjs.designer.WizardScenario.prototype = {
    start: function(componentNode) {
        this.componentNode = componentNode;
        if (!this.htmlEl) {
            this.htmlEl = YAHOO.ext.DomHelper.append(this.container, {tag:'div', cls:"r-designer-wizard-scwrp"});
        }
        this.generateItemNames();
        this.next();
    },
    generateItemNames : function() {
        if (this.itemNames == null) {
            this._generateItemNames();
        }
    },
    clearItemNames : function() {
        this.itemNames = null
    },
    hasNext: function() {
        return this.currentStep < this.steps.length - 1;
    },
    next:function() {
        if (this.currentStepEl) {
            YAHOO.util.Dom.setStyle(this.currentStepEl, 'display', 'none');
        }
        this.currentStep ++;
        var stepElId = this.getStepId();
        var stepEl = document.getElementById(stepElId);
        if (!stepEl) {
            stepEl = this.renderCurrentStep();
        }
        YAHOO.util.Dom.setStyle(stepEl, 'display', '');
        this.currentStepEl = stepEl;
        this.steps[this.currentStep].call(this);
    },
    back:function() {
        YAHOO.util.Dom.setStyle(this.currentStepEl, 'display', 'none');
        this.currentStep --;
        if (this.currentStep == -1) {
            this.cancel();
        }
        else {
            this.currentStepEl = document.getElementById(this.getStepId());
            YAHOO.util.Dom.setStyle(this.currentStepEl, 'display', '');
        }
    },
    canFinish: function() {
        return this.currentStep == this.steps.length - 1;
    },
    hasBack: function() {
        return this.currentStep > -1;
    },
    getStepId: function() {
        return this.name + '_' + this.currentStep;
    },
    finish: function() {
        this.finishScenario();
        this.clearItemNames();
        YAHOO.util.Dom.setStyle(this.currentStepEl, 'display', 'none');
        this.currentStep = -1
        this.currentStepEl = null;
        this.clearInputFields();
    },
    cancel:function() {
        YAHOO.util.Dom.setStyle(this.currentStepEl, 'display', 'none');
        this.currentStep = -1
        this.currentStepEl = null;
        this.clearInputFields();
    },
    getNextId: function() {
        return ++YAHOO.rapidjs.designer.WizardScenario.nextId
    },
    generateMenuItemName: function() {
        var menuItemNames = DesignerUtils.getMenuItemNames(this.componentNode);
        var itemNamePrefix = this.name + '_menuItem'
        return this.generateItemName(itemNamePrefix, menuItemNames);
    },
    generateActionName: function() {
        var actionNames = DesignerUtils.getActionNamesOfCurrentTab(this.componentNode);
        var itemNamePrefix = this.name + '_action'
        return this.generateItemName(itemNamePrefix, actionNames);
    },
    generateComponentName: function(componentType) {
        var compNames = DesignerUtils.getComponentNamesOfCurrentTab(this.componentNode);
        var itemNamePrefix = this.name + '_' + componentType
        return this.generateItemName(itemNamePrefix, compNames);
    },
    generateItemName : function(itemPrefix, nameList) {
        var itemName = itemPrefix + this.getNextId();
        while (YAHOO.rapidjs.ArrayUtils.contains(nameList, itemName)) {
            itemName = itemPrefix + this.getNextId();
        }
        return itemName;
    },
    getUrlExpression: function(url, paramsString) {
        var params = paramsString.split(",");
        var paramsHtml = [];
        for (var i = 0; i < params.length; i++) {
            var param = params[i].trim();
            if (param != '') {
                if (param == 'id') {
                    param = 'objectId'
                }
                paramsHtml.push(param + ':params.data.' + param)
            }
        }
        return "createURL('" + url + "', {" + paramsHtml.join(', ') + "})";
    },
    finishScenario:function() {
    },
    clearInputFields: function() {
    },
    renderCurrentStep: function() {
    },
    renderSummary:function() {
    },
    _generateItemNames: function() {
    },
    stepWithNoAction:function() {
    }

}

YAHOO.rapidjs.designer.WizardScenario.nextId = 0;

YAHOO.rapidjs.designer.MenuItemToHtmlScenario = function(container) {
    YAHOO.rapidjs.designer.MenuItemToHtmlScenario.superclass.constructor.call(this, container);
    this.steps = [this.stepWithNoAction, this.renderSummary]
    this.labelInput = null;
    this.urlInput = null;
    this.paramsInput = null;
    this.isDialogResizable = 'true';
    this.urlLabel = ''
};
YAHOO.lang.extend(YAHOO.rapidjs.designer.MenuItemToHtmlScenario, YAHOO.rapidjs.designer.WizardScenario, {
    finishScenario: function() {
        var designer = window.designer;
        var compNode = this.componentNode;
        var compName = this.componentNode.getAttribute('name');
        var currentTab = compNode.getAttribute(designer.itemTabAtt);
        //menu item
        var menuItemsNode = DesignerUtils.findMenuItemsNode(compNode);
        var menuItemNode = designer.createTreeNode(menuItemsNode, "MenuItem", {name:this.itemNames.menuItem, label:this.labelInput.value});
        designer.addExtraAttributesToChildNodes(menuItemsNode, currentTab);

        //html component
        var componentsNode = DesignerUtils.findComponentsNode(compNode)
        var htmlNode = designer.createTreeNode(componentsNode, "Html", {name:this.itemNames.htmlComponent});
        designer.addExtraAttributesToChildNodes(componentsNode, currentTab);

        //dialog
        var dialogsNode = DesignerUtils.findDialogsNode(compNode)
        var dialogNode = designer.createTreeNode(dialogsNode, "Dialog", {component:this.itemNames.htmlComponent, resizable:this.isDialogResizable});
        designer.addExtraAttributesToChildNodes(dialogsNode, currentTab);

        //function action
        var actionsNode = DesignerUtils.findActionsNode(compNode)
        var actionNode = designer.createTreeNode(actionsNode, "FunctionAction", {
            'component':this.itemNames.htmlComponent, 'function':'show', name:this.itemNames.action
        });
        var argumentsNode = DesignerUtils.findFunctionArgumentsNode(actionNode);
        var url = this.getUrlExpression(this.getUrlPrefix() + this.urlInput.value, this.paramsInput.value)
        var urlArg = designer.createTreeNode(argumentsNode, "FunctionArgument", {value:url});
        var titleArg = designer.createTreeNode(argumentsNode, "FunctionArgument");
        var triggersNode = DesignerUtils.findActionTriggersNode(actionNode);
        var trigger = designer.createTreeNode(triggersNode, "ActionTrigger", {
            type:'Menu', component:compName, event:this.itemNames.menuItem
        })
        designer.addExtraAttributesToChildNodes(actionsNode, currentTab);
        designer.refreshTree();
    },
    renderCurrentStep: function() {
        var dh = YAHOO.ext.DomHelper
        var stepEl;
        switch (this.currentStep) {
            case 0:
                stepEl = dh.append(this.htmlEl, {tag:'div', cls:'step', id:this.getStepId(),
                    html:'<table><tbody>' +
                         '<tr class="prop">' +
                         '<td class="name" valign="top"><label>Menu Item Label:</label></td>' +
                         '<td valign="top"><input></input></td>' +
                         '</tr>' +
                         '<tr class="prop">' +
                         '<td class="name" valign="top"><label>' + this.urlLabel + ':</label></td>' +
                         '<td valign="top"><input></input></td>' +
                         '</tr>' +
                         '<tr class="prop">' +
                         '<td class="name" valign="top"><label>Parameters:</label></td>' +
                         '<td valign="top"><input></input></td>' +
                         '</tr>' +
                         '</tbody></table><div style="padding:5px;">' + this.getDescription() + '</div>'});
                var inputs = stepEl.getElementsByTagName('input')
                this.labelInput = inputs[0]
                this.urlInput = inputs[1]
                this.paramsInput = inputs[2]
                break;
            case 1:
                stepEl = dh.append(this.htmlEl, {tag:'div', cls:'step', id:this.getStepId()})
                break;
            default:break
        }
        return stepEl;
    },
    renderSummary: function() {
        var compName = this.componentNode.getAttribute('name');
        var menuItem = this.itemNames.menuItem
        var htmlComp = this.itemNames.htmlComponent
        var action = this.itemNames.action
        this.currentStepEl.innerHTML = '<p>Following items will be created:<p><br>' +
                                       '<ul>' +
                                       '<li>A Menu Item with name <strong>' + menuItem + '</strong> for component <strong>' + compName + '</strong></li>' +
                                       '<li>An Html component with name <strong>' + htmlComp + '</strong></li>' +
                                       '<li>A Dialog for Html component <strong>' + htmlComp + '</strong></li>' +
                                       '<li>A Function Action with name  <strong>' + action + '</strong></li>' +
                                       '</ul>'
    },
    clearInputFields: function() {
        this.labelInput.value = '';
        this.urlInput.value = '';
        this.paramsInput.value = '';
    },
    _generateItemNames: function() {
        this.itemNames = {
            menuItem : this.generateMenuItemName(),
            htmlComponent:this.generateComponentName('html'),
            action: this.generateActionName()
        }
    },
    getUrlPrefix: function() {
        return '';
    },
    getDescription: function() {
    }
});

YAHOO.rapidjs.designer.ShellScriptScenario = function(container) {
    YAHOO.rapidjs.designer.ShellScriptScenario.superclass.constructor.call(this, container);
    this.name = "shellscriptscenario"
    this.urlLabel = "Script"
};

YAHOO.lang.extend(YAHOO.rapidjs.designer.ShellScriptScenario, YAHOO.rapidjs.designer.MenuItemToHtmlScenario, {
    getUrlPrefix: function() {
        return 'script/run/';
    },
    getDescription: function() {
        return 'Provide a label for your menu item and write down the server side groovy script you want to execute. As a starting point you can use ' +
               '<strong>executeShell</strong> script which is already deployed in RapidOSS server. Specify the script parameters as comma seperated values ' +
               '(name,severity,etc.) which can be selected from the data in the execution context. If your script requires different parameter keys you can modify it in ' +
               '<strong>arg0:url</strong> property of Function Action <strong>' + this.itemNames.action + '</strong> when you finished with the configuration.'

    }
});

YAHOO.rapidjs.designer.MenuToFormScenario = function(container) {
    YAHOO.rapidjs.designer.MenuToFormScenario.superclass.constructor.call(this, container);
    this.name = "menutoformscenario"
    this.isDialogResizable = "false"
    this.urlLabel = "Form Gsp"
};

YAHOO.lang.extend(YAHOO.rapidjs.designer.MenuToFormScenario, YAHOO.rapidjs.designer.MenuItemToHtmlScenario, {
    getDescription: function() {
        return 'Provide a label for your menu item and write down the server side .gsp file path (relative to web-app directory) which you build your form. ' +
               'Specify the form parameters as comma seperated values (name,severity,etc.) which can be selected from the data in the execution context. ' +
               'If your form requires different parameter keys you can modify it in <strong>arg0:url</strong> property of Function Action ' +
               '<strong>' + this.itemNames.action + '</strong> when you finished with the configuration.'

    }
});

YAHOO.rapidjs.designer.MenuToLinkScenario = function(container) {
    YAHOO.rapidjs.designer.MenuToLinkScenario.superclass.constructor.call(this, container);
    this.name = 'menutolinkscenario'
    this.steps = [this.stepWithNoAction, this.renderSummary]
    this.labelInput = null;
    this.urlInput = null;
    this.paramsInput = null;
};
YAHOO.lang.extend(YAHOO.rapidjs.designer.MenuToLinkScenario, YAHOO.rapidjs.designer.WizardScenario, {
    finishScenario: function() {
        var designer = window.designer;
        var compNode = this.componentNode;
        var compName = this.componentNode.getAttribute('name');
        var currentTab = compNode.getAttribute(designer.itemTabAtt);
        //menu item
        var menuItemsNode = DesignerUtils.findMenuItemsNode(compNode);
        var menuItemNode = designer.createTreeNode(menuItemsNode, "MenuItem", {name:this.itemNames.menuItem, label:this.labelInput.value});
        designer.addExtraAttributesToChildNodes(menuItemsNode, currentTab);

        //link action
        var actionsNode = DesignerUtils.findActionsNode(compNode)
        var url = this.getUrlExpression(this.urlInput.value, this.paramsInput.value);
        var actionNode = designer.createTreeNode(actionsNode, "LinkAction", {
            'url':url, 'target':'blank', name:this.itemNames.action
        });
        var triggersNode = DesignerUtils.findActionTriggersNode(actionNode);
        var trigger = designer.createTreeNode(triggersNode, "ActionTrigger", {
            type:'Menu', component:compName, event:this.itemNames.menuItem
        })
        designer.addExtraAttributesToChildNodes(actionsNode, currentTab);
        designer.refreshTree();
    },
    renderCurrentStep: function() {
        var dh = YAHOO.ext.DomHelper
        var stepEl;
        switch (this.currentStep) {
            case 0:
                stepEl = dh.append(this.htmlEl, {tag:'div', cls:'step', id:this.getStepId(),
                    html:'<table><tbody>' +
                         '<tr class="prop">' +
                         '<td class="name" valign="top"><label>Menu Item Label:</label></td>' +
                         '<td valign="top"><input></input></td>' +
                         '</tr>' +
                         '<tr class="prop">' +
                         '<td class="name" valign="top"><label>Url:</label></td>' +
                         '<td valign="top"><input></input></td>' +
                         '</tr>' +
                         '<tr class="prop">' +
                         '<td class="name" valign="top"><label>Parameters:</label></td>' +
                         '<td valign="top"><input></input></td>' +
                         '</tr>' +
                         '</tbody></table>' +
                         '<div style="padding:5;">Provide a label for your menu item ' +
                         'and write down the URL you want to launch. Specify the URL parameters as comma seperated values (name,severity,etc.) which can be ' +
                         'selected from the data in the execution context. If your URL requires different parameter keys you can modify it in <strong>url</strong> ' +
                         'property of Link Action <strong>' + this.itemNames.action + '</strong> when you finished with the configuration.' +
                         '</div>'});
                var inputs = stepEl.getElementsByTagName('input')
                this.labelInput = inputs[0]
                this.urlInput = inputs[1]
                this.paramsInput = inputs[2]
                break;
            case 1:
                stepEl = dh.append(this.htmlEl, {tag:'div', cls:'step', id:this.getStepId()})
                break;
            default:break
        }
        return stepEl;
    },
    renderSummary: function() {
        var compName = this.componentNode.getAttribute('name');
        var menuItem = this.itemNames.menuItem
        var action = this.itemNames.action
        this.currentStepEl.innerHTML = '<p>Following items will be created:<p><br>' +
                                       '<ul>' +
                                       '<li>A Menu Item with name <strong>' + menuItem + '</strong> for component <strong>' + compName + '</strong></li>' +
                                       '<li>A Link Action with name  <strong>' + action + '</strong></li>' +
                                       '</ul>'
    },
    clearInputFields: function() {
        this.labelInput.value = '';
        this.urlInput.value = '';
        this.paramsInput.value = '';
    },
    _generateItemNames: function() {
        this.itemNames = {
            menuItem : this.generateMenuItemName(),
            action: this.generateActionName()
        }
    }
});
YAHOO.rapidjs.designer.ColumnLinkScenario = function(container) {
    YAHOO.rapidjs.designer.ColumnLinkScenario.superclass.constructor.call(this, container);
    this.name = 'columnlinkscenario'
    this.steps = [this.renderColumns, this.renderSummary]
    this.columnSelect = null;
    this.urlInput = null;
    this.paramsInput = null;
};
YAHOO.lang.extend(YAHOO.rapidjs.designer.ColumnLinkScenario, YAHOO.rapidjs.designer.WizardScenario, {
    finishScenario: function() {
        var designer = window.designer;
        var compNode = this.componentNode;
        var compName = this.componentNode.getAttribute('name');
        var currentTab = compNode.getAttribute(designer.itemTabAtt);
        var columnName = this.columnSelect.selectedIndex > -1 ? this.columnSelect.options[this.columnSelect.selectedIndex].value : null;
        if (columnName) {
            var columnsNode = DesignerUtils.findColumnsNode(this.componentNode);
            var columns = columnsNode.childNodes();
            for (var i = 0; i < columns.length; i++) {
                var column = columns[i];
                if (column.getAttribute('attributeName') == columnName) {
                    column.setAttribute('type', 'link');
                    break;
                }
            }
        }
        //link action
        var actionsNode = DesignerUtils.findActionsNode(compNode)
        var url = this.getUrlExpression(this.urlInput.value, this.paramsInput.value);
        var actionNode = designer.createTreeNode(actionsNode, "LinkAction", {
            'url':url, 'target':'blank', name:this.itemNames.action,
            condition: columnName ? "params.key == '" + columnName + "'" : "false"
        });
        var triggersNode = DesignerUtils.findActionTriggersNode(actionNode);
        var trigger = designer.createTreeNode(triggersNode, "ActionTrigger", {
            type:'Component event', component:compName, event:'propertyClicked'
        })
        designer.addExtraAttributesToChildNodes(actionsNode, currentTab);
        designer.refreshTree();
    },

    renderColumns: function() {
        var columnNames = DesignerUtils.getColumnNames(this.componentNode);
        for (var i = 0; i < columnNames.length; i++) {
            var columnName = columnNames[i];
            SelectUtils.addOption(this.columnSelect, columnName, columnName);
        }
    },
    renderCurrentStep: function() {
        var dh = YAHOO.ext.DomHelper
        var compName = this.componentNode.getAttribute('name');
        var stepEl;
        switch (this.currentStep) {
            case 0:
                stepEl = dh.append(this.htmlEl, {tag:'div', cls:'step', id:this.getStepId(),
                    html:'<table><tbody>' +
                         '<tr class="prop">' +
                         '<td class="name" valign="top"><label>Column:</label></td>' +
                         '<td valign="top"><select></select></td>' +
                         '</tr>' +
                         '<tr class="prop">' +
                         '<td class="name" valign="top"><label>Url:</label></td>' +
                         '<td valign="top"><input></input></td>' +
                         '</tr>' +
                         '<tr class="prop">' +
                         '<td class="name" valign="top"><label>Parameters:</label></td>' +
                         '<td valign="top"><input></input></td>' +
                         '</tr>' +
                         '</tbody></table>' +
                         '<div style="padding:5;">Select a column which you have already defined for component <strong>' + compName + '</strong> ' +
                         'and write down the URL you want to launch. Specify the URL parameters as comma seperated values (name,severity,etc.) which can be ' +
                         'selected from the row data. If your URL requires different parameter keys you can modify it in <strong>url</strong> ' +
                         'property of Link Action <strong>' + this.itemNames.action + '</strong> when you finished with the configuration.' +
                         '</div>'});
                var inputs = stepEl.getElementsByTagName('input')
                this.columnSelect = stepEl.getElementsByTagName('select')[0]
                this.urlInput = inputs[0]
                this.paramsInput = inputs[1]
                break;
            case 1:
                stepEl = dh.append(this.htmlEl, {tag:'div', cls:'step', id:this.getStepId()})
                break;
            default:break
        }
        return stepEl;
    },
    renderSummary: function() {
        var action = this.itemNames.action
        this.currentStepEl.innerHTML = '<p>Following items will be created:<p><br>' +
                                       '<ul>' +
                                       '<li>A Link Action with name  <strong>' + action + '</strong></li>' +
                                       '</ul>'
    },
    clearInputFields: function() {
        SelectUtils.clear(this.columnSelect);
        this.urlInput.value = '';
        this.paramsInput.value = '';
    },
    _generateItemNames: function() {
        this.itemNames = {
            action: this.generateActionName()
        }
    }
});
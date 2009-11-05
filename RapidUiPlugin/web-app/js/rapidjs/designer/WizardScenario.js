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
        var urlArg = designer.createTreeNode(argumentsNode, "FunctionArgument", {value:this.urlInput.value});
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
                         '<td class="name" valign="top"><label>' + this.urlLabel +':</label></td>' +
                         '<td valign="top"><textarea></textarea></td>' +
                         '</tr>' +
                         '</tbody></table>'});
                this.labelInput = stepEl.getElementsByTagName('input')[0]
                this.urlInput = stepEl.getElementsByTagName('textarea')[0]
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
    },
    _generateItemNames: function() {
        this.itemNames = {
            menuItem : this.generateMenuItemName(),
            htmlComponent:this.generateComponentName('html'),
            action: this.generateActionName()
        }
    }
});

YAHOO.rapidjs.designer.ShellScriptScenario = function(container) {
    YAHOO.rapidjs.designer.ShellScriptScenario.superclass.constructor.call(this, container);
    this.name = "shellscriptscenario"
    this.urlLabel = "Script Url"
};

YAHOO.lang.extend(YAHOO.rapidjs.designer.ShellScriptScenario, YAHOO.rapidjs.designer.MenuItemToHtmlScenario, {});

YAHOO.rapidjs.designer.MenuToFormScenario = function(container) {
    YAHOO.rapidjs.designer.MenuToFormScenario.superclass.constructor.call(this, container);
    this.name = "menutoformscenario"
    this.isDialogResizable = "false"
    this.urlLabel = "Form Url"
};

YAHOO.lang.extend(YAHOO.rapidjs.designer.MenuToFormScenario, YAHOO.rapidjs.designer.MenuItemToHtmlScenario, {});

YAHOO.rapidjs.designer.MenuToLinkScenario = function(container) {
    YAHOO.rapidjs.designer.MenuToLinkScenario.superclass.constructor.call(this, container);
    this.name = 'menutolinkscenario'
    this.steps = [this.stepWithNoAction, this.renderSummary]
    this.labelInput = null;
    this.urlInput = null;
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
        var actionNode = designer.createTreeNode(actionsNode, "LinkAction", {
            'url':this.urlInput.value, 'target':'blank', name:this.itemNames.action
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
                         '<td valign="top"><textarea></textarea></td>' +
                         '</tr>' +
                         '</tbody></table>'});
                this.labelInput = stepEl.getElementsByTagName('input')[0]
                this.urlInput = stepEl.getElementsByTagName('textarea')[0]
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
};
YAHOO.lang.extend(YAHOO.rapidjs.designer.ColumnLinkScenario, YAHOO.rapidjs.designer.WizardScenario, {
    finishScenario: function() {
        var designer = window.designer;
        var compNode = this.componentNode;
        var compName = this.componentNode.getAttribute('name');
        var currentTab = compNode.getAttribute(designer.itemTabAtt);
        var columnName = this.columnSelect.selectedIndex > -1 ? this.columnSelect.options[this.columnSelect.selectedIndex].value : null;
        if(columnName){
            var columnsNode = DesignerUtils.findColumnsNode(this.componentNode);
            var columns = columnsNode.childNodes();
            for(var i=0; i< columns.length; i++){
                var column = columns[i];
                if(column.getAttribute('attributeName') == columnName){
                    column.setAttribute('type', 'link');
                    break;
                }
            }
        }
        //link action
        var actionsNode = DesignerUtils.findActionsNode(compNode)
        var actionNode = designer.createTreeNode(actionsNode, "LinkAction", {
            'url':this.urlInput.value, 'target':'blank', name:this.itemNames.action,
            condition: columnName ? "params.key == '" + columnName + "'" :"false"
        });
        var triggersNode = DesignerUtils.findActionTriggersNode(actionNode);
        var trigger = designer.createTreeNode(triggersNode, "ActionTrigger", {
            type:'Component event', component:compName, event:'propertyClicked'
        })
        designer.addExtraAttributesToChildNodes(actionsNode, currentTab);
        designer.refreshTree();
    },

    renderColumns: function(){
        var columnNames = DesignerUtils.getColumnNames(this.componentNode);
        for(var i=0; i < columnNames.length; i++){
            var columnName = columnNames[i];
            SelectUtils.addOption(this.columnSelect, columnName, columnName);
        }
    },
    renderCurrentStep: function() {
        var dh = YAHOO.ext.DomHelper
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
                         '<td valign="top"><textarea></textarea></td>' +
                         '</tr>' +
                         '</tbody></table>'});
                this.columnSelect = stepEl.getElementsByTagName('select')[0]
                this.urlInput = stepEl.getElementsByTagName('textarea')[0]
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
    },
    _generateItemNames: function() {
        this.itemNames = {
            action: this.generateActionName()
        }
    }
});
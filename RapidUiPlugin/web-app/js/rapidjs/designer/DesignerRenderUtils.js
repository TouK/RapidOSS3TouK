YAHOO.namespace("rapidjs", "rapidjs.designer");
YAHOO.rapidjs.designer.DesignerRenderUtils = new function() {

    this.renderLayout = function(layoutNode) {
        var dh = YAHOO.ext.DomHelper;
        var getLayoutUnits = function(lNode, wrpUnit) {

            var getUnitHeight = function(node, wrapperUnit) {
                var wrapperHeight = wrapperUnit.getSizes().body.h;
                var browserHeight = YAHOO.util.Dom.getViewportHeight();
                var unitHeight = 200;
                try {
                    unitHeight = parseInt(node.getAttribute('height') || 200, 10);
                }
                catch(e) {
                }
                return Math.floor(unitHeight * (wrapperHeight / browserHeight))
            };
            var getUnitWidth = function(node, wrapperUnit) {
                var wrapperWidth = wrapperUnit.getSizes().body.w;
                var browserWidth = YAHOO.util.Dom.getViewportWidth();
                var unitWidth = 200;
                try {
                    unitWidth = parseInt(node.getAttribute('width') || 200, 10);
                }
                catch(e) {
                }
                return Math.floor(unitWidth * (wrapperWidth / browserWidth))
            };
            var units = [];
            var unitNodes = lNode.childNodes();
            for (var i = 0; i < unitNodes.length; i++) {
                var unitConfig = {};
                var unitNode = unitNodes[i];
                var unitType = DesignerUtils.getItemType(this, unitNode);
                var unitPosition = unitType.substring(0, unitType.length - 4).toLowerCase();
                unitConfig['position'] = unitPosition;
                unitConfig['height'] = getUnitHeight.call(this, unitNode, wrpUnit);
                unitConfig['width'] = getUnitWidth.call(this, unitNode, wrpUnit);
                if (unitNode.childNodes().length == 0) {
                    var htmlEl = YAHOO.ext.DomHelper.append(document.body, {tag:'div', html:'<span style="position:absolute;top:50%;left:50%">' + unitPosition + '</span>', id:YAHOO.util.Dom.generateId(null, 'generatedLayoutUnit')});
                    unitConfig['body'] = htmlEl.id;
                }
                units.push(unitConfig);
            }
            return units;
        };
        var renderLayoutToParent = function(lNode, parentLayout, position) {
            parentLayout.on('render', function() {
                var unit = parentLayout.getUnitByPosition(position);
                var layout = new YAHOO.widget.Layout(unit.get('wrap'), {
                    parent: parentLayout,
                    units: getLayoutUnits.call(this, lNode, unit)
                });
                var childUnits = lNode.childNodes();
                for (var i = 0; i < childUnits.length; i++) {
                    var childUnitNode = childUnits[i];
                    var unitType = DesignerUtils.getItemType(this, childUnitNode);
                    var unitPosition = unitType.substring(0, unitType.length - 4).toLowerCase();
                    if (childUnitNode.childNodes().length > 0) {
                        renderLayoutToParent.call(this, childUnitNode.childNodes()[0], layout, unitPosition);
                    }
                }
                layout.render();
            }, this, true);
        }
        var centerUnit = this.leftLayout.getUnitByPosition('center');
        var curLay = new YAHOO.widget.Layout(centerUnit.get('wrap'), {
            parent: this.leftLayout,
            units: getLayoutUnits.call(this, layoutNode, centerUnit)
        });
        var childUnitNodes = layoutNode.childNodes();
        for (var i = 0; i < childUnitNodes.length; i++) {
            var cUnitNode = childUnitNodes[i];
            var uType = DesignerUtils.getItemType(this, cUnitNode);
            var uPosition = uType.substring(0, uType.length - 4).toLowerCase();
            if (cUnitNode.childNodes().length > 0) {
                renderLayoutToParent.call(this, cUnitNode.childNodes()[0], curLay, uPosition);
            }
        }
        curLay.render();
        this.currentLayout = curLay;
        this.currentLayoutNode = layoutNode;
        this.layout.resize();
    };

    this.handleContextMenuClick = function(p_sType, p_aArgs) {
        var task = p_aArgs[1];
        if (task) {
            var elRow = this.propertyContextMenu.contextEventTarget;
            elRow = this.propertyGrid.getTrEl(elRow);
            if (elRow) {
                var oRecord = this.propertyGrid.getRecord(elRow);
                var prop = oRecord.getData("name");
                this.propertyGrid.deleteRow(elRow);
                this.closeCellEditor();
                SelectUtils.addOption(this.propertySelect, prop, prop);
                this.currentDisplayedItemData.setAttribute(prop, null);
            }
        }
    };
    this.handleContextMenuShow = function() {
        var elRow = this.propertyContextMenu.contextEventTarget;
        elRow = this.propertyGrid.getTrEl(elRow);
        if (elRow) {
            var oRecord = this.propertyGrid.getRecord(elRow);
            var prop = oRecord.getData("name");
            var itemType = this.currentDisplayedItemData.getAttribute(this.treeTypeAttribute)
            if (UIConfig.isPropertyRequired(itemType, prop)) {
                this.propertyContextMenu.hide();
            }
        }
    }
    this.propertySelectedToBeAdded = function() {
        if (this.propertySelect.selectedIndex > 0) {
            var prop = this.propertySelect.options[this.propertySelect.selectedIndex].value;
            var itemType = DesignerUtils.getItemType(this, this.currentDisplayedItemData);
            var defaultValue = UIConfig.getPropertyDefaultValue(itemType, prop);
            var propValue = this.currentDisplayedItemData.getAttribute(prop);
            var gridValue = "";
            if (propValue != null) {
                gridValue = propValue;
            }
            else if (defaultValue != null) {
                gridValue = defaultValue
            }
            this.propertyGrid.addRow({name:prop, value:gridValue});
            SelectUtils.remove(this.propertySelect, prop)
        }
    };
    this.editorSaveFunc = function(oArgs) {
        var getFirstItem = function(object) {
            for (var member in object) {
                return member;
            }
            return null;
        }
        var createFunctionArgutments = function() {
            var argumentsNode;
            var childNodes = this.currentDisplayedItemData.childNodes();
            for (var i = 0; i < childNodes.length; i++) {
                var itemType = DesignerUtils.getItemType(this, childNodes[i])
                if (itemType == "FunctionArguments") {
                    argumentsNode = childNodes[i];
                    break;
                }
            }
            childNodes = argumentsNode.childNodes();
            for (var i = childNodes.length - 1; i >= 0; i--) {
                argumentsNode.removeChild(childNodes[i]);
            }
            var records = this.propertyGrid.getRecordSet().getRecords();
            for(var i = records.length -1; i >=0; i--){
                var propName = records[i].getData("name");
                if(propName.match(/arg\d+/)){
                    this.propertyGrid.deleteRow(i)
                }
            }
            var method = this.currentDisplayedItemData.getAttribute("function");
            var compName = this.currentDisplayedItemData.getAttribute("component");
            var currentComponents = DesignerUtils.getComponentsOfCurrentTab(this, this.currentDisplayedItemData);
            var args = UIConfig.getMethodArguments(currentComponents[compName], method);
            var argIndex = 0;
            for (var argName in args) {
                this.propertyGrid.addRow({name:"arg" + argIndex + ":" + argName, value:""});
                this.createTreeNode(argumentsNode, "FunctionArgument");
                argIndex ++;
            }
            var currentTab = this.currentDisplayedItemData.getAttribute(this.itemTabAtt);
            this.addExtraAttributesToChildNodes(argumentsNode, currentTab)
        }
        var record = oArgs.editor.getRecord();
        var propName = record.getData("name")
        var propValue = record.getData("value")
        var itemType = DesignerUtils.getItemType(this, this.currentDisplayedItemData);
        if (itemType == "Layout" && propName == "type") {
            this.createLayoutNode(propValue);
            var currentTab = this.currentDisplayedItemData.getAttribute(this.itemTabAtt);
            this.addExtraAttributesToChildNodes(this.currentDisplayedItemData, currentTab)
            this.refreshLayout(this.currentDisplayedItemData);
        }
        else if (itemType == "ActionTrigger" && propName == "type") {
            var oldType = this.currentDisplayedItemData.getAttribute("type");
            var oldName = this.currentDisplayedItemData.getAttribute("name");
            if (oldType != propValue) {
                this.clearPropertyGrid();
                if (propValue == "Action event") {
                    var oldTriggeringAction = this.currentDisplayedItemData.getAttribute("triggeringAction");
                    var currentActions = DesignerUtils.getActionsOfCurrentTab(this, this.currentDisplayedItemData);
                    var triggeringAction = oldTriggeringAction;
                    var name = "";
                    triggeringAction = "";
                    for (var actionName in currentActions) {
                        triggeringAction = actionName;
                        name = getFirstItem(UIConfig.getItemEvents(currentActions[actionName]))
                        break;
                    }
                    this.currentDisplayedItemData.setAttribute("name", name);
                    this.currentDisplayedItemData.setAttribute("triggeringAction", triggeringAction);
                    this.propertyGrid.addRows([{name:"type", value:propValue},{name:"triggeringAction", value:triggeringAction},{name:"name", value:name}])
                }
                else if (propValue == "Component event") {
                    var oldComponent = this.currentDisplayedItemData.getAttribute("component");
                    var currentComponents = DesignerUtils.getComponentsOfCurrentTab(this, this.currentDisplayedItemData);
                    var component = oldComponent;
                    var name = "";
                    component = "";
                    for (var compName in currentComponents) {
                        component = compName;
                        name = getFirstItem(UIConfig.getItemEvents(currentComponents[compName])) || "";
                        break;
                    }
                    this.currentDisplayedItemData.setAttribute("name", name);
                    this.currentDisplayedItemData.setAttribute("component", component);
                    this.propertyGrid.addRows([{name:"type", value:propValue},{name:"component", value:component},{name:"name", value:name}])
                }
                else if (propValue == "Menu") {
                    var oldComponent = this.currentDisplayedItemData.getAttribute("component");
                    var currentComponents = DesignerUtils.getComponentsWithMenuItems(this, this.currentDisplayedItemData);
                    var component = oldComponent;
                    var name = "";
                    component = "";
                    for (var compName in currentComponents) {
                        component = compName;
                        var currentCompConfig = currentComponents[compName];
                        nameLoop:for (var menuType in currentCompConfig) {
                            var menuNames = currentCompConfig[menuType];
                            if (menuNames.length > 0) {
                                name = menuNames[0];
                                break nameLoop;
                            }
                        }
                        break;
                    }
                    this.currentDisplayedItemData.setAttribute("name", name);
                    this.currentDisplayedItemData.setAttribute("component", component);
                    this.propertyGrid.addRows([{name:"type", value:propValue},{name:"component", value:component},{name:"name", value:name}])
                }
                else if (propValue == "Global event") {
                    var name = getFirstItem(UIConfig.getItemEvents("Global"));
                    this.currentDisplayedItemData.setAttribute("name", name);
                    this.propertyGrid.addRows([{name:"type", value:propValue},{name:"name", value:name}])
                }
                this.showHelp();
            }
        }
        else if (itemType == "ActionTrigger" && propName == "triggeringAction") {
            var oldAction = this.currentDisplayedItemData.getAttribute("triggeringAction")
            if (propValue != oldAction) {
                var currentActions = DesignerUtils.getActionsOfCurrentTab(this, this.currentDisplayedItemData)
                var name = getFirstItem(UIConfig.getItemEvents(currentActions[propValue]))
                var nameRecord = this.propertyGrid.findRecord("name", "name");
                this.propertyGrid.updateCell(nameRecord, "value", name)
                this.currentDisplayedItemData.setAttribute("name", name);
            }
            this.showHelp();
        }
        else if (itemType == "ActionTrigger" && propName == "component") {
            var oldComponent = this.currentDisplayedItemData.getAttribute("component")
            if (propValue != oldComponent) {
                var triggerType = this.currentDisplayedItemData.getAttribute("type")
                var name;
                if (triggerType == "Menu") {
                    var currentComponents = DesignerUtils.getComponentsWithMenuItems(this, this.currentDisplayedItemData);
                    var compMenuConfig = currentComponents[propValue];
                    for (var menuType in compMenuConfig) {
                        var menuNames = compMenuConfig[menuType];
                        if (menuNames.length > 0) {
                            name = menuNames[0];
                            break;
                        }
                    }
                }
                else {
                    var currentComponents = DesignerUtils.getComponentsOfCurrentTab(this, this.currentDisplayedItemData)
                    name = getFirstItem(UIConfig.getItemEvents(currentComponents[propValue])) || "";
                }
                var nameRecord = this.propertyGrid.findRecord("name", "name");
                this.propertyGrid.updateCell(nameRecord, "value", name)
                this.currentDisplayedItemData.setAttribute("name", name);
            }
            this.showHelp();
        }
        else if (itemType == "ActionTrigger" && propName == "name") {
            this.showHelp();
        }
        else if (itemType == "FunctionAction" && propName == "component") {
            var oldComponent = this.currentDisplayedItemData.getAttribute("component");
            if (propValue != oldComponent) {
                var currentComponents = DesignerUtils.getComponentsOfCurrentTab(this, this.currentDisplayedItemData);
                this.currentDisplayedItemData.setAttribute(propName, propValue);
                var componentMethods = UIConfig.getComponentMethods(currentComponents[propValue])
                var method = getFirstItem(componentMethods);
                this.currentDisplayedItemData.setAttribute("function", method);
                var funcRecord = this.propertyGrid.findRecord("name", "function");
                this.propertyGrid.updateCell(funcRecord, "value", method)
                createFunctionArgutments.call(this);
                this.showHelp();
            }
        }
        else if (itemType == "FunctionAction" && propName == "function") {
            var oldMethod = this.currentDisplayedItemData.getAttribute("function");
            if (propValue != oldMethod) {
                this.currentDisplayedItemData.setAttribute(propName, propValue);
                createFunctionArgutments.call(this);
                this.showHelp();
            }
        }
        else if (itemType == "FunctionAction" && propName.match(/arg\d+/)) {
            var matchValues = propName.match(/arg\d+/);
            var argIndex = parseInt(matchValues[0].substring(3), 10)
            var argumentsNode;
            var childNodes = this.currentDisplayedItemData.childNodes();
            for (var i = 0; i < childNodes.length; i++) {
                var itemType = DesignerUtils.getItemType(this, childNodes[i])
                if (itemType == "FunctionArguments") {
                    argumentsNode = childNodes[i];
                    break;
                }
            }
            childNodes = argumentsNode.childNodes();
            childNodes[argIndex].setAttribute("value", propValue);
        }
        this.currentDisplayedItemData.setAttribute(propName, "" + propValue);
        if (UIConfig.isDisplayProperty(itemType, propName)) {
            this.currentDisplayedItemData.setAttribute(this.treeDisplayAttribute, "" + propValue);
        }
        this.dataChanged = true;
        this.refreshTree();
    };

    this.displayFunctionActionProperties = function(xmlData) {
        var component = xmlData.getAttribute("component");
        var method = xmlData.getAttribute("function");
        if (component && component != "") {
            var currentCompoents = DesignerUtils.getComponentsOfCurrentTab(this, xmlData);
            var compMethods = UIConfig.getComponentMethods(currentCompoents[component]);
            if (compMethods && compMethods[method]) {
                var args = UIConfig.getMethodArguments(currentCompoents[component], method);
                var argumentsNode;
                var childNodes = xmlData.childNodes();
                for (var i = 0; i < childNodes.length; i++) {
                    var itemType = DesignerUtils.getItemType(this, childNodes[i])
                    if (itemType == "FunctionArguments") {
                        argumentsNode = childNodes[i];
                        break;
                    }
                }
                childNodes = argumentsNode.childNodes();
                var argIndex = 0;
                for (var argName in args) {
                    this.propertyGrid.addRow({name:"arg" + argIndex + ":" + argName, value:childNodes[argIndex].getAttribute("value")||""})
                    argIndex ++;
                }
            }
        }
    }
    this.displayActionTriggerProperties = function(xmlData) {
        var triggerType = xmlData.getAttribute("type");
        data[data.length] = {name:"type", value:triggerType}
        data[data.length] = {name:"name", value:xmlData.getAttribute("name") || ""}
        if (triggerType == "Action event") {
            data[data.length] = {name:"triggeringAction", value:xmlData.getAttribute("triggeringAction") || ""}
        }
        else if (triggerType == "Menu" || triggerType == "Component event") {
            data[data.length] = {name:"component", value:xmlData.getAttribute("component") || ""}
        }
    }
    this.propertyGridClickedFuntion = function (oArgs) {
        var target = oArgs.target,
                record = this.propertyGrid.getRecord(target),
                column = this.propertyGrid.getColumn(target),
                propertyName = record.getData("name");
        if (column.getKey() == "value") {
            var itemType = DesignerUtils.getItemType(this, this.currentDisplayedItemData);
            var editor;
            if (itemType == "Layout" && DesignerUtils.getItemType(this, this.currentDisplayedItemData.parentNode()) == "Tab") {
                editor = this.editors["InList"];
                editor.dropdownOptions = UIConfig.getLayoutTypeNames();
                editor.renderForm();
            }
            else if (propertyName == "component" && (itemType == "CenterUnit" || itemType == "LeftUnit" ||
                                                     itemType == "TopUnit" || itemType == "RightUnit" ||
                                                     itemType == "BottomUnit" || itemType == "Dialog" || itemType == "FunctionAction")) {
                editor = this.editors["InList"];
                var dropDownOptions = DesignerUtils.getComponentNamesOfCurrentTab(this, this.currentDisplayedItemData);
                dropDownOptions.splice(0, 0, '');
                editor.dropdownOptions = dropDownOptions;
                editor.renderForm();
            }
            else if (propertyName == "function" && itemType == "FunctionAction") {
                var componentName = this.currentDisplayedItemData.getAttribute('component')
                var comps = DesignerUtils.getComponentsOfCurrentTab(this, this.currentDisplayedItemData);
                editor = this.editors["InList"];
                if (comps[componentName]) {
                    var methods = UIConfig.getComponentMethods(comps[componentName]);
                    var dOptions = [];
                    for (var method in methods) {
                        dOptions.push(method);
                    }
                    editor.dropdownOptions = dOptions;
                }
                else {
                    editor.dropdownOptions = [];
                }
                editor.renderForm();
            }
            else if (itemType == "ActionTrigger" && propertyName == 'triggeringAction') {
                editor = this.editors["InList"];
                var currentActions = DesignerUtils.getActionNamesOfCurrentTab(this, this.currentDisplayedItemData);
                editor.dropdownOptions = currentActions;
                editor.renderForm();
            }
            else if (itemType == "ActionTrigger" && propertyName == 'component') {
                editor = this.editors["InList"];
                var currentComponents;
                var triggerType = this.currentDisplayedItemData.getAttribute("type")
                if (triggerType == "Menu") {
                    var menuConfig = DesignerUtils.getComponentsWithMenuItems(this, this.currentDisplayedItemData);
                    currentComponents = [];
                    for (var compName in menuConfig) {
                        currentComponents.push(compName);
                    }
                }
                else {
                    currentComponents = DesignerUtils.getComponentNamesOfCurrentTab(this, this.currentDisplayedItemData);
                }
                editor.dropdownOptions = currentComponents;
                editor.renderForm();
            }
            else if (itemType == "ActionTrigger" && propertyName == 'name') {
                var triggerType = this.currentDisplayedItemData.getAttribute("type")
                var names = [];
                if (triggerType == "Component event") {
                    var currentComponents = DesignerUtils.getComponentsOfCurrentTab(this, this.currentDisplayedItemData);
                    var component = this.currentDisplayedItemData.getAttribute("component");
                    if (currentComponents[component]) {
                        var events = UIConfig.getItemEvents(currentComponents[component]);
                        for (var event in events) {
                            names.push(event);
                        }
                    }
                }
                else if (triggerType == "Menu") {
                    var menuConfig = DesignerUtils.getComponentsWithMenuItems(this, this.currentDisplayedItemData);
                    var component = this.currentDisplayedItemData.getAttribute("component");
                    if (menuConfig[component]) {
                        var compMenuConfig = menuConfig[component]
                        for (var menuType in compMenuConfig) {
                            var menuItems = compMenuConfig[menuType];
                            for (var i = 0; i < menuItems.length; i++) {
                                names.push(menuItems[i]);
                            }
                        }
                    }
                }
                else if (triggerType == "Action event") {
                    var triggeringAction = this.currentDisplayedItemData("triggeringAction");
                    var currentActions = DesignerUtils.getActionsOfCurrentTab(this, this.currentDisplayedItemData);
                    if (currentActions[triggeringAction]) {
                        var events = UIConfig.getItemEvents(currentActions[triggeringAction]);
                        for (var event in events) {
                            names.push(event);
                        }
                    }
                }
                else if (triggerType == "Global event") {
                    var events = UIConfig.getItemEvents("Global");
                    for (var event in events) {
                        names.push(event);
                    }
                }
                editor = this.editors["InList"];
                editor.dropdownOptions = names;
                editor.renderForm();
            }
            else {
                var inList = UIConfig.getPropertyInList(itemType, propertyName)
                if (inList.length > 0) {
                    editor = this.editors["InList"];
                    editor.dropdownOptions = inList;
                    editor.renderForm();
                }
                else {
                    var propertyType = UIConfig.getPropertyType(itemType, propertyName)
                    editor = this.editors[propertyType];
                    if (editor == null)
                    {
                        editor = this.editors["String"];
                    }
                }

            }
            column.editor = editor;
            this.propertyGrid.showCellEditor(target);
        }
    };
    this.addTooltip = function() {
        var showTimer,hideTimer;
        var tt = new YAHOO.widget.Tooltip("propertyTooltip");

        this.propertyGrid.on('cellMouseoverEvent', function (oArgs) {
            if (showTimer) {
                window.clearTimeout(showTimer);
                showTimer = 0;
            }

            var target = oArgs.target;
            var record = this.propertyGrid.getRecord(target);
            var prop = record.getData("name")
            var itemType = this.currentDisplayedItemData.getAttribute(this.treeTypeAttribute);
            var descr = UIConfig.getPropertyDescription(itemType, prop);
            var xy = [parseInt(oArgs.event.clientX, 10) + 10 ,parseInt(oArgs.event.clientY, 10) + 10 ];

            showTimer = window.setTimeout(function() {
                tt.setBody(descr);
                tt.cfg.setProperty('xy', xy);
                tt.show();
                hideTimer = window.setTimeout(function() {
                    tt.hide();
                }, 5000);
            }, 500);
        }, this, true);
        this.propertyGrid.on('cellMouseoutEvent', function (oArgs) {
            if (showTimer) {
                window.clearTimeout(showTimer);
                showTimer = 0;
            }
            if (hideTimer) {
                window.clearTimeout(hideTimer);
                hideTimer = 0;
            }
            tt.hide();
        });
    };

    this.dateEditorAttachFunc = function(oDataTable, elCell) {
        // Validate
        if (oDataTable instanceof YAHOO.widget.DataTable) {
            this._oDataTable = oDataTable;

            // Validate cell
            elCell = oDataTable.getTdEl(elCell);
            if (elCell) {
                this._elTd = elCell;

                // Validate Column
                var oColumn = oDataTable.getColumn(elCell);
                if (oColumn) {
                    this._oColumn = oColumn;

                    // Validate Record
                    var oRecord = oDataTable.getRecord(elCell);
                    if (oRecord) {
                        this._oRecord = oRecord;
                        var value = oRecord.getData(this.getColumn().getKey());
                        this.value = (value !== undefined) ? Date.parseDate(value, "M d Y H:i:s T") : this.defaultValue;
                        return true;
                    }
                }
            }
        }
        YAHOO.log("Could not attach CellEditor", "error", this.toString());
        return false;
    };
    this.textAreaEditorMoveFunc = function() {
        this.textarea.style.width = "300px";
        this.textarea.style.height = "200px";
        YAHOO.widget.TextareaCellEditor.superclass.move.call(this);
    }
    this.handleFirefoxCursorBugForEditors = function(editors) {
        if (YAHOO.env.ua.gecko) {
            var func = function(oArgs) {
                var editorContainer = oArgs.editor.getContainerEl();
                YAHOO.util.Dom.setStyle(editorContainer, 'overflow', 'hidden');
            }
            for (var editorType in editors) {
                var editor = editors[editorType];
                editor.subscribe('saveEvent', func)
                editor.subscribe('cancelEvent', func)
                editor.subscribe('showEvent', function(oArgs) {
                    YAHOO.util.Dom.setStyle(oArgs.editor.getContainerEl(), 'overflow', 'auto');
                })
            }
        }
    }
};
var RenderUtils = YAHOO.rapidjs.designer.DesignerRenderUtils; 
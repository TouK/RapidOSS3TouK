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
        var record = oArgs.editor.getRecord();
        var propName = record.getData("name")
        var propValue = record.getData("value")
        this.currentDisplayedItemData.setAttribute(propName, "" + propValue);
        var itemType = DesignerUtils.getItemType(this, this.currentDisplayedItemData);
        if (UIConfig.isDisplayProperty(itemType, propName)) {
            this.currentDisplayedItemData.setAttribute(this.treeDisplayAttribute, "" + propValue);
        }
        if (itemType == "Layout" && propName == "type") {
            this.createLayoutNode(propValue);
            var currentTab = this.currentDisplayedItemData.getAttribute(this.itemTabAtt);
            this.addExtraAttributesToChildNodes(this.currentDisplayedItemData, currentTab)
            this.refreshLayout(this.currentDisplayedItemData);
        }
        this.dataChanged = true;
        this.refreshTree();
    };
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
                    var events = UIConfig.getComponentEvents(comps[componentName]);
                    var dOptions = [];
                    for (var event in events) {
                        dOptions.push(event);
                    }
                    editor.dropdownOptions = dOptions;
                }
                else {
                    editor.dropdownOptions = [];
                }
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
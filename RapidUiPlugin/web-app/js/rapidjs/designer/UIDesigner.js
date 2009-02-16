YAHOO.namespace('rapidjs', 'rapidjs.designer');
YAHOO.rapidjs.designer.UIDesigner = function(config) {
    this.rootTag = null;
    this.contentPath = null;
    this.keyAttribute = null
    this.treeTypeAttribute = null;
    this.url = null
    this.saveUrl = null
    this.metaDataUrl = null
    this.generateUrl = null
    this.treeHideAttribute = null;
    YAHOO.ext.util.Config.apply(this, config);
    this.treeDisplayAttribute = 'designer_name'
    this.itemTabAtt = 'designer_item_tab';
    this.tree = null;
    this.propertyGrid = null;
    this.currentLayout = null;
    this.currentLayoutNode = null;
    this.dataChanged = false;
    this.data = null;
    this.editors = {
        String: new YAHOO.widget.TextboxCellEditor({disableBtns:true}),
        Number:new YAHOO.widget.TextboxCellEditor({disableBtns:true, validator:function (val) {
            val = parseFloat(val);
            if (YAHOO.lang.isNumber(val)) {
                return val;
            }
            return 0;
        }}),
        Float:new YAHOO.widget.TextboxCellEditor({disableBtns:true, validator:function (val) {
            val = parseFloat(val);
            if (YAHOO.lang.isNumber(val)) {
                return val;
            }
            return 0;
        }}),
        "DateMock":new YAHOO.widget.DateCellEditor(),
        InList: new YAHOO.widget.DropdownCellEditor({}),
        Expression: new YAHOO.widget.TextareaCellEditor(),
        Boolean: new YAHOO.widget.DropdownCellEditor({dropdownOptions:['true', 'false']})

    };
    this.editors['Expression'].move = function() {
        this.textarea.style.width = "300px";
        this.textarea.style.height = "200px";
        YAHOO.widget.TextareaCellEditor.superclass.move.call(this);
    }
    this.editors['DateMock'].attach = function(oDataTable, elCell) {
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

    if (YAHOO.env.ua.gecko) {
        var func = function(oArgs) {
            var editorContainer = oArgs.editor.getContainerEl();
            YAHOO.util.Dom.setStyle(editorContainer, 'overflow', 'hidden');
        }
        for (var editorType in this.editors) {
            var editor = this.editors[editorType];
            editor.subscribe('saveEvent', func)
            editor.subscribe('cancelEvent', func)
            editor.subscribe('showEvent', function(oArgs) {
                YAHOO.util.Dom.setStyle(oArgs.editor.getContainerEl(), 'overflow', 'auto');
            })
        }
    }

    this.actionDlg = new YAHOO.rapidjs.designer.ActionDefinitionDialog(this);
    this.loadingMask = new YAHOO.rapidjs.component.LoadingMask({});
    this.confirmBox = new YAHOO.rapidjs.component.ConfirmBox({handler:this.confirmBoxHandler, scope:this});
    this.render();
    this.getMetaData();
};

YAHOO.rapidjs.designer.UIDesigner.prototype = {
    addExtraAttributesToChildNodes : function(node, currentTab) {
        var children = node.childNodes();
        for (var i = 0; i < children.length; i++) {
            var childNode = children[i]
            if (childNode.nodeName == this.contentPath) {
                var itemType = childNode.getAttribute(this.treeTypeAttribute);
                var tabId = currentTab
                if (itemType == "Tab") {
                    tabId = childNode.getAttribute(this.keyAttribute);
                }
                var displayName = UIConfig.getDisplayName(itemType, childNode)
                childNode.setAttribute(this.treeDisplayAttribute, displayName);
                if (UIConfig.canBeDeleted(itemType)) {
                    childNode.setAttribute("canBeDeleted", "true");
                }
                if (tabId != null) {
                    childNode.setAttribute(this.itemTabAtt, tabId);
                }
                this.addExtraAttributesToChildNodes(childNode, tabId);
            }

        }
    },

    getMetaData: function() {
        var callback = {
            success: this.processSuccess,
            failure: this.handleFailure,
            scope:this,
            cache:false,
            argument:[this.loadMetaData]
        }
        YAHOO.util.Connect.asyncRequest('GET', this.metaDataUrl, callback);
    },
    loadMetaData: function(response) {
        UIConfig.loadMetaData(response);
        var modifyTreeConfig = function() {
            var stringToAddToUrl = getUrlPrefix();
            var rootImages = [];
            var menuItems = [];
            window.designerMenuEvaluate = function(dataNode, parentType, itemType, typeAttribute) {
                var childNodes = dataNode.childNodes();
                for (var i = 0; i < childNodes.length; i++) {
                    var iType = childNodes[i].getAttribute(typeAttribute)
                    if (iType == itemType && !UIConfig.isChildMultiple(parentType, itemType)) {
                        return false;
                    }
                }
                return true;
            }
            var configItems = UIConfig.getConfig()
            for (var item in configItems) {
                var imageConfig = UIConfig.getImageConfig(item);
                if (imageConfig) {
                    var expanded = stringToAddToUrl + imageConfig['expanded']
                    var collapsed = stringToAddToUrl + imageConfig['collapsed']
                    rootImages.push({visible:"params.data." + this.treeTypeAttribute + " == '" + item + "'", expanded:expanded, collapsed:collapsed})
                }
                if (item == "Actions") {
                    menuItems.push({id:'add_Action', label:'Add Action', visible:"params.data." + this.treeTypeAttribute + " =='" + item + "'"});
                    menuItems.push({id:'edit_Action', label:'Edit',
                        visible:"params.data." + this.treeTypeAttribute + " =='FunctionAction' || " +
                                "params.data." + this.treeTypeAttribute + " =='RequestAction' || " +
                                "params.data." + this.treeTypeAttribute + " =='MergeAction' || " +
                                "params.data." + this.treeTypeAttribute + " =='CombinedAction' || " +
                                "params.data." + this.treeTypeAttribute + " =='LinkAction'"});
                }
                else {
                    var children = UIConfig.getChildren(item);
                    if (children) {
                        for (var childType in children) {
                            if (UIConfig.canBeDeleted(childType)) {
                                var displayName = UIConfig.getDisplayName(childType);

                                var addExpr = "window.designerMenuEvaluate(params.dataNode, '" + item + "', '" + childType + "', '" + this.treeTypeAttribute + "')"
                                menuItems.push({id:'add_' + childType, label:'Add ' + displayName, visible:"params.data." + this.treeTypeAttribute + " =='" + item + "' && " + addExpr});
                            }
                        }
                    }
                }

            }
            menuItems.push({id:"clone", label:"Clone", visible:"params.data.canBeDeleted && !(params.data." + this.treeTypeAttribute + " == 'Layout' && params.dataNode.parentNode().getAttribute('" + this.treeTypeAttribute + "') == 'Tab')"});
            menuItems.push({id:"delete", label:"Delete", visible:"params.data.canBeDeleted && !(params.data." + this.treeTypeAttribute + " == 'Layout' && params.dataNode.parentNode().getAttribute('" + this.treeTypeAttribute + "') == 'Tab')"});
            this.tree.treeGridView.setMenuItems(menuItems);
            this.tree.treeGridView.rootImages = rootImages;
        }
        modifyTreeConfig.call(this)
        this.getData();
    },
    getData: function() {
        var callback = {
            success: this.processSuccess,
            failure: this.handleFailure,
            scope:this,
            cache:false,
            argument:[this.loadData]
        }
        YAHOO.util.Connect.asyncRequest('GET', this.url, callback);
    },

    loadData: function(response) {
        var data = new YAHOO.rapidjs.data.RapidXmlDocument(response, [this.keyAttribute]);
        var rootNode = data.getRootNode(this.rootTag)
        this.addExtraAttributesToChildNodes(rootNode);
        this.data = data;
        this.tree.loadData(this.data);
        var defferedExpandNode = function() {
            var rows = this.tree.treeGridView.bufferView.rows;
            if (rows.length > 0) {
                this.expandTreeNode(rows[0])
            }
        }
        defferedExpandNode.defer(100, this, []);
    },

    processSuccess: function(response) {
        YAHOO.rapidjs.ErrorManager.serverUp();
        try
        {
            if (YAHOO.rapidjs.Connect.checkAuthentication(response) == false)
            {
                return;
            }
            else if (YAHOO.rapidjs.Connect.containsError(response) == false)
            {
                this.tree.events["success"].fireDirect(this.tree);
                var callback = response.argument[0];
                if (callback) {
                    callback.call(this, response);
                }
            }
            else
            {
                this.loadingMask.hide();
                var errors = YAHOO.rapidjs.Connect.getErrorMessages(response.responseXML);
                this.tree.events["error"].fireDirect(this.tree, errors);
                YAHOO.rapidjs.ErrorManager.errorOccurred(this.tree, errors);
            }

        }
        catch(e)
        {
        }
        this.tree.events["loadstatechanged"].fireDirect(this.tree, false);

    },

    handleFailure: function(response) {
        this.loadingMask.hide();
        this.tree.processFailure(response)
    },

    treeSelectionChanged:function(xmlData) {
        this.displayItemProperties(xmlData);
        this.changeLayout(xmlData);
    },

    displayItemProperties: function(xmlData) {
        this.currentDisplayedItemData = xmlData;
        var itemType = DesignerUtils.getItemType(this, xmlData);
        var properties = UIConfig.getProperties(itemType)
        var data = [];
        if (itemType == "Layout" && DesignerUtils.getItemType(this, xmlData.parentNode()) == "Tab") {
            data[data.length] = {name:"type", value:xmlData.getAttribute("type") || ""}
        }
        SelectUtils.clear(this.propertySelect);
        SelectUtils.addOption(this.propertySelect, '', '');
        for (var prop in properties) {
            var propValue = xmlData.getAttribute(prop);
            if (UIConfig.isPropertyRequired(itemType, prop)) {
                var defaultValue = UIConfig.getPropertyDefaultValue(itemType, prop);
                var gridValue = "";
                if (propValue != null) {
                    gridValue = propValue
                }
                else if (defaultValue != null) {
                    gridValue = defaultValue
                }
                data[data.length] = {name:prop, value:gridValue}
            }
            else if (propValue) {
                data[data.length] = {name:prop, value:propValue}
            }
            else {
                SelectUtils.addOption(this.propertySelect, prop, prop)
            }
        }
        var length = this.propertyGrid.getRecordSet().getLength()
        this.propertyGrid.deleteRows(0, length)
        this.propertyGrid.addRows(data)
        this.closeCellEditor();
    },
    closeCellEditor: function() {
        var column = this.propertyGrid.getColumn("value");
        var editor = column.editor;
        if (editor) {
            editor.cancel();
        }
    },
    destroyCurrentLayout: function() {
        if (this.currentLayout) {
            this.currentLayout.destroy();
                //destroying child layout removes parent layout's window resize subscription, I couldn't understand why
            YAHOO.util.Event.on(window, 'resize', this.layout.resize, this.layout, true);
        }
        this.currentLayout = null;
        this.currentLayoutNode = null;
    },
    refreshLayout: function(xmlData) {
        var layoutNode = DesignerUtils.getCurrentLayoutNode(this, xmlData);
        this.destroyCurrentLayout();
        if (layoutNode) {
            this.renderLayout(layoutNode)
        }
    },
    changeLayout: function(xmlData) {
        var layoutNode = DesignerUtils.getCurrentLayoutNode(this, xmlData);
        if ((!layoutNode && this.currentLayout) || layoutNode != this.currentLayoutNode) {
            this.destroyCurrentLayout();
        }
        if (layoutNode && layoutNode != this.currentLayoutNode) {
            this.renderLayout(layoutNode)
        }
    },
    renderLayout:function(layoutNode) {
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
    },

    createTreeNode: function(parentNode, itemType, attributes) {
        var atts = attributes;
        if (!atts) {
            atts = {};
        }
        var id = YAHOO.util.Dom.generateId(null, itemType);
        var newNode = parentNode.createChildNode(null, 1, this.contentPath);
        newNode.setAttribute(this.keyAttribute, id);
        newNode.setAttribute(this.treeTypeAttribute, itemType);
        for (var att in atts) {
            newNode.setAttribute(att, atts[att])
        }
        var itemMetaProperties = UIConfig.getProperties(itemType);
        for (var prop in itemMetaProperties) {
            if (!atts[prop]) {
                if (UIConfig.isPropertyRequired(itemType, prop)) {
                    var defaultValue = UIConfig.getPropertyDefaultValue(itemType, prop);
                    if (UIConfig.isDisplayProperty(itemType, prop)) {
                        var propValue = !defaultValue || defaultValue == '' ? itemType : defaultValue
                        newNode.setAttribute(prop, propValue);
                    }
                    else {
                        newNode.setAttribute(prop, defaultValue || "");
                    }
                }
            }
        }
        parentNode.appendChild(newNode);
        var metaChildren = UIConfig.getChildren(itemType)
        for (var childType in metaChildren) {
            if (!UIConfig.canBeDeleted(childType) || (itemType == "Tab" && childType == "Layout")) {
                this.createTreeNode(newNode, childType);
            }
        }
        this.dataChanged = true;
        return newNode;
    },

    createLayoutNode: function(layoutType) {
        var childNodes = this.currentDisplayedItemData.childNodes();
        for (var i = childNodes.length - 1; i >= 0; i--) {
            var unitNode = childNodes[i]
            var nodeType = DesignerUtils.getItemType(this, unitNode);
            if (nodeType != "CenterUnit") {
                this.currentDisplayedItemData.removeChild(unitNode);
            }
            else {
                var childLayoutNode = unitNode.firstChild();
                if (childLayoutNode) {
                    unitNode.removeChild(childLayoutNode);
                }
            }
        }
        var _populateLayoutNode = function(layoutNode, lConf) {
            var centerUnitNode = layoutNode.firstChild();
            for (var unitType in lConf) {
                var unitNode;
                if (unitType != "CenterUnit") {
                    unitNode = this.createTreeNode(layoutNode, unitType);
                }
                else {
                    unitNode = centerUnitNode;
                }
                if (typeof lConf[unitType] == 'object') {
                    var childLayoutNode = this.createTreeNode(unitNode, "Layout");
                    _populateLayoutNode.call(this, childLayoutNode, lConf[unitType])
                }
            }
        }
        var layoutConfig = UIConfig.getLayoutType(layoutType);
        _populateLayoutNode.call(this, this.currentDisplayedItemData, layoutConfig);
    },
    expandTreeNode: function(treeRow) {
        this.tree.treeGridView.expandNode(treeRow)
    },
    treeMenuClicked: function(xmlData, id, parentId, row) {
        var isClickedDataInCurrentTab = function(xmlData) {
            if (this.currentDisplayedItemData) {
                var currentTab = DesignerUtils.getTabNodeFromNode(this, this.currentDisplayedItemData)
                var clickedTab = DesignerUtils.getTabNodeFromNode(this, xmlData)
                return currentTab && currentTab == clickedTab;
            }
            return false;
        }
        if (id.indexOf("add_") == 0) {
            var itemType = id.substr(4);
            if (itemType == "Action") {
                this.actionDlg.show(YAHOO.rapidjs.designer.ActionDefinitionDialog.CREATE_MODE, xmlData);
            }
            else {
                var newNode = this.createTreeNode(xmlData, itemType);
                var currentTab = xmlData.getAttribute(this.itemTabAtt);
                this.addExtraAttributesToChildNodes(xmlData, currentTab);
                if (isClickedDataInCurrentTab.call(this, xmlData) && (id == "add_Layout" || id == "add_TopUnit" || id == "add_BottomUnit" || id == "add_LeftUnit" || id == "add_RightUnit")) {
                    this.refreshLayout(xmlData);
                }
                this.refreshTree();
                this.expandTreeNode(row)
            }

        }
        else if (id == "delete") {
            var itemType = DesignerUtils.getItemType(this, xmlData);
            if (this.currentDisplayedItemData) {
                var currentTabNode = DesignerUtils.getTabNodeFromNode(this, this.currentDisplayedItemData);
                if ((itemType == 'WebPage' && currentTabNode && currentTabNode.parentNode().parentNode() == xmlData)
                        || (itemType == 'Tab' && currentTabNode == xmlData)) {
                    this.destroyCurrentLayout();
                }
            }
            var parentNode = xmlData.parentNode();
            parentNode.removeChild(xmlData);
            if (this.currentDisplayedItemData && isClickedDataInCurrentTab.call(this, parentNode) &&
                (itemType == 'Layout' || itemType == 'TopUnit' || itemType == 'LeftUnit' || itemType == 'BottomUnit' || itemType == 'RightUnit' )) {
                this.refreshLayout(parentNode)
            }
            if (this.currentDisplayedItemData && !this.currentDisplayedItemData.parentNode()) {
                this.currentDisplayedItemData = null;
                var length = this.propertyGrid.getRecordSet().getLength()
                this.propertyGrid.deleteRows(0, length)
                this.closeCellEditor();
            }
            this.refreshTree();
        }
        else if (id == "edit_Action") {
            this.actionDlg.show(YAHOO.rapidjs.designer.ActionDefinitionDialog.EDIT_MODE, xmlData);
        }
        else if (id == 'clone'){
            var clonedNode = xmlData.cloneNode(true, true);
            xmlData.parentNode().appendChild(clonedNode);
            var currentTab = xmlData.getAttribute(this.itemTabAtt);
            this.addExtraAttributesToChildNodes(clonedNode, currentTab);
            this.refreshTree();
        }
    },

    refreshTree: function() {
        this.tree.treeGridView.refreshData();
    },

    save : function() {
        var callback = {
            success: this.processSuccess,
            failure: this.handleFailure,
            scope:this,
            cache:false,
            argument:[this.saveSuccess]
        }
        var postData = 'configuration=' + encodeURIComponent(this.data.firstChild().toString());
        YAHOO.util.Connect.asyncRequest('POST', this.saveUrl, callback, postData);
        this.loadingMask.show("Saving, please wait...");
    },
    confirmBoxHandler: function() {
        this.confirmBox.hide();
        this._generate();
    },
    generate: function() {
        if (this.dataChanged) {
            this.confirmBox.show('Some changes has not been saved. Do you want to continue?');
        }
        else {
            this._generate();
        }
    },
    _generate : function() {
        var callback = {
            success: this.processSuccess,
            failure: this.handleFailure,
            scope:this,
            cache:false,
            argument:[this.generateSuccess]
        }
        YAHOO.util.Connect.asyncRequest('GET', this.generateUrl, callback);
        this.loadingMask.show("Generating, please wait...");
    },
    saveSuccess: function(response) {
        this.loadingMask.show("Successfully saved.");
        this.dataChanged = false;
        var self = this;
        setTimeout(function() {
            self.loadingMask.hide();
        }, 1000)
    },
    generateSuccess: function(response) {
        this.loadingMask.show("Successfully generated.");
        var self = this;
        setTimeout(function() {
            self.loadingMask.hide();
        }, 1000)
    },
    render: function() {
        YAHOO.util.Dom.addClass(document.body, 'r-designer');
        var dh = YAHOO.ext.DomHelper;
        var treeConfig = {
            id:'componentTree',
            url:'',
            title:'',
            hideAttribute: this.treeHideAttribute,
            rootTag:this.rootTag,
            contentPath:this.contentPath,
            keyAttribute:this.keyAttribute,
            columns:[{colLabel:'', attributeName:this.treeDisplayAttribute, width:400, sortable:false}]
        }
        this.tree = new YAHOO.rapidjs.component.TreeGrid(dh.append(document.body, {tag:'div'}), treeConfig);
        var toolsWrp = dh.append(this.tree.toolbar.el, {tag:'div', cls:'r-designer-tools',
            html:'<table><tbody><tr><td><div class="wrp"></td><td></div><div class="wrp"></div></td></tr></tbody></table>'});
        var wrps = YAHOO.util.Dom.getElementsByClassName('wrp', 'div', toolsWrp);
        new YAHOO.rapidjs.component.Button(wrps[0], {className:'r-designer-saveButton', scope:this, click:this.save, text:'Save'});
        new YAHOO.rapidjs.component.Button(wrps[1], {className:'r-designer-generateButton', scope:this, click:this.generate, text:'Generate'});
        this.tree.events["selectionChanged"].subscribe(this.treeSelectionChanged, this, true);
        this.tree.events["rowMenuClicked"].subscribe(this.treeMenuClicked, this, true);

        var propertySavedFunc = function(oArgs) {
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
        }
        for (var editor in this.editors) {
            this.editors[editor].subscribe('saveEvent', propertySavedFunc, this, true);
        }
        var myColumnDefs = [
            {key:"name", label:"Name", sortable:true, width:150},
            {key:"value", label:"Value", sortable:true, width:270}
        ];


        var myDataSource = new YAHOO.util.DataSource([]);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
            fields: ["name","value"]
        };
        var propWrp = dh.append(document.body, {tag:'div'});
        var propWrpId = YAHOO.util.Dom.generateId(propWrp, "propertiesGrid");
        propWrp.id = propWrpId;
        var propGridWrp = dh.append(propWrp, {tag:'div'}, true);
        this.propertyGrid = new YAHOO.widget.DataTable(propGridWrp.dom, myColumnDefs, myDataSource, {'MSG_EMPTY':'', scrollable:true, width:'450px', height:'345px'});
        var highlightEditableCell = function(oArgs) {
            var elCell = oArgs.target;
            if (YAHOO.util.Dom.hasClass(elCell, "yui-dt-editable")) {
                this.highlightCell(elCell);
            }
        };
        this.propertyGrid.subscribe("cellMouseoverEvent", highlightEditableCell);
        this.propertyGrid.subscribe("cellMouseoutEvent", this.propertyGrid.onEventUnhighlightCell);
        this.propertyGrid.subscribe("cellClickEvent", function (oArgs) {
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
        }, this, true);

        var addTooltip = function() {
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
        }

        addTooltip.call(this);

        var myContextMenu = new YAHOO.widget.ContextMenu("propcontextmenu", {trigger:this.propertyGrid.getTbodyEl()});
        myContextMenu.addItem({text:'<em class="r-designer-property-delete">Delete property</em>'});
        myContextMenu.render(document.body);
        myContextMenu.showEvent.subscribe(function() {
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
        }, this, true)

        myContextMenu.clickEvent.subscribe(function(p_sType, p_aArgs) {
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
        }, this, true)

        this.propertyContextMenu = myContextMenu;
        this.propGridWrp = propGridWrp;


        var selectWrp = dh.append(propWrp, {tag:'div', cls:'r-designer-property-toolbar',
            html:'<table><tbody><tr><td width="100%">Add:</td><td width="0%"><select style="width:150px"></select></td></tr></tbody></table>'});
        this.propertySelect = selectWrp.getElementsByTagName('select')[0];


        var propertySelectedToBeAdded = function() {
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
        }

        YAHOO.util.Event.addListener(this.propertySelect, 'change', propertySelectedToBeAdded, this, true)


        YAHOO.util.Event.onDOMReady(function() {
            var layout = new YAHOO.widget.Layout({
                units: [
                    { position: 'center', resize: false},
                    { position: 'left', resize: true, gutter: '1px', width:450}
                ]
            });
            layout.on('render', function() {
                var resizeGrid = function(width, height) {
                    var grid = this.propertyGrid;
                    var theadEl = getEl(grid.getTheadEl())
                    var bodyHeight = height - theadEl.getHeight()
                    grid.set('height', '' + bodyHeight + 'px')
                    grid.set('width', '' + width + 'px')
                    var nameCol = grid.getColumn('name');
                    var valueCol = grid.getColumn('value');
                    var thEl = getEl(valueCol.getThEl());
                    var linerEl = getEl(valueCol.getThLinerEl());
                    var difBetweenThAndLinerWidths;
                    if (YAHOO.env.ua.ie) {
                        difBetweenThAndLinerWidths = thEl.getWidth() - linerEl.getWidth()
                    }
                    else {
                        difBetweenThAndLinerWidths = thEl.getWidth() - linerEl.getWidth() + linerEl.getPadding('lr');
                    }
                    grid.setColumnWidth(valueCol, width - nameCol.width - 5 - difBetweenThAndLinerWidths * 2);
                }
                var el = layout.getUnitByPosition('left').get('wrap');
                var layout2 = new YAHOO.widget.Layout(el, {
                    parent: layout,
                    minWidth: 400,
                    units: [
                        { position: 'center', body:this.tree.container.id},
                        { position: 'bottom', body: propWrpId, height: 400, resize: true, header:'Property Editor'},
                    ]
                });
                layout2.render();
                var centerUnit = layout2.getUnitByPosition('center');
                var bottomUnit = layout2.getUnitByPosition('bottom');
                this.tree.resize(centerUnit.getSizes().body.w, centerUnit.getSizes().body.h);
                var self = this;
                setTimeout(function() {
                    var gridHeight = bottomUnit.getSizes().body.h - getEl(self.propGridWrp.dom.nextSibling).getHeight();
                    self.propGridWrp.setHeight(gridHeight);
                    resizeGrid.call(self, centerUnit.getSizes().body.w, gridHeight)
                }, 50)
                layout2.on('resize', function() {
                    this.tree.resize(centerUnit.getSizes().body.w, centerUnit.getSizes().body.h);
                    var bottomHeight = bottomUnit.getSizes().body.h;
                    var toolBarHeight = getEl(this.propGridWrp.dom.nextSibling).getHeight();
                    var gridHeight = bottomHeight - toolBarHeight
                    this.propGridWrp.setHeight(gridHeight);
                    resizeGrid.call(this, centerUnit.getSizes().body.w, gridHeight)
                }, this, true);

                el = layout.getUnitByPosition('center').get('wrap');
                var topHtml = YAHOO.ext.DomHelper.append(document.body, {tag:'div', cls:'r-designer-layout-top', html:'Layout Preview'})
                YAHOO.util.Dom.generateId(topHtml, 'layouttop')
                this.helpView = YAHOO.ext.DomHelper.append(document.body, {tag:'div'})
                YAHOO.util.Dom.generateId(this.helpView, 'designer_help')
                var layout3 = new YAHOO.widget.Layout(el, {
                    parent: layout,
                    units: [
                        { position: 'center', gutter:"7px"},
                        { position: 'bottom', body: this.helpView.id, height: 400},
                        { position: 'top', body: topHtml.id, height: 25},
                    ]
                });
                layout3.render();
                this.leftLayout = layout3;
            }, this, true);
            layout.render();
            this.layout = layout;
        }, this, true);
    }
};
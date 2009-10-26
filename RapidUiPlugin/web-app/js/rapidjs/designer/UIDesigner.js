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
    this.helpUrl = null;
    YAHOO.ext.util.Config.apply(this, config);
    this.treeDisplayAttribute = 'designer_name'
    this.itemTabAtt = 'designer_item_tab';
    this.tree = null;
    this.propertyGrid = null;
    this.currentLayout = null;
    this.currentLayoutNode = null;
    this.dataChanged = false;
    this.data = null;
    this.nextNumber = 0;
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
    this.requester = new YAHOO.rapidjs.Requester(this.processSuccess, this.handleFailure, this);
    this.editors['Expression'].move = RenderUtils.textAreaEditorMoveFunc;
    this.editors['DateMock'].attach = RenderUtils.dateEditorAttachFunc;
    RenderUtils.handleFirefoxCursorBugForEditors(this.editors);
    this.loadingMask = new YAHOO.rapidjs.component.LoadingMask({});
    this.confirmBox = new YAHOO.rapidjs.component.ConfirmBox({handler:this.confirmBoxHandler, scope:this});
    this.render();
    this.loadingMask.show("Loading...")
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
        this.requester.doGetRequest(this.metaDataUrl, null, this.loadMetaData)
    },
    loadMetaData: function(response) {
        if (YAHOO.rapidjs.Connect.containsError(response) == false) {
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
                menuItems.push({id:"clone", label:"Clone", visible:"params.data.canBeDeleted && params.data." + this.treeTypeAttribute + " != 'Layout'"});
                menuItems.push({id:"delete", label:"Delete", visible:"params.data.canBeDeleted && !(params.data." + this.treeTypeAttribute + " == 'Layout' && params.dataNode.parentNode().getAttribute('" + this.treeTypeAttribute + "') == 'Tab')"});
                this.tree.treeGridView.setMenuItems(menuItems);
                this.tree.treeGridView.rootImages = rootImages;
            }
            modifyTreeConfig.call(this)
            this.getHelp();
        }

    },
    getHelp: function() {
        this.requester.doGetRequest(this.helpUrl, null, this.loadHelp)
    },
    getData: function() {
        this.requester.doGetRequest(this.url, null, this.loadData)
    },
    loadHelp: function(response) {
        if (YAHOO.rapidjs.Connect.containsError(response) == false) {
            UIConfig.loadHelp(response);
            this.getData();
        }
    },
    loadData: function(response) {
        if (YAHOO.rapidjs.Connect.containsError(response) == false) {
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
            this.loadingMask.hide();
        }
    },

    processSuccess: function(response) {
        YAHOO.rapidjs.ErrorManager.serverUp();
        if (YAHOO.rapidjs.Connect.checkAuthentication(response) == false)
        {
            return;
        }
        else
        {
            this.tree.events["success"].fireDirect(this.tree);
        }
        this.tree.events["loadstatechanged"].fireDirect(this.tree, false);

    },

    handleFailure: function(errors, statusCodes) {
        this.loadingMask.hide();
        this.tree.events["error"].fireDirect(this.tree, errors);
    },

    treeSelectionChanged:function(xmlData) {
        this.displayItemProperties(xmlData);
        this.showHelp();
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
        if (itemType == "ActionTrigger") {
            var triggerType = xmlData.getAttribute("type");
            data[data.length] = {name:"type", value:triggerType}
            if (triggerType == "Action event") {
                data[data.length] = {name:"triggeringAction", value:xmlData.getAttribute("triggeringAction") || ""}
            }
            else if (triggerType == "Menu" || triggerType == "Component event") {
                data[data.length] = {name:"component", value:xmlData.getAttribute("component") || ""}
            }
            data[data.length] = {name:"event", value:xmlData.getAttribute("event") || ""}
        }
        else {
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
        }
        this.clearPropertyGrid();
        this.propertyGrid.addRows(data)
        if (itemType == "FunctionAction") {
            RenderUtils.displayFunctionActionProperties.call(this, xmlData)
        }
        this.closeCellEditor();
    },
    clearPropertyGrid: function() {
        var length = this.propertyGrid.getRecordSet().getLength()
        this.propertyGrid.deleteRows(0, length)
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
            RenderUtils.renderLayout.call(this, layoutNode)
        }
    },
    changeLayout: function(xmlData) {
        var layoutNode = DesignerUtils.getCurrentLayoutNode(this, xmlData);
        if ((!layoutNode && this.currentLayout) || layoutNode != this.currentLayoutNode) {
            this.destroyCurrentLayout();
        }
        if (layoutNode && layoutNode != this.currentLayoutNode) {
            RenderUtils.renderLayout.call(this, layoutNode)
        }
    },

    createTreeNode: function(parentNode, itemType, attributes) {
        var atts = attributes;
        if (!atts) {
            atts = {};
        }
        if (UIConfig.getProperties(itemType)[this.treeHideAttribute] != null) {
            atts[this.treeHideAttribute] = 'true';
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
                        var propValue = !defaultValue || defaultValue == '' ? itemType + (this.nextNumber ++) : defaultValue
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
            var newNode = this.createTreeNode(xmlData, itemType);
            var currentTab = xmlData.getAttribute(this.itemTabAtt);
            this.addExtraAttributesToChildNodes(xmlData, currentTab);
            if (isClickedDataInCurrentTab.call(this, xmlData) && (id == "add_Layout" || id == "add_TopUnit" || id == "add_BottomUnit" || id == "add_LeftUnit" || id == "add_RightUnit")) {
                this.refreshLayout(xmlData);
            }
            this.refreshTree();
            this.expandTreeNode(row)
            if (itemType == "ActionTrigger" && this.currentDisplayedItemData) {
                this.showHelp();
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
        else if (id == 'clone') {
            var clonedNode = xmlData.cloneNode(true, true);
            if (DesignerUtils.getItemType(this, clonedNode) == 'Tab') {
                clonedNode.setAttribute(this.itemTabAtt, clonedNode.getAttribute(this.keyAttribute));
            }
            xmlData.parentNode().appendChild(clonedNode);
            var currentTab = clonedNode.getAttribute(this.itemTabAtt);
            this.addExtraAttributesToChildNodes(clonedNode, currentTab);
            this.refreshTree();
        }
    },

    refreshTree: function() {
        this.tree.treeGridView.refreshData();
    },
    showHelp:function() {
        RenderUtils.showHelp.call(this)
    },
    save : function() {
        this.loadingMask.show("Saving, please wait...");
        this.requester.doPostRequest(this.saveUrl, {configuration:this.data.firstChild().toString()}, this.saveReturned);
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
        this.loadingMask.show("Generating, please wait...");
        this.requester.doGetRequest(this.generateUrl, {}, this.generateReturned);
    },
    saveReturned: function(response) {
        if (YAHOO.rapidjs.Connect.containsError(response) == false) {
            this.loadingMask.show("Successfully saved.");
            this.dataChanged = false;
            var self = this;
            setTimeout(function() {
                self.loadingMask.hide();
            }, 1000)
        }
    },
    generateReturned: function(response) {
        if (YAHOO.rapidjs.Connect.containsError(response) == false) {
            this.loadingMask.show("Successfully generated.");
            var self = this;
            setTimeout(function() {
                self.loadingMask.hide();
            }, 1000)
        }
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

        for (var editor in this.editors) {
            this.editors[editor].subscribe('saveEvent', RenderUtils.editorSaveFunc, this, true);
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
        this.propertyGrid.subscribe("cellClickEvent", RenderUtils.propertyGridClickedFuntion, this, true);

        RenderUtils.addTooltip.call(this);

        var myContextMenu = new YAHOO.widget.ContextMenu("propcontextmenu", {trigger:this.propertyGrid.getTbodyEl()});
        myContextMenu.addItem({text:'<em class="r-designer-property-delete">Delete property</em>'});
        myContextMenu.render(document.body);
        myContextMenu.showEvent.subscribe(RenderUtils.handleContextMenuShow, this, true)
        myContextMenu.clickEvent.subscribe(RenderUtils.handleContextMenuClick, this, true)

        this.propertyContextMenu = myContextMenu;
        this.propGridWrp = propGridWrp;


        var selectWrp = dh.append(propWrp, {tag:'div', cls:'r-designer-property-toolbar',
            html:'<table><tbody><tr><td width="100%">Add:</td><td width="0%"><select style="width:150px"></select></td></tr></tbody></table>'});
        this.propertySelect = selectWrp.getElementsByTagName('select')[0];

        YAHOO.util.Event.addListener(this.propertySelect, 'change', RenderUtils.propertySelectedToBeAdded, this, true)


        YAHOO.util.Event.onDOMReady(function() {
            var layout = new YAHOO.widget.Layout({
                units: [
                    { position: 'center', resize: false},
                    { position: 'left', resize: true, gutter: '0 5 0 0', width:450}
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
                        { position: 'bottom', body: propWrpId, height: 400, resize: true, header:'Property Editor', gutter:"5 0 0 0"},
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
                this.helpView = YAHOO.ext.DomHelper.append(document.body, {tag:'div', cls:'r-designer-help'})
                YAHOO.util.Dom.generateId(this.helpView, 'designer_help')
                var layout3 = new YAHOO.widget.Layout(el, {
                    parent: layout,
                    units: [
                        { position: 'center', gutter:"7px"},
                        { position: 'bottom', body: this.helpView.id, height: 400, header:'Help', scroll:true},
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
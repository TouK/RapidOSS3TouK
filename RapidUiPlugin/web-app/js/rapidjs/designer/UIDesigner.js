YAHOO.namespace('rapidjs', 'rapidjs.designer');
YAHOO.rapidjs.designer.UIDesigner = function(config) {
    this.rootTag = null;
    this.contentPath = null;
    this.keyAttribute = null
    this.treeTypeAttribute = null;
    this.url = null
    this.saveUrl = null
    YAHOO.ext.util.Config.apply(this, config);
    this.treeDisplayAttribute = 'designer_name'
    this.itemTabAtt = 'designer_item_tab';
    this.tree = null;
    this.propertyGrid = null;
    this.currentLayout = null;
    this.currentLayoutNode = null;
    this.data = null;

    this.render();
    this.getData();
}

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
                this.tree.handleErrors(response);
            }

        }
        catch(e)
        {
        }
        this.tree.events["loadstatechanged"].fireDirect(this.tree, false);
    },

    handleFailure: function(response) {
        this.tree.processFailure(response)
    },

    treeSelectionChanged:function(xmlData) {
        this.displayItemProperties(xmlData);
        this.changeLayout(xmlData);
    },

    displayItemProperties: function(xmlData) {
        this.currentDisplayedItemData = xmlData;
        var itemType = xmlData.getAttribute(this.treeTypeAttribute);
        var properties = UIConfig.getProperties(itemType)
        var data = [];
        SelectUtils.clear(this.propertySelect);
        SelectUtils.addOption(this.propertySelect, '', '');
        for (var prop in properties) {
            if (UIConfig.isPropertyRequired(itemType, prop)) {
                data[data.length] = {name:prop, value:xmlData.getAttribute(prop) || prop}
            }
            else {
                SelectUtils.addOption(this.propertySelect, prop, prop)
            }
        }
        var length = this.propertyGrid.getRecordSet().getLength()
        this.propertyGrid.deleteRows(0, length)
        this.propertyGrid.addRows(data)
    },
    changeLayout: function(xmlData) {
        var layoutNode = this.getCurrentLayoutNode(xmlData);
        if ((!layoutNode && this.currentLayout) || layoutNode != this.currentLayoutNode) {
            if (this.currentLayout) {
                this.currentLayout.destroy();
                //destroying child layout removes parent layout's window resize subscription, I couldn't understand why
                YAHOO.util.Event.on(window, 'resize', this.layout.resize, this.layout, true);

            }
            this.currentLayout = null;
            this.currentLayoutNode = null;
        }
        if (layoutNode && layoutNode != this.currentLayoutNode) {
            this.renderLayout(layoutNode)
        }

    },
    renderLayout:function(layoutNode) {
        var dh = YAHOO.ext.DomHelper;
        var centerUnit = this.layout.getUnitByPosition('center');
        var curLay = new YAHOO.widget.Layout(centerUnit.get('wrap'), {
            parent: this.layout,
            units: [
                { position: 'center', body:dh.append(document.body, {tag:'div', html:'sezgin', id:YAHOO.util.Dom.generateId(null, 'layoutUnit')}).id},
                { position: 'bottom',height: 200, resize: true, body:dh.append(document.body, {tag:'div', html:'sezgin2', id:YAHOO.util.Dom.generateId(null, 'layoutUnit')}).id},
            ]
        });
        curLay.render();
        this.currentLayout = curLay;
        this.currentLayoutNode = layoutNode;
        this.layout.resize();
    },
    getCurrentLayoutNode: function(xmlData) {
        var currentTabNode = this.getTabNodeFromNode(xmlData);
        if (currentTabNode) {
            var childNodes = currentTabNode.childNodes();
            for (var i = 0; i < childNodes.length; i++) {
                if (childNodes[i].getAttribute(this.treeTypeAttribute) == "Layout") {
                    return childNodes[i]
                }
            }
        }
        return null;
    },

    getTabNodeFromNode: function(xmlData) {
        var currentTab = xmlData.getAttribute(this.itemTabAtt);
        if (currentTab) {
            var tabNodes = this.data.findAllObjects(this.keyAttribute, currentTab, this.contentPath);
            if (tabNodes.length > 0) {
                return tabNodes[0];
            }
        }
        return null;
    },
    treeMenuClicked: function(xmlData, id, parentId) {

        var createTreeNode = function(parentNode, itemType) {
            var id = YAHOO.util.Dom.generateId(null, itemType);
            var newNode = xmlData.createChildNode(null, 1, this.contentPath);
            newNode.setAttribute(this.keyAttribute, id);
            newNode.setAttribute(this.treeTypeAttribute, itemType);
            var itemMetaProperties = UIConfig.getProperties(itemType);
            for (var prop in itemMetaProperties) {
                if (UIConfig.isPropertyRequired(itemType, prop)) {
                    if (UIConfig.isDisplayProperty(itemType,prop)) {
                        newNode.setAttribute(prop, itemType);
                    }
                    else {
                        newNode.setAttribute(prop, prop);
                    }
                }
            }
            parentNode.appendChild(newNode);
            var metaChildren = UIConfig.getChildren(itemType)
            for (var i = 0; i < metaChildren.length; i++) {
                if (!UIConfig.canBeDeleted(metaChildren[i]) || (itemType == "Tab" && metaChildren[i] == "Layout")) {
                    createTreeNode.call(this, newNode, metaChildren[i]);
                }
            }
            return newNode;
        };

        if (id.indexOf("add_") == 0) {
            var itemType = id.substr(4);
            var newNode = createTreeNode.call(this, xmlData, itemType);
            var currentTab = xmlData.getAttribute(this.itemTabAtt);
            this.addExtraAttributesToChildNodes(xmlData, currentTab);
            this.refreshTree();
        }
        else if (id == "delete") {
            xmlData.parentNode().removeChild(xmlData);
            this.refreshTree();
            //TODO: destroy layout if needed.
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
        var postData = 'data=' + this.data.firstChild().toString();
        YAHOO.util.Connect.asyncRequest('POST', this.saveUrl, callback, postData);
    },
    saveSuccess: function(response) {
        alert("Save is successfull")
    },
    render: function() {
        var dh = YAHOO.ext.DomHelper;
        var getTreeConfig = function() {
            var pathName = window.location.pathname;
            var splits = pathName.split('/');
            var distanceToWebApp = splits.length - 3;
            var stringToAddToUrl = '';
            for (var index = 0; index < distanceToWebApp; index++) {
                stringToAddToUrl += '../';
            }
            var rootImages = [];
            var menuItems = [];
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
                    for (var i = 0; i < children.length; i++) {
                        var child = children[i];
                        if (UIConfig.canBeDeleted(child)) {
                            var displayName = UIConfig.getDisplayName(child);
                            menuItems.push({id:'add_' + child, label:'Add New ' + displayName, visible:"params.data." + this.treeTypeAttribute + " =='" + item + "'"});
                        }
                    }
                }
            }
            menuItems.push({id:"delete", label:"Delete", visible:"params.data.canBeDeleted"});
            var treeConfig = {
                id:'componentTree',
                url:'',
                title:'',
                rootTag:this.rootTag,
                contentPath:this.contentPath,
                rootImages: rootImages,
                menuItems:menuItems,
                expanded:true,
                keyAttribute:this.keyAttribute,
                columns:[{colLabel:'', attributeName:this.treeDisplayAttribute, width:400}]
            }
            return treeConfig;
        }
        var treeConfig = getTreeConfig.call(this);
        this.tree = new YAHOO.rapidjs.component.TreeGrid(dh.append(document.body, {tag:'div'}), treeConfig);
        var toolsWrp = dh.append(this.tree.toolbar.el, {tag:'div', class:'r-designer-tools',
            html:'<div class="wrp"></div>'}) ;
        var wrps = YAHOO.util.Dom.getElementsByClassName('wrp', 'div', toolsWrp);
        new YAHOO.rapidjs.component.Button(wrps[0], {className:'r-designer-saveButton', scope:this, click:this.save, text:'Save'});
        this.tree.events["selectionChange"].subscribe(this.treeSelectionChanged, this, true);
        this.tree.events["rowMenuClick"].subscribe(this.treeMenuClicked, this, true);


        var editor = new YAHOO.widget.TextboxCellEditor({disableBtns:true});
        editor.subscribe('saveEvent', function(oArgs) {
            var record = oArgs.editor.getRecord();
            var propName = record.getData("name")
            var propValue = record.getData("value")
            this.currentDisplayedItemData.setAttribute(propName, propValue);
            var itemType = this.currentDisplayedItemData.getAttribute(this.treeTypeAttribute)
            if (UIConfig.isDisplayProperty(itemType, propName)) {
                this.currentDisplayedItemData.setAttribute(this.treeDisplayAttribute, propValue);
            }
            this.refreshTree();
        }, this, true);
        var myColumnDefs = [
            {key:"name", label:"Name", sortable:true, width:150},
            {key:"value", label:"Value", sortable:true, width:250, editor:editor}
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
        this.propertyGrid = new YAHOO.widget.DataTable(propGridWrp.dom, myColumnDefs, myDataSource, {'MSG_EMPTY':''});
        var highlightEditableCell = function(oArgs) {
            var elCell = oArgs.target;
            if (YAHOO.util.Dom.hasClass(elCell, "yui-dt-editable")) {
                this.highlightCell(elCell);
            }
        };
        this.propertyGrid.subscribe("cellMouseoverEvent", highlightEditableCell);
        this.propertyGrid.subscribe("cellMouseoutEvent", this.propertyGrid.onEventUnhighlightCell);
        this.propertyGrid.subscribe("cellClickEvent", this.propertyGrid.onEventShowCellEditor);

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
                    SelectUtils.addOption(this.propertySelect, prop, prop);
                    this.currentDisplayedItemData.setAttribute(prop, null);
                }
            }
        }, this, true)

        this.propertyContextMenu = myContextMenu;
        this.propGridWrp = propGridWrp;


        var selectWrp = dh.append(propWrp, {tag:'div', class:'r-designer-property-toolbar',
            html:'<table><tbody><tr><td width="100%">Add:</td><td width="0%"><select style="width:150px"></select></td></tr></tbody></table>'});
        this.propertySelect = selectWrp.getElementsByTagName('select')[0];


        var propertySelectedToBeAdded = function() {
            if (this.propertySelect.selectedIndex > 0) {
                var prop = this.propertySelect.options[this.propertySelect.selectedIndex].value;
                this.propertyGrid.addRow({name:prop, value:this.currentDisplayedItemData.getAttribute(prop) || ''});
                SelectUtils.remove(this.propertySelect, prop)
            }
        }

        YAHOO.util.Event.addListener(this.propertySelect, 'change', propertySelectedToBeAdded, this, true)


        YAHOO.util.Event.onDOMReady(function() {
            var layout = new YAHOO.widget.Layout({
                units: [
                    { position: 'center', resize: false, gutter: '3px' },
                    { position: 'left', resize: true, gutter: '1px', width:450}
                ]
            });
            layout.on('render', function() {
                var el = layout.getUnitByPosition('left').get('wrap');
                var layout2 = new YAHOO.widget.Layout(el, {
                    parent: layout,
                    minWidth: 400,
                    units: [
                        { position: 'center', body:this.tree.container.id},
                        { position: 'bottom', body: propWrpId, height: 400, resize: true},
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
                }, 50)
                layout2.on('resize', function() {
                    this.tree.resize(centerUnit.getSizes().body.w, centerUnit.getSizes().body.h);
                    var bottomHeight = bottomUnit.getSizes().body.h;
                    var toolBarHeight = getEl(this.propGridWrp.dom.nextSibling).getHeight();
                    this.propGridWrp.setHeight(bottomHeight - toolBarHeight);
                }, this, true);
            }, this, true);
            layout.render();
            this.layout = layout;
        }, this, true);
    }
}
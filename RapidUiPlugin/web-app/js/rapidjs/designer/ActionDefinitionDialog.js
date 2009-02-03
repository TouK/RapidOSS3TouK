YAHOO.namespace("rapidjs", "rapidjs.designer");

YAHOO.rapidjs.designer.ActionDefinitionDialog = function(designer) {
    this.designer = designer;
    this.dialog = null;
};

YAHOO.rapidjs.designer.ActionDefinitionDialog.prototype = {

    render: function() {
        var config = {
            width:800,
            height:700,
            minWidth:100,
            minHeight:100,
            x:250,y:0,
            resizable: false,
            title: 'Configure Action',
            buttons:[
                {text:"Save", handler:this.handleSave, scope:this, isDefault:true },
                {text:"Cancel", handler:this.hide, scope:this }]
        }
        this.dialog = new YAHOO.rapidjs.component.Dialog(config);
        this.combEditor = new YAHOO.widget.DropdownCellEditor({});

        var dh = YAHOO.ext.DomHelper;
        var wrp = dh.append(this.dialog.body, {tag:'div', cls:'r-designer-actdlg-wrp',
            html:'<table border="2"><tbody><tr>' +
                 '<td style="vertical-align:top"><div style="width:395px;overflow:hidden"></div></td>' +
                 '<td style="vertical-align:top"><div style="width:355px;overflow:hidden"></div></td></tr></tbody></table>'});
        var wrappers = wrp.getElementsByTagName('div');
        var left = wrappers[0];
        var right = wrappers[1];
        this.wrpEl = getEl(wrp);
        var commonView = dh.append(left, {tag:'div', style:'padding:5 0 5 0;',
            html:'<form action="javascript:void(0)"><div style="padding:5px;"><table><tbody>' +
                 '<tr><td class="field-name">Name:</td><td><input style="width:200px"></input></td></tr>' +
                 '<tr><td class="field-name">Type:</td><td><select style="width:200px"><option name="request">request</option><option name="merge">merge</option>' +
                 '<option name="link">link</option><option name="function">function</option><option name="combined">combined</option></select></td></tr>' +
                 '<tr><td class="field-name">Condition:</td><td><textarea cols="40"></textarea></td></tr>' +
                 '</tbody></table>' +
                 '<div><div style="font-family:verdana;padding:3px 5px;color:#0A52AF">Select a component or a global event to trigger your action.</div>' +
                 '<div><table><tbody><tr><td><table><tbody>' +
                 '<tr><td class="field-name">Component:</td><td><select style="width:150px;"></select></td></tr>' +
                 '<tr><td class="field-name">Event:</td><td><select style="width:150px;"></select></td></tr>' +
                 '</tbody></table></td>' +
                 '<td><div class="r-designer-actdlg-btnwrp"><div></div></div></td>' +
                 '</tr></tbody></table></div></div>' +
                 '<div class="r-designer-actdlg-eventdef"></div>' +
                 '<div class="grid-def">Triggering Events:</div>' +
                 '<div class="r-designer-actdlg-gridwrp"></div>' +
                 '</div></form>'})

        var selects = commonView.getElementsByTagName('select');
        this.typeSelect = selects[0];
        this.eventCompSelect = selects[1];
        this.eventSelect = selects[2];
        this.nameInput = commonView.getElementsByTagName('input')[0];
        this.conditionInput = commonView.getElementsByTagName('textarea')[0];
        var btnwrps = YAHOO.util.Dom.getElementsByClassName("r-designer-actdlg-btnwrp", 'div', commonView);
        var gridwrps = YAHOO.util.Dom.getElementsByClassName("r-designer-actdlg-gridwrp", 'div', commonView);

        this.eventDescrEl = YAHOO.util.Dom.getElementsByClassName("r-designer-actdlg-eventdef", 'div', commonView)[0];
        YAHOO.util.Event.addListener(this.eventSelect, "change", this.eventChanged, this, true);
        YAHOO.util.Event.addListener(this.typeSelect, "change", this.typeChanged, this, true);
        YAHOO.util.Event.addListener(this.eventCompSelect, "change", this.eventComponentChanged, this, true);


        var eventColumnDefs = [
            {key:"component", label:"Component", sortable:true, width:150, editor:this.combEditor},
            {key:"event", label:"Event", sortable:true, width:190, editor:this.combEditor}
        ];
        var eventDs = new YAHOO.util.DataSource([]);
        eventDs.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        eventDs.responseSchema = {
            fields: ["component","event"]
        };
        this.eventsGrid = new YAHOO.widget.DataTable(gridwrps[0], eventColumnDefs, eventDs, {'MSG_EMPTY':'',scrollable:true, height:"70px"});

        var highlightEditableCell = function(oArgs) {
            var elCell = oArgs.target;
            if (YAHOO.util.Dom.hasClass(elCell, "yui-dt-editable")) {
                this.highlightCell(elCell);
            }
        };
        this.eventsGrid.subscribe("cellMouseoverEvent", highlightEditableCell);
        this.eventsGrid.subscribe("cellMouseoutEvent", this.eventsGrid.onEventUnhighlightCell);
        this.eventsGrid.subscribe("cellClickEvent", function (oArgs) {
            var target = oArgs.target,
                    column = this.eventsGrid.getColumn(target),
                    record = this.eventsGrid.getRecord(target)

            var dropDownOptions = [];
            if (column.getKey() == 'component') {
                dropDownOptions.push('Global');
                for (var compName in this.currentComponents) {
                    dropDownOptions.push(compName);
                }
            }
            else {
                var component = record.getData("component");
                var compType;
                if (component == 'Global') {
                    compType = 'Global';
                }
                else {
                    compType = this.currentComponents[component];
                }
                if (compType) {
                    var events = UIConfig.getComponentEvents(compType);
                    for (var event in events) {
                        dropDownOptions.push(event);
                    }
                }
            }
            this.combEditor.dropdownOptions = dropDownOptions;
            this.combEditor.renderForm();
            column.editor = this.combEditor;
            this.eventsGrid.showCellEditor(target);

        }, this, true);

        new YAHOO.widget.Button(btnwrps[0].firstChild, {onclick:{fn:function() {
            var comp = this.eventCompSelect.options[this.eventCompSelect.selectedIndex].value
            var event = this.eventSelect.options[this.eventSelect.selectedIndex].value
            this.eventsGrid.addRow({component:comp, event:event})
        }, scope:this},label: 'Add Event'});

        this.requestView = dh.append(right, {tag:'div', style:'padding:5 0 5 0;',
            html:'<div style="padding:5px;">' +
                 '<div><table><tbody><tr><td class="field-name">Url:</td><td><input style="width:200px;"></input></td></tr>' +
                 '<tr><td class="field-name">Timeout:</td><td><input style="width:200px;"></input></td></tr>' +
                 '<tr><td class="field-name">Remove Attribute:</td><td><input style="width:200px;"></input></td></tr></tbody></table></div>' +
                 '<div style="margin-top:10px"><table><tbody><tr><td class="grid-def" width="100%">Request Attributes:</td>' +
                 '<td width="0%"><div class="r-designer-actdlg-btnwrp"><div></div></div></td></tr></tbody></table></div>' +
                 '<div class="r-designer-actdlg-gridwrp"></div>' +
                 '<div style="margin-top:10px"><table><tbody><tr><td class="grid-def" width="100%">Components:</td>' +
                 '<td width="0%"><div class="r-designer-actdlg-btnwrp"><div></div></div></td></tr></tbody></table></div>' +
                 '<div class="r-designer-actdlg-gridwrp"></div>' +
                 '</div>'})
        var trs = this.requestView.getElementsByTagName('tr');
        this.mergeView = trs[2];
        var requestInputs = this.requestView.getElementsByTagName('input');
        this.requestUrlInput = requestInputs[0];
        this.timeoutInput = requestInputs[1];
        this.removeAttInput = requestInputs[2];
        gridwrps = YAHOO.util.Dom.getElementsByClassName("r-designer-actdlg-gridwrp", 'div', this.requestView);
        btnwrps = YAHOO.util.Dom.getElementsByClassName("r-designer-actdlg-btnwrp", 'div', this.requestView);

        var requestColumnDefs = [
            {key:"key", label:"Key", sortable:true, width:150, editor:new YAHOO.widget.TextboxCellEditor({disableBtns:true})},
            {key:"value", label:"Value", sortable:true, width:150, editor:new YAHOO.widget.TextboxCellEditor({disableBtns:true})}
        ];
        var requestDs = new YAHOO.util.DataSource([]);
        requestDs.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        requestDs.responseSchema = {
            fields: ["key","value"]
        };
        this.requestParamsGrid = new YAHOO.widget.DataTable(gridwrps[0], requestColumnDefs, requestDs, {'MSG_EMPTY':'',scrollable:true, height:"70px"});
        this.requestParamsGrid.subscribe("cellMouseoverEvent", highlightEditableCell);
        this.requestParamsGrid.subscribe("cellMouseoutEvent", this.requestParamsGrid.onEventUnhighlightCell);
        this.requestParamsGrid.subscribe("cellClickEvent", this.requestParamsGrid.onEventShowCellEditor);

        new YAHOO.widget.Button(btnwrps[0].firstChild, {onclick:{fn:function() {
            this.requestParamsGrid.addRow({key:'key', value:'value'})
        }, scope:this},label: 'Add Request Parameter'});

        var compColumnDefs = [
            {key:"component", label:"Component", sortable:true, width:320, editor:this.combEditor}
        ];
        var compDs = new YAHOO.util.DataSource([]);
        compDs.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        compDs.responseSchema = {
            fields: ["component"]
        };
        this.compGrid = new YAHOO.widget.DataTable(gridwrps[1], compColumnDefs, compDs, {'MSG_EMPTY':'',scrollable:true, height:"70px"});
        this.compGrid.subscribe("cellMouseoverEvent", highlightEditableCell);
        this.compGrid.subscribe("cellMouseoutEvent", this.compGrid.onEventUnhighlightCell);
        this.compGrid.subscribe("cellClickEvent", function (oArgs) {
            var target = oArgs.target,
                    column = this.compGrid.getColumn(target)

            var records = this.compGrid.getRecordSet().getRecords();
            var listedComps = {};
            for (var i = 0; i < records.length; i++) {
                var comp = records[i].getData("component");
                listedComps[comp] = comp;
            }
            var dropDownOptions = [];
            for (var compName in this.currentComponents) {
                if (!listedComps[compName]) {
                    dropDownOptions.push(compName);
                }
            }
            this.combEditor.dropdownOptions = dropDownOptions;
            this.combEditor.renderForm();
            column.editor = this.combEditor;
            this.compGrid.showCellEditor(target);

        }, this, true);

        new YAHOO.widget.Button(btnwrps[1].firstChild, {onclick:{fn:function() {
            this.compGrid.addRow({component:'component'})
        }, scope:this},label: 'Add Component'});

        this.linkView = dh.append(right, {tag:'div', style:'padding:5 0 5 0;',
            html:'<div style="padding:5px;">' +
                 '<div><table><tbody><tr><td class="field-name">Url:</td><td><input style="width:200px;"></input></td></tr></tbody></table></div></div>'})

        this.linkUrlInput = this.linkView.getElementsByTagName('input')[0];
        this.combinedView = dh.append(right, {tag:'div', style:'padding:5 0 5 0;',
            html:'<div style="padding:5px;">' +
                 '<div style="margin-top:10px"><table><tbody><tr><td class="grid-def" width="100%">Actions:</td>' +
                 '<td width="0%"><div class="r-designer-actdlg-btnwrp"><div></div></div></td></tr></tbody></table></div>' +
                 '<div class="r-designer-actdlg-gridwrp"></div>' +
                 '</div>'})

        var actionColumnDefs = [
            {key:"action", label:"Action", sortable:true, width:320, editor:this.combEditor}
        ];
        var actionDs = new YAHOO.util.DataSource([]);
        actionDs.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        actionDs.responseSchema = {
            fields: ["action"]
        };
        gridwrps = YAHOO.util.Dom.getElementsByClassName("r-designer-actdlg-gridwrp", 'div', this.combinedView);
        btnwrps = YAHOO.util.Dom.getElementsByClassName("r-designer-actdlg-btnwrp", 'div', this.combinedView);
        this.actionsGrid = new YAHOO.widget.DataTable(gridwrps[0], actionColumnDefs, actionDs, {'MSG_EMPTY':'',scrollable:true, height:"70px"});
        new YAHOO.widget.Button(btnwrps[0].firstChild, {onclick:{fn:function() {
            this.actionsGrid.addRow({action:'action'})
        }, scope:this},label: 'Add Action'});

        this.actionsGrid.subscribe("cellMouseoverEvent", highlightEditableCell);
        this.actionsGrid.subscribe("cellMouseoutEvent", this.compGrid.onEventUnhighlightCell);
        this.actionsGrid.subscribe("cellClickEvent", function (oArgs) {
            var target = oArgs.target,
                    column = this.actionsGrid.getColumn(target)

            var records = this.actionsGrid.getRecordSet().getRecords();
            var listedActions = {};
            for (var i = 0; i < records.length; i++) {
                var action = records[i].getData("action");
                listedActions[action] = action;
            }
            var dropDownOptions = [];
            var actions = DesignerUtils.getActionNamesOfCurrentTab(this.designer, this.currentParentNode);
            for (var i = 0; i < actions.length; i++) {
                var actionName = actions[i]
                if (!listedActions[actionName]) {
                    dropDownOptions.push(actionName);
                }
            }
            this.combEditor.dropdownOptions = dropDownOptions;
            this.combEditor.renderForm();
            column.editor = this.combEditor;
            this.actionsGrid.showCellEditor(target);

        }, this, true);

        this.functionView = dh.append(right, {tag:'div', style:'padding:5 0 5 0;',
            html:'<div style="padding:5px;">' +
                 '<div><table><tbody><tr><td class="field-name">Component:</td><td><select style="width:200px;"></select></td></tr>' +
                 '<tr><td class="field-name">Method:</td><td><select style="width:200px;"></select></td></tr></tbody></table></div>' +
                 '<div class="r-designer-actdlg-methoddef"></div>' +
                 '<div class="r-designer-actdlg-fncargs"></div>' +
                 '</div>'})

        selects = this.functionView.getElementsByTagName('select')
        this.componentSelect = selects[0]
        this.methodSelect = selects[1]
        YAHOO.util.Event.addListener(this.componentSelect, 'change', this.componentChanged, this, true);
        YAHOO.util.Event.addListener(this.methodSelect, 'change', this.methodChanged, this, true);
        this.methodDescrEl = YAHOO.util.Dom.getElementsByClassName("r-designer-actdlg-methoddef", 'div', this.functionView)[0];
        this.argsEl = YAHOO.util.Dom.getElementsByClassName("r-designer-actdlg-fncargs", 'div', this.functionView)[0];

        var myContextMenu = new YAHOO.widget.ContextMenu("actcontextmenu",
        {zindex:10000,trigger:[
            this.eventsGrid.getTbodyEl(),
            this.compGrid.getTbodyEl(),
            this.requestParamsGrid.getTbodyEl(),
            this.actionsGrid.getTbodyEl()]});
        myContextMenu.addItem({text:'<em class="r-designer-property-delete">Delete</em>'});
        myContextMenu.render(document.body);
        myContextMenu.clickEvent.subscribe(function(p_sType, p_aArgs) {
            var task = p_aArgs[1];
            if (task) {
                var elRow = this.contextMenu.contextEventTarget;
                var tBodyEl = YAHOO.rapidjs.DomUtils.getElementFromChild(elRow, 'yui-dt-data');
                var grid;
                if (tBodyEl == this.compGrid.getTbodyEl()) {
                    grid = this.compGrid;
                }
                else if (tBodyEl == this.eventsGrid.getTbodyEl()) {
                    grid = this.eventsGrid;
                }
                else if (tBodyEl == this.requestParamsGrid.getTbodyEl()) {
                    grid = this.requestParamsGrid;
                }
                else if (tBodyEl == this.actionsGrid.getTbodyEl()) {
                    grid = this.actionsGrid;
                }
                elRow = grid.getTrEl(elRow);
                if (elRow) {
                    grid.deleteRow(elRow);
                    this.closeCellEditors(grid);
                }
            }
        }, this, true)
        this.contextMenu = myContextMenu;
    },
    adjustHeight: function() {
        this.dialog.adjustHeight(this.wrpEl.getHeight());
    },
    eventComponentChanged: function() {
        SelectUtils.clear(this.eventSelect);
        var component = this.eventCompSelect.options[this.eventCompSelect.selectedIndex].value
        var compType;
        if (component == "Global") {
            compType = "Global";
        }
        else {
            compType = this.currentComponents[component]
        }
        var events = UIConfig.getComponentEvents(compType)
        for (var event in events) {
            SelectUtils.addOption(this.eventSelect, event, event);
        }
        this.eventChanged();
    },
    eventChanged : function() {
        if (this.eventSelect.selectedIndex > -1) {
            var option = this.eventSelect.options[this.eventSelect.selectedIndex];
            var compName = this.eventCompSelect.options[this.eventCompSelect.selectedIndex].value;
            var compType;
            if (compName == "Global") {
                compType = "Global";
            }
            else {
                compType = this.currentComponents[compName]
            }

            var eventName = option.value;
            var eventDesc = UIConfig.getEventDescription(compType, eventName);
            var eventParameters = UIConfig.getEventParameters(compType, eventName);
            var nOfParams = 0;
            var paramsHtml = []
            for (var eventParam in eventParameters) {
                if (nOfParams == 0) {
                    paramsHtml[paramsHtml.length] = ['<ul style="padding-left:20px;">'];
                }
                paramsHtml[paramsHtml.length] = '<li><span style="font-weight:bold;font-size:13px">' + eventParam + ':</span><span> ' + eventParameters[eventParam] + '</span></li>'
                nOfParams++;
            }
            if (nOfParams > 0) {
                paramsHtml[paramsHtml.length] = '</ul>'
            }
            this.eventDescrEl.innerHTML = '<p>' + eventDesc + (nOfParams > 0 ? (' Available parameters:</p><br>' + paramsHtml.join('')) : '');
        }
        else {
            this.eventDescrEl.innerHTML = ''
        }

        this.adjustHeight();
    },
    componentChanged:function() {
        if (this.componentSelect.selectedIndex > -1) {
            YAHOO.util.Dom.setStyle(this.methodDescrEl, 'display', '')
            YAHOO.util.Dom.setStyle(this.argsEl, 'display', '')
            var component = this.componentSelect.options[this.componentSelect.selectedIndex].value
            var compType = this.currentComponents[component];
            SelectUtils.clear(this.methodSelect);
            var methods = UIConfig.getComponentMethods(compType);
            for (var method in methods) {
                SelectUtils.addOption(this.methodSelect, method, method);
            }
            this.methodChanged();
        }
        else {
            YAHOO.util.Dom.setStyle(this.methodDescrEl, 'display', 'none')
            YAHOO.util.Dom.setStyle(this.argsEl, 'display', 'none')
        }
    },

    methodChanged: function() {
        if (this.methodSelect.selectedIndex > -1) {
            var method = this.methodSelect.options[this.methodSelect.selectedIndex].value;
            var component = this.componentSelect.options[this.componentSelect.selectedIndex].value
            var compType = this.currentComponents[component];
            var methodDesc = UIConfig.getMethodDescription(compType, method);
            var methodArgs = UIConfig.getMethodArguments(compType, method);
            var nOfArgs = 0;
            var methodHtml = []
            var argsHtml = []
            for (var methodArg in methodArgs) {
                if (nOfArgs == 0) {
                    methodHtml[methodHtml.length] = ['<ul style="padding-left:20px;">'];
                    argsHtml[argsHtml.length] = ["<table><tbody>"]
                }
                methodHtml[methodHtml.length] = '<li><span style="font-weight:bold;font-size:13px">' + methodArg + ':</span><span> ' + methodArgs[methodArg] + '</span></li>'
                argsHtml[argsHtml.length] = '<tr><td class="field-name">' + methodArg + ':</td><td><textarea cols="35"></textarea></td></tr>'
                nOfArgs++;
            }
            if (nOfArgs > 0) {
                methodHtml[methodHtml.length] = '</ul>'
                argsHtml[argsHtml.length] = ["</tbody></table>"]
            }
            this.methodDescrEl.innerHTML = '<p>' + methodDesc + (nOfArgs > 0 ? (' Method arguments:</p><br>' + methodHtml.join('')) : '');
            this.argsEl.innerHTML = nOfArgs > 0 ? argsHtml.join('') : '';
        }
        else {
            this.methodDescrEl.innerHTML = '';
            this.argsEl.innerHTML = '';
        }
        this.adjustHeight();
    },
    closeCellEditors: function(grid) {
        var columns = grid.getColumnSet().flat;
        for (var i = 0; i < columns.length; i++) {
            if (columns[i].editor) {
                columns[i].editor.cancel();
            }
        }
    },
    typeChanged:function() {
        var actionType = this.typeSelect.options[this.typeSelect.selectedIndex].text;
        if (actionType == "request" || actionType == "merge") {
            YAHOO.util.Dom.setStyle(this.requestView, 'display', '');
            //grids will arrange their widths;
            this.requestParamsGrid.addRow({action:'dummy'});
            var length = this.requestParamsGrid.getRecordSet().getLength();
            this.requestParamsGrid.deleteRows(length - 1, length);
            this.compGrid.addRow({action:'dummy'});
            length = this.compGrid.getRecordSet().getLength();
            this.compGrid.deleteRows(length - 1, length);
            if (actionType == "merge") {
                YAHOO.util.Dom.setStyle(this.mergeView, 'display', '');
            }
            else {
                YAHOO.util.Dom.setStyle(this.mergeView, 'display', 'none');
            }
        }
        else {
            YAHOO.util.Dom.setStyle(this.requestView, 'display', 'none');
        }
        if (actionType == "link") {
            YAHOO.util.Dom.setStyle(this.linkView, 'display', '');
        }
        else {
            YAHOO.util.Dom.setStyle(this.linkView, 'display', 'none');
        }
        if (actionType == "combined") {
            YAHOO.util.Dom.setStyle(this.combinedView, 'display', '');
            //grid will arrange its width;
            this.actionsGrid.addRow({action:'dummy'});
            var length = this.actionsGrid.getRecordSet().getLength();
            this.actionsGrid.deleteRows(length - 1, length);
        }
        else {
            YAHOO.util.Dom.setStyle(this.combinedView, 'display', 'none');
        }
        if (actionType == "function") {
            YAHOO.util.Dom.setStyle(this.functionView, 'display', '');
        }
        else {
            YAHOO.util.Dom.setStyle(this.functionView, 'display', 'none');
        }
        this.adjustHeight();
    },

    show: function(mode, xmlData) {
        if (!this.dialog) {
            this.render();
        }
        this.clear();
        this.mode = mode;
        if (mode == YAHOO.rapidjs.designer.ActionDefinitionDialog.CREATE_MODE) {
            this.currentActionNode = null;
            this.currentParentNode = xmlData;
        }
        else {
            this.currentActionNode = xmlData;
            this.currentParentNode = xmlData.parentNode();
        }
        this.currentComponents = DesignerUtils.getComponentsOfCurrentTab(this.designer, this.currentParentNode);
        this.populateComponents();
        if (this.mode == YAHOO.rapidjs.designer.ActionDefinitionDialog.EDIT_MODE) {
            this.populateFieldsForUpdate();
        }
        else {
            this.componentChanged();
        }
        this.eventComponentChanged();
        this.typeChanged();
        this.dialog.show();
        this.adjustHeight();
    },
    populateFieldsForUpdate: function() {
        var itemType = DesignerUtils.getItemType(this.designer, this.currentActionNode);
        this.nameInput.value = this.currentActionNode.getAttribute('name') || ''
        this.conditionInput.value = this.currentActionNode.getAttribute('condition') || ''
        var eventsNode;
        var childNodes = this.currentActionNode.childNodes();
        for(var i=0; i<childNodes.length;i++){
            if(childNodes[i].getAttribute(this.designer.treeTypeAttribute) == 'Events'){
                eventsNode = childNodes[i];
                break;
            }
        }
        if(eventsNode){
            var eventNodes = eventsNode.childNodes();
            for(var i=0; i<eventNodes.length; i++){
                var component = eventNodes[i].getAttribute('component');
                var event = eventNodes[i].getAttribute('eventName');
                component = !component  || component == ''? 'Global' : component;
                this.eventsGrid.addRow({component:component, event:event});
            }
        }
        if (itemType == "FunctionAction") {
            SelectUtils.selectTheValue(this.typeSelect, "function", 0);
            var component = this.currentActionNode.getAttribute("component");
            SelectUtils.selectTheValue(this.componentSelect, component, 0);
            this.componentChanged();
            var method = this.currentActionNode.getAttribute('function');
            SelectUtils.selectTheValue(this.methodSelect, method, 0);
            this.methodChanged();
            var argInputs = this.argsEl.getElementsByTagName('textarea');
            var childNodes = this.currentActionNode.childNodes();
            var argIndex = 0;
            for (var i = 0; i < childNodes.length; i++) {
                if(DesignerUtils.getItemType(this.designer,childNodes[i]) == 'FunctionArgument'){
                    argInputs[argIndex].value = childNodes[i].getAttribute('value');
                    argIndex ++;
                }
            }
        }
        else if (itemType == "LinkAction") {
            SelectUtils.selectTheValue(this.typeSelect, 'link', 0);
            this.linkUrlInput.value = this.currentActionNode.getAttribute('url') || '';
        }
        else if (itemType == "CombinedAction") {
            SelectUtils.selectTheValue(this.typeSelect, 'combined', 0);
            var actionsStr = this.currentActionNode.getAttribute('actions')
            var actions = actionsStr ? actionsStr.split(',') : [];
            for (var i = 0; i < actions.length; i++) {
                this.actionsGrid.addRow({action:actions[i]})
            }
        }
        else if (itemType == "RequestAction" || itemType == "MergeAction") {
            var compsStr = this.currentActionNode.getAttribute('components');
            var components = compsStr ? compsStr.split(',') : [];
            for (var i = 0; i < components.length; i++) {
                this.compGrid.addRow({component:components[i]})
            }
            this.requestUrlInput.value = this.currentActionNode.getAttribute('url') || '';
            this.timeoutInput.value = this.currentActionNode.getAttribute('timeout') || '';
            var childNodes = this.currentActionNode.childNodes();
            for (var i = 0; i < childNodes.length; i++) {
                if(DesignerUtils.getItemType(this.designer,childNodes[i]) == 'RequestParameter'){
                    this.requestParamsGrid.addRow({key:childNodes[i].getAttribute('key'), value:childNodes[i].getAttribute('value')})    
                }
            }
            if (itemType == "MergeAction") {
                this.removeAttInput.value = this.currentActionNode.getAttribute('removeAttribute') || ''
                SelectUtils.selectTheValue(this.typeSelect, 'merge', 0)
            }
            else {
                SelectUtils.selectTheValue(this.typeSelect, 'request', 0)
            }
        }
    },
    clear: function() {
        var length = this.actionsGrid.getRecordSet().getLength()
        this.actionsGrid.deleteRows(0, length)
        length = this.requestParamsGrid.getRecordSet().getLength()
        this.requestParamsGrid.deleteRows(0, length)
        length = this.compGrid.getRecordSet().getLength()
        this.compGrid.deleteRows(0, length);
        length = this.eventsGrid.getRecordSet().getLength()
        this.eventsGrid.deleteRows(0, length);

        this.nameInput.value = '';
        this.conditionInput.value = '';
        this.requestUrlInput.value = '';
        this.timeoutInput.value = '';
        this.removeAttInput.value = '';
        this.linkUrlInput.value = '';
        SelectUtils.selectTheValue(this.typeSelect, 'request', 0);
    },
    populateComponents: function() {
        SelectUtils.clear(this.componentSelect);
        SelectUtils.clear(this.eventCompSelect);
        SelectUtils.addOption(this.eventCompSelect, 'Global', 'Global');
        for (var compName in this.currentComponents) {
            SelectUtils.addOption(this.componentSelect, compName, compName)
            SelectUtils.addOption(this.eventCompSelect, compName, compName)
        }
    },

    handleSave: function() {
        var actionAtts = {
            "name":this.nameInput.value,
            "condition":this.conditionInput.value
        };
        var actionType = this.typeSelect.options[this.typeSelect.selectedIndex].text;
        if (actionType == 'request' || actionType == 'merge') {
            actionAtts['url'] = this.requestUrlInput.value;
            actionAtts['timeout'] = this.timeoutInput.value;
            actionAtts[this.designer.treeTypeAttribute] = "RequestAction";
            if (actionType == 'merge') {
                actionAtts['removeAttribute'] = this.removeAttInput.value;
                actionAtts[this.designer.treeTypeAttribute] = "MergeAction";
            }
            var records = this.compGrid.getRecordSet().getRecords(0);
            var recArray = [];
            for (var i = 0; i < records.length; i++) {
                recArray[recArray.length] = records[i].getData("component");
            }
            actionAtts['components'] = recArray.join(',');
        }
        else if (actionType == 'link') {
            actionAtts[this.designer.treeTypeAttribute] = "LinkAction";
            actionAtts["url"] = this.linkUrlInput.value;
        }
        else if (actionType == 'combined') {
            actionAtts[this.designer.treeTypeAttribute] = "CombinedAction";
            var records = this.actionsGrid.getRecordSet().getRecords(0);
            var recArray = [];
            for (var i = 0; i < records.length; i++) {
                recArray[recArray.length] = records[i].getData("action");
            }
            actionAtts['actions'] = recArray.join(',');
        }
        else if (actionType == 'function') {
            actionAtts[this.designer.treeTypeAttribute] = "FunctionAction";
            actionAtts["component"] = ''
            actionAtts["function"] = ''
            if (this.componentSelect.selectedIndex > -1) {
                actionAtts["component"] = this.componentSelect.options[this.componentSelect.selectedIndex].value;
            }
            if (this.methodSelect.selectedIndex > -1) {
                actionAtts["function"] = this.methodSelect.options[this.methodSelect.selectedIndex].value;
            }
        }

        var actionNode;
        if (this.mode == YAHOO.rapidjs.designer.ActionDefinitionDialog.EDIT_MODE) {
            actionNode = this.currentActionNode;
        }
        else {
            actionNode = this.designer.createTreeNode(this.currentParentNode, actionAtts[this.designer.treeTypeAttribute], actionAtts);
        }
        actionNode.setAttribute(this.designer.treeDisplayAttribute, actionAtts["name"])
        var childNodes = actionNode.childNodes();
        for (var i = childNodes.length - 1; i >= 0; i--) {
            actionNode.removeChild(childNodes[i]);
        }
        if (actionType == 'request' || actionType == 'merge') {
            var records = this.requestParamsGrid.getRecordSet().getRecords(0);
            var recArray = [];
            for (var i = 0; i < records.length; i++) {
                var paramNode = this.designer.createTreeNode(actionNode, "RequestParameter");
                paramNode.setAttribute('key', records[i].getData('key'))
                paramNode.setAttribute('value', records[i].getData('value'))
            }
        }
        else if (actionType == 'function') {
            var argInputs = this.argsEl.getElementsByTagName('textarea');
            for (var i = 0; i < argInputs.length; i++) {
                var argNode = this.designer.createTreeNode(actionNode, "FunctionArgument");
                argNode.setAttribute('value', argInputs[i].value)
            }
        }

        var eventRecords = this.eventsGrid.getRecordSet().getRecords(0);
        if(eventRecords.length > 0){
            var atts = {};
            atts[this.designer.treeHideAttribute] = 'true';
            var eventsNode = this.designer.createTreeNode(actionNode, "Events", atts);
            for(var i = 0; i < eventRecords.length; i++){
                var eventNode = this.designer.createTreeNode(eventsNode, "Event");
                var component = eventRecords[i].getData('component')
                var eventName = eventRecords[i].getData('event')
                component = component == 'Global'? '': component
                eventNode.setAttribute('component', component)
                eventNode.setAttribute('eventName', eventName)
            }
        }
        var currentTab = this.currentParentNode.getAttribute(this.designer.itemTabAtt);
        this.designer.addExtraAttributesToChildNodes(this.currentParentNode, currentTab);
        this.designer.refreshTree();
        this.hide();
    },

    hide:function() {
        this.dialog.hide();
    }

}
YAHOO.rapidjs.designer.ActionDefinitionDialog.CREATE_MODE = 1;
YAHOO.rapidjs.designer.ActionDefinitionDialog.EDIT_MODE = 2;
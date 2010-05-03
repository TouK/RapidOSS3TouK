/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.search');
YAHOO.rapidjs.component.search.ViewBuilder = function(searchGrid, viewType) {
    YAHOO.rapidjs.component.search.ViewBuilder.superclass.constructor.call(this, null, {id:searchGrid.id + '_viewBuilder', format:'xml'});
    this.searchGrid = searchGrid;
    this.viewData = null;
    this.viewType = viewType;
    var config = {
        width:640,
        height:522,
        minWidth:100,
        minHeight:100,
        resizable: false,
        title: 'View Builder',
        mask:true,
        buttons:[
            {text:"Save", handler:this.handleSave, scope:this, isDefault:true },
            {text:"Cancel", handler:this.hide, scope:this }]
    }
    this.availableFields = null;
    this.columnsConfig = {};
    this.dialog = new YAHOO.rapidjs.component.Dialog(config);
    this.events['error'].subscribe(function() {
        this.dialog.hideMask()
    }, this, true)
    this.render();
    this.getViews(this.loadViews);
}
YAHOO.lang.extend(YAHOO.rapidjs.component.search.ViewBuilder, YAHOO.rapidjs.component.PollingComponentContainer, {
    render: function() {
        var dh = YAHOO.ext.DomHelper;
        var wrp = dh.append(this.dialog.body, {tag:'div', cls:'rcmdb-searchgrid-view-wrp'});
        var nameView = dh.append(wrp, {tag:'div',
            html:'<table><tbody><tr><td><div class="rcmdb-searchgrid-view-text">View Name:</div></td>' +
                 '<td><div class="rcmdb-searchgrid-view-inputwrp"><input class="rcmdb-searchgrid-view-input"></input></div></td>' +
                 '<td><div style="margin-left:25px;">Public:</div></td>' +
                 '<td><div class="rcmdb-searchgrid-view-inputwrp"><input type="checkbox" style="height:20px"></input></div></td></tr></tbody></table>'});
        var inputs = nameView.getElementsByTagName("input");
        this.nameInput = inputs[0];
        this.isPublicInput = inputs[1];
        if (!this.publicViewCreationAllowed()) {
            var tds = nameView.getElementsByTagName('td')
            YAHOO.util.Dom.setStyle(tds[2], 'display', 'none')
            YAHOO.util.Dom.setStyle(tds[3], 'display', 'none')
        }
        var columnView = dh.append(wrp, {tag:'div',
            html:'<table><tbody>' +
                 '<tr><td><div class="rcmdb-searchgrid-view-text">Available Fields:</div></td><td></td><td><div class="rcmdb-searchgrid-view-text">Grid Columns:</div></td><td></td></tr>' +
                 '<tr>' +
                 '<td><div class="rcmdb-searchgrid-view-fields"><select class="rcmdb-searchgrid-view-fieldlist" size="26"></select></div></td>' +
                 '<td valign="top"><div class="rcmdb-searchgrid-view-buttons"><div class="rcmdb-searchgrid-view-btnwrp"></div><div class="rcmdb-searchgrid-view-btnwrp"></div>' +
                 '<div class="rcmdb-searchgrid-view-btnwrp"></div><div class="rcmdb-searchgrid-view-btnwrp"></div></div>' +
                 '</td>' +
                 '<td><div class="rcmdb-searchgrid-view-gridcols"><select class="rcmdb-searchgrid-view-collist" size="10"></select></div>' +
                 '<div class="rcmdb-searchgrid-view-text">Field Name:</div>' +
                 '<div class="rcmdb-searchgrid-view-inputwrp"><input class="rcmdb-searchgrid-view-input"></input></div>' +
                 '<div class="rcmdb-searchgrid-view-text">Column Title:</div>' +
                 '<div class="rcmdb-searchgrid-view-inputwrp"><input class="rcmdb-searchgrid-view-input"></input></div>' +
                 '<div class="rcmdb-searchgrid-view-text">Column Width:</div>' +
                 '<div class="rcmdb-searchgrid-view-inputwrp"><input class="rcmdb-searchgrid-view-input"></input></div>' +
                 '<div class="rcmdb-searchgrid-view-text">Default Sort Column:</div>' +
                 '<div class="rcmdb-searchgrid-view-inputwrp"><select class="rcmdb-searchgrid-view-input"><option value=""></option></select></div>' +
                 '<div class="rcmdb-searchgrid-view-text">Sort Order:</div>' +
                 '<div class="rcmdb-searchgrid-view-inputwrp"><select class="rcmdb-searchgrid-view-input"><option value="asc">asc</option><option value="desc">desc</option></select></div>' +
                 '</td>' +
                 '<td valign="top"><div class="rcmdb-searchgrid-view-buttons"><div class="rcmdb-searchgrid-view-btnwrp"></div><div class="rcmdb-searchgrid-view-btnwrp"></div>' +
                 '<div class="rcmdb-searchgrid-view-btnwrp"></div><div class="rcmdb-searchgrid-view-btnwrp"></div></div>' +
                 '</td>' +
                 '</tr>' +
                 '</tbody></table>'});
        this.allFields = YAHOO.util.Dom.getElementsByClassName('rcmdb-searchgrid-view-fieldlist', 'select', columnView)[0];
        this.allFields.multiple = true;
        YAHOO.util.Event.addListener(this.allFields, 'change', this.handleButtons, this, true);
        YAHOO.util.Event.addListener(this.allFields, 'dblclick', this.handleFieldDblClick, this, true);
        this.colList = YAHOO.util.Dom.getElementsByClassName('rcmdb-searchgrid-view-collist', 'select', columnView)[0];
        this.colList.multiple = true;
        YAHOO.util.Event.addListener(this.colList, 'change', this.handleColumnSelect, this, true);
        YAHOO.util.Event.addListener(this.colList, 'dblclick', this.handleColumnDblClick, this, true);
        var inputs = YAHOO.util.Dom.getElementsByClassName('rcmdb-searchgrid-view-input', 'input', columnView);
        this.attInput = inputs[0];
        this.attInput.readOnly = true;
        this.headerInput = inputs[1];
        YAHOO.util.Event.addListener(this.headerInput, 'keyup', this.headerChanged, this, true);
        this.colWidthInput = inputs[2];
        YAHOO.util.Event.addListener(this.colWidthInput, 'keyup', this.widthChanged, this, true);
        var selects = YAHOO.util.Dom.getElementsByClassName('rcmdb-searchgrid-view-input', 'select', columnView);
        this.defaultSortInput = selects[0];
        this.sortOrderInput = selects[1];
        var buttonWrappers = YAHOO.util.Dom.getElementsByClassName('rcmdb-searchgrid-view-btnwrp', 'div', columnView);
        this.addButton = new YAHOO.widget.Button(buttonWrappers[0], {onclick:{fn:this.handleAdd, scope:this},label: 'Add >>', disabled:true});
        this.addAllButton = new YAHOO.widget.Button(buttonWrappers[1], {onclick:{fn:this.handleAddAll, scope:this},label: 'Add All >>', disabled:true});
        this.removeButton = new YAHOO.widget.Button(buttonWrappers[2], {onclick:{fn:this.handleRemove, scope:this},label: '<< Remove', disabled:true});
        this.removeAllButton = new YAHOO.widget.Button(buttonWrappers[3], {onclick:{fn:this.handleRemoveAll, scope:this},label: '<< Remove All', disabled:true});
        this.topButton = new YAHOO.widget.Button(buttonWrappers[4], {onclick:{fn:this.handleTop, scope:this},label: 'Top', disabled:true});
        this.upButton = new YAHOO.widget.Button(buttonWrappers[5], {onclick:{fn:this.handleUp, scope:this},label: 'Up', disabled:true});
        this.downButton = new YAHOO.widget.Button(buttonWrappers[6], {onclick:{fn:this.handleDown, scope:this},label: 'Down', disabled:true});
        this.bottomButton = new YAHOO.widget.Button(buttonWrappers[7], {onclick:{fn:this.handleBottom, scope:this},label: 'Bottom', disabled:true});
    },
    getViews: function(callback) {
        this.doRequest(getUrlPrefix() + 'gridView/list', {type:this.viewType}, callback);
    },
    hide: function() {
        this.dialog.hide();
    },
    handleSave: function() {
        var parameters = {};
        var url;
        if(this.currentNode){
            url = getUrlPrefix() + 'gridView/update';
            parameters["id"] = this.currentNode.getAttribute("id");
        }
        else{
            url = getUrlPrefix() + 'gridView/add';    
        }
        parameters['name'] = this.nameInput.value;
        parameters['isPublic'] = this.isPublicInput.checked;
        parameters['type'] = this.viewType;
        var defaultSortColumn = this.defaultSortInput.options[this.defaultSortInput.selectedIndex].value;
        var sortOrder = this.sortOrderInput.options[this.sortOrderInput.selectedIndex].value;
        parameters['defaultSortColumn'] = defaultSortColumn;
        parameters['sortOrder'] = sortOrder;
        var colList = this.colList;
        var nOfColumns = colList.options.length;
        var columnsArray = [];
        for (var index = 0; index < nOfColumns; index++) {
            var colArray = [];
            var fieldName = colList.options[index].value;
            var colConfig = this.columnsConfig[fieldName];
            colArray[colArray.length] = fieldName;
            colArray[colArray.length] = colConfig['header'];
            colArray[colArray.length] = colConfig['width'];
            columnsArray[columnsArray.length] = colArray.join(";;");
        }
        parameters['columns'] = columnsArray.join("::");
        this.dialog.showMask();
        this.doPostRequest(url, parameters, this.saveSuccess);
    },
    saveSuccess: function(response, containsErrors) {
        this.dialog.hideMask();
        if (!containsErrors) {
            this.dialog.hide();
            if (!this.currentNode) {
                var currentView = this.nameInput.value;
                this.getViews(this.viewAdded.createDelegate(this, [currentView], true));
            }
            else {
                var viewId = this.currentNode.getAttribute("id");
                this.getViews(this.viewUpdated.createDelegate(this, [viewId], true));
            }
        }
    },
    removeView : function(viewId) {
        var url = getUrlPrefix() + 'gridView/delete';
        this.doPostRequest(url, {id:encodeURIComponent(viewId)}, this.removeSuccess.createDelegate(this, [viewId], true))
    },
    removeSuccess : function(response, containsErrors, viewId) {
        if (!containsErrors) {
            this.getViews(this.viewRemoved.createDelegate(this, [viewId], true));
        }
    },
    loadViews: function(response, containsErrors) {
        if (!containsErrors) {
            this._getViewData(response);
            this.searchGrid.loadViews(response);
        }
    },
    viewAdded: function(response, containsErrors, view) {
        if (!containsErrors) {
            this._getViewData(response);
            var viewNodes = this.viewData.findChildNode('name', view, 'View');
            if (viewNodes.length > 0) {
                this.searchGrid.viewAdded(viewNodes[viewNodes.length -1]);
            }
        }
    },
    viewUpdated: function(response, containsErrors, viewId) {
        if (!containsErrors) {
            this._getViewData(response);
            var viewNode = this.viewData.findChildNode('id', viewId, 'View')[0];
            if (viewNode) {
                this.searchGrid.viewUpdated(viewNode);
            }
        }
    },
    viewRemoved : function(response, containsErrors, viewId) {
        if (!containsErrors) {
            this._getViewData(response);
            this.searchGrid.viewRemoved(viewId);
        }
    },
    _getViewData: function(response, containsErrors) {
        if (!containsErrors) {
            var data = new YAHOO.rapidjs.data.RapidXmlDocument(response, ['name', 'id']);
            var rootNode = data.getRootNode('Views');
            if (rootNode) {
                this.viewData = rootNode;
            }
        }
    },
    clear: function() {
        SelectUtils.clear(this.allFields);
        SelectUtils.clear(this.colList);
        SelectUtils.clear(this.defaultSortInput);
        SelectUtils.addOption(this.defaultSortInput, '', '');
        SelectUtils.selectTheValue(this.sortOrderInput, 'asc', 0);
        this.nameInput.value = '';
        this.isPublicInput.checked = false;
        this.nameInput.readOnly = false;
        this.attInput.readOnly = false;
        this.headerInput.readOnly = false;
        this.colWidthInput.readOnly = false;
        this.attInput.value = '';
        this.headerInput.value = '';
        this.colWidthInput.value = '100';
        this.disableAll();
        this.columnsConfig = {};
    },
    show: function(currentViewId) {
        this.clear();
        if (currentViewId) {
            this.currentNode = this.viewData.findChildNode('id', currentViewId, 'View')[0];
        }
        else {
            this.currentNode = null;
        }
        this.dialog.show();
        if (!this.availableFields) {
            this.doRequest(this.searchGrid.fieldsUrl, {}, this.getFieldsSuccess)
        }
        else {
            if (this.currentNode) {
                this.populateFieldsForUpdate(this.currentNode);
            }
            else {
                this.populateFieldsForAdd();
            }
            this.loadAvailableFields();
        }
    },
    getFieldsSuccess: function(response, containsErrors) {
        if (!containsErrors) {
            this.availableFields = response.responseXML.getElementsByTagName('Field');
            if (this.currentNode) {
                this.populateFieldsForUpdate(this.currentNode);
            }
            else {
                this.populateFieldsForAdd();
            }
            this.loadAvailableFields();
        }
    },
    populateFieldsForAdd: function() {
        var viewInput = this.searchGrid.viewInput;
        var currentViewId = viewInput.options[viewInput.selectedIndex].value;
        if (currentViewId == 'default') {
            this.populteFieldsFromDefaultView();
        }
        else {
            var node = this.viewData.findChildNode('id', currentViewId, 'View')[0];
            this.populateFieldsForUpdate(node);
            this.nameInput.readOnly = false;
            this.nameInput.value = '';
        }
    },
    populteFieldsFromDefaultView: function() {
        var columns = YAHOO.rapidjs.ObjectUtils.clone(this.searchGrid.defaultColumns, true);
        var defaultSortColumn = '';
        var sortOrder = 'asc';
        for (var index = 0; index < columns.length; index++) {
            var column = columns[index]
            var attName = column['attributeName'];
            var header = column['colLabel'];
            var width = column['width'];
            if (column['sortBy'] == true) {
                defaultSortColumn = attName;
                if (column['sortOrder']) {
                    sortOrder = column['sortOrder'];
                }
            }
            SelectUtils.addOption(this.colList, attName, attName);
            SelectUtils.addOption(this.defaultSortInput, attName, attName);
            this.columnsConfig[attName] = {header:header, width:width};
        }
        SelectUtils.selectTheValue(this.defaultSortInput, defaultSortColumn, 0);
        SelectUtils.selectTheValue(this.sortOrderInput, sortOrder, 0);

    },
    populateFieldsForUpdate: function(node) {
        var viewName = node.getAttribute('name');
        var isPublic = node.getAttribute('isPublic');
        this.nameInput.value = viewName;
        if (isPublic == "true" && this.publicViewCreationAllowed()) {
            this.isPublicInput.checked = true;
        }
        this.nameInput.readOnly = true;
        var defaultSortColumn = node.getAttribute('defaultSortColumn');
        var sortOrder = node.getAttribute('sortOrder');
        var colNodes = node.getElementsByTagName('Column');
        var nOfColumns = colNodes.length;
        var orderedArray = new Array(nOfColumns);
        for (var index = 0; index < nOfColumns; index++) {
            var colNode = colNodes[index];
            var colOrder = parseInt(colNode.getAttribute('columnIndex'), 10);
            var att = colNode.getAttribute('attributeName');
            var header = colNode.getAttribute('header');
            var width = colNode.getAttribute('width');
            this.columnsConfig[att] = {header:header, width:width};
            orderedArray.push({attribute:att, order:colOrder});
        }
        orderedArray.sort(function(col1, col2) {
            return col1.order - col2.order;
        })
        for (var index = 0; index < nOfColumns; index++) {
            var attName = orderedArray[index]['attribute'];
            SelectUtils.addOption(this.colList, attName, attName);
            SelectUtils.addOption(this.defaultSortInput, attName, attName);
        }
        SelectUtils.selectTheValue(this.defaultSortInput, defaultSortColumn, 0);
        SelectUtils.selectTheValue(this.sortOrderInput, sortOrder, 0);
    },
    loadAvailableFields : function() {
        var nOfAvailableFields = this.availableFields.length;
        for (var index = 0; index < nOfAvailableFields; index++) {
            var field = this.availableFields[index];
            var fieldName = field.getAttribute('Name');
            if (!this.columnsConfig[fieldName]) {
                SelectUtils.addOption(this.allFields, fieldName, fieldName);
            }
        }
        this.handleButtons();
    },
    handleColumnSelect: function() {
        if (this.checkMultipleSelection(this.colList)) {
            this.disableColumnInputs();
            this.handleButtons(true);
        }
        else {
            this.fillColumnInputs();
            this.handleButtons();
        }
    },
    checkMultipleSelection : function(select) {
        var selectedCount = 0;
        var options = select.options;
        for (var index = 0; index < options.length; index++) {
            var option = options[index];
            if (option.selected == true) {
                selectedCount ++;
                if (selectedCount > 1) {
                    return true;
                }
            }
        }
        return false;
    },

    fillColumnInputs : function() {
        var selectedIndex = this.colList.selectedIndex;
        if (selectedIndex == -1) {
            this.attInput.value = '';
            this.headerInput.value = '';
            this.colWidthInput.value = '100';
        }
        else {
            var fieldName = this.colList.options[selectedIndex].value;
            var colConfig = this.columnsConfig[fieldName];
            this.attInput.value = fieldName;
            this.headerInput.value = colConfig['header'];
            this.colWidthInput.value = colConfig['width'];
        }
        this.attInput.readOnly = false;
        this.headerInput.readOnly = false;
        this.colWidthInput.readOnly = false;
    },

    disableColumnInputs : function() {
        this.attInput.value = '';
        this.attInput.readOnly = true;
        this.headerInput.value = '';
        this.headerInput.readOnly = true;
        this.colWidthInput.value = '100';
        this.colWidthInput.readOnly = true;
    },
    headerChanged: function() {
        if (this.colList.selectedIndex != -1) {
            var header = this.headerInput.value;
            var fieldName = this.colList.options[this.colList.selectedIndex].value;
            this.columnsConfig[fieldName]['header'] = header;
        }
    },
    widthChanged: function() {
        if (this.colList.selectedIndex != -1) {
            var width = this.colWidthInput.value;
            var fieldName = this.colList.options[this.colList.selectedIndex].value;
            this.columnsConfig[fieldName]['width'] = width;
        }
    },
    handleAdd: function() {
        var selectedIndexes = SelectUtils.collectSelectedIndicesFromSelect(this.allFields);
        for (var index = selectedIndexes.length - 1; index >= 0; index--) {
            var selectedIndex = selectedIndexes[index];
            var fieldName = this.allFields.options[selectedIndex].value;
            SelectUtils.moveFromSelectToSelect(selectedIndex, this.allFields, this.colList);
            SelectUtils.addOption(this.defaultSortInput, fieldName, fieldName);
            this.columnsConfig[fieldName] = {header:fieldName, width:'100'};
        }
        this.handleColumnSelect();
    },
    handleFieldDblClick: function(event) {
        var selectedIndex = this.allFields.selectedIndex;
        var fieldName = this.allFields.options[selectedIndex].value;
        SelectUtils.moveFromSelectToSelect(selectedIndex, this.allFields, this.colList);
        SelectUtils.addOption(this.defaultSortInput, fieldName, fieldName);
        this.columnsConfig[fieldName] = {header:fieldName, width:'100'};
        this.handleColumnSelect();
    },
    handleAddAll: function() {
        var allFields = this.allFields;
        var nOfFields = allFields.options.length;
        for (var index = nOfFields - 1; index >= 0; index--) {
            var fieldName = allFields[index].value;
            SelectUtils.moveFromSelectToSelect(index, this.allFields, this.colList);
            SelectUtils.addOption(this.defaultSortInput, fieldName, fieldName);
            this.columnsConfig[fieldName] = {header:fieldName, width:'100'};
        }
        this.handleColumnSelect();
    },
    handleRemove: function() {
        var selectedIndexes = SelectUtils.collectSelectedIndicesFromSelect(this.colList);
        for (var index = selectedIndexes.length - 1; index >= 0; index--) {
            var selectedIndex = selectedIndexes[index];
            var fieldName = this.colList.options[selectedIndex].value;
            SelectUtils.moveFromSelectToSelect(selectedIndex, this.colList, this.allFields);
            SelectUtils.remove(this.defaultSortInput, fieldName);
            delete this.columnsConfig[fieldName];
        }
        this.handleColumnSelect();
    },
    handleColumnDblClick: function(event) {
        var selectedIndex = this.colList.selectedIndex;
        var fieldName = this.colList.options[selectedIndex].value;
        SelectUtils.moveFromSelectToSelect(selectedIndex, this.colList, this.allFields);
        SelectUtils.remove(this.defaultSortInput, fieldName);
        delete this.columnsConfig[fieldName];
        this.handleColumnSelect();
    },
    handleRemoveAll: function() {
        var colList = this.colList;
        var nOfFields = colList.options.length;
        for (var index = nOfFields - 1; index >= 0; index--) {
            var fieldName = colList[index].value;
            SelectUtils.moveFromSelectToSelect(index, this.colList, this.allFields);
            SelectUtils.remove(this.defaultSortInput, fieldName);
            delete this.columnsConfig[fieldName];
        }
        this.handleColumnSelect();
    },
    handleTop: function() {
        var colList = this.colList
        var selectedIndex = colList.selectedIndex;
        var fieldName = colList.options[selectedIndex].value;
        colList.remove(selectedIndex);
        SelectUtils.addOptionBefore(colList, fieldName, fieldName, 0);
        SelectUtils.selectTheValue(colList, fieldName, 0);
        this.handleColumnSelect();
    },
    handleUp: function() {
        var colList = this.colList
        var selectedIndex = colList.selectedIndex;
        var fieldName = colList.options[selectedIndex].value;
        colList.remove(selectedIndex);
        SelectUtils.addOptionBefore(colList, fieldName, fieldName, selectedIndex - 1);
        SelectUtils.selectTheValue(colList, fieldName, selectedIndex - 1);
        this.handleColumnSelect();
    },
    handleDown: function() {
        var colList = this.colList
        var selectedIndex = colList.selectedIndex;
        var fieldName = colList.options[selectedIndex].value;
        if (selectedIndex == colList.options.length - 2) {
            colList.remove(selectedIndex);
            SelectUtils.addOption(colList, fieldName, fieldName);
            SelectUtils.selectTheValue(colList, fieldName, colList.options.length - 1);
        }
        else {
            colList.remove(selectedIndex);
            SelectUtils.addOptionBefore(colList, fieldName, fieldName, selectedIndex + 1);
            SelectUtils.selectTheValue(colList, fieldName, selectedIndex + 1);
        }
        this.handleColumnSelect();
    },
    handleBottom: function() {
        var colList = this.colList
        var selectedIndex = colList.selectedIndex;
        var fieldName = colList.options[selectedIndex].value;
        colList.remove(selectedIndex);
        SelectUtils.addOption(colList, fieldName, fieldName);
        SelectUtils.selectTheValue(colList, fieldName, colList.options.length - 1);
        this.handleColumnSelect();
    },
    disableAll : function() {
        this.addButton.set('disabled', true);
        this.addAllButton.set('disabled', true);
        this.removeButton.set('disabled', true);
        this.removeAllButton.set('disabled', true);
        this.topButton.set('disabled', true);
        this.upButton.set('disabled', true);
        this.downButton.set('disabled', true);
        this.bottomButton.set('disabled', true);
    },
    handleButtons: function(colListMultipleSelected) {
        if (this.availableFields.length > 0) {
            this.addAllButton.set('disabled', false);
            if (this.allFields.selectedIndex != -1) {
                this.addButton.set('disabled', false);
            }
            else {
                this.addButton.set('disabled', true);
            }
        }
        else {
            this.addAllButton.set('disabled', true);
            this.addButton.set('disabled', true);
        }
        if (this.colList.options.length > 0) {
            this.removeAllButton.set('disabled', false);
            if (this.colList.selectedIndex != -1) {
                this.removeButton.set('disabled', false);
                if (this.colList.options.length > 1) {
                    if (colListMultipleSelected) {
                        this.topButton.set('disabled', true);
                        this.upButton.set('disabled', true);
                        this.downButton.set('disabled', true);
                        this.bottomButton.set('disabled', true);
                    }
                    else {
                        if (this.colList.selectedIndex == 0) {
                            this.topButton.set('disabled', true);
                            this.upButton.set('disabled', true);
                            this.downButton.set('disabled', false);
                            this.bottomButton.set('disabled', false);
                        }
                        else if (this.colList.selectedIndex == this.colList.options.length - 1) {
                            this.topButton.set('disabled', false);
                            this.upButton.set('disabled', false);
                            this.downButton.set('disabled', true);
                            this.bottomButton.set('disabled', true);
                        }
                        else {
                            this.topButton.set('disabled', false);
                            this.upButton.set('disabled', false);
                            this.downButton.set('disabled', false);
                            this.bottomButton.set('disabled', false);
                        }
                    }

                }
                else {
                    this.topButton.set('disabled', true);
                    this.upButton.set('disabled', true);
                    this.downButton.set('disabled', true);
                    this.bottomButton.set('disabled', true);
                }
            }
            else {
                this.removeButton.set('disabled', true);
                this.topButton.set('disabled', true);
                this.upButton.set('disabled', true);
                this.downButton.set('disabled', true);
                this.bottomButton.set('disabled', true);
            }
        }
        else {
            this.removeAllButton.set('disabled', true);
            this.removeButton.set('disabled', true);
            this.topButton.set('disabled', true);
            this.upButton.set('disabled', true);
            this.downButton.set('disabled', true);
            this.bottomButton.set('disabled', true);
        }
    },
    publicViewCreationAllowed: function() {
        return true;
    }
});
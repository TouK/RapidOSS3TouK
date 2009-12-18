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
YAHOO.rapidjs.component.search.SearchList = function(container, config) {
    this.fields = null;
    this.defaultFields = null;
    this.lineSize = 4;
    this.showMax = 0;
    this.maxRowCellLength = 0;
    this.rowHeaderAttribute = null;
    YAHOO.rapidjs.component.search.SearchList.superclass.constructor.call(this, container, config);
};


YAHOO.lang.extend(YAHOO.rapidjs.component.search.SearchList, YAHOO.rapidjs.component.search.AbstractSearchList, {
    init: function() {
        this.cellMenu = null;
        this.propertiesToRequest = [];
        var events = {
            'cellMenuClicked' : new YAHOO.util.CustomEvent('cellMenuClicked'),
            'rowHeaderClicked' : new YAHOO.util.CustomEvent('rowHeaderClicked')
        };
        YAHOO.ext.util.Config.apply(this.events, events);
    },

    showCurrentState: function() {
        this.searchCountEl.innerHTML = "Count: " + this.totalRowCount;
        this.sortTextEl.innerHTML = "Sorted By: " + this.getSortAttribute().split(',').join(', ') + " " + this.getSortOrder().split(',').join(', ');
    },

    getScrolledEl: function() {
        return this.body;
    },

    getClassName: function() {
        return 'rcmdb-searchlist';
    },

    addTools: function() {
        this.toolbar.addTool(new YAHOO.rapidjs.component.tool.LoadingTool(document.body, this));
        this.toolbar.addTool(new YAHOO.rapidjs.component.tool.SearchListSettingsTool(document.body, this));
        this.toolbar.addTool(new YAHOO.rapidjs.component.search.ExportTool(document.body, this));
        this.toolbar.addTool(new YAHOO.rapidjs.component.tool.ErrorTool(document.body, this));
    },

    _render : function() {
        var dh = YAHOO.ext.DomHelper;
        this.scrollPos = dh.append(this.body.dom, {tag: 'div'}, true);
        this.bufferPos = dh.append(this.scrollPos.dom, {tag:'div'}, true);
        this.bufferView = dh.append(this.scrollPos.dom, {tag:'div'}, true);
        this.bufferView.rowEls = [];
        YAHOO.util.Event.addListener(this.body.dom, 'scroll', this.handleScroll, this, true);
        YAHOO.util.Event.addListener(this.scrollPos.dom, 'click', this.handleClick, this, true);
        YAHOO.util.Event.addListener(this.scrollPos.dom, 'contextmenu', this.handleContextMenu, this, true);
        YAHOO.util.Event.addListener(this.scrollPos.dom, 'dblclick', this.handleDoubleClick, this, true);
        var searchInHtml = this.searchInEnabled ? '<td  width="0%"><div>in:</div></td><td  width="0%"><div><select style="width:80px;"></select></div></td>' : ''
        this.searchBox = dh.append(this.header.dom, {tag: 'div', cls:'rcmdb-searchlist-box',
            html:'<div><form action="javascript:void(0)" style="overflow:auto;"><table><tbody>' +
                 '<tr>' +
                 '<td  width="93%"><input type="textbox" style="width:100%;" name="search"/></td>' +
                 '<td><div class="rcmdb-searchlist-searchbutton"></div></td>' +
                 '<td  width="100%"><div class="rcmdb-searchlist-savequery"></div></td>' +
                 searchInHtml
                    +
                 '<td  width="0%"><div class="rcmdb-searchlist-count"></div></td>' +
                 '<td  width="0%"><div class="rcmdb-searchlist-sortOrder"></div></td>' +
                 '</tr>' +
                 '</tbody></table></form></div>'}, true);

        this.searchInput = this.searchBox.dom.getElementsByTagName('input')[0];
        this.searchCountEl = YAHOO.util.Dom.getElementsByClassName('rcmdb-searchlist-count', 'div', this.searchBox.dom)[0];
        this.sortTextEl = YAHOO.util.Dom.getElementsByClassName('rcmdb-searchlist-sortOrder', 'div', this.searchBox.dom)[0];
        var searchButton = YAHOO.ext.Element.get(YAHOO.util.Dom.getElementsByClassName('rcmdb-searchlist-searchbutton', 'div', this.searchBox.dom)[0]);
        searchButton.addClassOnOver('rcmdb-searchlist-searchbutton-hover');
        YAHOO.util.Event.addListener(searchButton.dom, 'click', this.handleSearch, this, true);
        var saveQueryButton = YAHOO.ext.Element.get(YAHOO.util.Dom.getElementsByClassName('rcmdb-searchlist-savequery', 'div', this.searchBox.dom)[0]);
        saveQueryButton.addClassOnOver('rcmdb-searchlist-savequery-hover');
        YAHOO.util.Event.addListener(saveQueryButton.dom, 'click', this.handleSaveQueryClick, this, true);
        YAHOO.util.Event.addListener(this.searchInput.form, 'keypress', this.handleInputEnter, this, true);

        if (this.showMax > 0) {
            this.maxRowCellLength = this.showMax;
        }
        else {
            if (this.fields)
            {
                for (var i = 0; i < this.fields.length; i++)
                    if (this.maxRowCellLength < this.fields[i]['fields'].length)
                        this.maxRowCellLength = this.fields[i]['fields'].length;
                if (this.defaultFields && this.maxRowCellLength < this.defaultFields.length)
                    this.maxRowCellLength = this.defaultFields.length;
            }
            else
            {
                this.fields = [{exp:"true", fields:this.defaultFields}];
                this.maxRowCellLength = this.defaultFields ? this.defaultFields.length : 0;
            }
        }
        this.preparePropertiesToRequest();
        this.cellMenu = new YAHOO.widget.Menu(this.id + '_cellMenu', {position: "dynamic", autofillheight:false, minscrollheight:300});

        for (var i in this.propertyMenuItems) {
            var item = this.cellMenu.addItem({text:this.propertyMenuItems[i].label });
            YAHOO.util.Event.addListener(item.element, "click", this.cellMenuItemClicked, i, this);

        }
        this.cellMenu.render(document.body);
        YAHOO.rapidjs.component.OVERLAY_MANAGER.register(this.cellMenu);
        if (this.searchInEnabled) {
            this.classesInput = this.searchBox.dom.getElementsByTagName('select')[0];
            this.retrieveSearchClasses();
        }
    },

    handleSearch: function(e)
    {
        YAHOO.rapidjs.component.search.SearchList.superclass.handleSearch.call(this, e);
        var newHistoryState = [];
        newHistoryState[newHistoryState.length] = this.searchInput.value;
        newHistoryState[newHistoryState.length] = this.getSortAttribute();
        newHistoryState[newHistoryState.length] = this.getSortOrder();
        newHistoryState[newHistoryState.length] = this.params['searchIn'];
        this.saveHistoryChange(newHistoryState.join("!::!"));
    },

    createMask: function() {
        var dh = YAHOO.ext.DomHelper;
        this.mask = dh.append(this.wrapper, {tag:'div', cls:'rcmdb-search-mask'}, true);
        this.maskMessage = dh.append(this.wrapper, {tag:'div', cls:'rcmdb-search-mask-loadingwrp', html:'<div class="rcmdb-search-mask-loading">Loading...</div>'}, true)
        this.hideMask();
    },
    showMask: function() {
        this._showMask(this.body);
    },
    createEmptyRows : function(rowCount) {
        var innerHtml = '';
        for (var fieldIndex = 0; fieldIndex < this.maxRowCellLength; fieldIndex++) {
            innerHtml += '<div class="rcmdb-search-cell">' +
                         '<span class="rcmdb-search-cell-key"></span>' +
                         '<a href="" class="rcmdb-search-cell-value"></a>' +
                         '<a class="rcmdb-search-cell-menu">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> | ' +
                         '</div>';
        }
        if (innerHtml.length > 0) {
            innerHtml = innerHtml.substring(0, innerHtml.length - 9) + '</div>';
        }
        for (var rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            var rowHtml = '<table style="height:100%"><tr>' +
                          '<td width="0%"><div class="rcmdb-search-row-headermenu"><span class="rcmdb-search-row-menupos">&nbsp;</span></div></td>' +
                          '<td width="100%">';
            if (this.rowHeaderAttribute != null)
            {
                rowHtml += '<div class="rcmdb-search-rowheader"><a class="rcmdb-search-rowheader-value"></a></div>';
            }
            rowHtml += '<div class="rcmdb-search-rowdata">' + innerHtml + '</div>' +
                       '</td></tr></table>';
            var rowEl = YAHOO.ext.DomHelper.append(this.bufferView.dom, {tag:'div', cls:'rcmdb-search-row',
                html:rowHtml}, true);
            this.bufferView.rowEls[this.bufferView.rowEls.length] = rowEl;
            YAHOO.util.Dom.setStyle(rowEl.dom, 'display', 'none');
            rowEl.setHeight(this.rowHeight);
            var cells = YAHOO.util.Dom.getElementsByClassName('rcmdb-search-cell', 'div', rowEl.dom)
            var header = YAHOO.util.Dom.getElementsByClassName('rcmdb-search-rowheader-value', 'a', rowEl.dom)
            rowEl.cells = cells;
            rowEl.header = header[0];
        }

    },

    renderRow: function(rowEl) {
        if (this.fields) {
            var insertedFields = null;
            var searchNode = this.searchData[rowEl.dom.rowIndex - this.lastOffset];
            var dataNode = searchNode.xmlData;
            var data = dataNode.getAttributes()
            var params = {data:data};
            if (this.images) {
                var evaluationResult = false;
                var imageSrc;
                for (var i = 0; i < this.images.length; i++)
                {
                    imageSrc = this.images[i]['src'];
                    var currentExpressionStr = this.images[i]['visible'];
                    try {
                        evaluationResult = eval(currentExpressionStr);
                        if (evaluationResult) {
                            break;
                        }
                    }
                    catch(e) {
                    }
                }
                var menuEl = YAHOO.util.Dom.getElementsByClassName('rcmdb-search-row-headermenu', 'div', rowEl.dom)[0]
                menuEl.style.backgroundImage = evaluationResult ? 'url("' + imageSrc + '")' : '';
            }
            if (this.rowHeaderAttribute != null)
            {
                rowEl.header.innerHTML = dataNode.getAttribute(this.rowHeaderAttribute);
            }
            if (!this.showMax) {
                if (this.fields != null)
                {
                    for (var i = 0; i < this.fields.length; i++)
                    {
                        var currentExpressionStr = this.fields[i]['exp'];
                        var evaluationResult = eval(currentExpressionStr);
                        if (evaluationResult)
                            insertedFields = this.fields[i]['fields'];
                    }
                    if (!insertedFields)
                        insertedFields = this.defaultFields || [];
                }
                else {
                    insertedFields = [];
                }
                var nOfFields = insertedFields.length;
                for (var fieldIndex = 0; fieldIndex < nOfFields; fieldIndex++) {
                    var att = insertedFields[fieldIndex];
                    var cell = rowEl.cells[fieldIndex];
                    var keyEl = cell.firstChild;
                    var valueEl = keyEl.nextSibling;
                    var value = dataNode.getAttribute(att);
                    valueEl.innerHTML = (this.renderCellFunction ? this.renderCellFunction(att, value, dataNode, valueEl) || "" : value || "");
                    keyEl.innerHTML = att + '=';
                    cell.propKey = att;
                    cell.propValue = value;
                    YAHOO.util.Dom.setStyle(cell, 'display', 'inline');
                }
                for (var fieldIndex = insertedFields.length; fieldIndex < this.maxRowCellLength; fieldIndex++)
                {
                    var cell = rowEl.cells[fieldIndex];
                    YAHOO.util.Dom.setStyle(cell, 'display', 'none');
                }
            }
            else {
                var tempIndex = 0;
                for (var att in data) {
                    if (tempIndex == this.showMax) {
                        break;
                    }
                    if (att != this.sortOrderAttribute && att != 'rsAlias') {
                        var cell = rowEl.cells[tempIndex];
                        var keyEl = cell.firstChild;
                        var valueEl = keyEl.nextSibling;
                        var value = data[att];
                        valueEl.innerHTML = (this.renderCellFunction ? this.renderCellFunction(att, value, dataNode, valueEl) || "" : value || "");
                        keyEl.innerHTML = att + '=';
                        cell.propKey = att;
                        cell.propValue = value;
                        YAHOO.util.Dom.setStyle(cell, 'display', 'inline');
                        tempIndex ++;
                    }
                }
                for (var fieldIndex = tempIndex; fieldIndex < this.maxRowCellLength; fieldIndex++)
                {
                    var cell = rowEl.cells[fieldIndex];
                    YAHOO.util.Dom.setStyle(cell, 'display', 'none');
                }
            }

        }

    },

    cellClicked: function(cell, row, target, e, dataNode) {
        if (YAHOO.util.Dom.hasClass(target, 'rcmdb-search-cell-value')) {
            if (e.ctrlKey)
            {
                this.appendExceptQuery(cell.propKey, cell.propValue);
            }
            else
            {
                this.appendToQuery(cell.propKey + ":" + cell.propValue.toQuery() + "");
                this.firePropertyClick(cell.propKey, cell.propValue, dataNode);
            }
        }
        else if (YAHOO.util.Dom.hasClass(target, 'rcmdb-search-cell-menu')) {
            var index = 0;
            for (var i in this.propertyMenuItems) {
                var menuItemConfig = this.propertyMenuItems[i];
                if (menuItemConfig['visible'] != null) {
                    var params = {data:dataNode.getAttributes(), label:menuItemConfig.label, menuId:menuItemConfig.id, key:cell.propKey, value:cell.propValue}
                    var menuItem = this.cellMenu.getItem(index);
                    var condRes = eval(menuItemConfig['visible'])
                    if (!condRes)
                        menuItem.element.style.display = "none";
                    else
                        menuItem.element.style.display = "";
                }
                index++;
            }
            this.cellMenu.row = row;
            this.cellMenu.cell = cell;
            this.cellMenu.cfg.setProperty("context", [target, 'tl', 'bl']);
            this.cellMenu.show();
            YAHOO.rapidjs.component.OVERLAY_MANAGER.bringToTop(this.cellMenu);
        }
    },
    rowClicked: function(row, target, e, dataNode) {
        YAHOO.rapidjs.component.search.SearchList.superclass.rowClicked.call(this, row, target, e, dataNode);
        if (YAHOO.util.Dom.hasClass(target, 'rcmdb-search-rowheader') || YAHOO.util.Dom.hasClass(target, 'rcmdb-search-rowheader-value'))
        {
            this.fireRowHeaderClicked(dataNode);
        }
    },

    cellMenuItemClicked: function(eventType, key) {
        var id = this.propertyMenuItems[key].id;
        var row = this.cellMenu.row;
        var cell = this.cellMenu.cell;
        this.cellMenu.row = null;
        this.cellMenu.cell = null;
        var xmlData = this.searchData[row.rowIndex - this.lastOffset].xmlData;
        this.fireCellMenuClick(cell.propKey, cell.propValue, xmlData, id);
    },

    calculateRowHeight: function() {
        var lineHeight = 20;
        var paddingTop = 0;
        var paddingBottom = 0;
        var borderTop = 0;
        var borderBottom = 0;
        var rule = YAHOO.ext.util.CSS.getRule(this.getClassName() + ' .rcmdb-search-row');
        if (rule) {
            lineHeight = this._getIntStyle(rule.style.lineHeight, lineHeight);
            paddingTop = this._getIntStyle(rule.style.paddingTop, paddingTop);
            paddingBottom = this._getIntStyle(rule.style.paddingBottom, paddingBottom);
            borderTop = this._getIntStyle(rule.style.borderTop, borderTop);
            borderBottom = this._getIntStyle(rule.style.borderBottom, borderBottom);
        }
        this.rowHeight = (lineHeight * this.lineSize) + paddingBottom + paddingTop + borderBottom + borderTop;
    },
    _getIntStyle : function(style, defaultValue) {
        var value;
        if (style) {
            value = parseInt(style, 10);
            if (!isNaN(value)) {
                return value;
            }
        }
        return defaultValue;
    },


    handleLineSizeChange: function(value) {
        if (value != this.lineSize)
        {
            this.showMask();
            this.lineSize = value;
            this.calculateRowHeight();
            for (var rowIndex = 0; rowIndex < this.bufferView.rowEls.length; rowIndex++) {
                this.bufferView.rowEls[rowIndex].setHeight(this.rowHeight);
            }
            this.updateBodyHeight();
        }
    },
    preparePropertiesToRequest: function() {
        var propsMap = {};
        if (this.fields) {
            for (var i = 0; i < this.fields.length; i++) {
                var fields = this.fields[i]['fields'];
                for (var j = 0; j < fields.length; j++) {
                    var field = fields[j];
                    propsMap[field] = field;
                }
            }
        }
        if (this.defaultFields) {
            for (var j = 0; j < this.defaultFields.length; j++) {
                var field = this.defaultFields[j];
                propsMap[field] = field;
            }
        }
        for (var prop in propsMap) {
            if (prop.trim() != '') {
                this.propertiesToRequest.push(prop);
            }
        }
    },
    getViewedProperties : function() {
        return this.propertiesToRequest;
    },
    fireCellMenuClick: function(key, value, data, id) {
        this.events['cellMenuClicked'].fireDirect(key, value, data, id);
    },
    fireRowHeaderClicked: function(data) {
        this.events['rowHeaderClicked'].fireDirect(data);
    }

});
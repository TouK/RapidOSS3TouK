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
YAHOO.rapidjs.component.search.SearchGrid = function(container, config) {
    this.columns = null;
    this.minColumnWidth = 30;
    this.fieldsUrl = null;
    this.queryEnabled = true;
    YAHOO.rapidjs.component.search.SearchGrid.superclass.constructor.call(this, container, config);
};


YAHOO.lang.extend(YAHOO.rapidjs.component.search.SearchGrid, YAHOO.rapidjs.component.search.AbstractSearchList, {
    init: function() {
        this.totalColumnWidth = null;
        this.viewBuilder = null;
        this.defaultColumns = YAHOO.rapidjs.ObjectUtils.clone(this.columns, true);
        this.lastSortedHeader = null;
        this.bodyId = this.id + "_body";
        this.addMenuColumn(this.columns);
        for (var index = 0; index < this.columns.length; index++) {
            var colCssName = "#" + this.bodyId + " .rcmdb-searchgrid-col-" + index;
            this.addColCss(colCssName);
        }

        var colCssName = "#" + this.bodyId + " .rcmdb-searchgrid-col-last";
        this.addColCss(colCssName);
        YAHOO.ext.util.CSS.getRules(true);
        this.numberOfColumnsDrawn = this.columns.length;
    },
    addColCss: function(cssName)
    {
        var ds = document.styleSheets;
        if (ds.length > 0)
        {
            if (ds[0].insertRule)
                ds[0].insertRule(cssName + " {margin:0px;} ", 0);
            else if (ds[0].addRule)
                ds[0].addRule(cssName, " {margin:0px;} ");
        }
    },
    _render : function() {
        var dh = YAHOO.ext.DomHelper;
        var searchInputWrp = dh.append(this.toolbar.el, {tag:'div', cls:'rcmdb-searchgrid-tools',
            html:'<div class="wrp"></div><div class="wrp"></div><div class="wrp"></div>' +
                 '<div class="rcmdb-searchgrid-viewselect-wrp wrp"><select class="rcmdb-searchgrid-viewselect"></select></div>' +
                 '<div class="rcmdb-searchgrid-count wrp">&nbsp;</div>' +
                 '<div class="wrp"></div><div class="wrp"></div>' +
                 '<div class="wrp"><form action="javascript:void(0)" style="overflow:auto;"><input class="rcmdb-searchgrid-searchinput"></input></form></div>'});
        var wrps = YAHOO.util.Dom.getElementsByClassName('wrp', 'div', searchInputWrp);
        this.removeViewButton = new YAHOO.rapidjs.component.Button(wrps[0], {className:'rcmdb-searchgrid-removeview', scope:this, click:this.handleRemoveView, tooltip: 'Remove View'});
        this.updateViewButton = new YAHOO.rapidjs.component.Button(wrps[1], {className:'rcmdb-searchgrid-editview', scope:this, click:this.handleEditView, tooltip: 'Edit View'});
        this.removeViewButton.disable();
        this.updateViewButton.disable();
        new YAHOO.rapidjs.component.Button(wrps[2], {className:'rcmdb-searchgrid-addview', scope:this, click:this.handleAddView, tooltip: 'Add View'});
        this.viewInput = searchInputWrp.getElementsByTagName('select')[0];
        YAHOO.util.Event.addListener(this.viewInput, 'change', this.handleViewChange, this, true);
        this.searchCountEl = wrps[4];
        new YAHOO.rapidjs.component.Button(wrps[5], {className:'rcmdb-searchgrid-saveButton', scope:this, click:this.handleSaveQueryClick, tooltip: 'Save Query'});
        new YAHOO.rapidjs.component.Button(wrps[6], {className:'rcmdb-searchgrid-searchButton', scope:this, click:this.handleSearch, tooltip: 'Search'});
        this.searchInput = searchInputWrp.getElementsByTagName('input')[0];
        YAHOO.util.Event.addListener(this.searchInput.form, 'keypress', this.handleInputEnter, this, true);
        if(!this.queryEnabled){
            YAHOO.util.Dom.setStyle(wrps[5], 'display', 'none')
            YAHOO.util.Dom.setStyle(wrps[6], 'display', 'none')
            YAHOO.util.Dom.setStyle(wrps[7], 'display', 'none')
        }
        this.body.dom.id = this.bodyId;
        this.pwrap = dh.append(this.body.dom, {tag: 'div', cls: 'rcmdb-searchgrid-positioner'});
        this.hwrap = dh.append(this.pwrap, {tag: 'div', cls: 'rcmdb-searchgrid-wrap-headers'}, true);
        this.hrow = dh.append(this.hwrap.dom, {tag: 'span', cls: 'rcmdb-searchgrid-hrow'});
        if (!YAHOO.ext.util.Browser.isGecko) {
            var iframe = document.createElement('iframe');
            iframe.className = 'rcmdb-searchgrid-hrow-frame';
            iframe.frameBorder = 0;
            iframe.src = YAHOO.ext.SSL_SECURE_URL;
            this.hwrap.dom.appendChild(iframe);
        }
        this.bwrap = dh.append(this.pwrap, {tag: 'div', cls:'rcmdb-searchgrid-bwrap'}, true);
        this.scrollPos = dh.append(this.bwrap.dom, {tag: 'div'}, true);
        this.bufferPos = dh.append(this.scrollPos.dom, {tag:'div'}, true);
        this.bufferView = dh.append(this.scrollPos.dom, {tag:'div'}, true);
        this.bufferView.rowEls = [];
        this.headers = [];
        var htemplate = dh.createTemplate({
            tag: 'span', cls: 'rcmdb-searchgrid-hd rcmdb-searchgrid-header-{0}', children: [{
            tag: 'span',
            cls: 'rcmdb-searchgrid-hd-body',
            html: '<table border="0" cellpadding="0" cellspacing="0" title="{1}">' +
                  '<tbody><tr><td><span>{1}</span></td>' +
                  '<td><span class="sort-desc"></span><span class="sort-asc"></span></td>' +
                  '</tr></tbody></table>'
        }]
        });
        htemplate.compile();
        this.htemplate = htemplate;
        var sortColIndex = null;
        var sortOrder = 'asc';
        for (var i = 0; i < this.columns.length; i++) {
            if (this.columns[i]['sortBy'] == true) {
                sortColIndex = i;
                if (this.columns[i]['sortOrder']) {
                    sortOrder = this.columns[i]['sortOrder'];
                }
            }
            this.addHeader(i, this.columns[i].colLabel);
        }
        if (sortColIndex != null) {
            this.lastSortedHeader = this.headers[sortColIndex];
            this.lastSortedHeader.sortDir = sortOrder;
            this.lastSortAtt = this.columns[sortColIndex]['attributeName'];
        }
        YAHOO.util.Event.addListener(this.bwrap.dom, 'scroll', this.handleScroll, this, true);
        YAHOO.util.Event.addListener(this.scrollPos.dom, 'click', this.handleClick, this, true);
        YAHOO.util.Event.addListener(this.scrollPos.dom, 'dblclick', this.handleDoubleClick, this, true);
        this.updateColumns();
        this.viewBuilder = new YAHOO.rapidjs.component.search.ViewBuilder(this);
        this.viewBuilder.events['success'].subscribe(this.viewBuilderSuccess, this, true);
        this.viewBuilder.events['error'].subscribe(this.viewBuilderError, this, true);
    },

    addHeader: function(columnIndex, label) {
        var hd = this.htemplate.append(this.hrow, [columnIndex, label]);

        var spans = hd.getElementsByTagName('span');
        hd.textNode = spans[1];
        hd.sortDesc = spans[2];
        hd.sortAsc = spans[3];
        hd.columnIndex = columnIndex;
        this.headers.push(hd);
        var split = YAHOO.ext.DomHelper.append(this.hrow, {tag: 'span', cls: 'rcmdb-searchgrid-hd-split', style:columnIndex == 0 ? 'cursor:default' : ''});
        hd.split = split;
        if (columnIndex > 0) {
            //            YAHOO.util.Event.on(split, 'dblclick', autoSizeDelegate.createCallback(i + 0, true));
            getEl(hd).addClassOnOver('rcmdb-searchgrid-hd-over');
            YAHOO.util.Event.addListener(hd, 'click', this.headerClicked, this, true);
            var sb = new YAHOO.rapidjs.component.Split(split, hd, null, YAHOO.rapidjs.component.Split.LEFT);
            sb.columnIndex = columnIndex;
            sb.minSize = this.minColumnWidth;
            sb.onMoved.subscribe(this.onColumnSplitterMoved, this, true);
            YAHOO.util.Dom.addClass(sb.proxy, 'rcmdb-searchgrid-column-sizer');
            YAHOO.util.Dom.setStyle(sb.proxy, 'background-color', '');
            var hwrap = this.hwrap;
            var bwrap = this.bwrap;
            sb.dd._resizeProxy = function() {
                var el = this.getDragEl();
                YAHOO.util.Dom.setStyle(el, 'height', (hwrap.dom.clientHeight + bwrap.dom.clientHeight - 2) + 'px');
            };
            hd.sb = sb;
        }
        return hd;
    },

    addCellsToCurrentRows: function(nOfColumns) {
        var nOfRows = this.bufferView.rowEls.length
        for (var rowIndex = 0; rowIndex < nOfRows; rowIndex++) {
            var rowEl = this.bufferView.rowEls[rowIndex];
            var currentCellLength = rowEl.cells.length;
            var nOfCellsToAdd = nOfColumns - currentCellLength;
            for (var colIndex = 0; colIndex < nOfCellsToAdd; colIndex++) {
                var columnIndex = currentCellLength + colIndex;
                YAHOO.ext.DomHelper.append(rowEl.dom, {tag:'span', cls:'rcmdb-search-cell rcmdb-searchgrid-col-' + columnIndex,
                    html:'<span class="rcmdb-search-cell-value"></span>'});
            }
            rowEl.cells = YAHOO.util.Dom.getElementsByClassName('rcmdb-search-cell', 'span', rowEl.dom);
        }
    },

    createEmptyRows : function(rowCount) {
        for (var rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            var colCount = this.columns.length;
            var innerHtml = '<span class="rcmdb-search-cell rcmdb-searchgrid-col-0" style="padding:0px"><div class="rcmdb-search-row-headermenu"></div></span>';
            for (var colIndex = 1; colIndex < colCount; colIndex++) {
                innerHtml += '<span class="rcmdb-search-cell rcmdb-searchgrid-col-' + colIndex + (colIndex == colCount - 1 ? ' rcmdb-searchgrid-col-last' : '') + '">' +
                             '<span class="rcmdb-search-cell-value"></span></span>';
            }
            var rowEl = YAHOO.ext.DomHelper.append(this.bufferView.dom, {tag:'div', cls:'rcmdb-search-row', html:innerHtml}, true);
            this.bufferView.rowEls[this.bufferView.rowEls.length] = rowEl;
            YAHOO.util.Dom.setStyle(rowEl.dom, 'display', 'none');
            rowEl.setHeight(this.rowHeight);
            var cells = YAHOO.util.Dom.getElementsByClassName('rcmdb-search-cell', 'span', rowEl.dom)
            rowEl.cells = cells;
        }

    },

    renderRow: function(rowEl) {
        var searchNode = this.searchData[rowEl.dom.rowIndex - this.lastOffset];
        var dataNode = searchNode.xmlData;
        var params = {data:dataNode.getAttributes()};
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
        var nOfColumns = this.columns.length;
        for (var colIndex = 1; colIndex < nOfColumns; colIndex++) {
            var colConfig = this.columns[colIndex];
            var att = colConfig.attributeName;
            var cell = rowEl.cells[colIndex];
            var valueEl = cell.firstChild;
            var value = dataNode.getAttribute(att);
            valueEl.innerHTML = (this.renderCellFunction ? this.renderCellFunction(att, value, dataNode) || "" : value || "");
            cell.propKey = att;
            cell.propValue = value;
        }
    },

    createMask: function() {
        var dh = YAHOO.ext.DomHelper;
        this.mask = dh.append(this.pwrap, {tag:'div', cls:'rcmdb-search-mask'}, true);
        this.maskMessage = dh.append(this.pwrap, {tag:'div', cls:'rcmdb-search-mask-loadingwrp', html:'<div class="rcmdb-search-mask-loading">Loading...</div>'}, true)
        this.hideMask();
    },
    showMask: function() {
        this._showMask(this.bwrap);
    },

    addTools: function() {
        this.toolbar.addTool(new YAHOO.rapidjs.component.tool.LoadingTool(document.body, this));
        this.toolbar.addTool(new YAHOO.rapidjs.component.tool.SettingsTool(document.body, this));
        this.toolbar.addTool(new YAHOO.rapidjs.component.search.ExportTool(document.body, this));
        this.toolbar.addTool(new YAHOO.rapidjs.component.tool.ErrorTool(document.body, this));
    },

    showCurrentState: function() {
        this.searchCountEl.innerHTML = "Count: " + this.totalRowCount;
    },


    calculateRowHeight: function() {
        this.rowHeight = 21;
    },
    onColumnSplitterMoved : function(splitter, newSize) {
        this.columns[splitter.columnIndex].width = newSize;
        this.totalColumnWidth = null;
        this.updateColumns();
    },

    updateColumns : function() {
        var hcols = this.headers;
        var colCount = this.columns.length;
        var pos = 0;
        var totalWidth = this.getTotalColumnWidth();
        for (var i = 0; i < colCount; i++) {
            var width = this.columns[i].width;
            hcols[i].style.width = width + 'px';
            hcols[i].style.left = pos + 'px';
            hcols[i].split.style.left = (pos + width - 3) + 'px';
            this.setCSSWidth(i, width, pos);
            pos += width;
        }
        this.scrollPos.setWidth(totalWidth);
        this.syncScroll();
    },

    handleScroll: function() {
        this.syncScroll();
        var scrollLeft = this.bwrap.dom.scrollLeft;
        if (this.prevScrollLeft == scrollLeft && !this.scrollChangedIntentially)
        {
            this._verticalScrollChanged();
        }
        this.prevScrollLeft = scrollLeft;
        this.scrollChangedIntentially = false;
    },

    setCSSWidth : function(colIndex, width, pos) {
        var selector = ["#" + this.bodyId + " .rcmdb-searchgrid-col-" + colIndex, ".rcmdb-searchgrid-col-" + colIndex];
        YAHOO.ext.util.CSS.updateRule(selector, 'width', width + 'px');
        if (typeof pos == 'number') {
            YAHOO.ext.util.CSS.updateRule(selector, 'left', pos + 'px');
        }
    },
    getClassName: function() {
        return "rcmdb-searchgrid";
    },
    resize : function(width, height) {
        this.body.setWidth(width);
        this.body.setHeight(height - this.header.dom.offsetHeight);
        this.bwrap.setHeight(height - (this.header.dom.offsetHeight + this.hwrap.getHeight()));
        var totalWidth = this.getTotalColumnWidth();
        this.scrollPos.setWidth(totalWidth);
        this._verticalScrollChanged();
    },
    syncScroll : function() {
        this.hwrap.dom.scrollLeft = this.bwrap.dom.scrollLeft;
    },
    getTotalColumnWidth: function() {
        if (!this.totalColumnWidth) {
            this.totalColumnWidth = 0;
            for (var i = 0; i < this.columns.length; i++) {
                this.totalColumnWidth += this.columns[i].width;
            }
        }
        return this.totalColumnWidth;
    },
    getScrolledEl: function() {
        return this.bwrap;
    },
    getHeaderFromChild : function(childEl) {
        return YAHOO.rapidjs.DomUtils.getElementFromChild(childEl, 'rcmdb-searchgrid-hd');
    },
    headerClicked: function(e) {
        var target = YAHOO.util.Event.getTarget(e)
        var header = this.getHeaderFromChild(target);
        var direction = header.sortDir == 'asc' ? 'desc' : 'asc';
        header.sortDir = direction;
        this.sort(this.columns[header.columnIndex]['attributeName'], header.sortDir);
    },

    updateHeaderSortState: function(hd) {
        var sortAscDisplay = hd.sortDir == 'asc' ? 'block' : 'none';
        var sortDescDisplay = hd.sortDir == 'desc' ? 'block' : 'none';
        YAHOO.util.Dom.setStyle(hd.sortDesc, 'display', sortDescDisplay)
        YAHOO.util.Dom.setStyle(hd.sortAsc, 'display', sortAscDisplay)
    },
    getCellFromChild : function(childEl) {
        var cell = YAHOO.rapidjs.DomUtils.getElementFromChild(childEl, 'rcmdb-search-cell');
        if (!YAHOO.util.Dom.hasClass(cell, 'rcmdb-searchgrid-col-0')) {
            return cell;
        }
        return null;
    },
    addMenuColumn: function(columns) {
        columns.splice(0, 0, {colLabel:"&#160;", width:19});
    },
    loadViews: function(response) {
        SelectUtils.clear(this.viewInput);
        SelectUtils.addOption(this.viewInput, 'default', 'default');
        var viewNodes = response.responseXML.getElementsByTagName('View');
        for (var index = 0; index < viewNodes.length; index++) {
            var viewNode = viewNodes[index];
            var viewName = viewNode.getAttribute('name');
            SelectUtils.addOption(this.viewInput, viewName, viewName);
        }
    },
    handleRemoveView: function() {
        var currentView = this.viewInput.options[this.viewInput.selectedIndex].value;
        if (confirm('Remove ' + currentView + '?')) {
            this.viewBuilder.removeView(currentView);
        }
    },
    handleEditView: function() {
        var view = this.viewInput.options[this.viewInput.selectedIndex].value;
        this.viewBuilder.show(view);
    },
    handleAddView: function() {
        this.viewBuilder.show();
    },
    viewAdded: function(viewNode) {
        var view = viewNode.getAttribute('name');
        SelectUtils.remove(this.viewInput, view);
        SelectUtils.addOption(this.viewInput, view, view);
        SelectUtils.selectTheValue(this.viewInput, view, 0);
        this.viewChanged();
    },
    viewUpdated: function(viewNode) {
        var view = viewNode.getAttribute('name');
        SelectUtils.selectTheValue(this.viewInput, view, 0);
        this.viewChanged();
    },
    viewRemoved : function(view) {
        SelectUtils.remove(this.viewInput, view);
        this.viewChanged();
    },
    handleViewChange: function(e) {
        this.viewChanged();
    },
    viewChanged: function(newQuery, willSaveHistory) {
        var view = this.viewInput.options[this.viewInput.selectedIndex].value;
        var viewNode = this.viewBuilder.viewData.findChildNode('name', view, 'View')[0];
        if (view == 'default' || (viewNode && viewNode.getAttribute("updateAllowed") == "false")) {
            this.updateViewButton.disable();
            this.removeViewButton.disable();
        }
        else {
            this.updateViewButton.enable();
            this.removeViewButton.enable();
        }
        this.activateView(view, newQuery, willSaveHistory);
    },
    activateView : function(view, newQuery, willSaveHistory) {
        this.showMask();
        var viewNode = this.viewBuilder.viewData.findChildNode('name', view, 'View')[0];
        var columns;
        if (viewNode) {
            columns = this.getColumnConfigFromViewNode(viewNode);
        }
        else {
            columns = YAHOO.rapidjs.ObjectUtils.clone(this.defaultColumns, true);
        }
        this.addMenuColumn(columns);
        if (columns.length > this.columns.length) {
            var columnsToUnhide;
            if (this.numberOfColumnsDrawn >= columns.length) {
                var columnsToUnhide = columns.length - this.columns.length;
            }
            else if (this.numberOfColumnsDrawn < columns.length) {
                var columnsToUnhide = this.numberOfColumnsDrawn - this.columns.length;
                var columnsToBeAdded = columns.length - this.numberOfColumnsDrawn;
                for (var index = 0; index < columnsToBeAdded; index++) {
                    var columnIndex = this.numberOfColumnsDrawn + index;
                    this.addHeader(columnIndex, "&#160;")
                    var colCssName = "#" + this.bodyId + " .rcmdb-searchgrid-col-" + columnIndex;
                    this.addColCss(colCssName);
                }
                YAHOO.ext.util.CSS.getRules(true);
                this.numberOfColumnsDrawn = columns.length;
            }
            for (var index = 0; index < columnsToUnhide; index++) {
                this.unhideColumn(this.columns.length + index);
            }
            this.addCellsToCurrentRows(columns.length);
        }
        else if (columns.length < this.columns.length) {
            var columnsToHide = this.columns.length - columns.length;
            for (var index = 0; index < columnsToHide; index++) {
                this.hideColumn(this.columns.length - (index + 1));
            }
        }
        this.totalColumnWidth = null;
        var rowCount = this.bufferView.rowEls.length;
        for (var index = 0; index < rowCount; index++) {
            var rowEl = this.bufferView.rowEls[index]
            var cells = rowEl.cells;
            YAHOO.util.Dom.removeClass(cells[this.columns.length - 1], 'rcmdb-searchgrid-col-last');
            YAHOO.util.Dom.addClass(cells[columns.length - 1], 'rcmdb-searchgrid-col-last');

        }
        if (this.lastSortedHeader) {
            this.lastSortedHeader.sortDir = null;
            this.updateHeaderSortState(this.lastSortedHeader);
            this.lastSortedHeader = null;
        }
        this.columns = columns;
        for (var index = 0; index < this.columns.length; index++) {
            var header = this.headers[index];
            header.textNode.innerHTML = this.columns[index].colLabel;
        }
        this.updateColumns();
        for (var index = 0; index < rowCount; index++) {
            var rowEl = this.bufferView.rowEls[index];
            this.renderRow(rowEl);
        }
        var sortedColumnIndex = this.getSortedColumnIndex();
        var sortAtt = this.lastSortAtt;
        var sortOrder = this.lastSortOrder;
        if (sortedColumnIndex > -1) {
            var column = this.columns[sortedColumnIndex];
            sortAtt = column['attributeName'];
            sortOrder = column['sortOrder'] || 'asc';
        }
        this._setQuery(newQuery || this.currentlyExecutingQuery || '', sortAtt, sortOrder);
        this.handleSearch(null, willSaveHistory);
    },
    getColumnConfigFromViewNode: function(viewNode) {
        var sortColumn = viewNode.getAttribute('defaultSortColumn');
        var sortOrder = viewNode.getAttribute('sortOrder');
        var columnNodes = viewNode.getElementsByTagName('Column');
        var columns = [];
        for (var index = 0; index < columnNodes.length; index++) {
            var columnNode = columnNodes[index];
            var attributeName = columnNode.getAttribute('attributeName');
            var colLabel = columnNode.getAttribute('header');
            var width = parseInt(columnNode.getAttribute('width'), 10);
            var columnIndex = parseInt(columnNode.getAttribute('columnIndex'), 10);
            var sortBy = sortColumn == attributeName;
            var column = {attributeName:attributeName, colLabel:colLabel, width:width, sortBy:sortBy, columnIndex:columnIndex}
            if (sortBy) {
                column['sortOrder'] = sortOrder;
            }
            columns.push(column);
        }
        columns.sort(function(col1, col2) {
            return col1.columnIndex - col2.columnIndex;
        });
        return columns;
    },
    hideColumn : function(colIndex) {
        var selector = ["#" + this.bodyId + " .rcmdb-searchgrid-col-" + colIndex, ".rcmdb-searchgrid-col-" + colIndex];
        YAHOO.ext.util.CSS.updateRule(selector, 'position', 'absolute');
        YAHOO.ext.util.CSS.updateRule(selector, 'display', 'none');
        this.headers[colIndex].style.display = 'none';
        this.headers[colIndex].split.style.display = 'none';
    },

    unhideColumn : function(colIndex) {
        var selector = ["#" + this.bodyId + " .rcmdb-searchgrid-col-" + colIndex, ".rcmdb-searchgrid-col-" + colIndex];
        YAHOO.ext.util.CSS.updateRule(selector, 'position', '');
        YAHOO.ext.util.CSS.updateRule(selector, 'display', '');
        this.headers[colIndex].style.display = '';
        this.headers[colIndex].split.style.display = '';
    },

    viewBuilderError:function(component, errors) {
        this.events["error"].fireDirect(this, errors, true);
    },
    viewBuilderSuccess: function() {
        this.events["success"].fireDirect(this);
    },
    _poll : function() {
        var lastClicked = this.lastSortedHeader;
        if (lastClicked) {
            lastClicked.sortDir = null;
            this.updateHeaderSortState(lastClicked);
            this.lastSortedHeader = null;
        }
        var sortedHeaderIndex = -1;
        for (var i = 0; i < this.columns.length; i++) {
            if (this.columns[i]['attributeName'] == this.lastSortAtt) {
                sortedHeaderIndex = i;
                break;
            }
        }
        if (sortedHeaderIndex > -1) {
            this.lastSortedHeader = this.headers[sortedHeaderIndex];
            this.lastSortedHeader.sortDir = this.lastSortOrder;
            this.updateHeaderSortState(this.lastSortedHeader);
        }
        YAHOO.rapidjs.component.search.SearchGrid.superclass._poll.call(this);
    },
    setQueryWithView: function(queryString, view, title, extraParams)
    {
        if (title) {
            this.setTitle(title);
        }
        var currentView = this.viewInput.options[this.viewInput.selectedIndex].value;
        if (currentView != view) {
            SelectUtils.selectTheValue(this.viewInput, view, 0);
            this.viewChanged(queryString);
        }
        else {
            this._setQuery(queryString, this.lastSortAtt, this.lastSortOrder, extraParams);
            this.handleSearch();
        }
    },

    handleSearch: function(e, willSaveHistory) {
        this.offset = 0;
        this._poll();
        this._changeScroll(0);
        if (willSaveHistory !== false) {
            var newHistoryState = [];
            newHistoryState[newHistoryState.length] = this.searchInput.value;
            newHistoryState[newHistoryState.length] = this.viewInput[this.viewInput.selectedIndex].value;
            this.saveHistoryChange(newHistoryState.join("!::!"));
        }
    },

    historyChanged: function(state) {
        if (state != "noAction") {
            var params = state.split("!::!");
            var queryString = params[0]
            var view = params[1]
            SelectUtils.selectTheValue(this.viewInput, view, 0);
            this.viewChanged(queryString, false);
        }
    },

    getSortedColumnIndex: function() {
        for (var i = 0; i < this.columns.length; i++) {
            if (this.columns[i]['sortBy'] == true) {
                return i;
            }
        }
        return -1;
    }
});
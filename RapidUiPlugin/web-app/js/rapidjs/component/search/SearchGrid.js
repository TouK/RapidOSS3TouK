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
    this.rowColors = null;
    this.viewType = null;
    this.queryEnabled = true;
    YAHOO.rapidjs.component.search.SearchGrid.superclass.constructor.call(this, container, config);
};


YAHOO.lang.extend(YAHOO.rapidjs.component.search.SearchGrid, YAHOO.rapidjs.component.search.AbstractSearchList, {
    init: function() {
        this.totalColumnWidth = null;
        this.viewBuilder = null;
        this.viewsLoaded = false;
        this.currentView = 'default';
        if (this.config.defaultView != null)
        {
            this.currentView = this.config.defaultView;
        }

        this.defaultColumns = YAHOO.rapidjs.ObjectUtils.clone(this.columns, true);
        var defaultColumnMap = {};
        for (var index = 0; index < this.defaultColumns.length; index ++) {
            var columnConfig = this.defaultColumns[index];
            defaultColumnMap[columnConfig['attributeName']] = columnConfig
        }
        this.defaultColumnMap = defaultColumnMap;
        this.bodyId = this.id + "_body";
        this.addMenuColumn(this.columns);
        for (var index = 0; index < this.columns.length; index++) {
            var colCssName = "#" + this.bodyId.toLowerCase() + " .rcmdb-searchgrid-col-" + index;
            this.addColCss(colCssName);
        }

        var colCssName = "#" + this.bodyId.toLowerCase() + " .rcmdb-searchgrid-col-last";
        this.addColCss(colCssName);
        YAHOO.ext.util.CSS.getRules(true);
        for (var index = 0; index < this.columns.length; index++) {
            var defaultColumnConfig = this.defaultColumnMap[this.columns[index]['attributeName']]
            var isHyperlink = defaultColumnConfig ? defaultColumnConfig['type'] == 'link' : false
            this.setCssHyperlink(index, isHyperlink);
        }
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
                 '<div class="rcmdb-searchgrid-classes-wrp wrp"><select class="rcmdb-searchgrid-classes" style="width:80px"></select></div><div class="wrp" style="margin-top:3px">in:</div>' +
                 '<div class="wrp"></div><div class="wrp"></div>' +
                 '<div class="wrp"><form action="javascript:void(0)" style="overflow:auto;"><input class="rcmdb-searchgrid-searchinput"></input></form></div>'});
        var wrps = YAHOO.util.Dom.getElementsByClassName('wrp', 'div', searchInputWrp);
        this.removeViewButton = new YAHOO.rapidjs.component.Button(wrps[0], {className:'rcmdb-searchgrid-removeview', scope:this, click:this.handleRemoveView, tooltip: 'Remove View'});
        this.updateViewButton = new YAHOO.rapidjs.component.Button(wrps[1], {className:'rcmdb-searchgrid-editview', scope:this, click:this.handleEditView, tooltip: 'Edit View'});
        this.removeViewButton.disable();
        this.updateViewButton.disable();
        new YAHOO.rapidjs.component.Button(wrps[2], {className:'rcmdb-searchgrid-addview', scope:this, click:this.handleAddView, tooltip: 'Add View'});
        var selects = searchInputWrp.getElementsByTagName('select')
        this.viewInput = selects[0];
        this.classesInput = selects[1];
        YAHOO.util.Event.addListener(this.viewInput, 'change', this.handleViewChange, this, true);
        this.searchCountEl = wrps[4];
        new YAHOO.rapidjs.component.Button(wrps[7], {className:'rcmdb-searchgrid-saveButton', scope:this, click:this.handleSaveQueryClick, tooltip: 'Save Query'});
        new YAHOO.rapidjs.component.Button(wrps[8], {className:'rcmdb-searchgrid-searchButton', scope:this, click:this.handleSearch, tooltip: 'Search'});
        this.searchInput = searchInputWrp.getElementsByTagName('input')[0];
        YAHOO.util.Event.addListener(this.searchInput.form, 'keypress', this.handleInputEnter, this, true);
        if (!this.queryEnabled) {
            YAHOO.util.Dom.setStyle(wrps[5], 'display', 'none')
            YAHOO.util.Dom.setStyle(wrps[6], 'display', 'none')
            YAHOO.util.Dom.setStyle(wrps[7], 'display', 'none')
            YAHOO.util.Dom.setStyle(wrps[8], 'display', 'none')
            YAHOO.util.Dom.setStyle(wrps[9], 'display', 'none')
        }
        else if (!this.searchInEnabled) {
            YAHOO.util.Dom.setStyle(wrps[5], 'display', 'none')
            YAHOO.util.Dom.setStyle(wrps[6], 'display', 'none')
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
            var sortAtt = this.columns[sortColIndex]['attributeName']
            this.sortHelper.setSort(sortAtt, sortOrder)
            this.params['order'] = this.getSortOrder();
            this.params['sort'] = this.getSortAttribute();
        }
        YAHOO.util.Event.addListener(this.bwrap.dom, 'scroll', this.handleScroll, this, true);
        YAHOO.util.Event.addListener(this.scrollPos.dom, 'click', this.handleClick, this, true);
        YAHOO.util.Event.addListener(this.scrollPos.dom, 'contextmenu', this.handleContextMenu, this, true);
        YAHOO.util.Event.addListener(this.scrollPos.dom, 'dblclick', this.handleDoubleClick, this, true);
        this.updateColumns();
        this.viewBuilder = new YAHOO.rapidjs.component.search.ViewBuilder(this, this.viewType);
        this.viewBuilder.events['success'].subscribe(this.viewBuilderSuccess, this, true);
        this.viewBuilder.events['error'].subscribe(this.viewBuilderError, this, true);

        if (this.queryEnabled && this.searchInEnabled) {
            this.retrieveSearchClasses();
        }
    },

    addHeader: function(columnIndex, label) {
        if(label == '') label = '&nbsp;' 
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
        if (this.rowColors) {
            var evaluationResult = false;
            var backColor, textColor;
            for (var i = 0; i < this.rowColors.length; i++)
            {
                var rowColor = this.rowColors[i]
                backColor = rowColor['color'];
                textColor = rowColor['textColor'] || '#000000';
                var currentExpressionStr = rowColor['visible'];
                try {
                    evaluationResult = eval(currentExpressionStr);
                    if (evaluationResult) {
                        break;
                    }
                }
                catch(e) {
                }
            }
            var backgroundColor = evaluationResult ? backColor : ''
            var tColor = evaluationResult ? textColor : ''
            YAHOO.util.Dom.setStyle(rowEl.dom, 'background-color', backgroundColor)
            YAHOO.util.Dom.setStyle(rowEl.dom, 'color', tColor)
        }
        var nOfColumns = this.columns.length;
        for (var colIndex = 1; colIndex < nOfColumns; colIndex++) {
            var colConfig = this.columns[colIndex];
            var att = colConfig.attributeName;
            var cell = rowEl.cells[colIndex];
            var valueEl = cell.firstChild;
            var value = dataNode.getAttribute(att);
            var innerHTML = "";
            if (colConfig['type'] == 'image') {
                this.setImageSource(colConfig, dataNode, cell);
            }
            else {
                innerHTML = (this.renderCellFunction ? this.renderCellFunction(att, value, dataNode) || "" : value || "");
            }
            valueEl.innerHTML = colIndex == nOfColumns - 1 ? innerHTML + '<br>' : innerHTML + '&nbsp'
            cell.propKey = att;
            cell.propValue = value;
        }
    },

    setImageSource: function(columnConfig, dataNode, htmlEl) {
        var expressionsArray = columnConfig['images'];
        var isImageSet = false;
        for (var i = 0; i < expressionsArray.length; i++)
        {
            var currentExpressionStr = expressionsArray[i]['visible'];
            var params = {data: dataNode.getAttributes()}
            var evaluationResult = eval(currentExpressionStr);
            if (evaluationResult == true)
            {
                isImageSet = true
                var imageSrc = expressionsArray[i]['src'];
                var align = expressionsArray[i]['align'] ? 'center ' + expressionsArray[i]['align'] : 'center left'
                var backgroundImage = 'url("' + imageSrc + '")';
                YAHOO.util.Dom.setStyle(htmlEl, "background", backgroundImage + ' no-repeat ' + align)
                break;
            }
        }
        if (!isImageSet) {
            YAHOO.util.Dom.setStyle(htmlEl, "background", '')
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
        var selector = "#" + this.bodyId.toLowerCase() + " .rcmdb-searchgrid-col-" + colIndex
        YAHOO.ext.util.CSS.updateRule(selector, 'width', width + 'px');
        if (typeof pos == 'number') {
            YAHOO.ext.util.CSS.updateRule(selector, 'left', pos + 'px');
        }
    },
    setCssHyperlink: function(colIndex, isHyperlink) {
        var selector = "#" + this.bodyId.toLowerCase() + " .rcmdb-searchgrid-col-" + colIndex
        YAHOO.ext.util.CSS.updateRule(selector, 'background', '');
        if (isHyperlink) {
            YAHOO.ext.util.CSS.updateRule(selector, 'cursor', 'pointer');
            YAHOO.ext.util.CSS.updateRule(selector, 'text-decoration', 'underline');
        }
        else {
            YAHOO.ext.util.CSS.updateRule(selector, 'cursor', '');
            YAHOO.ext.util.CSS.updateRule(selector, 'text-decoration', '');
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
        this.sortHelper.headerClicked(this.columns[header.columnIndex]['attributeName']);
        this._pollForSort();
    },

    getCellFromChild : function(childEl) {
        var cell = YAHOO.rapidjs.DomUtils.getElementFromChild(childEl, 'rcmdb-search-cell');
        if (!YAHOO.util.Dom.hasClass(cell, 'rcmdb-searchgrid-col-0')) {
            return cell;
        }
        return null;
    },
    addMenuColumn: function(columns) {
        columns.splice(0, 0, {colLabel:"&#160;", width:19, attributeName:''});
    },
    loadViews: function(response) {
        SelectUtils.clear(this.viewInput);
        SelectUtils.addOption(this.viewInput, 'default', 'default');
        var viewNodes = response.responseXML.getElementsByTagName('View');
        for (var index = 0; index < viewNodes.length; index++) {
            var viewNode = viewNodes[index];
            var viewName = viewNode.getAttribute('name');
            var viewId = viewNode.getAttribute('id');
            SelectUtils.addOption(this.viewInput, viewName, viewId);
        }
        this.selectDefaultView();
        this.viewsLoaded = true;
    },
    selectDefaultView: function()
    {
        YAHOO.rapidjs.SelectUtils.selectTheText(this.viewInput, this.currentView, 0);
        var willPollForViewChange=false;
        if(this.currentView != 'default')
        {
            willPollForViewChange=true;
        }
        this.viewChanged(null, null, null, willPollForViewChange);
    },
    handleRemoveView: function() {
        var selectedOption = this.viewInput.options[this.viewInput.selectedIndex]
        var currentView = selectedOption.text;
        var currentViewId = selectedOption.value;
        if (confirm('Remove ' + currentView + '?')) {
            this.viewBuilder.removeView(currentViewId);
        }
    },
    handleEditView: function() {
        var viewId = this.viewInput.options[this.viewInput.selectedIndex].value;
        this.viewBuilder.show(viewId);
    },
    handleAddView: function() {
        this.viewBuilder.show();
    },
    viewAdded: function(viewNode) {
        var viewName = viewNode.getAttribute('name');
        var viewId = viewNode.getAttribute('id');
        for (var i = 0; i < this.viewInput.options.length; i++)
        {
            if (this.viewInput.options[i].value == viewId)
            {
                this.viewInput.remove(i);
            }
        }
        SelectUtils.addOption(this.viewInput, viewName, viewId);
        SelectUtils.selectTheValue(this.viewInput, viewId, 0);
        this.viewChanged();
    },
    viewUpdated: function(viewNode) {
        var viewId = viewNode.getAttribute('id');
        SelectUtils.selectTheValue(this.viewInput, viewId, 0);
        this.viewChanged();
    },
    viewRemoved : function(viewId) {
        SelectUtils.remove(this.viewInput, viewId);
        this.viewChanged();
    },
    handleViewChange: function(e) {
        this.viewChanged();
    },
    viewChanged: function(newQuery, willSaveHistory, extraSearchParams, willPoll) {
        var viewId = this.viewInput.options[this.viewInput.selectedIndex].value;
        var viewNode;
        if(viewId != 'default'){
           var viewNodes = this.viewBuilder.viewData.findChildNode('id', viewId, 'View');
           if(viewNodes.length > 0){
               viewNode = viewNodes[0]
           }
        }
        if (viewId == 'default' || (viewNode && viewNode.getAttribute("updateAllowed") == "false")) {
            this.updateViewButton.disable();
            this.removeViewButton.disable();
        }
        else {
            this.updateViewButton.enable();
            this.removeViewButton.enable();
        }
        this.activateView({id:viewId}, newQuery, willSaveHistory, extraSearchParams, willPoll);
    },
    _clearBackgroundImages:function() {
        for (var i = 0; i < this.columns.length; i++) {
            var colConfig = this.columns[i]
            if (colConfig['type'] == 'image') {
                var rowCount = this.bufferView.rowEls.length;
                for (var index = 0; index < rowCount; index++) {
                    var rowEl = this.bufferView.rowEls[index]
                    var cell = rowEl.cells[i];
                    YAHOO.util.Dom.setStyle(cell, "background", '');
                }
            }
        }
    },
    activateView : function(view, newQuery, willSaveHistory, extraSearchParams, willPoll) {
        this.showMask();
        this._clearBackgroundImages();
        var viewNode;
        if(typeof view == "object"){
            viewNode = this.viewBuilder.viewData.findChildNode('id', view.id, 'View')[0];
        }
        else{
            var viewNodes =  this.viewBuilder.viewData.findChildNode('name', view, 'View');
            viewNode = viewNodes[viewNodes.length -1]; 
        }
        var columns;
        if (viewNode) {
            this.currentView = viewNode.getAttribute("name");
            columns = this.getColumnConfigFromViewNode(viewNode);
        }
        else {
            this.currentView = 'default';
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
                    var colCssName = "#" + this.bodyId.toLowerCase() + " .rcmdb-searchgrid-col-" + columnIndex;
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
        this.sortHelper.clearSorting();
        this.columns = columns;
        for (var index = 0; index < this.columns.length; index++) {
            var columnConfig = this.columns[index]
            var header = this.headers[index];
            header.textNode.innerHTML = columnConfig.colLabel;
            var defaultColumnConfig = this.defaultColumnMap[columnConfig['attributeName']]
            var isHyperlink = false
            if (defaultColumnConfig) {
                isHyperlink = defaultColumnConfig['type'] == 'link'
                if (defaultColumnConfig['type'] == 'image') {
                    columnConfig['images'] = YAHOO.rapidjs.ObjectUtils.clone(defaultColumnConfig['images'], true)
                    columnConfig['type'] = 'image';
                }
            }
            this.setCssHyperlink(index, isHyperlink);
        }
        this.updateColumns();
        for (var index = 0; index < rowCount; index++) {
            var rowEl = this.bufferView.rowEls[index];
            this.renderRow(rowEl);
        }
        var sortedColumnIndex = this.getSortedColumnIndex();
        var sortAtt = this.getSortAttribute();
        var sortOrder = this.getSortOrder();
        if (sortedColumnIndex > -1) {
            var column = this.columns[sortedColumnIndex];
            sortAtt = column['attributeName'];
            sortOrder = column['sortOrder'] || 'asc';
        }
        this._setQuery(newQuery || this.currentlyExecutingQuery || '', sortAtt, sortOrder, this.getSearchClass(), extraSearchParams);
        if (willPoll !== false) {
            this.handleSearch(null, willSaveHistory);
        }
        else{
            this.hideMask();
        }
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
        var selector = "#" + this.bodyId.toLowerCase() + " .rcmdb-searchgrid-col-" + colIndex;
        YAHOO.ext.util.CSS.updateRule(selector, 'position', 'absolute');
        YAHOO.ext.util.CSS.updateRule(selector, 'display', 'none');
        this.headers[colIndex].style.display = 'none';
        this.headers[colIndex].split.style.display = 'none';
    },

    unhideColumn : function(colIndex) {
        var selector = "#" + this.bodyId.toLowerCase() + " .rcmdb-searchgrid-col-" + colIndex
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
    setQueryWithView: function(queryString, view, searchIn, title, extraParams)
    {
        if (title) {
            this.setTitle(title);
        }
        var currentView = this.viewsLoaded ? this.viewInput.options[this.viewInput.selectedIndex].text : 'default';
        if (this.searchClassesLoaded) {
            SelectUtils.selectTheValue(this.classesInput, searchIn, 0);
        }
        if (currentView != view && this.viewsLoaded) {
            SelectUtils.selectTheText(this.viewInput, view, 0);
            this.viewChanged(queryString, true, extraParams);
        }
        else {
            this._setQuery(queryString, this.getSortAttribute(), this.getSortOrder(), this.getSearchClass(), extraParams);
            this.handleSearch();
        }
    },

    getCurrentView: function() {
        return this.currentView;
    },

    handleSearch: function(e, willSaveHistory) {
        YAHOO.rapidjs.component.search.SearchGrid.superclass.handleSearch.call(this, e);
        if (willSaveHistory !== false) {
            var newHistoryState = [];
            newHistoryState[newHistoryState.length] = this.searchInput.value;
            newHistoryState[newHistoryState.length] = this.params['searchIn']
            newHistoryState[newHistoryState.length] = this.viewInput[this.viewInput.selectedIndex].value;
            this.saveHistoryChange(newHistoryState.join("!::!"));
        }
    },

    historyChanged: function(state) {
        if (state != "noAction") {
            var params = state.split("!::!");
            var queryString = params[0]
            var searchClass = params[1]
            var view = params[2]
            SelectUtils.selectTheValue(this.viewInput, view, 0);
            if (this.searchClassesLoaded) {
                SelectUtils.selectTheValue(this.classesInput, searchClass, 0);
            }
            this.viewChanged(queryString, false);
        }
    },
    getViewedProperties : function() {
        var props = [];
        var columns = this.columns;
        for (var i = 0; i < columns.length; i++) {
            var att = columns[i].attributeName.trim();
            if (att != '') {
                props.push(att);
            }
        }
        return props;
    },
    getSortedColumnIndex: function() {
        return ArrayUtils.findIndex(this.columns, function(it) {
            return it['sortBy'] === true
        })
    },
    createSortHelper : function() {
        if(this.multipleFieldSorting){
            return new YAHOO.rapidjs.component.search.SearchGridSortHelper(this, this.keyAttribute);    
        }
        else{
            return new YAHOO.rapidjs.component.search.SearchGridSingleSortHelper(this, this.keyAttribute);
        }
    }
});

YAHOO.rapidjs.component.search.SearchGridSortHelper = function(searchGrid, defaultSort) {
    YAHOO.rapidjs.component.search.SearchGridSortHelper.superclass.constructor.call(this, defaultSort);
    this.searchGrid = searchGrid
};
YAHOO.extend(YAHOO.rapidjs.component.search.SearchGridSortHelper, YAHOO.rapidjs.component.search.SortHelper, {
    clearSorting: function() {
        YAHOO.rapidjs.component.search.SearchGridSortHelper.superclass.clearSorting.call(this)
        var headers = this.searchGrid.headers;
        for (var i = 0; i < headers.length; i++) {
            var header = headers[i]
            YAHOO.util.Dom.setStyle(header.sortDesc, 'display', 'none')
            YAHOO.util.Dom.setStyle(header.sortAsc, 'display', 'none')
        }
    },
    updateHeaderStates: function() {
        var columns = this.searchGrid.columns;
        for (var i = 0; i < columns.length; i++) {
            var attName = columns[i]['attributeName']
            if (attName != '') {
                var header = this.searchGrid.headers[i];
                var sortIndex = ArrayUtils.findIndex(this.sorts, function(it) {
                    return it == attName
                });
                if (sortIndex > -1) {
                    var order = this.orders[sortIndex];
                    if (order == 'asc') {
                        YAHOO.util.Dom.setStyle(header.sortDesc, 'display', 'none')
                        YAHOO.util.Dom.setStyle(header.sortAsc, 'display', 'block')
                    }
                    else {
                        YAHOO.util.Dom.setStyle(header.sortDesc, 'display', 'block')
                        YAHOO.util.Dom.setStyle(header.sortAsc, 'display', 'none')
                    }
                }
                else {
                    YAHOO.util.Dom.setStyle(header.sortDesc, 'display', 'none')
                    YAHOO.util.Dom.setStyle(header.sortAsc, 'display', 'none')
                }
            }

        }
    },
    addSort: function(sortAtt, sortOrder) {
        YAHOO.rapidjs.component.search.SearchGridSortHelper.superclass.addSort.call(this, sortAtt, sortOrder)
        this.updateHeaderStates();
    },
    removeSort : function(sort) {
        YAHOO.rapidjs.component.search.SearchGridSortHelper.superclass.removeSort.call(this, sort)
        this.updateHeaderStates();
    },
    setSort: function(sort, order) {
        YAHOO.rapidjs.component.search.SearchGridSortHelper.superclass.setSort.call(this, sort, order)
        this.updateHeaderStates();
    },
    headerClicked : function(att) {
        var sortIndex = ArrayUtils.findIndex(this.sorts, function(it) {
            return it == att
        });
        if (sortIndex > -1) {
            var order = this.orders[sortIndex];
            if (order == 'asc') {
                this.addSort(att, 'desc')
            }
            else {
                this.removeSort(att)
            }
        }
        else {
            this.addSort(att, this.defaultOrder)
        }
    }
})

YAHOO.rapidjs.component.search.SearchGridSingleSortHelper = function(searchGrid, defaultSort) {
    YAHOO.rapidjs.component.search.SearchGridSingleSortHelper.superclass.constructor.call(this, searchGrid, defaultSort);
}

YAHOO.extend(YAHOO.rapidjs.component.search.SearchGridSingleSortHelper, YAHOO.rapidjs.component.search.SearchGridSortHelper, {
    addSort: function(sortAtt, sortOrder) {
        YAHOO.rapidjs.component.search.SearchGridSingleSortHelper.superclass.setSort.call(this, sortAtt, sortOrder)
    },
    removeSort : function(sort) {
        YAHOO.rapidjs.component.search.SearchGridSingleSortHelper.superclass.setSort.call(this, this.defaultSort, this.defaultOrder)
    },
    headerClicked : function(att) {
        var sortIndex = ArrayUtils.findIndex(this.sorts, function(it) {
            return it == att
        });
        if (sortIndex > -1) {
            var order = this.orders[sortIndex];
            if (order == 'asc') {
                this.setSort(att, 'desc')
            }
            else {
                this.setSort(att, 'asc')
            }
        }
        else {
            this.setSort(att, 'asc')
        }
    }
});
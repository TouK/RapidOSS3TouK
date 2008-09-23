YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.search');
YAHOO.rapidjs.component.search.SearchGrid = function(container, config) {
    this.columns = null;
    this.minColumnWidth = 30;
    this.fieldsUrl = null;
    YAHOO.rapidjs.component.search.SearchGrid.superclass.constructor.call(this, container, config);
};


YAHOO.lang.extend(YAHOO.rapidjs.component.search.SearchGrid, YAHOO.rapidjs.component.search.AbstractSearchList, {
    init: function() {
        this.totalColumnWidth = null;
        this.viewBuilder = null;
        this.defaultColumns = YAHOO.rapidjs.ObjectUtils.clone(this.columns, true);
        this.sortState = {header:null, direction:null};
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
        YAHOO.util.Event.addListener(this.viewInput, 'change', this.viewChanged, this, true);
        this.searchCountEl = wrps[4];
        new YAHOO.rapidjs.component.Button(wrps[5], {className:'rcmdb-searchgrid-saveButton', scope:this, click:this.handleSaveQueryClick, tooltip: 'Save Query'});
        new YAHOO.rapidjs.component.Button(wrps[6], {className:'rcmdb-searchgrid-searchButton', scope:this, click:this.handleSearch, tooltip: 'Search'});
        this.searchInput = searchInputWrp.getElementsByTagName('input')[0];
        YAHOO.util.Event.addListener(this.searchInput.form, 'keypress', this.handleInputEnter, this, true);
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
        for (var i = 0; i < this.columns.length; i++) {
            this.addHeader(i, this.columns[i].colLabel);
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

    addColumn: function(columnIndex){
        var nOfRows = this.bufferView.rowEls.length
        for (var rowIndex = 0; rowIndex < nOfRows; rowIndex++) {
            var rowEl = this.bufferView.rowEls[rowIndex];
            YAHOO.ext.DomHelper.append(rowEl.dom, {tag:'span', cls:'rcmdb-search-cell rcmdb-searchgrid-col-' + columnIndex,
                        html:'<span class="rcmdb-search-cell-value"></span>'});
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
        var data = dataNode.getAttributes();
        if (this.images) {
            for (var i = 0; i < this.images.length; i++)
            {
                var currentExpressionStr = this.images[i]['exp'];
                var evaluationResult = false;
                try {
                    evaluationResult = eval(currentExpressionStr);
                }
                catch(e) {
                }
                if (evaluationResult == true)
                {
                    var imageSrc = this.images[i]['src'];
                    YAHOO.util.Dom.getElementsByClassName('rcmdb-search-row-headermenu', 'div', rowEl.dom)[0].style.backgroundImage = 'url("' + imageSrc + '")';
                }
            }
        }
        var nOfColumns = this.columns.length;
        for (var colIndex = 1; colIndex < nOfColumns; colIndex++) {
            var colConfig = this.columns[colIndex];
            var att = colConfig.attributeName;
            var cell = rowEl.cells[colIndex];
            var valueEl = cell.firstChild;
            var value = dataNode.getAttribute(att);
            valueEl.innerHTML = (this.renderCellFunction ? this.renderCellFunction(att, value, dataNode) : value);
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
        this._showMask(this.hwrap.dom.offsetHeight, this.bwrap.dom.clientWidth, this.bwrap.dom.clientHeight);
    },

    addTools: function() {
        this.toolbar.addTool(new YAHOO.rapidjs.component.tool.LoadingTool(document.body, this));
        this.toolbar.addTool(new YAHOO.rapidjs.component.tool.SettingsTool(document.body, this));
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
        if (this.prevScrollLeft == scrollLeft)
        {
            this._verticalScrollChanged();
        }
        this.prevScrollLeft = scrollLeft;
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
        this.body.setStyle("height", height - this.header.dom.offsetHeight);
        this.bwrap.setStyle("height", height - (this.header.dom.offsetHeight + this.hwrap.getHeight()));
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
        var lastClicked = this.sortState['header'];
        this.sortState['header'] = header;
        this.sortState['direction'] = direction;
        if (lastClicked && lastClicked != header) {
            lastClicked.sortDir = null;
            this.updateHeaderSortState(lastClicked);
        }
        header.sortDir = direction;
        this.updateHeaderSortState(header);
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
    viewChanged: function() {
        var view = this.viewInput.options[this.viewInput.selectedIndex].value;
        if (view == 'default') {
            this.updateViewButton.disable();
            this.removeViewButton.disable();
        }
        else {
            this.updateViewButton.enable();
            this.removeViewButton.enable();
        }
        this.activateView(view);
    },
    activateView : function(view) {
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
                    var columnIndex = this.numberOfColumnsDrawn + index - 1;
                    this.addHeader(columnIndex, "&#160;")
                    this.addColumn(columnIndex);
                }
                this.numberOfColumnsDrawn = columns.length;
            }
            for (var index = 0; index < columnsToUnhide; index++) {
                this.unhideColumn(this.columns.length + index);
            }
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
            var cells = this.bufferView.rowEls[index].cells;
            YAHOO.util.Dom.removeClass(cells[this.columns.length -1], 'rcmdb-searchgrid-col-last');
            YAHOO.util.Dom.addClass(cells[columns.length -1], 'rcmdb-searchgrid-col-last');
        }
        this.columns = columns;
        for (var index = 0; index < this.columns.length; index++) {
            this.headers[index].textNode.innerHTML = this.columns[index].colLabel;
        }
        this.updateColumns();
        this._verticalScrollChanged();
    },
    getColumnConfigFromViewNode: function(viewNode) {
        var sortColumn = viewNode.getAttribute('defaultSortColumn');
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

    viewBuilderError:function(component, errors){
         this.events["error"].fireDirect(this, errors, true);
    },
    viewBuilderSuccess: function(){
         this.events["success"].fireDirect(this);
    }
});
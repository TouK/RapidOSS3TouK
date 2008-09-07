YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.search');
YAHOO.rapidjs.component.search.AbstractSearchList = function(container, config) {
    YAHOO.rapidjs.component.search.AbstractSearchList.superclass.constructor.call(this, container, config);
    this.searchQueryParamName = null
    this.contentPath = null;
    this.currentlyExecutingQuery = null;
    this.keyAttribute = null;
    this.rootTag = null;
    this.totalCountAttribute = null;
    this.offsetAttribute = null;
    this.maxRowsDisplayed = 100;
    this.sortOrderAttribute = null;
    this.images = null;
    this.menuItems = null;
    this.propertyMenuItems = null;
    YAHOO.ext.util.Config.apply(this, config);
    this.searchInput = null;
    this.rootNode = null;
    this.searchData = [];
    this.rowHeight = null;
    this.totalRowCount = 0;
    this.lastOffset = 0;
    this.lastSortAtt = this.keyAttribute;
    this.lastSortOrder = 'asc';
    this.params = {'offset':this.lastOffset, 'sort':this.lastSortAtt, 'order':this.lastSortOrder, 'max':this.maxRowsDisplayed};
    this.rowHeaderMenu = null;
    this.cellMenu = null;
    this.renderTask = new YAHOO.ext.util.DelayedTask(this.renderRows, this);
    this.scrollPollTask = new YAHOO.ext.util.DelayedTask(this.scrollPoll, this);
    var events = {
        'rowHeaderMenuClick' : new YAHOO.util.CustomEvent('rowHeaderMenuClick'),
        'cellMenuClick' : new YAHOO.util.CustomEvent('cellMenuClick'),
        'propertyClick' : new YAHOO.util.CustomEvent('propertyClick'),
        'rowDoubleClicked' : new YAHOO.util.CustomEvent('rowDoubleClicked')
    };
    YAHOO.ext.util.Config.apply(this.events, events);
    this.calculateRowHeight();
    this.render();
}

YAHOO.lang.extend(YAHOO.rapidjs.component.search.AbstractSearchList, YAHOO.rapidjs.component.PollingComponentContainer, {
    handleSearch: function(e) {
        var newHistoryState = [];
        this._poll();
        newHistoryState[newHistoryState.length] = this.searchInput.value;
        newHistoryState[newHistoryState.length] = this.lastSortAtt;
        newHistoryState[newHistoryState.length] = this.lastSortOrder;
        this.saveHistoryChange(newHistoryState.join("!::!"));
    },

    historyChanged: function(state) {
        if (state != "noAction") {
            var params = state.split("!::!");
            var queryString = params[0]
            var sort = params[1]
            var order = params[2]
            this._setQuery(queryString, sort, order);
            this._poll();
        }

    },

    setQuery: function(queryString, sortAtt, sortOrder)
    {
        this._setQuery(queryString, sortAtt, sortOrder)
        this.handleSearch();
    },

    _setQuery: function(queryString, sortAtt, sortOrder)
    {
        this.currentlyExecutingQuery = queryString;
        this.searchInput.value = queryString;
        this.lastSortAtt = sortAtt || this.keyAttribute;
        this.lastSortOrder = sortOrder || 'asc';
        this.offset = 0;
    },

    appendToQuery: function(query)
    {
        var queryString = this.searchInput.value + " " + query;
        this.setQuery(queryString, this.lastSortAtt, this.lastSortOrder);
    },
    handleInputEnter : function(e) {
        if ((e.type == "keypress" && e.keyCode == 13))
        {
            this.handleSearch();
        }
    },

    handleSuccess: function(response, keepExisting, removeAttribute)
    {
        var newData = new YAHOO.rapidjs.data.RapidXmlDocument(response, [this.keyAttribute]);
        var node = this.getRootNode(newData);
        if (node) {
            var rowCount = node.getAttribute(this.totalCountAttribute);
            if (rowCount != null) {
                this.totalRowCount = parseInt(rowCount, 10)
                this.showCurrentState();
            }
            var offset = node.getAttribute(this.offsetAttribute);
            if (offset != null) {
                this.lastOffset = parseInt(offset, 10)
            }
            if (this.data) {
                this.data.mergeData(node, this.keyAttribute, keepExisting, removeAttribute);
                this.refreshData();
            }
            else {
                this.data = node;
                this.loadData(node);
            }
        }
    },

    _poll: function() {
        this.currentlyExecutingQuery = this.searchInput.value;
        if (this.defaultFilter != null)
        {
            if (this.currentlyExecutingQuery.trim() != "")
            {
                this.currentlyExecutingQuery = "(" + this.currentlyExecutingQuery + ") AND " + this.defaultFilter;
            }
            else
            {
                this.currentlyExecutingQuery = this.defaultFilter;
            }
        }
        this.showMask();
        this.params['offset'] = this.offset;
        this.params[this.searchQueryParamName] = this.currentlyExecutingQuery;
        this.params['sort'] = this.lastSortAtt;
        this.params['order'] = this.lastSortOrder;
        this.poll();
    },

    loadData : function(data) {
        if (this.rootNode) {
            this.rootNode.destroy();
        }
        this.rootNode = new YAHOO.rapidjs.component.search.RootSearchNode(data, this.contentPath);
        this.updateSearchData();
        this.updateBodyHeight();
    },

    clearData: function() {
        this.totalRowCount = 0;
        this.lastOffset = 0;
        this.showCurrentState();
        if (this.rootNode && this.rootNode.xmlData) {
            var currentChildren = this.rootNode.xmlData.childNodes();
            while (currentChildren.length > 0) {
                var childNode = currentChildren[0];
                this.rootNode.xmlData.removeChild(childNode);
            }
        }
        this.refreshData();
        this.hideMask();
    },

    refreshData: function() {
        if (this.rootNode) {
            var tempNodes = [];
            var childNodes = this.rootNode.childNodes;
            var nOfChildNodes = childNodes.length;
            for (var rowIndex = 0; rowIndex < nOfChildNodes; rowIndex++) {
                var childNode = childNodes[rowIndex];
                if (childNode.isRemoved == true) {
                    childNode.destroy();
                }
                else {
                    tempNodes.push(childNode);
                }
            }
            this.rootNode.childNodes = tempNodes;
            this.updateSearchData();
        }
        this.updateBodyHeight();
    },

    updateSearchData: function() {
        this.searchData = [];
        var childNodes = this.rootNode.childNodes;
        this._sort(childNodes);
        this.searchData = this.searchData.concat(childNodes);
    },

    updateBodyHeight : function() {
        this.scrollPos.setHeight(this.totalRowCount * this.rowHeight);
        this._verticalScrollChanged();
    },

    sort:function(sortAtt, sortOrder) {
        this.lastSortAtt = sortAtt;
        this.lastSortOrder = sortOrder;
        this.showCurrentState();
        this.offset = 0;
        this._poll();
    },

    handleScroll: function() {
        var scrollLeft = this.body.dom.scrollLeft;
        if (this.prevScrollLeft == scrollLeft)
        {
            this._verticalScrollChanged();
        }
        this.prevScrollLeft = scrollLeft;
    },

    scrollPoll : function(offset) {
        this.offset = offset;
        this._poll();
    },

    _verticalScrollChanged : function() {
        var scrollTop = this.body.dom.scrollTop;
        var rowStartIndex = Math.floor(scrollTop / this.rowHeight);

        var interval = Math.floor(this.body.getHeight() / this.rowHeight);
        interval = interval + 2;
        var nOfSearchData = this.searchData.length;
        if (this.totalRowCount < rowStartIndex + interval)
        {
            rowStartIndex = this.totalRowCount - interval;
        }
        if (rowStartIndex < 0)
        {
            rowStartIndex = 0;
            interval = this.totalRowCount;
        }

        if (rowStartIndex < this.lastOffset + 2) {
            var nextOffset = (rowStartIndex + interval * 2) - this.maxRowsDisplayed;

            if (nextOffset < 0) {
                nextOffset = 0;
            }
            if (nextOffset < this.lastOffset) {
                this.renderTask.cancel();
                this.scrollPollTask.delay(100, this.scrollPoll, this, [nextOffset]);
            }
            else {
                this.scrollPollTask.cancel();
                this.renderTask.delay(100);
            }
        }
        else if ((rowStartIndex + interval) > (this.lastOffset + nOfSearchData - 3)) {
            var nextOffset = rowStartIndex - interval;
            if (nextOffset > this.lastOffset) {
                this.renderTask.cancel();
                this.scrollPollTask.delay(100, this.scrollPoll, this, [nextOffset]);
            }
            else {
                this.scrollPollTask.cancel();
                this.renderTask.delay(100);
            }
        }
        else {
            this.scrollPollTask.cancel();
            this.renderTask.delay(100);
        }

    },

    renderRows : function() {
        var scrollTop = this.body.dom.scrollTop;
        var rowStartIndex = Math.floor(scrollTop / this.rowHeight);
        var interval = Math.floor(this.body.getHeight() / this.rowHeight);
        interval = interval + 2;
        if (this.totalRowCount < rowStartIndex + interval)
        {
            rowStartIndex = this.totalRowCount - interval;
        }
        if (rowStartIndex < 0)
        {
            rowStartIndex = 0;
            interval = this.totalRowCount;
        }
        if (interval > this.bufferView.rowEls.length)
        {
            this.createEmptyRows(interval - this.bufferView.rowEls.length);
        }
        else if (interval < this.bufferView.rowEls.length)
        {
            while (this.bufferView.rowEls.length > interval) {
                var rowEl = this.bufferView.rowEls[this.bufferView.rowEls.length - 1];
                rowEl.dom.rowIndex = null;
                if (this.rowHeaderMenu.row == rowEl.dom) {
                    this.rowHeaderMenu.row = null;
                    this.rowHeaderMenu.hide();
                }
                var nOfCells = rowEl.cells.length;
                for (var cellIndex = 0; cellIndex < nOfCells; cellIndex++) {
                    var cell = rowEl.cells[cellIndex];
                    cell.propKey = null;
                    cell.propValue = null;
                    rowEl.cells[cellIndex] = null;
                }
                rowEl.cells = null;
                rowEl.remove();
                this.bufferView.rowEls.splice(this.bufferView.rowEls.length - 1, 1);
            }
        }

        if (rowStartIndex == 0)
        {
            YAHOO.util.Dom.setStyle(this.bufferPos.dom, 'display', 'none');
        }
        else
        {
            YAHOO.util.Dom.setStyle(this.bufferPos.dom, 'display', 'block');
        }

        this.bufferPos.setHeight(rowStartIndex * this.rowHeight);
        this.bufferView.setHeight(interval * this.rowHeight);

        for (var rowIndex = 0; rowIndex < interval; rowIndex++) {
            var rowEl = this.bufferView.rowEls[rowIndex];
            YAHOO.util.Dom.setStyle(rowEl.dom, 'display', 'block');
            var realRowIndex = rowStartIndex + rowIndex;
            rowEl.dom.rowIndex = realRowIndex;
            this.renderRow(rowEl);
        }
        this.hideMask();
    },

    getRowHeight : function() {
        return this.rowHeight;
    },

    showMask: function() {
        this.mask.setTop(this.header.dom.offsetHeight);
        this.mask.setWidth(this.body.dom.clientWidth);
        this.mask.setHeight(this.body.dom.clientHeight);
        YAHOO.util.Dom.setStyle(this.mask.dom, 'display', '');
        YAHOO.util.Dom.setStyle(this.maskMessage.dom, 'display', '');
        this.maskMessage.center(this.mask.dom);
    },

    hideMask: function() {
        YAHOO.util.Dom.setStyle(this.mask.dom, 'display', 'none');
        YAHOO.util.Dom.setStyle(this.maskMessage.dom, 'display', 'none');
    },

    _sort:function(arrayToBeSorted) {
        var dsc = false;
        var attribute = this.sortOrderAttribute;
        var sortType = this.asInt;
        var fn = function(node1, node2) {
            var v1 = sortType(node1.xmlData.getAttribute(attribute));
            var v2 = sortType(node2.xmlData.getAttribute(attribute));
            if (v1 < v2) {
                return dsc ? +1 : -1;
            }
            else if (v1 > v2) {
                return dsc ? -1 : +1;
            }
            else if (v1 == v2)
            {
                if (node1.indexInParent < node2.indexInParent) {
                    return -1;
                }
                else {
                    return +1;
                }
            }
            return 0;
        };
        arrayToBeSorted.sort(fn);
        for (var index = 0; index < arrayToBeSorted.length; index++) {
            var treeNode = arrayToBeSorted[index];
            treeNode.indexInParent = index;
        }
    },

    asInt : function(s) {
        var val = parseInt(String(s).replace(/,/g, ''));
        if (isNaN(val)) val = 0;
        return val;
    },

    resize : function(width, height) {
        this.body.setStyle("height", height - this.header.dom.offsetHeight);
        this._verticalScrollChanged();
    },
    getRowFromChild : function(childEl) {
        return YAHOO.rapidjs.DomUtils.getElementFromChild(childEl, 'rcmdb-search-row');
    },
    getCellFromChild : function(childEl) {
        return YAHOO.rapidjs.DomUtils.getElementFromChild(childEl, 'rcmdb-search-cell');
    },
    handleDoubleClick: function(e)
    {
        var target = YAHOO.util.Event.getTarget(e);
        var row = this.getRowFromChild(target);
        var xmlData = this.searchData[row.rowIndex - this.lastOffset].xmlData;
        if (row) {
            this.fireRowDoubleClick(xmlData, e);
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

    fireRowHeaderMenuClick: function(data, id, parentId) {
        this.events['rowHeaderMenuClick'].fireDirect(data, id, parentId);
    },
    fireCellMenuClick: function(key, value, data, id) {
        this.events['cellMenuClick'].fireDirect(key, value, data, id);
    },
    firePropertyClick: function(key, value, data) {
        this.events['propertyClick'].fireDirect(key, value, data);
    },
    fireRowDoubleClick: function(data, event) {
        this.events['rowDoubleClicked'].fireDirect(data, event);
    },
    getSortAttribute: function() {
        return this.lastSortAtt;
    },
    getSortOrder: function() {
        return this.lastSortOrder;
    },

    showCurrentState: function() {
    },

    calculateRowHeight: function(){},
    render: function(){}
});
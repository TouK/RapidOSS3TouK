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
YAHOO.rapidjs.component.search.AbstractSearchList = function(container, config) {
    YAHOO.rapidjs.component.search.AbstractSearchList.superclass.constructor.call(this, container, config);
    this.queryParameter = null;
    this.renderCellFunction = null;
    this.contentPath = null;
    this.currentlyExecutingQuery = null;
    this.keyAttribute = null;
    this.rootTag = null;
    this.maxRowsDisplayed = 100;
    this.images = null;
    this.menuItems = null;
    this.multiSelectionMenuItems = null;
    this.propertyMenuItems = null;
    this.defaultFilter = null;
    this.searchClassesUrl = null;
    this.defaultSearchClass = null;
    this.searchInEnabled = true;
    this.bringAllProperties = true;
    this.extraPropertiesToRequest = null;
    this.multipleFieldSorting = true;
    YAHOO.ext.util.Config.apply(this, config);
    this.totalCountAttribute = 'total';
    this.offsetAttribute = 'offset';
    this.sortOrderAttribute = '__sortOrder';
    this.configureTimeout(config);
    this.searchInput = null;
    this.classesInput = null;
    this.searchClassesLoaded = false;
    this.rootNode = null;
    this.searchData = [];
    this.subComponents = [];
    this.subComponentsContinuePolling = {};
    this.rowHeight = null;
    this.totalRowCount = 0;
    this.lastOffset = 0;
    this.offset = 0;
    this.sortHelper = this.createSortHelper();
    this.params = {'offset':this.lastOffset, 'sort':this.getSortAttribute(), 'order':this.getSortOrder(), 'max':this.maxRowsDisplayed, "searchIn":this.defaultSearchClass};
    this.rowHeaderMenu = null;
    this.multiSelectionMenu = null;
    this.bufferPos = null;
    this.scrollPos = null;
    this.mask = null;
    this.maskMessage = null;
    this.filtersFromOtherComponents = {};
    this.searchClassRequester = new YAHOO.rapidjs.Requester(this.searchClassesSuccess, this.processFailure, this, this.timeout)
    this.renderTask = new YAHOO.ext.util.DelayedTask(this.renderRows, this);
    this.scrollPollTask = new YAHOO.ext.util.DelayedTask(this.scrollPoll, this);
    this.handleSuccessTask = new YAHOO.ext.util.DelayedTask(this.handleSuccess, this);
    this.handleSuccessInProgress = false;
    var events = {
        'rowHeaderMenuClicked' : new YAHOO.util.CustomEvent('rowHeaderMenuClicked'),
        'multiSelectionMenuClicked' : new YAHOO.util.CustomEvent('multiSelectionMenuClicked'),
        'propertyClicked' : new YAHOO.util.CustomEvent('propertyClicked'),
        'rowDoubleClicked' : new YAHOO.util.CustomEvent('rowDoubleClicked'),
        'rowClicked' : new YAHOO.util.CustomEvent('rowClicked'),
        'selectionChanged' : new YAHOO.util.CustomEvent('selectionChanged'),
        'saveQueryClicked' : new YAHOO.util.CustomEvent('saveQueryClicked')
    };
    YAHOO.ext.util.Config.apply(this.events, events);
    this.createSubComponents();
    this.calculateRowHeight();
    this.selectionHelper = new YAHOO.rapidjs.component.SelectionHelper(this, "rcmdb-search-rowselected");
    this.init();
    this.render();
    this.events['error'].subscribe(function() {
        this.hideMask()
    }, this, true)
}

YAHOO.lang.extend(YAHOO.rapidjs.component.search.AbstractSearchList, YAHOO.rapidjs.component.PollingComponentContainer, {
    createSubComponents: function()
    {
        var isTimeRangeSelectorEnabled = this.config["timeRangeSelectorEnabled"];
        if (isTimeRangeSelectorEnabled == true)
        {
            this.subComponents[this.subComponents.length] = new YAHOO.rapidjs.component.search.TimeSelectorSubComponent(this);
        }

        for (var i = 0; i < this.subComponents.length; i++)
        {
            var subComponent = this.subComponents[i]
            this.subComponentsContinuePolling[subComponent] = false;
            subComponent.events.pollCompleted.subscribe(this.subComponentPollFinished, this, true);
            subComponent.events.pollStarted.subscribe(this.subComponentPollStarted, this, true);
        }
    },
    subComponentPollStarted: function(subComponent) {
        this.subComponentsContinuePolling[subComponent] = true;
    },
    subComponentPollFinished: function(subComponent) {
        this.subComponentsContinuePolling[subComponent] = false;
        this.hideMask();
    },

    isSubComponentsContinuePolling: function()
    {
        for (var i = 0; i < this.subComponents.length; i++)
        {
            if (this.subComponentsContinuePolling[this.subComponents[i]] == true)
            {
                return true
            }
        }
        return false;
    },
    retrieveSearchClasses: function() {
        var urlAndParams = parseURL(this.searchClassesUrl);
        if (!urlAndParams.params['format']) {
            urlAndParams.params['format'] = this.format;
        }
        this.searchClassRequester.doGetRequest(urlAndParams.url, urlAndParams.params)
    },
    searchClassesSuccess:function(response) {
        var classes = response.responseXML.getElementsByTagName("Class");
        for (var i = 0; i < classes.length; i++) {
            var className = classes[i].getAttribute('name')
            SelectUtils.addOption(this.classesInput, className, className);
        }
        SelectUtils.selectTheValue(this.classesInput, this.defaultSearchClass, 0);
        this.searchClassesLoaded = true;
    },
    handleSearch: function(e) {
        this.offset = 0;
        this.poll(true);
        this._changeScroll(0);
    },

    addFilter: function(object, filterQuery)
    {
        this.filtersFromOtherComponents[object] = filterQuery;
    },

    _changeScroll: function(scrollTop) {
        this.scrollChangedIntentially = true;
        this.getScrolledEl().dom.scrollTop = scrollTop;
    },

    historyChanged: function(state) {
        if (state != "noAction") {
            var params = state.split("!::!");
            var queryString = params[0]
            var sort = params[1]
            var order = params[2]
            var searchClass = params[3]
            this._setQuery(queryString, sort, order, searchClass);
            this.poll();
        }

    },

    setQuery: function(queryString, sortAtt, sortOrder, searchIn, extraParams)
    {
        this._setQuery(queryString, sortAtt, sortOrder, searchIn, extraParams)
        this.handleSearch();
    },

    _setQuery: function(queryString, sortAtt, sortOrder, searchIn, extraParams)
    {
        this.currentlyExecutingQuery = queryString;
        this.searchInput.value = queryString;
        this.sortHelper.setSort(sortAtt, sortOrder)
        if (this.searchClassesLoaded) {
            SelectUtils.selectTheValue(this.classesInput, searchIn, 0);
        }
        this.offset = 0;
        if (extraParams) {
            for (var extraParam in extraParams) {
                this.params[extraParam] = extraParams[extraParam]
            }
        }
    },

    appendToQuery: function(query)
    {
        var queryString = this.searchInput.value + " " + query;
        this.setQuery(queryString, this.getSortAttribute(), this.getSortOrder(), this.getSearchClass());
    },
    appendExceptQuery: function(key, value) {
        if (this.searchInput.value != "")
            this.appendToQuery("NOT " + key + ": " + value.toExactQuery());
        else
            this.appendToQuery("alias:* NOT " + key + ": " + value.toExactQuery());
    },

    handleInputEnter : function(e) {
        if ((e.type == "keypress" && e.keyCode == 13))
        {
            this.handleSearch();
        }
    },
    handleSaveQueryClick: function(e)
    {
        if (this.searchInput.value != "")
        {
            this.fireSaveQueryClick(this.searchInput.value);
        }
    },
    handleSuccess: function(response, keepExisting, removeAttribute)
    {
        if (this.handleSuccessInProgress) {
            this.handleSuccessTask.delay(50, null, null, [response, keepExisting, removeAttribute])
        }
        else {
            this.handleSuccessInProgress = true;
            var newData = new YAHOO.rapidjs.data.RapidXmlDocument(response, [this.keyAttribute]);
            var node = this.getRootNode(newData);
            if (node) {
                var rowCount = node.getAttribute(this.totalCountAttribute);
                if (rowCount != null) {
                    this.totalRowCount = parseInt(rowCount, 10)
                }
                var offset = node.getAttribute(this.offsetAttribute);
                if (offset != null) {
                    this.lastOffset = parseInt(offset, 10)
                }
                this.showCurrentState();
                if (this.data) {
                    this.data.mergeData(node, this.keyAttribute, keepExisting, removeAttribute);
                    this.refreshData();
                }
                else {
                    this.data = node;
                    this.loadData(node);
                }
            }
            this.handleSuccessInProgress = false;
        }
    },

    modifyQuery: function(query, queryToBeAppend)
    {
        if (query.trim() != "")
        {
            query = "(" + query + ") AND " + queryToBeAppend;
        }
        else
        {
            query = queryToBeAppend;
        }
        return query;
    },

    getCurrentlyExecutingQuery: function()
    {
        return this.currentlyExecutingQuery;
    },

    poll: function(showMask) {
        for (var i = 0; i < this.subComponents.length; i++)
        {
            this.subComponents[i].preparePoll();
        }
        this._poll(showMask);
        for (var i = 0; i < this.subComponents.length; i++)
        {
            this.subComponents[i].poll();
        }
    },
    doRequest: function(url, params, callback) {
        YAHOO.rapidjs.component.search.AbstractSearchList.superclass.doPostRequest.call(this, url, params, callback);
    },
    _poll: function(showMask) {
        if (this.searchInput != null)
        {
            this.currentlyExecutingQuery = this.searchInput.value;
            if (this.defaultFilter != null)
            {
                this.currentlyExecutingQuery = this.modifyQuery(this.currentlyExecutingQuery, this.defaultFilter)
            }
            for (var i in this.filtersFromOtherComponents)
            {
                var filterQuery = this.filtersFromOtherComponents[i];
                if (filterQuery)
                {
                    this.currentlyExecutingQuery = this.modifyQuery(this.currentlyExecutingQuery, filterQuery)
                }
            }
            if (showMask === true) {
                this.showMask();
            }
            this.params['offset'] = this.offset;
            this.params[this.queryParameter] = this.currentlyExecutingQuery;
            this.params['sort'] = this.getSortAttribute();
            this.params['order'] = this.getSortOrder();
            this.params['searchIn'] = this.getSearchClass();
            if (!this.bringAllProperties) {
                this.params['propertyList'] = this.getPropertyListToRequest();
            }
            YAHOO.rapidjs.component.search.AbstractSearchList.superclass.poll.call(this);
        }
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
    },

    refreshData: function() {
        if (this.rootNode) {
            var tempNodes = [];
            var childNodes = this.rootNode.childNodes;
            var nOfChildNodes = childNodes.length;
            for (var rowIndex = 0; rowIndex < nOfChildNodes; rowIndex++) {
                var childNode = childNodes[rowIndex];
                if (childNode.isRemoved == true) {
                    this.searchNodeRemoved(childNode)
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

    searchNodeRemoved: function(searchNode) {
        if (this.rowHeaderMenu.searchNode == searchNode) {
            this.rowHeaderMenu.searchNode = null;
            this.rowHeaderMenu.hide();
        }
        this.selectionHelper.nodeRemoved(searchNode)
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
        this.sortHelper.setSort(sortAtt, sortOrder)
        this._pollForSort();
    },
    addSort: function(sortAtt, sortOrder) {
        this.sortHelper.addSort(sortAtt, sortOrder)
        this._pollForSort();
    },
    removeSort : function(sortAtt) {
        this.sortHelper.removeSort(sortAtt)
        this._pollForSort();
    },
    clearSorting : function() {
        this.sortHelper.clearSorting();
        this._pollForSort();
    },
    _pollForSort : function() {
        this.offset = 0;
        this._poll(true);
    },

    handleScroll: function() {
        var scrollLeft = this.getScrolledEl().dom.scrollLeft;
        if (this.prevScrollLeft == scrollLeft && !this.scrollChangedIntentially)
        {
            this._verticalScrollChanged();
        }
        this.prevScrollLeft = scrollLeft;
        this.scrollChangedIntentially = false;
    },

    scrollPoll : function(offset) {
        this.offset = offset;
        this._poll(true);
    },

    _verticalScrollChanged : function() {
        var scrollTop = this.getScrolledEl().dom.scrollTop;
        var rowStartIndex = Math.floor(scrollTop / this.rowHeight);

        var interval = Math.floor(this.getScrolledEl().getHeight() / this.rowHeight);
        interval = interval + 2;
        var nOfSearchData = this.searchData.length;
        var currentUpperBound = rowStartIndex + interval;
        if (currentUpperBound > this.totalRowCount)
        {
            rowStartIndex = this.totalRowCount - interval;
        }
        if (rowStartIndex < 0) {
            rowStartIndex = 0;
        }
        var outOfBounds = rowStartIndex < this.lastOffset || currentUpperBound > (this.lastOffset + nOfSearchData) && this.lastOffset + nOfSearchData < this.totalRowCount;
        if (outOfBounds)
        {
            var nextOffset = rowStartIndex - Math.floor(this.maxRowsDisplayed / 2);
            if (nextOffset < 0)
                nextOffset = 0;
            this.renderTask.cancel();
            this.scrollPollTask.delay(100, this.scrollPoll, this, [nextOffset]);
        }
        else
        {
            this.scrollPollTask.cancel();
            this.renderTask.delay(100);
        }
    },

    renderRows : function() {
        var scrollTop = this.getScrolledEl().dom.scrollTop;
        var rowStartIndex = Math.floor(scrollTop / this.rowHeight);
        var interval = Math.floor(this.getScrolledEl().getHeight() / this.rowHeight);
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
                this.removeRowReferences(rowEl);
                this.selectionHelper.rowRemoved(rowEl.dom)
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
            var row = rowEl.dom
            row.rowIndex = realRowIndex;
            this.renderRow(rowEl);
            this.selectionHelper.rowRendered(row);
        }
        this.hideMask();
    },

    removeRowReferences: function(rowEl) {
        var nOfCells = rowEl.cells.length;
        for (var cellIndex = 0; cellIndex < nOfCells; cellIndex++) {
            var cell = rowEl.cells[cellIndex];
            cell.propKey = null;
            cell.propValue = null;
            rowEl.cells[cellIndex] = null;
        }
        rowEl.cells = null;
    },

    render : function() {
        var dh = YAHOO.ext.DomHelper;
        this.wrapper = dh.append(this.container, {tag: 'div', cls:this.getClassName()});
        this.header = dh.append(this.wrapper, {tag:'div'}, true);
        this.toolbar = new YAHOO.rapidjs.component.tool.ButtonToolBar(this.header.dom, {title:this.title});
        this.addTools();
        this.body = dh.append(this.wrapper, {tag: 'div', cls:'rcmdb-search-body'}, true);
        this._render();
        for (var i = 0; i < this.subComponents.length; i++)
        {
            var subComponentWrapper = dh.append(this.header.dom, {tag:'div', cls:'rcmdb-search-sub-component'});
            this.subComponents[i].render(subComponentWrapper)
        }
        this.createMask();

        var renderMenus = function(menuItems, menuType, clickFunc) {
            var menu = new YAHOO.widget.Menu(this.id + '_' + menuType + 'Menu', {position: "dynamic", autofillheight:false, minscrollheight:300});
            for (var i in menuItems) {
                var subMenu = null;
                if (menuItems[i].submenuItems)
                {
                    subMenu = new YAHOO.widget.Menu(this.id + '_' + menuType + 'Submenu_' + i, {position: "dynamic"});
                    for (var j in menuItems[i].submenuItems)
                    {

                        var subItem = subMenu.addItem({text:menuItems[i].submenuItems[j].label });
                        YAHOO.util.Event.addListener(subItem.element, "click", clickFunc, { parentKey:i, subKey:j}, this);
                    }
                }
                var item = menu.addItem({text:menuItems[i].label, submenu : subMenu });
                if (!(menuItems[i].submenuItems))
                    YAHOO.util.Event.addListener(item.element, "click", clickFunc, { parentKey:i }, this);
            }
            menu.render(document.body);
            YAHOO.rapidjs.component.OVERLAY_MANAGER.register(menu);
            return menu;
        }
        this.rowHeaderMenu = renderMenus.call(this, this.menuItems, "rowHeader", this.rowHeaderMenuItemClicked)
        this.multiSelectionMenu = renderMenus.call(this, this.multiSelectionMenuItems, "multiSelection", this.multiSelectionMenuItemClicked)
    },

    getRowHeight : function() {
        return this.rowHeight;
    },

    _showMask: function(bodyEl) {
        this.mask.show();
        this.maskMessage.show();
        this.mask.setRegion(getEl(bodyEl).getRegion());
        this.maskMessage.center(this.mask.dom);
    },

    hideMask: function() {
        if (this.isSubComponentsContinuePolling() == false && this.requester.isRequesting() == false)
        {
            this.mask.hide();
            this.maskMessage.hide();
        }
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
        this.body.setWidth(width);
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

    handleClick: function(e) {
        YAHOO.util.Event.stopEvent(e);
        var target = YAHOO.util.Event.getTarget(e);
        var row = this.getRowFromChild(target);
        var cell = this.getCellFromChild(target);
        if (row) {
            var searchNode = this.getSearchNode(row)
            var dataNode = searchNode.xmlData;
            this.fireRowClick(dataNode, e);
            this.selectionHelper.rowClicked(row, e);
            if (cell) {
                this.cellClicked(cell, row, target, e, searchNode);
            }
            else {
                this.rowClicked(row, target, e, searchNode);
            }
        }
    },

    handleContextMenu : function(e) {
        YAHOO.util.Event.stopEvent(e);
        var target = YAHOO.util.Event.getTarget(e);
        var row = this.getRowFromChild(target);
        if (row) {
            this.selectionHelper.contextMenuClicked(row, e);
        }
        var selectedNodes = this.selectionHelper.getSelectedNodes();
        if (selectedNodes.length > 0) {
            var xy = YAHOO.util.Event.getXY(e)
            this.multiSelectionMenu.cfg.setProperty("xy", xy);
            var datas = ArrayUtils.collect(selectedNodes, function(node) {
                return node.xmlData.getAttributes()
            });
            var index = 0;
            for (var i in this.multiSelectionMenuItems) {
                var menuItemConfig = this.multiSelectionMenuItems[i];
                if (menuItemConfig['visible'] != null) {
                    var params = {datas: datas, label:menuItemConfig.label, menuId:menuItemConfig.id}
                    var condRes = eval(menuItemConfig['visible']);
                    var menuItem = this.multiSelectionMenu.getItem(index);
                    if (!condRes)
                        menuItem.element.style.display = "none";
                    else
                        menuItem.element.style.display = "";

                }
                var subIndex = 0;
                for (var j in this.multiSelectionMenuItems[i].submenuItems)
                {
                    var subMenuItemConfig = this.multiSelectionMenuItems[i].submenuItems[j];
                    var submenuItem = this.multiSelectionMenu.getItem(index)._oSubmenu.getItem(subIndex);
                    if (subMenuItemConfig['visible'] != null)
                    {
                        var params = {datas: datas, label:subMenuItemConfig.label}
                        var condRes = eval(subMenuItemConfig['visible']);
                        if (!condRes)
                            submenuItem.element.style.display = "none";
                        else
                            submenuItem.element.style.display = "";
                    }
                    subIndex++;
                }
                index++;
            }
            this.multiSelectionMenu.show();
            YAHOO.rapidjs.component.OVERLAY_MANAGER.bringToTop(this.multiSelectionMenu);
        }
    },

    rowClicked: function(row, target, e, searchNode) {
        if (YAHOO.util.Dom.hasClass(target, 'rcmdb-search-row-headermenu')) {
            var dataNode = searchNode.xmlData;
            this.rowHeaderMenu.searchNode = searchNode;
            this.rowHeaderMenu.cfg.setProperty("context", [target.firstChild || target, 'tl', 'bl']);
            var index = 0;
            for (var i in this.menuItems) {
                var menuItemConfig = this.menuItems[i];
                if (menuItemConfig['visible'] != null) {
                    var params = {data: dataNode.getAttributes(), label:menuItemConfig.label, menuId:menuItemConfig.id}
                    var condRes = eval(menuItemConfig['visible']);
                    var menuItem = this.rowHeaderMenu.getItem(index);
                    if (!condRes)
                        menuItem.element.style.display = "none";
                    else
                        menuItem.element.style.display = "";

                }
                var subIndex = 0;
                for (var j in this.menuItems[i].submenuItems)
                {
                    var subMenuItemConfig = this.menuItems[i].submenuItems[j];
                    var submenuItem = this.rowHeaderMenu.getItem(index)._oSubmenu.getItem(subIndex);
                    if (subMenuItemConfig['visible'] != null)
                    {
                        var params = {data: dataNode.getAttributes(), label:subMenuItemConfig.label}
                        var condRes = eval(subMenuItemConfig['visible']);
                        if (!condRes)
                            submenuItem.element.style.display = "none";
                        else
                            submenuItem.element.style.display = "";
                    }
                    subIndex++;
                }
                index++;
            }
            this.rowHeaderMenu.show();
            YAHOO.rapidjs.component.OVERLAY_MANAGER.bringToTop(this.rowHeaderMenu);
        }
    },
    cellClicked: function(cell, row, target, e, searchNode) {
        this.firePropertyClick(cell.propKey, cell.propValue, searchNode.xmlData);
    },

    rowHeaderMenuItemClicked: function(eventType, params) {
        var id;
        var parentKey = params.parentKey;
        if (params.subKey != null)
        {
            var subKey = params.subKey;
            id = this.menuItems[parentKey].submenuItems[subKey].id;
        }
        else
        {
            id = this.menuItems[parentKey].id;
        }
        var parentId = this.menuItems[parentKey].id;
        var searchNode = this.rowHeaderMenu.searchNode;
        this.rowHeaderMenu.searchNode = null;
        var xmlData = searchNode.xmlData;
        this.fireRowHeaderMenuClick(xmlData, id, parentId);
    },
    multiSelectionMenuItemClicked: function(eventType, params) {
        var id;
        var parentKey = params.parentKey;
        if (params.subKey != null)
        {
            var subKey = params.subKey;
            id = this.multiSelectionMenuItems[parentKey].submenuItems[subKey].id;
        }
        else
        {
            id = this.multiSelectionMenuItems[parentKey].id;
        }
        var parentId = this.multiSelectionMenuItems[parentKey].id;
        var xmlDatas = ArrayUtils.collect(this.selectionHelper.getSelectedNodes(), function(item) {
            return item.xmlData
        });
        this.fireMultiSelectionMenuClick(xmlDatas, id, parentId);
    },

    fireRowHeaderMenuClick: function(data, id, parentId) {
        this.events['rowHeaderMenuClicked'].fireDirect(data, id, parentId);
    },
    fireMultiSelectionMenuClick: function(datas, id, parentId) {
        this.events['multiSelectionMenuClicked'].fireDirect(datas, id, parentId);
    },
    firePropertyClick: function(key, value, data) {
        this.events['propertyClicked'].fireDirect(key, value, data);
    },
    fireRowDoubleClick: function(data, event) {
        this.events['rowDoubleClicked'].fireDirect(data, event);
    },
    fireRowClick: function(data, event) {
        this.events['rowClicked'].fireDirect(data, event);
    },
    fireSelectionChange: function(nodes, event) {
        this.events['selectionChanged'].fireDirect(ArrayUtils.collect(nodes, function(node) {
            return node.xmlData
        }), event);
    },
    fireSaveQueryClick: function(query) {
        this.events['saveQueryClicked'].fireDirect(query);
    },
    getSortAttribute: function() {
        return this.sortHelper.getSort();
    },
    getSortOrder: function() {
        return this.sortHelper.getOrder();
    },

    getVisibleDataQueryParams: function() {
        var params = YAHOO.rapidjs.ObjectUtils.clone(this.params, true);
        var indexOfQm = this.url.indexOf("?");
        if (indexOfQm > -1 && this.url.length > indexOfQm + 1) {
            var postdata = this.url.substr(indexOfQm + 1)
            var keyValues = postdata.split("&");
            for (var index = 0; index < keyValues.length; index++) {
                var keyValue = keyValues[index];
                var keyValueArray = keyValue.split("=");
                params[keyValueArray[0]] = keyValueArray[1];
            }
        }
        var offset = 0;
        var max = this.bufferView.rowEls.length;
        if (this.bufferView.rowEls.length > 0) {
            offset = this.bufferView.rowEls[0].dom.rowIndex;
        }
        params['sort'] = this.getSortAttribute();
        params['order'] = this.getSortOrder();
        params['query'] = this.currentlyExecutingQuery || '';
        params['offset'] = offset;
        params['max'] = max;
        if (!this.bringAllProperties) {
            params['propertyList'] = this.getPropertyListToRequest();
        }
        return params;
    },

    getSearchClass:function() {
        if (this.searchClassesLoaded) {
            return this.classesInput.options[this.classesInput.selectedIndex].value;
        }
        return this.defaultSearchClass;
    },
    configureTimeout: function(config) {
        YAHOO.rapidjs.component.search.AbstractSearchList.superclass.configureTimeout.call(this, config);
        if (this.searchClassRequester)
        {
            this.searchClassRequester.timeout = this.timeout;
        }
    },
    getPropertyListToRequest : function() {
        var viewedProps = this.getViewedProperties();
        if (this.extraPropertiesToRequest && this.extraPropertiesToRequest != '') {
            viewedProps = viewedProps.concat(this.extraPropertiesToRequest.split(','));
        }
        var propsMap = {};
        for (var index = 0; index < viewedProps.length; index++) {
            var prop = viewedProps[index];
            propsMap[prop] = prop;
        }
        viewedProps = [];
        for (var prop in propsMap) {
            viewedProps.push(prop);
        }
        return viewedProps.join(',');
    },
    getSearchNode: function(row) {
        return this.searchData[row.rowIndex - this.lastOffset]
    },
    getNodeFromRow: function(row) {
        return this.getSearchNode(row)
    },
    getRowsInRange: function(startIndex, endIndex) {
        var rows = [];
        var rowEls = this.bufferView.rowEls;
        if (rowEls.length > 0) {
            if (startIndex < 0) {
                startIndex = rowEls[0].dom.rowIndex;
            }
            for (var i = 0; i < rowEls.length; i++) {
                var row = rowEls[i].dom;
                var rowIndex = row.rowIndex;
                if (rowIndex > endIndex)break;
                else if (rowIndex < startIndex) continue;
                else {
                    rows.push(row);
                }
            }
        }
        return rows;
    },
    getViewedProperties : function() {
        return [];
    },
    createSortHelper : function() {
        if (this.multipleFieldSorting) {
            return new YAHOO.rapidjs.component.search.SortHelper(this.keyAttribute);
        }
        else {
            return new YAHOO.rapidjs.component.search.SingleSortHelper(this.keyAttribute);
        }

    },
    showCurrentState: function() {
    },
    calculateRowHeight: function() {
    },
    _render: function() {
    },
    addTools:function() {
    },
    getClassName: function() {
    },
    init: function() {
    },

    createMask:function() {
    },
    showMask: function() {
    },
    getScrolledEl: function() {
    }
});

YAHOO.rapidjs.component.search.SortHelper = function(defaultSort) {
    this.defaultSort = defaultSort;
    this.defaultOrder = 'asc';
    this.sortString = defaultSort;
    this.orderString = this.defaultOrder;
    this.sorts = [];
    this.orders = [];
};

YAHOO.rapidjs.component.search.SortHelper.prototype = {
    getSort: function() {
        return this.sortString;
    },
    getOrder: function() {
        return this.orderString;
    },
    clearSorting: function() {
        this.sorts = [];
        this.orders = [];
        this.sortString = this.defaultSort;
        this.orderString = this.defaultOrder;
    },
    setSort: function(sortAtt, sortOrder) {
        sortAtt = sortAtt || this.defaultSort;
        sortOrder = sortOrder || this.defaultOrder;
        if (this.sortString != sortAtt || this.orderString != sortOrder) {
            this.sorts = sortAtt.split(',')
            this.orders = sortOrder.split(',')
            if (this.orders.length < this.sorts.length) {
                while (this.orders.length != this.sorts.length) {
                    this.orders.push(this.defaultOrder);
                }
            }
            this.sortString = sortAtt;
            this.orderString = sortOrder;
        }
    },
    addSort : function(sort, order) {
        var index = ArrayUtils.findIndex(this.sorts, function(it) {
            return sort == it
        })
        if (index > -1) {
            this.sorts.splice(index, 1, sort)
            this.orders.splice(index, 1, order)
        }
        else {
            this.sorts.push(sort);
            this.orders.push(order);
        }
        this.sortString = this.sorts.join(',')
        this.orderString = this.orders.join(',')
    },
    removeSort: function(sort) {
        var index = ArrayUtils.findIndex(this.sorts, function(it) {
            return sort == it
        })
        if (index > -1) {
            this.sorts.splice(index, 1)
            this.orders.splice(index, 1)
            this.sortString = this.sorts.length > 0 ? this.sorts.join(',') : this.defaultSort
            this.orderString = this.orders.length > 0 ? this.orders.join(',') : 'asc'
        }

    }
}

YAHOO.rapidjs.component.search.SingleSortHelper = function(defaultSort) {
    YAHOO.rapidjs.component.search.SingleSortHelper.superclass.constructor.call(this, defaultSort);
}

YAHOO.extend(YAHOO.rapidjs.component.search.SingleSortHelper, YAHOO.rapidjs.component.search.SortHelper, {
    addSort: function(sortAtt, sortOrder) {
        YAHOO.rapidjs.component.search.SingleSortHelper.superclass.setSort.call(this, sortAtt, sortOrder)
    },
    removeSort : function(sort) {
        YAHOO.rapidjs.component.search.SingleSortHelper.superclass.setSort.call(this, this.defaultSort, this.defaultOrder)
    }
});
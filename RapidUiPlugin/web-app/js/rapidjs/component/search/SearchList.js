YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.search');
YAHOO.rapidjs.component.search.SearchList = function(container, config) {
    YAHOO.rapidjs.component.search.SearchList.superclass.constructor.call(this, container, config);
    this.saveQueryFunction = null
    this.searchQueryParamName = null
    this.contentPath = null;
    this.currentlyExecutingQuery = null;
    this.keyAttribute = null;
    this.rootTag = null;
    this.totalCountAttribute = null;
    this.offsetAttribute = null;
    this.fields = null;
    this.titleAttribute = null;
    this.maxRowsDisplayed = 200;
    this.lineSize = 4;
    this.sortOrderAttribute = null;
    this.renderCellFunction = null;
    this.images = null;
    YAHOO.ext.util.Config.apply(this, config);
    this.rootNode = null;
    this.container = container;
    this.searchData = [];
    this.rowHeight = null;
    this.totalRowCount = 0;
    this.lastOffset = 0;
    this.lastSortAtt = this.keyAttribute;
    this.lastSortOrder = 'asc';
    this.params = {'offset':this.lastOffset, 'sort':this.lastSortAtt, 'order':this.lastSortOrder, 'max':this.maxRowsDisplayed};
    this.rowHeaderMenu = null;
    this.cellMenu = null;
    this.rowHeaderAttribute = config.rowHeaderAttribute;
    this.renderTask = new YAHOO.ext.util.DelayedTask(this.renderRows, this);
    this.scrollPollTask = new YAHOO.ext.util.DelayedTask(this.scrollPoll, this);
    var events = {
        'rowHeaderMenuClick' : new YAHOO.util.CustomEvent('rowHeaderMenuClick'),
        'cellMenuClick' : new YAHOO.util.CustomEvent('cellMenuClick'),
        'rowHeaderClick' : new YAHOO.util.CustomEvent('rowHeaderClick'),
        'propertyClick' : new YAHOO.util.CustomEvent('propertyClick')
    };
    YAHOO.ext.util.Config.apply(this.events, events);
    this.calculateRowHeight();
    this.render();

    this.menuItems = config.menuItems;
    this.menuItemUrlParamName = "id";
    this.propertyMenuItems = config.propertyMenuItems;
};


YAHOO.lang.extend(YAHOO.rapidjs.component.search.SearchList, YAHOO.rapidjs.component.PollingComponentContainer, {

    scrollPoll : function(offset) {
        this.showMask();
        this.params['offset'] = offset;
        this.params['sort'] = this.lastSortAtt;
        this.params['order'] = this.lastSortOrder;
        this.poll();
    },

    setQuery: function(queryString)
    {
        this.currentlyExecutingQuery = queryString;
        this.searchBox.dom.getElementsByTagName('input')[0].value = queryString;
        this.showMask();
        this.params[this.searchQueryParamName] = this.currentlyExecutingQuery;
        this.poll();
    },

    appendToQuery: function(query)
    {
        this.currentlyExecutingQuery = this.searchBox.dom.getElementsByTagName('input')[0].value + " " + query;
        this.searchBox.dom.getElementsByTagName('input')[0].value = this.currentlyExecutingQuery;
        this.showMask();
        this.params[this.searchQueryParamName] = this.currentlyExecutingQuery;
        this.poll();
    },
    handleInputEnter : function(e) {
        if ((e.type == "keypress" && e.keyCode == 13))
        {
            this.handleSearchClick();
        }
    },

    handleSearchClick: function(e) {
        this.currentlyExecutingQuery = this.searchBox.dom.getElementsByTagName('input')[0].value;
        this.showMask();
        this.params[this.searchQueryParamName] = this.currentlyExecutingQuery;
        this.poll();
    },
    sort:function(sortAtt, sortOrder) {
        this.showMask();
        this.lastSortAtt = sortAtt;
        this.lastSortOrder = sortOrder;
        this.params['sort'] = this.lastSortAtt;
        this.params['order'] = this.lastSortOrder;
        this.poll(this.lastOffset, sortAtt, sortOrder);
    },

    handleSuccess: function(response, keepExisting, removeAttribute)
    {
        var newData = new YAHOO.rapidjs.data.RapidXmlDocument(response, [this.keyAttribute]);
        var node = this.getRootNode(newData);
        if (node) {
            var rowCount = node.getAttribute(this.totalCountAttribute);
            if (rowCount != null) {
                this.totalRowCount = parseInt(rowCount, 10)
                this.searchBox.dom.getElementsByTagName('label')[0].innerHTML = "Count: " + this.totalRowCount;
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

    clearData: function() {
        this.totalRowCount = 0;
        this.searchBox.dom.getElementsByTagName('label')[0].innerHTML = "Count: " + this.totalRowCount;
        this.lastOffset = 0;
        if (this.rootNode && this.rootNode.xmlData) {
            var currentChildren = this.rootNode.xmlData.childNodes();
            while (currentChildren.length > 0) {
                var childNode = currentChildren[0];
                this.rootNode.xmlData.removeChild(childNode);
            }
        }
        this.refreshData();
    },
    handleSaveQueryClick: function(e)
    {
        if (this.searchBox.dom.getElementsByTagName('input')[0].value != "")
        {
            this.saveQueryFunction(this.searchBox.dom.getElementsByTagName('input')[0].value);
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

    render : function() {
        var dh = YAHOO.ext.DomHelper;
        this.wrapper = dh.append(this.container, {tag: 'div', cls:'rcmdb-search'});
        this.header = dh.append(this.wrapper, {tag:'div'}, true);
        this.toolbar = new YAHOO.rapidjs.component.tool.ButtonToolBar(this.header.dom, {title:this.title});
        this.toolbar.addTool(new YAHOO.rapidjs.component.tool.LoadingTool(document.body, this));
        this.searchBox = dh.append(this.header.dom, {tag: 'div', cls:'rcmdb-search-box',
            html:'<div><form action="javascript:void(0)"><table><tbody>' +
                 '<tr>' +
                 '<td  width="93%"><input type="text" style="width:100%;"/></td>' +
                 '<td><button type="button">Search</button></td>' +
                 '<td  width="100%"><a href="#">Save Query</a></td>' +
                 '</tr>' +
                 '</tbody></table></form></div>' +
                 '<div>' +
                 '<table align="right">' +
                 '<tr>' +
                 '<td align="right"><label for="count"> </label></td>' +
                 '<td width="5px"/>' +
                 '<td width="5px"/>' +
                 '<td><span>Line Size:</span><select><option value="1">1</option><option value="2">2</option>' +
                 '<option value="3">3</option><option value="4">4</option><option value="5">5</option><option value="6">6</option>' +
                 '<option value="7">7</option><option value="8">8</option></select>' +
                 '</td>' +
                 '</tr>' +
                 '</table>' +
                 '</div>'}, true);
        this.lineSizeSelector = this.searchBox.dom.getElementsByTagName('select')[0];
        SelectUtils.selectTheValue(this.lineSizeSelector, this.lineSize, 0);

        this.body = dh.append(this.wrapper, {tag: 'div', cls:'rcmdb-search-body'}, true);
        this.scrollPos = dh.append(this.body.dom, {tag: 'div'}, true);
        this.bufferPos = dh.append(this.scrollPos.dom, {tag:'div'}, true);
        this.bufferView = dh.append(this.scrollPos.dom, {tag:'div'}, true);
        this.bufferView.rowEls = [];
        this.mask = dh.append(this.wrapper, {tag:'div', cls:'rcmdb-search-mask', html:'Loading..', style:'text-align:center;'}, true);
        this.hideMask();
        YAHOO.util.Event.addListener(this.searchBox.dom.getElementsByTagName('button')[0], 'click', this.handleSearchClick, this, true);
        YAHOO.util.Event.addListener(this.searchBox.dom.getElementsByTagName('input')[0], 'keypress', this.handleInputEnter, this, true);
        YAHOO.util.Event.addListener(this.searchBox.dom.getElementsByTagName('a')[0], 'click', this.handleSaveQueryClick, this, true);
        YAHOO.util.Event.addListener(this.lineSizeSelector, 'change', this.handleLineSizeChange, this, true);
        YAHOO.util.Event.addListener(this.body.dom, 'scroll', this.handleScroll, this, true);
        YAHOO.util.Event.addListener(this.scrollPos.dom, 'click', this.handleClick, this, true);

        this.rowHeaderMenu = new YAHOO.widget.Menu(this.id + '_rowHeaderMenu', {position: "dynamic"});

        for (var i in this.menuItems) {
            if (this.menuItems[i].submenuItems)
            {
                var subMenu = new YAHOO.widget.Menu(this.id + '_rowHeaderSubmenu_' + i, {position: "dynamic"});
                for (var j in this.menuItems[i].submenuItems)
                {

                    var subItem = subMenu.addItem({text:this.menuItems[i].submenuItems[j].label });
                    YAHOO.util.Event.addListener(subItem.element, "click", this.rowHeaderMenuItemClicked, { parentKey:i, subKey:j}, this);
                }
            }
            var item = this.rowHeaderMenu.addItem({text:this.menuItems[i].label, submenu : subMenu });
            if (!(this.menuItems[i].submenuItems))
                YAHOO.util.Event.addListener(item.element, "click", this.rowHeaderMenuItemClicked, { parentKey:i }, this);
        }


        this.rowHeaderMenu.render(document.body);

        this.cellMenu = new YAHOO.widget.Menu(this.id + '_cellMenu', {position: "dynamic"});

        for (var i in this.propertyMenuItems) {
            var item = this.cellMenu.addItem({text:this.propertyMenuItems[i].label });
            YAHOO.util.Event.addListener(item.element, "click", this.cellMenuItemClicked, i, this);

        }
        this.cellMenu.render(document.body);


    },

    updateSearchData: function() {
        this.searchData = [];
        var childNodes = this.rootNode.childNodes;
        this._sort(childNodes);
        this.searchData = this.searchData.concat(childNodes);
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

    handleScroll: function() {
        var scrollLeft = this.body.dom.scrollLeft;
        if (this.prevScrollLeft == scrollLeft)
        {
            this._verticalScrollChanged();
        }
        this.prevScrollLeft = scrollLeft;
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

    createEmptyRows : function(rowCount) {
        var innerHtml = '';
        if (this.fields) {
            for (var fieldIndex = 0; fieldIndex < this.fields.length; fieldIndex++) {
                innerHtml += '<div class="rcmdb-search-cell">' +
                             '<span class="rcmdb-search-cell-key"></span>' +
                             '<a href="#" class="rcmdb-search-cell-value"></a>' +
                             '<a class="rcmdb-search-cell-menu">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> | ' +
                             '</div>';
            }
        }
        if (innerHtml.length > 0) {
            innerHtml = innerHtml.substring(0, innerHtml.length - 9) + '</div>';
        }
        for (var rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            var rowHtml = '<table><tr>' + (this.images ? '<td width="0%"><div class="rcmdb-search-row-headerimage"></div></td>' : '') +
                          '<td width="0%"><div class="rcmdb-search-row-headermenu"></div></td>' +
                          '<td width="100%">';
            if (this.rowHeaderAttribute != null)
            {
                rowHtml += '<div class="rcmdb-search-rowheader"><a href="#" class="rcmdb-search-rowheader-value"></a></div>';
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
            var searchNode = this.searchData[rowEl.dom.rowIndex - this.lastOffset];
            var dataNode = searchNode.xmlData;
            if (this.images) {
                var data = dataNode.getAttributes();
                for (var i = 0; i < this.images.length; i++)
                {
                    var currentExpressionStr = this.images[i]['exp'];
                    var evaluationResult = eval(currentExpressionStr);
                    if (evaluationResult == true)
                    {
                        var imageSrc = this.images[i]['src'];
                        YAHOO.util.Dom.getElementsByClassName('rcmdb-search-row-headerimage', 'div', rowEl.dom)[0].style.backgroundImage = 'url("' + imageSrc + '")';
                    }
                }
            }
            var nOfFields = this.fields.length;
            if (this.rowHeaderAttribute != null)
            {
                rowEl.header.innerHTML = dataNode.getAttribute(this.rowHeaderAttribute);
            }
            for (var fieldIndex = 0; fieldIndex < nOfFields; fieldIndex++) {
                var att = this.fields[fieldIndex];
                var cell = rowEl.cells[fieldIndex];
                var keyEl = cell.firstChild;
                var valueEl = keyEl.nextSibling;
                var value = dataNode.getAttribute(att);
                valueEl.innerHTML = (this.renderCellFunction ? this.renderCellFunction(att, value, dataNode) : value);
                keyEl.innerHTML = att + '=';
                cell.propKey = att;
                cell.propValue = value;
            }
        }

    },
    handleClick: function(e) {
        var target = YAHOO.util.Event.getTarget(e);
        var row = this.getRowFromChild(target);
        if (row) {
            if (YAHOO.util.Dom.hasClass(target, 'rcmdb-search-row-headermenu')) {
                this.rowHeaderMenu.row = row;
                this.rowHeaderMenu.cfg.setProperty("context", [target, 'tl', 'bl']);
                var searchNode = this.searchData[row.rowIndex - this.lastOffset];
                var dataNode = searchNode.xmlData;
                var index = 0;

                for (var i in this.menuItems) {
                    if (this.menuItems[i].condition != null) {
                        var condRes = this.menuItems[i].condition(dataNode);
                        var menuItem = this.rowHeaderMenu.getItem(index);
                        if (!condRes)
                            menuItem.element.style.display = "none";
                        else
                            menuItem.element.style.display = "";

                    }
                    var subIndex = 0;
                    for (var j in this.menuItems[i].submenuItems)
                    {
                        var submenuItem = this.rowHeaderMenu.getItem(index)._oSubmenu.getItem(subIndex);
                        if (this.menuItems[i].submenuItems[j].condition != null)
                        {
                            var conSub = this.menuItems[i].submenuItems[j].condition(dataNode, this.menuItems[i].submenuItems[j].label)
                            if (!conSub)
                                submenuItem.element.style.display = "none";
                            else
                                submenuItem.element.style.display = "";
                        }
                        subIndex++;
                    }
                    index++;
                }
                this.rowHeaderMenu.show();
            }
            else if (YAHOO.util.Dom.hasClass(target, 'rcmdb-search-rowheader') || YAHOO.util.Dom.hasClass(target, 'rcmdb-search-rowheader-value'))
            {
                var xmlData = this.searchData[row.rowIndex - this.lastOffset].xmlData;
                this.fireRowHeaderClicked(xmlData);
            }
            else {
                var cell = this.getCellFromChild(target);
                if (cell) {
                    var xmlData = this.searchData[row.rowIndex - this.lastOffset].xmlData;
                    if (YAHOO.util.Dom.hasClass(target, 'rcmdb-search-cell-menu')) {
                        var index = 0;
                        for (var i in this.propertyMenuItems) {
                            if (this.propertyMenuItems[i].condition != null) {
                                var value = xmlData.getAttribute(this.menuItemUrlParamName);
                                var menuItem = this.cellMenu.getItem(index);
                                var condRes = this.propertyMenuItems[i].condition(cell.propKey, cell.propValue, xmlData);
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
                    }
                    else if (YAHOO.util.Dom.hasClass(target, 'rcmdb-search-cell-value')) {
                        this.appendToQuery(cell.propKey + ":\"" + cell.propValue + "\"");
                        this.firePropertyClick(cell.propKey, cell.propValue, xmlData);
                    }
                }
            }
        }
    },



    updateBodyHeight : function() {
        this.scrollPos.setHeight(this.totalRowCount * this.rowHeight);
        this._verticalScrollChanged();
    },

    getRowHeight : function() {
        return this.rowHeight;
    },


    showMask: function() {
        this.mask.setTop(this.header.dom.offsetHeight);
        this.mask.setWidth(this.body.dom.clientWidth);
        this.mask.setHeight(this.body.dom.clientHeight);
        YAHOO.util.Dom.setStyle(this.mask.dom, 'display', '');
    },
    hideMask: function() {
        YAHOO.util.Dom.setStyle(this.mask.dom, 'display', 'none');
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

    calculateRowHeight: function() {
        var lineHeight = 20;
        var paddingTop = 0;
        var paddingBottom = 0;
        var borderTop = 0;
        var borderBottom = 0;
        var rule = YAHOO.ext.util.CSS.getRule('.rcmdb-search-row');
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

    handleLineSizeChange: function(e) {
        this.showMask();
        this.lineSize = parseInt(this.lineSizeSelector.options[this.lineSizeSelector.selectedIndex].value, 10);
        this.calculateRowHeight();
        for (var rowIndex = 0; rowIndex < this.bufferView.rowEls.length; rowIndex++) {
            this.bufferView.rowEls[rowIndex].setHeight(this.rowHeight);
        }
        this.updateBodyHeight();
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

    setSortDirection: function(propKey, isAsc)
    {
        if (isAsc) {
            this.sort(propKey, 'asc');
        }
        else {
            this.sort(propKey, 'desc');
        }
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
        var row = this.rowHeaderMenu.row;
        this.rowHeaderMenu.row = null;
        var xmlData = this.searchData[row.rowIndex - this.lastOffset].xmlData;
        this.fireRowHeaderMenuClick(xmlData, id, parentId);
    },

    fireRowHeaderMenuClick: function(data, id, parentId) {
        this.events['rowHeaderMenuClick'].fireDirect(data, id, parentId);
    },
    fireCellMenuClick: function(key, value, data, id) {
        this.events['cellMenuClick'].fireDirect(key, value, data, id);
    },
    fireRowHeaderClicked: function(data) {
        this.events['rowHeaderClick'].fireDirect(data);
    },
    firePropertyClick: function(key, value, data) {
        this.events['propertyClick'].fireDirect(key, value, data);
    }
});

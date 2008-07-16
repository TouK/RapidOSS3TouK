YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.search');
YAHOO.rapidjs.component.search.SearchList = function(container, config) {
    this.id = null;
    this.url = null;
    this.saveQueryFunction = config.saveQueryFunction;
    this.searchQueryParamName = config.searchQueryParamName;
    this.contentPath = null;
    this.currentlyExecutingQuery = null;
    this.indexAtt = null;
    this.rootTag = null;
    this.totalCountAttribute = null;
    this.offsetAttribute = null;
    this.fields = null;
    this.maxRowsDisplayed = 200;
    this.lineSize = 4;
    this.sortOrderAttribute = null;
    YAHOO.ext.util.Config.apply(this, config);
    this.rootNode = null;
    this.container = container;
    this.searchData = [];
    this.rowHeight = null;
    this.totalRowCount = 0;
    this.lastOffset = 0;
    this.lastSortAtt = this.indexAtt;
    this.lastSortOrder = 'asc';
    this.rowHeaderMenu = null;
    this.cellMenu = null;
    this.renderTask = new YAHOO.ext.util.DelayedTask(this.renderRows, this);
    this.scrollPollTask = new YAHOO.ext.util.DelayedTask(this.scrollPoll, this);
    this.events = {
        'rowHeaderMenuClick' : new YAHOO.util.CustomEvent('rowHeaderMenuClick'),
        'cellMenuClick' : new YAHOO.util.CustomEvent('cellMenuClick'),
        'propertyClick' : new YAHOO.util.CustomEvent('propertyClick')
    };
    this.calculateRowHeight();
    this.render();

    this.menuItems = config.menuItems;
    this.menuItemUrlParamName = config.menuItemUrlParamName;
};



YAHOO.rapidjs.component.search.SearchList.prototype = {
    render : function() {
        var dh = YAHOO.ext.DomHelper;


        this.wrapper = dh.append(this.container, {tag: 'div', cls:'rcmdb-search'});
        
        this.header = dh.append(this.wrapper, {tag:'div'}, true);
        this.searchBox = dh.append(this.header.dom, {tag: 'div', cls:'rcmdb-search-box',
            html:'<table><tr><td width="100%"><table width="100%"><tr><td><input type="text" style="width:100%;"/></td></tr></table></td><td><table width="215px"><tr><td><button>Search</button></td>' +
                 '<td><a href="#">Save Query</a></td><td><span>Line Size:</span><select><option value="1">1</option><option value="2">2</option>' +
                 '<option value="3">3</option><option value="4">4</option><option value="5">5</option><option value="6">6</option>' +
                 '<option value="7">7</option><option value="8">8</option></select></td></tr></table></td></tr></table>'}, true);

        this.lineSizeSelector = this.searchBox.dom.getElementsByTagName('select')[0];
        SelectUtils.selectTheValue(this.lineSizeSelector, this.lineSize, 0);

        this.body = dh.append(this.wrapper, {tag: 'div', cls:'rcmdb-search-body'}, true);
        this.scrollPos = dh.append(this.body.dom, {tag: 'div'}, true);
        this.bufferPos = dh.append(this.scrollPos.dom, {tag:'div'}, true);
        this.bufferView = dh.append(this.scrollPos.dom, {tag:'div'}, true);
        this.bufferView.rowEls = [];
        this.mask = dh.append(this.wrapper, {tag:'div', cls:'rcmdb-search-mask', html:'Loading..', style:'text-align:center;'}, true);
        this.hideMask();
        YAHOO.util.Event.addListener(this.searchBox.dom.getElementsByTagName('input')[0], 'keypress', this.handleSearchClick, this, true);
        YAHOO.util.Event.addListener(this.searchBox.dom.getElementsByTagName('button')[0], 'click', this.handleSearchClick, this, true);
        YAHOO.util.Event.addListener(this.searchBox.dom.getElementsByTagName('a')[0], 'click', this.handleSaveQueryClick, this, true);
        YAHOO.util.Event.addListener(this.lineSizeSelector, 'change', this.handleLineSizeChange, this, true);
        YAHOO.util.Event.addListener(this.body.dom, 'scroll', this.handleScroll, this, true);
        YAHOO.util.Event.addListener(this.body.dom, 'click', this.handleGridClick, this, true);
        YAHOO.util.Event.addListener(this.scrollPos.dom, 'click', this.handleClick, this, true);

        this.rowHeaderMenu = new YAHOO.widget.Menu(this.id + '_rowHeaderMenu', {position: "dynamic"});

        for (var i in this.menuItems){
            var item = this.rowHeaderMenu.addItem( {text:this.menuItems[i].label });
            YAHOO.util.Event.addListener(item.element, "click" , this.rowHeaderMenuItemClicked, i , this);
        }

        this.rowHeaderMenu.render(document.body);

        this.cellMenu = new YAHOO.widget.Menu(this.id + '_cellMenu', {position: "dynamic"});
        this.cellMenu.addItems([
            {text:'sort asc', onclick: { fn: this.cellMenuItemClicked, scope: this }},
            {text:'sort desc', onclick: { fn: this.cellMenuItemClicked, scope: this }}
        ]);
        this.cellMenu.render(document.body);
         

  },

    setQuery: function(queryString)
    {
        this.currentlyExecutingQuery = queryString;
        this.searchBox.dom.getElementsByTagName('input')[0].value = queryString;
        this.poll();
    },

    handleSaveQueryClick: function(e)
    {
        if(this.searchBox.dom.getElementsByTagName('input')[0].value != "")
        {
            this.saveQueryFunction(this.searchBox.dom.getElementsByTagName('input')[0].value);
            //alert( "Query " + escape(this.searchBox.dom.getElementsByTagName('input')[0].value) + " saved succesfully.");
        }
    },
    handleGridClick: function(e)
    {

        var dh = YAHOO.ext.DomHelper;
        var sender = (typeof( window.event ) != "undefined" ) ? e.srcElement : e.target;
        if(YAHOO.util.Dom.hasClass(sender, "rcmdb-search-cell-value") )
        {
            var key = sender.previousSibling.innerHTML;
            var value = sender.innerHTML;
            key = key.substring(0, key.length - 1);
            this.currentlyExecutingQuery = this.searchBox.dom.getElementsByTagName('input')[0].value + " " + key + ":\"" + value + "\"";
            this.searchBox.dom.getElementsByTagName('input')[0].value = this.currentlyExecutingQuery;
            this.poll();
        }

    },
    handleSearchClick: function(e) {
        if( (e.type == "keypress" && e.keyCode == 13) || (e.type == "click" ) )
        {
            this.currentlyExecutingQuery = this.searchBox.dom.getElementsByTagName('input')[0].value;
            this.poll();
        }
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
                if(this.rowHeaderMenu.row == rowEl.dom){
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
        if(this.fields){
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
            var rowEl = YAHOO.ext.DomHelper.append(this.bufferView.dom, {tag:'div', cls:'rcmdb-search-row',
                html:'<table><tr><td width="0%"><div class="rcmdb-search-row-headermenu"></div></td>' +
                     '<td width="100%"><div class="rcmdb-search-rowdata">' + innerHtml + '</div></td></tr></table>'}, true);
            this.bufferView.rowEls[this.bufferView.rowEls.length] = rowEl;
            YAHOO.util.Dom.setStyle(rowEl.dom, 'display', 'none');
            rowEl.setHeight(this.rowHeight);
            var cells = YAHOO.util.Dom.getElementsByClassName('rcmdb-search-cell', 'div', rowEl.dom)
            rowEl.cells = cells;
        }

    },

    renderRow: function(rowEl) {
        if(this.fields){
            var searchNode = this.searchData[rowEl.dom.rowIndex - this.lastOffset];
            var dataNode = searchNode.xmlData;
            var nOfFields = this.fields.length;
            for (var fieldIndex = 0; fieldIndex < nOfFields; fieldIndex++) {
                var att = this.fields[fieldIndex];
                var cell = rowEl.cells[fieldIndex];
                var keyEl = cell.firstChild;
                var valueEl = keyEl.nextSibling;
                var value = dataNode.getAttribute(att);
                valueEl.innerHTML = value;
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
                for (var i in this.menuItems){
                    if( this.menuItems[i].condition != null ){
                        var value = dataNode.getAttribute(this.menuItemUrlParamName);
                        var menuItem = this.rowHeaderMenu.getItem(index);
                        var condRes = this.menuItems[i].condition( value);
                        if( !condRes)
                            menuItem.element.style.display = "none";
                        else
                            menuItem.element.style.display = "";
                    }
                    index++;
                }
                this.rowHeaderMenu.show();
            }
            else {
                var cell = this.getCellFromChild(target);
                if (cell) {
                    if (YAHOO.util.Dom.hasClass(target, 'rcmdb-search-cell-menu')) {
                        this.cellMenu.row = row;
                        this.cellMenu.cell = cell;
                        this.cellMenu.cfg.setProperty("context", [target, 'tl', 'bl']);
                        this.cellMenu.show();
                    }
                    else if (YAHOO.util.Dom.hasClass(target, 'rcmdb-search-cell-value')) {
                         var xmlData = this.searchData[row.rowIndex - this.lastOffset].xmlData;
                         this.firepropertyClick(cell.propKey, cell.propValue, xmlData);
                    }
                }
            }
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

    updateBodyHeight : function() {
        this.scrollPos.setHeight(this.totalRowCount * this.rowHeight);
        this._verticalScrollChanged();
    },

    getRowHeight : function() {
        return this.rowHeight;
    },

    scrollPoll : function(offset) {

        this.showMask();
        this.poll(offset, this.lastSortAtt, this.lastSortOrder);
    },
    poll: function(offset, sortAtt, sortOrder) {
        if (this.lastConnection) {
            var callStatus = YAHOO.util.Connect.isCallInProgress(this.lastConnection);
            if (callStatus == true) {
                YAHOO.util.Connect.abort(this.lastConnection);
                this.lastConnection = null;
            }
        }
        var callback = {
            success: this.processSuccess,
            failure : this.processFailure,
            scope : this,
            timeout : 30000
        };
        var url;
        var params = [];
        if(this.currentlyExecutingQuery != null)
        {
            params[params.length] = this.searchQueryParamName + "="+this.currentlyExecutingQuery;
        }
        params[params.length] = 'max=' + this.maxRowsDisplayed;
        if (offset != null) {
            params[params.length] = 'offset=' + offset;
        }
        else {
            params[params.length] = 'offset=' + this.lastOffset;
        }
        params[params.length] = 'sort=' + (sortAtt || this.indexAtt);
        params[params.length] = 'order=' + (sortOrder || 'asc');
        url = this.url + '?' + params.join('&');
        this.lastConnection = YAHOO.util.Connect.asyncRequest('GET', url, callback);
    },

    processSuccess: function(response) {

        var newData = new YAHOO.rapidjs.data.RapidXmlDocument(response, this.indexAtt);
        var node = newData.getRootNode(this.rootTag);
        if (node) {
            this.totalRowCount = parseInt(node.getAttribute(this.totalCountAttribute), 10)
            this.lastOffset = parseInt(node.getAttribute(this.offsetAttribute), 10)
            if (this.data) {

                this.data.mergeData(node, this.indexAtt);
                this.refreshData();

            }
            else {
                this.data = node;
                this.loadData(node);
            }
        }
        else{
            alert('xmlde hata var');
            this.hideMask();
        }
    },

    processFailure: function(response) {
        var st = response.status;
		if(st == -1){
			alert('Request received a timeout');
		}
		else if(st == 404){
			alert('Specified url cannot be found');
		}
		else if(st == 0){
			alert('Server is not available');
		}
    },
    showMask: function() {
        //console.log("show mask");
        this.mask.setTop(this.header.dom.offsetHeight);
        this.mask.setWidth(this.body.dom.clientWidth);
        this.mask.setHeight(this.body.dom.clientHeight);
        YAHOO.util.Dom.setStyle(this.mask.dom, 'display', '');
    },
    hideMask: function() {
        //console.log("show mask");
        YAHOO.util.Dom.setStyle(this.mask.dom, 'display', 'none');
    },

    sort:function(sortAtt, sortOrder) {

        this.showMask();
        this.lastSortAtt = sortAtt;
        this.lastSortOrder = sortOrder;
        this.poll(this.lastOffset, sortAtt, sortOrder);
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
        this.body.setStyle("height",height- this.header.dom.offsetHeight);
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

    cellMenuItemClicked: function(eventType, args, menuItem){
        //var event = args[0];
        var menuItemText = menuItem.cfg.getProperty("text");
        var row = this.cellMenu.row;
        var cell = this.cellMenu.cell;
        this.cellMenu.row = null;
        this.cellMenu.cell = null;
        var xmlData = this.searchData[row.rowIndex - this.lastOffset].xmlData;
        if(menuItemText == 'sort asc'){
            this.sort(cell.propKey, 'asc');
        }
        else if(menuItemText == 'sort desc'){
            this.sort(cell.propKey, 'desc');
        }
        this.firecellMenuClick(cell.propKey, cell.propValue, xmlData, menuItemText);
    },

    rowHeaderMenuItemClicked: function(eventType, key){
        //var event = args[0];
        var id = this.menuItems[key].id;
        var row = this.rowHeaderMenu.row;
        this.rowHeaderMenu.row = null;
        var xmlData = this.searchData[row.rowIndex - this.lastOffset].xmlData;
        this.firerowHeaderMenuClick(xmlData, id);
    },

    firerowHeaderMenuClick: function(data, id){
        this.events['rowHeaderMenuClick'].fireDirect(data, id);
    },
    firecellMenuClick: function(key, value, data, menuText){
        this.events['cellMenuClick'].fireDirect(key, value, data, menuText);
    },
    firepropertyClick: function(key, value, data){
        this.events['propertyClick'].fireDirect(key, value, data);
    }


}

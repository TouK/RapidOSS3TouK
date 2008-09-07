YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.search');
YAHOO.rapidjs.component.search.SearchGrid = function(container, config) {
    YAHOO.rapidjs.component.search.SearchList.superclass.constructor.call(this, container, config);
};


YAHOO.lang.extend(YAHOO.rapidjs.component.search.SearchGrid, YAHOO.rapidjs.component.search.AbstractSearchList, {



    render : function() {
        var dh = YAHOO.ext.DomHelper;
        this.wrapper = dh.append(this.container, {tag: 'div', cls:'rcmdb-search'});

        this.header = dh.append(this.wrapper, {tag:'div'}, true);
        this.toolbar = new YAHOO.rapidjs.component.tool.ButtonToolBar(this.header.dom, {title:this.title});
        this.toolbar.addTool(new YAHOO.rapidjs.component.tool.LoadingTool(document.body, this));
        this.toolbar.addTool(new YAHOO.rapidjs.component.tool.SearchListSettingsTool(document.body, this));
        this.toolbar.addTool(new YAHOO.rapidjs.component.tool.ErrorTool(document.body, this));
        this.searchBox = dh.append(this.header.dom, {tag: 'div', cls:'rcmdb-search-box',
            html:'<div><form action="javascript:void(0)" style="overflow:auto;"><table><tbody>' +
                 '<tr>' +
                 '<td  width="93%"><input type="textbox" style="width:100%;" name="search"/></td>' +
                 '<td><div class="rcmdb-search-searchbutton"></div></td>' +
                 '<td  width="100%"><div class="rcmdb-search-savequery"></div></td>' +
                 '<td  width="0%"><div class="rcmdb-search-count"></div></td>' +
                 '<td  width="0%"><div class="rcmdb-search-sortOrder"></div></td>' +
                 '</tr>' +
                 '</tbody></table></form></div>'}, true);

        this.searchInput = this.searchBox.dom.getElementsByTagName('input')[0];
        this.body = dh.append(this.wrapper, {tag: 'div', cls:'rcmdb-search-body'}, true);
        this.scrollPos = dh.append(this.body.dom, {tag: 'div'}, true);
        this.bufferPos = dh.append(this.scrollPos.dom, {tag:'div'}, true);
        this.bufferView = dh.append(this.scrollPos.dom, {tag:'div'}, true);
        this.bufferView.rowEls = [];
        this.mask = dh.append(this.wrapper, {tag:'div', cls:'rcmdb-search-mask'}, true);
        this.maskMessage = dh.append(this.wrapper, {tag:'div', cls:'rcmdb-search-mask-loadingwrp', html:'<div class="rcmdb-search-mask-loading">Loading...</div>'}, true)
        this.hideMask();
        var searchButton = YAHOO.ext.Element.get(YAHOO.util.Dom.getElementsByClassName('rcmdb-search-searchbutton', 'div', this.searchBox.dom)[0]);
        searchButton.addClassOnOver('rcmdb-search-searchbutton-hover');
        YAHOO.util.Event.addListener(searchButton.dom, 'click', this.handleSearch, this, true);
        var saveQueryButton = YAHOO.ext.Element.get(YAHOO.util.Dom.getElementsByClassName('rcmdb-search-savequery', 'div', this.searchBox.dom)[0]);
        saveQueryButton.addClassOnOver('rcmdb-search-savequery-hover');
        YAHOO.util.Event.addListener(saveQueryButton.dom, 'click', this.handleSaveQueryClick, this, true);
        YAHOO.util.Event.addListener(this.searchInput.form, 'keypress', this.handleInputEnter, this, true);
        YAHOO.util.Event.addListener(this.body.dom, 'scroll', this.handleScroll, this, true);
        YAHOO.util.Event.addListener(this.scrollPos.dom, 'click', this.handleClick, this, true);
        YAHOO.util.Event.addListener(this.scrollPos.dom, 'dblclick', this.handleDoubleClick, this, true);

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
            this.fields = this.defaultFields;
            this.defaultFields = null;
            this.maxRowCellLength = this.fields.length;
        }

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
            var data = dataNode.getAttributes();
            if (this.defaultFields)
            {
                for (var i = 0; i < this.fields.length; i++)
                {
                    var currentExpressionStr = this.fields[i]['exp'];
                    var evaluationResult = eval(currentExpressionStr);
                    if (evaluationResult)
                        insertedFields = this.fields[i]['fields'];
                }
                if (!insertedFields)
                    insertedFields = this.defaultFields;
            }
            else
                insertedFields = this.fields;
            if (this.images) {
                for (var i = 0; i < this.images.length; i++)
                {
                    var currentExpressionStr = this.images[i]['exp'];
                    var evaluationResult = eval(currentExpressionStr);
                    if (evaluationResult == true)
                    {
                        var imageSrc = this.images[i]['src'];
                        YAHOO.util.Dom.getElementsByClassName('rcmdb-search-row-headermenu', 'div', rowEl.dom)[0].style.backgroundImage = 'url("' + imageSrc + '")';
                    }
                }
            }
            var nOfFields = insertedFields.length;
            if (this.rowHeaderAttribute != null)
            {
                rowEl.header.innerHTML = dataNode.getAttribute(this.rowHeaderAttribute);
            }
            for (var fieldIndex = 0; fieldIndex < nOfFields; fieldIndex++) {
                var att = insertedFields[fieldIndex];
                var cell = rowEl.cells[fieldIndex];
                var keyEl = cell.firstChild;
                var valueEl = keyEl.nextSibling;
                var value = dataNode.getAttribute(att);
                valueEl.innerHTML = (this.renderCellFunction ? this.renderCellFunction(att, value, dataNode) : value);
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

    },

    showCurrentState: function() {
    },


    calculateRowHeight: function() {
    }

});
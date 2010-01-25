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
YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.treegrid');
YAHOO.rapidjs.component.treegrid.TreeGridView = function(container, config) {
    this.container = container;
    this.columns = null;
    this.headers = [];
    this.rootNode = null;
    this.rootImages = null;
    this.contentPath = null;
    this.hideAttribute = null;
    this.expandNodeAttribute = null;
    this.menuItems = null;
    this.multiSelectionMenuItems = null;
    this.expandedNodes = [];
    this.events = {
        'selectionchanged' : new YAHOO.util.CustomEvent('selectionchanged'),
        'treenodeclicked' : new YAHOO.util.CustomEvent('treenodeclicked'),
        'rowMenuClick' : new YAHOO.util.CustomEvent('rowMenuClick'),
        'multiSelectionMenuClicked' : new YAHOO.util.CustomEvent('multiSelectionMenuClicked')
    };
    YAHOO.ext.util.Config.apply(this, config);
    this.renderTask = new YAHOO.ext.util.DelayedTask(this.renderRows, this);
    this.sortState = {header:null, direction:null};
    this.isSortingDisabled = false;
    this.selectedNode = null;
    this.selectionHelper = new YAHOO.rapidjs.component.SelectionHelper(this, "r-tree-rowselected");
};

YAHOO.rapidjs.component.treegrid.TreeGridView.prototype = {
    render:function() {
        var dh = YAHOO.ext.DomHelper;
        this.wrapper = dh.append(this.container, {tag: 'div', cls:'r-tree'});
        this.header = dh.append(this.wrapper, {tag: 'div', cls:'r-tree-header',
            html:'<div><table cellspacing="0" cellpadding="0" class="r-tree-headertable"><tbody>' +
                 '<tr class="r-tree-headerrow"></tr></tbody></table></div>'});
        this.headerInnerDiv = this.header.firstChild;
        var headerRow = this.header.getElementsByTagName('tr')[0];

        var numberOfCols = this.columns.length;
        var sortColIndex, sortOrder;
        for (var index = 0; index < numberOfCols; index++) {
            var colLabel = this.columns[index].colLabel;
            var colWidth = this.columns[index].width;
            var isSortable = this.columns[index].sortable;
            var headerCell = new YAHOO.rapidjs.component.treegrid.TreeHeaderCell(this, headerRow, colLabel, colWidth, index, isSortable);
            this.headers[index] = headerCell;
            this.headers[index].events["resize"].subscribe(this.onColumnSplitterMoved, this, true);
            this.headers[index].events["click"].subscribe(this.headerClicked, this, true);
            if (this.columns[index]['sortBy'] == true) {
                sortColIndex = index;
                sortOrder = this.columns[index]['sortOrder'] || 'asc'
            }
        }
        if (sortColIndex != null && this.columns[sortColIndex]) {
            this.sortState['header'] = this.headers[sortColIndex];
            this.sortState['direction'] = sortOrder;
            this.headers[sortColIndex].updateSortState(sortOrder);
        }
        this.refreshHeaderWidth();
        this.body = dh.append(this.wrapper, {tag: 'div', cls:'r-tree-body'}, true);
        this.treeBody = dh.append(this.body.dom, {tag: 'div'}, true);
        this.bufferPos = dh.append(this.treeBody.dom, {tag:'div'}, true);
        this.bufferView = dh.append(this.treeBody.dom, {tag:'div'}, true);
        this.bufferView.rows = [];

        YAHOO.util.Event.addListener(this.body.dom, 'scroll', this.handleScroll, this, true);
        YAHOO.util.Event.addListener(this.treeBody.dom, 'click', this.handleClick, this, true);
        YAHOO.util.Event.addListener(this.treeBody.dom, 'contextmenu', this.handleRightClick, this, true);


        if (this.tooltip == true) {
            YAHOO.util.Event.addListener(this.treeBody.dom, 'mouseover', this.updateTooltip, this, true);
            Tooltip.add(this.treeBody.dom, '');
        }
        this.setMenuItems(this.menuItems);
        this.multiSelectionMenu = this.renderMenus(this.multiSelectionMenuItems, "multiSelection", this.multiSelectionMenuItemClicked)
    },

    setMenuItems: function(menuItems) {
        this.menuItems = menuItems;
        this.rowMenu = this.renderMenus(this.menuItems, 'rowMenu', this.rowMenuItemClicked)
    },

    renderMenus : function(menuItems, menuType, clickFunc) {
        if (menuItems) {
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
    },

    onColumnSplitterMoved:function(colIndex, newSize) {
        var rows = this.bufferView.rows;
        this.columns[colIndex]['width'] = newSize;
        for (var index = 0; index < rows.length; index++) {
            var cells = rows[index].cells;
            this.setCellWidth(cells[colIndex], newSize);
        }
        this.refreshHeaderWidth();
        this.handleScroll();
    },

    refreshHeaderWidth: function()
    {
        var numberOfHeaders = this.headers.length;
        var totalWidth = 0;
        for (var index = 0; index < numberOfHeaders; index++) {
            totalWidth += this.headers[index].width;
        }
        this.headerInnerDiv.style.width = (totalWidth + 1000) + "px";
    },

    resize: function(width, height)
    {
        this.body.setHeight(height - this.header.offsetHeight);
        this.handleScroll();
    },

    handleScroll: function() {
        var scrollLeft = this.body.dom.scrollLeft;
        this.header.scrollLeft = scrollLeft;
        if (this.prevScrollLeft == scrollLeft)
        {
            this.renderTask.delay(50);
        }
        this.prevScrollLeft = scrollLeft;
    },

    handleData : function(data, expandAll) {
        if (this.rootNode) {
            this.rootNode.destroy();
        }
        this.rootNode = new YAHOO.rapidjs.component.treegrid.TreeRootNode(data, this.contentPath, this.hideAttribute, this.expandNodeAttribute);
        if (expandAll == true) {
            this.expandAll();
        }
        else {
            this.expandNode();
        }

    },

    expandAll : function(treeRow) {
        this._expandNodes(this.rootNode, true);
        this.updateExpandedNodes(this.rootNode, -1, true);
        this.updateBodyHeight();
    },

    _expandNodes : function(treeNode, isExpanded) {
        treeNode.isExpanded = isExpanded;
        var childNodes = treeNode.childNodes;
        for (var index = 0; index < childNodes.length; index++) {
            var childNode = childNodes[index];
            childNode.isExpanded = isExpanded;
            this._expandNodes(childNode, isExpanded);
        }
    },

    expandNode: function(treeRow) {
        var treeNode;
        var expandedRowIndex;
        if (treeRow) {
            treeNode = this.getTreeNode(treeRow)
            expandedRowIndex = treeRow.rowIndex;
        }
        else {
            treeNode = this.rootNode;
            expandedRowIndex = -1;
        }
        if (!treeNode.isExpanded && treeNode.isLeaf == false) {
            this.updateIcon(treeRow, treeNode);
            treeNode.isExpanded = true;
            this.updateExpandedNodes(treeNode, expandedRowIndex, true);
            this.updateBodyHeight();
        }
    },

    collapseNode : function(treeNode, treeRow, collapsedRowIndex) {
        if (treeNode.isLeaf == false) {
            this.updateIcon(treeRow, treeNode);
            this.updateExpandedNodes(treeNode, collapsedRowIndex, false);
            treeNode.isExpanded = false;
            this.updateBodyHeight();
        }
    },

    updateExpandedNodes : function(treeNode, rowIndex, isExpand) {
        if (isExpand == true) {
            var populateArray = [];
            var willBeSorted = false;
            var sortColumnIndex = null;
            var sortDirection = this.sortState['direction'];
            if (this.sortState['header']) {
                willBeSorted = true;
                sortColumnIndex = this.sortState['header'].colIndex;
            }
            this._getNewlyExpandedNodes(treeNode, populateArray, willBeSorted, sortColumnIndex, sortDirection);
            var firstPart = this.expandedNodes.slice(0, rowIndex + 1);
            if (rowIndex != this.expandedNodes.length - 1) {
                var lastPart = this.expandedNodes.slice(rowIndex + 1);
                this.expandedNodes = firstPart.concat(populateArray, lastPart);
            }
            else {
                this.expandedNodes = firstPart.concat(populateArray);
            }

        }
        else {
            var collapseEndIndex = this._getNewlyCollapsedNodeIndex(treeNode, rowIndex);
            this.expandedNodes.splice(rowIndex + 1, collapseEndIndex - rowIndex);
        }
    },

    _getNewlyExpandedNodes: function(treeNode, populateArray, willBeSorted, sortColumnIndex, sortDirection) {
        if (treeNode.isExpanded == true) {
            var childNodes = treeNode.childNodes;
            if (willBeSorted == true) {
                this.sort(childNodes, sortColumnIndex, sortDirection);
            }
            var nOfChildren = childNodes.length;
            for (var index = 0; index < nOfChildren; index++) {
                var childNode = childNodes[index];
                if (childNode.childNodes.length == 0) {
                    childNode.isExpanded = true;
                }
                populateArray.push(childNode);
                this._getNewlyExpandedNodes(childNode, populateArray, willBeSorted, sortColumnIndex, sortDirection);
            }
        }
    },
    _getNewlyCollapsedNodeIndex: function(treeNode, rowIndex) {
        var lastIndex = rowIndex;
        if (treeNode.isExpanded == true) {
            var childNodes = treeNode.childNodes;
            var nOfChildren = childNodes.length;
            for (var index = 0; index < nOfChildren; index++) {
                lastIndex = lastIndex + 1
                var childNode = childNodes[index];
                lastIndex = this._getNewlyCollapsedNodeIndex(childNode, lastIndex);
            }
        }
        return lastIndex;
    },
    updateIcon : function(treeRow, treeNode) {
        if (treeRow && treeRow.icon && treeNode) {
            if (treeNode.isExpanded == true || treeNode.isLeaf == true)
            {
                YAHOO.util.Dom.replaceClass(treeRow.icon, 'r-tree-treerowicon-collapsed', 'r-tree-treerowicon-expanded');
            }
            else
            {
                YAHOO.util.Dom.replaceClass(treeRow.icon, 'r-tree-treerowicon-expanded', 'r-tree-treerowicon-collapsed');
            }
        }
    },

    updateBodyHeight : function() {
        this.treeBody.setHeight(this.expandedNodes.length * this.getRowHeight());
        this.renderTask.delay(50);
    },
    getRowHeight : function() {
        if (!this.rowHeight) {
            var rule = YAHOO.ext.util.CSS.getRule('.r-tree-treerow');
            if (rule && rule.style.height) {
                this.rowHeight = parseInt(rule.style.height, 10);
            } else {
                this.rowHeight = 25;
            }
        }
        return this.rowHeight;
    },

    renderRows : function() {
        var scrollTop = this.body.dom.scrollTop;
        var rowStartIndex = Math.floor(scrollTop / this.getRowHeight());
        var interval = Math.floor(this.body.getHeight() / this.getRowHeight());
        interval = interval + 2;
        var nOfExpandedNodes = this.expandedNodes.length;
        if (nOfExpandedNodes < rowStartIndex + interval)
        {
            rowStartIndex = nOfExpandedNodes - interval;
        }
        if (rowStartIndex < 0)
        {
            rowStartIndex = 0;
            interval = nOfExpandedNodes;
        }
        if (interval > this.bufferView.rows.length)
        {
            this.createEmptyRows(interval - this.bufferView.rows.length);
        }
        else if (interval < this.bufferView.rows.length)
        {
            while (this.bufferView.rows.length > interval) {
                var row = this.bufferView.rows[this.bufferView.rows.length - 1];
                for (var cellIndex = 0; cellIndex < row.cells.length; cellIndex++) {
                    var cell = row.cells[cellIndex];
                    cell.body = null;
                    cell.wrapper.value = null;
                    cell.wrapper = null;
                    cell.rootImage = null;
                    row.cells[cellIndex] = null;
                }
                this.selectionHelper.rowRemoved(row)
                row.cells = null;
                row.icon = null;
                row.rowIndex = null;
                this.bufferView.dom.removeChild(row);
                this.bufferView.rows.splice(this.bufferView.rows.length - 1, 1);
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

        this.bufferPos.setHeight(rowStartIndex * this.getRowHeight());
        this.bufferView.setHeight(interval * this.getRowHeight());

        for (var rowIndex = 0; rowIndex < interval; rowIndex++) {
            var row = this.bufferView.rows[rowIndex];
            YAHOO.util.Dom.setStyle(row, 'display', 'block');
            var realRowIndex = rowStartIndex + rowIndex;
            row.rowIndex = realRowIndex;
            this.renderRow(row);
            this.selectionHelper.rowRendered(row);
        }
    },
    createEmptyRows: function(rowCount)
    {
        if (this.columns) {
            var dh = YAHOO.ext.DomHelper;
            var colCount = this.columns.length;
            for (var rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                var rowCells = [];
                var row = dh.append(this.bufferView.dom, {tag:'div', cls:'r-tree-treerow',
                    html:'<table cellspacing="0" cellpadding="0" style="height:100%;"><tbody><tr></tr></tbody></table>'});
                //YAHOO.util.Dom.addClass(row, 'r-tree-rowunselected');
                var tr = row.getElementsByTagName('tr')[0];
                if (colCount > 0) {
                    var firstTd = document.createElement('td');
                    tr.appendChild(firstTd);
                    firstTd.className = 'r-tree-treecell';
                    var firstCellBody, rootImageEl;
                    firstCellBody = dh.append(firstTd, {tag:'div', cls:'r-tree-treecell-content',
                        html:'<table style="position:relative;"><tbody><tr><td><div class="r-tree-treerowicon-collapsed"></div></td>' +
                             (this.rootImages ? '<td><div class="r-tree-rootimage"></div></td>' : '') +
                             '<td class="r-tree-firstcell"></td>' +
                             (this.menuItems ? '<td><div class="r-tree-row-headermenu"></div></td>' : '') +
                             '</tr></tbody></table>'});

                    if (this.rootImages) {
                        rootImageEl = YAHOO.util.Dom.getElementsByClassName('r-tree-rootimage', 'div', firstCellBody)[0];
                        YAHOO.util.Dom.setStyle(rootImageEl, 'display', 'none');
                    }
                    var firstCell = {wrapper:firstTd, body:firstCellBody, rootImage:rootImageEl};
                    this.setCellWidth(firstCell, this.columns[0]['width']);
                    rowCells.push(firstCell);
                    row.icon = firstCellBody.getElementsByTagName('div')[0];
                    for (var cellindex = 1; cellindex < colCount; cellindex++) {
                        var td = document.createElement('td');
                        tr.appendChild(td);
                        td.className = 'r-tree-treecell';
                        var cell = null;
                        if (this.columns[cellindex].type == 'image' || this.columns[cellindex].type == 'Image') {
                            var cellBody = dh.append(td, {tag:'div', cls:'r-tree-treecell-imagecontent'});
                            cell = {wrapper:td, body:cellBody};
                        }
                        else {
                            var cellBody = dh.append(td, {tag:'div', cls:'r-tree-treecell-content'});
                            cell = {wrapper:td, body:cellBody};
                        }
                        this.setCellWidth(cell, this.columns[cellindex]['width']);
                        rowCells.push(cell);
                    }
                }
                row.cells = rowCells;
                this.bufferView.rows[this.bufferView.rows.length] = row;
                YAHOO.util.Dom.setStyle(row, 'display', 'none');
            }
        }

    },

    setCellWidth: function(cell, width) {
        YAHOO.util.Dom.setStyle(cell['body'], 'width', width + 'px');
        YAHOO.util.Dom.setStyle(cell['wrapper'], 'width', (width * 1 - 2) + 'px');
    },

    renderRow : function(row) {
        if (row.cells && row.cells.length > 0) {
            var treeNode = this.getTreeNode(row);
            var dataNode = treeNode.xmlData;
            var firstCell = row.cells[0];
            var cellBody = firstCell['body'];
            var labelEl;
            if (this.rootImages) {
                this.setRootImage(firstCell['rootImage'], dataNode, treeNode.isExpanded);
                labelEl = cellBody.getElementsByTagName('td')[2];
            }
            else {
                labelEl = cellBody.getElementsByTagName('td')[1];
            }
            var value = dataNode.getAttribute(this.columns[0]['attributeName']);
            if (!value) {
                value = '-';
            }
            labelEl.innerHTML = value;
            firstCell['wrapper'].value = value;
            YAHOO.util.Dom.setStyle(cellBody.firstChild, 'left', (treeNode.level * 20) + 'px');

            this.updateIcon(row, treeNode);
            this.setCellWidth(firstCell, this.columns[0]['width']);
            for (var colIndex = 1; colIndex < row.cells.length; colIndex++) {
                var columnConfig = this.columns[colIndex];
                var cell = row.cells[colIndex];
                if (columnConfig.type == "Image" || columnConfig.type == "image")
                {
                    this.setImageSource(columnConfig, dataNode, cell.body);
                }
                else
                {
                    var cellValue = dataNode.getAttribute(columnConfig['attributeName']) || '-';
                    cell.wrapper.value = cellValue;
                    cell.body.innerHTML = cellValue;
                }
                this.setCellWidth(cell, columnConfig['width']);
            }
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
                htmlEl.style.backgroundImage = 'url("' + imageSrc + '")';
                YAHOO.util.Dom.setStyle(htmlEl, "background-position", expressionsArray[i]['align'] ? 'center ' + expressionsArray[i]['align'] : 'center left')
            }
        }
        if (!isImageSet) {
            htmlEl.style.backgroundImage = '';
        }
    },
    setRootImage: function(imageEl, dataNode, isExpanded) {
        var expressionsArray = this.rootImages;
        for (var i = 0; i < expressionsArray.length; i++)
        {
            var currentExpressionStr = expressionsArray[i]['visible'];
            var params = {data: dataNode.getAttributes()}
            var evaluationResult = eval(currentExpressionStr);
            if (evaluationResult == true)
            {
                YAHOO.util.Dom.setStyle(imageEl, 'display', '');
                var imageSrc;
                if (isExpanded == true) {
                    imageSrc = expressionsArray[i]['expanded'];
                }
                else {
                    imageSrc = expressionsArray[i]['collapsed'];
                }
                imageEl.style.background = 'url("' + imageSrc + '") no-repeat center';
                break;
            }
            else {
                YAHOO.util.Dom.setStyle(imageEl, 'display', 'none');
            }
        }
    },

    handleClick: function(e) {
        var target = YAHOO.util.Event.getTarget(e);
        var row = this.getRowFromChild(target);
        if (row) {
            if (YAHOO.util.Dom.hasClass(target, "r-tree-treerowicon-collapsed"))
            {
                this.expandNode(row);
            }
            else if (YAHOO.util.Dom.hasClass(target, "r-tree-treerowicon-expanded")) {
                this.collapseNode(this.getTreeNode(row), row, row.rowIndex);
            }
            else if (YAHOO.util.Dom.hasClass(target, "r-tree-row-headermenu")) {
                this.showRowMenu(target, row);
            }
            else {
                this.selectionHelper.rowClicked(row, e);
                this.events['treenodeclicked'].fireDirect(this.getTreeNode(row));
            }
        }
    },

    showRowMenu : function(target, row) {
        this.rowMenu.treeNode = this.getTreeNode(row);
        this.rowMenu.cfg.setProperty("context", [target, 'tl', 'bl']);
        var treeNode = this.getTreeNode(row);
        var dataNode = treeNode.xmlData;
        var index = 0;
        var numberOfDisplayedItems = 0;
        for (var i in this.menuItems) {
            var menuItemConfig = this.menuItems[i];
            var evaluationResult = true;
            var menuItem = this.rowMenu.getItem(index);
            if (menuItemConfig['visible'] != null) {
                var params = {data: dataNode.getAttributes(), label:menuItemConfig.label, dataNode:dataNode}
                evaluationResult = eval(menuItemConfig['visible']);
            }
            if (!evaluationResult)
                menuItem.element.style.display = "none";
            else {
                menuItem.element.style.display = "";
                numberOfDisplayedItems ++;
            }
            var subIndex = 0;
            for (var j in this.menuItems[i].submenuItems)
            {
                var subMenuItemConfig = this.menuItems[i].submenuItems[j];
                var submenuItem = this.rowMenu.getItem(index)._oSubmenu.getItem(subIndex);
                var subEvaluationResult = true;
                if (subMenuItemConfig['visible'] != null)
                {
                    var data = dataNode.getAttributes();
                    var label = subMenuItemConfig.label
                    var subEvaluationResult = eval(subMenuItemConfig['visible']);
                }
                if (!subEvaluationResult)
                    submenuItem.element.style.display = "none";
                else
                    submenuItem.element.style.display = "";
                subIndex++;
            }
            index++;
        }
        if (numberOfDisplayedItems > 0) {
            this.rowMenu.show();
            YAHOO.rapidjs.component.OVERLAY_MANAGER.bringToTop(this.rowMenu);
        }
    },

    rowMenuItemClicked: function(eventType, params) {
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
        var treeNode = this.rowMenu.treeNode;
        this.rowMenu.treeNode = null;
        this.events['rowMenuClick'].fireDirect(treeNode.xmlData, id, parentId);
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
    handleRightClick : function(e) {
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
    updateTooltip : function(event) {
        var target = YAHOO.util.Event.getTarget(event);
        var cell = this.getCellFromChild(target);
        if (cell) {
            if (YAHOO.util.Dom.hasClass(cell.firstChild, 'r-tree-treecell-imagecontent')) {
                Tooltip.update(this.treeBody.dom, '');
            }
            else {
                Tooltip.update(this.treeBody.dom, cell.value);
            }
        }
        else {
            Tooltip.update(this.treeBody.dom, '');
        }
    },


    getRowFromChild : function(childEl) {
        return YAHOO.rapidjs.DomUtils.getElementFromChild(childEl, 'r-tree-treerow');
    },

    getCellFromChild : function(childEl) {
        return YAHOO.rapidjs.DomUtils.getElementFromChild(childEl, 'r-tree-treecell');
    },

    refreshData: function() {
        this.expandedNodes = [];
        if (this.rootNode) {
            this._refreshData(this.rootNode, true);
        }
        this.updateBodyHeight();
    },

    _refreshData : function(treeNode, populateExpandedNodes) {
        var childNodes = treeNode.childNodes;
        if (this.sortState['header']) {
            this.sort(childNodes, this.sortState['header'].colIndex, this.sortState['direction']);
        }
        var tempNodes = [];
        var nOfChildren = childNodes.length;
        for (var index = 0; index < childNodes.length; index++) {
            var childNode = childNodes[index];
            if (populateExpandedNodes == true && treeNode.isExpanded == true) {
                if (childNode.isRemoved == false) {
                    this.expandedNodes.push(childNode);
                    if (childNode.childNodes.length == 0) {
                        childNode.isExpanded = true;
                    }

                }
            }
            if (childNode.isRemoved == true) {
                this.selectionHelper.nodeRemoved(childNode);
                if (this.rowMenu.treeNode == childNode) {
                    this.rowMenu.treeNode = null;
                    this.rowMenu.hide();
                }
                childNode.destroy();
            }
            else {
                tempNodes.push(childNode);
                if (treeNode.isExpanded == false) {
                    this._refreshData(childNode, false);
                }
                else {
                    this._refreshData(childNode, populateExpandedNodes);
                }

            }
        }
        treeNode.setChildNodes(tempNodes);
    },

    clear: function() {
        if (this.rootNode && this.rootNode.xmlData) {
            var currentChildren = this.rootNode.xmlData.childNodes();
            while (currentChildren.length > 0) {
                var childNode = currentChildren[0];
                this.rootNode.xmlData.removeChild(childNode);
            }
        }
        this.refreshData();
    },

    sort:function(arrayToBeSorted, columnIndex, direction) {
        var dsc = (direction && direction.toUpperCase() == 'DESC');
        var attribute = this.columns[columnIndex]['attributeName'];
        var sortType = this.columns[columnIndex]['sortType'];
        if (!sortType) {
            sortType = YAHOO.rapidjs.component.treegrid.sortTypes.none;
        }

        var fn = function(node1, node2) {
            var v1 = sortType(node1.xmlData.getAttribute(attribute));
            var v2 = sortType(node2.xmlData.getAttribute(attribute));
            if (v1 < v2)
                return dsc ? +1 : -1;
            else if (v1 > v2)
                return dsc ? -1 : +1;
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
    getRowsInRange: function(startIndex, endIndex) {
        var rows = [];
        var rowEls = this.bufferView.rows;
        if (rowEls.length > 0) {
            if (startIndex < 0) {
                startIndex = rowEls[0].rowIndex;
            }
            for (var i = 0; i < rowEls.length; i++) {
                var row = rowEls[i];
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
    headerClicked: function(header, direction) {
        var lastClicked = this.sortState['header'];
        this.sortState['header'] = header;
        this.sortState['direction'] = direction;
        this.expandedNodes = [];
        this.rootNode.isExpanded = false;
        this.expandNode();
        if (lastClicked && lastClicked != header) {
            lastClicked.updateSortState(null);
        }
        header.updateSortState(direction);
    },
    getTreeNode: function(row) {
        return this.expandedNodes[row.rowIndex]
    },
    getNodeFromRow : function(row) {
        return this.getTreeNode(row)
    },
    fireSelectionChange:function(selectedNodes, e) {
        this.events["selectionchanged"].fireDirect(selectedNodes);
    },
    fireMultiSelectionMenuClick: function(datas, id, parentId) {
        this.events['multiSelectionMenuClicked'].fireDirect(datas, id, parentId);
    }

};

YAHOO.rapidjs.component.treegrid.sortTypes = {
    none : function(s) {
        return s;
    },

    asUCString : function(s) {
        return String(s).toUpperCase();
    },

    asDate : function(s) {
        if (s instanceof Date) {
            return s.getTime();
        }
        var date = Date.parse(String(s));
        if (isNaN(date)) {
            return new Date().setTime(0)
        }
        else {
            return date;
        }
    },

    asFloat : function(s) {
        var val = parseFloat(String(s).replace(/,/g, ''));
        if (isNaN(val)) val = 0;
        return val;
    },

    asInt : function(s) {
        var val = parseInt(String(s).replace(/,/g, ''));
        if (isNaN(val)) val = 0;
        return val;
    }
};







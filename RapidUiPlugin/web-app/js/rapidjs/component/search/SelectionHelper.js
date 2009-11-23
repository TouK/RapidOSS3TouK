YAHOO.rapidjs.component.search.SelectionHelper = function(searchComponent) {
    this.searchComponent = searchComponent;
    this.selectedNodes = [];
    this.selectedRows = [];
    this.cssClass = "rcmdb-search-rowselected"
    this.lastRow = null;
};

YAHOO.rapidjs.component.search.SelectionHelper.prototype = {
    getSelectedNodes : function(){
        return this.selectedNodes
    },
    getSelectedRows :function(){
        return this.selectedRows;        
    },
    nodeRemoved: function(searchNode) {
        var nodeIndex = -1;
        for (var i = 0; i < this.selectedNodes.length; i++) {
            if (this.selectedNodes[i] == searchNode) {
                nodeIndex = i;
                break;
            }
        }
        if (nodeIndex > -1) {
            this.selectedNodes.splice(nodeIndex, 1);
        }
    },

    rowRemoved: function(row) {
        var rowIndex = -1;
        for (var i = 0; i < this.selectedRows.length; i++) {
            if (this.selectedRows[i] == row) {
                rowIndex = i;
                break;
            }
        }
        if (rowIndex > -1) {
            this.selectedRows.splice(rowIndex, 1);
        }
        if (this.lastRow == row) {
            this.lastRow = null;
        }
    },

    rowRendered: function(row) {
        var searchNode = this.searchComponent.getSearchNode(row);
        if (ArrayUtils.contains(this.selectedNodes, searchNode)) {
            if (!ArrayUtils.contains(this.selectedRows, row)) {
                this.selectedRows.push(row);
                YAHOO.util.Dom.addClass(row, this.cssClass);
            }
        }
        else {
            this.rowRemoved(row);
            YAHOO.util.Dom.removeClass(row, this.cssClass);
        }
    },

    rowClicked: function(row, e) {
        if (e.shiftKey) {
            if (this.lastRow) {
                var startIndex = this.lastRow.rowIndex;
                var endIndex = row.rowIndex;
                if (startIndex > endIndex) {
                    endIndex = startIndex;
                    startIndex = row.rowIndex;
                }
                this.selectRange(startIndex, endIndex)
            }
            else {
                this.selectRange(-1, row.rowIndex)
                if (this.selectedRows.length > 0) {
                    this.lastRow = this.selectedRows[0]
                }
            }
        }
        else if (e.ctrlKey) {
            if (ArrayUtils.contains(this.selectedRows, row)) {
                this.deselectRow(row);
            }
            else {
                this.selectRow(row, true);
            }
        }
        else {
            this.selectRow(row);
        }
        this.searchComponent.fireSelectionChange(this.selectedNodes, e)
    },

    contextMenuClicked:function(row, e){
        if(!ArrayUtils.contains(this.selectedRows, row)){
            this.selectRow(row);
            this.searchComponent.fireSelectionChange(this.selectedNodes, e)
        }
    },

    selectRange: function(startIndex, endIndex) {
        var rows = this.searchComponent.getRowsInRange(startIndex, endIndex);
        this.removeSelection();
        var lastRow = this.lastRow;
        for (var i = 0; i < rows.length; i++) {
            this.selectRow(rows[i], true);
        }
        this.lastRow = lastRow;
    },
    removeSelection: function() {
        this.selectedNodes = [];
        var selectedRows = this.selectedRows
        this.selectedRows = [];
        for (var i = 0; i < selectedRows.length; i++) {
            var selectedRow = selectedRows[i]
            YAHOO.util.Dom.removeClass(selectedRow, this.cssClass);
        }
    },
    selectRow: function(row, keepExisting) {
        if (keepExisting !== true) {
            this.removeSelection();
        }
        if (!ArrayUtils.contains(this.selectedRows, row)) {
            var searchNode = this.searchComponent.getSearchNode(row);
            YAHOO.util.Dom.addClass(row, this.cssClass);
            this.selectedNodes.push(searchNode);
            this.selectedRows.push(row);
        }
        this.lastRow = row;
    },
    deselectRow: function(row) {
        this.rowRemoved(row)
        var searchNode = this.searchComponent.getSearchNode(row)
        this.nodeRemoved(searchNode)
        YAHOO.util.Dom.removeClass(row, this.cssClass);
    }
}
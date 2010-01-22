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
YAHOO.namespace('rapidjs', 'rapidjs.component');
YAHOO.rapidjs.component.TreeGrid = function(container, config) {
    YAHOO.rapidjs.component.TreeGrid.superclass.constructor.call(this, container, config);
    this.keyAttribute = config.keyAttribute;
    this.rootTag = config.rootTag;
    this.expanded = config.expanded;
    var events = {
        'selectionChanged' : new YAHOO.util.CustomEvent('selectionChanged'),
        'nodeClicked' : new YAHOO.util.CustomEvent('nodeClicked'),
        'rowMenuClicked' : new YAHOO.util.CustomEvent('rowMenuClicked')
    };
    YAHOO.ext.util.Config.apply(this.events, events);
    this.header = YAHOO.ext.DomHelper.append(this.container, {tag:'div'});
    this.toolbar = new YAHOO.rapidjs.component.tool.ButtonToolBar(this.header, {title:this.title});
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.LoadingTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.SettingsTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.ErrorTool(document.body, this));
    this.body = YAHOO.ext.DomHelper.append(this.container, {tag:'div'}, true);
    this.treeGridView = new YAHOO.rapidjs.component.treegrid.TreeGridView(this.body.dom, config);
    this.treeGridView.render();
    this.treeGridView.events['selectionchanged'].subscribe(this.fireSelectionChange, this, true);
    this.treeGridView.events['rowMenuClick'].subscribe(this.fireRowMenuClick, this, true);
    this.treeGridView.events['treenodeclicked'].subscribe(this.fireTreeNodeClick, this, true);
}
YAHOO.lang.extend(YAHOO.rapidjs.component.TreeGrid, YAHOO.rapidjs.component.PollingComponentContainer, {
    handleSuccess: function(response, keepExisting, removeAttribute)
    {
        var data = new YAHOO.rapidjs.data.RapidXmlDocument(response, [this.keyAttribute]);
        this.loadData(data);
    },

    loadData: function(data, keepExisting, removeAttribute) {
        var node = this.getRootNode(data);
        if (node) {
            if (!this.rootNode || keepExisting == false) {
                this.rootNode = node;
                this.treeGridView.handleData(this.rootNode, this.expanded);
            }
            else
            {
                this.treeGridView.isSortingDisabled = true;
                this.rootNode.mergeData(node, this.keyAttribute, keepExisting, removeAttribute);
                this.treeGridView.refreshData();
                this.treeGridView.isSortingDisabled = false;
            }
        }
    },

    clearData: function() {
        this.treeGridView.clear();
    },

    resize: function(width, height) {
        var bodyHeight = height - this.header.offsetHeight;
        this.body.setHeight(bodyHeight);
        this.treeGridView.resize(width, bodyHeight);
    },

    fireSelectionChange: function(treeNodes) {
        this.events['selectionChanged'].fireDirect(ArrayUtils.collect(treeNodes, function(it){return it.xmlData}));
    },
    fireRowMenuClick: function(xmlData, id, parentId, row) {
        this.events['rowMenuClicked'].fireDirect(xmlData, id, parentId, row);
    },
    fireTreeNodeClick: function(treeNode) {
        this.events['nodeClicked'].fireDirect(treeNode.xmlData);
    }
});
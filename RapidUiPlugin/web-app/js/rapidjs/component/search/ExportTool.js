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
YAHOO.rapidjs.component.search.ExportTool = function(container, component) {
    var config = {className:'rcmdb-search-exporttool', tooltip:'Export'};
    YAHOO.rapidjs.component.search.ExportTool.superclass.constructor.call(this, container, component, config);
    this.render();
};

YAHOO.lang.extend(YAHOO.rapidjs.component.search.ExportTool, YAHOO.rapidjs.component.tool.BasicTool, {
    performAction : function() {
        this.clear();
        this.dialog.show();
    },

    render: function() {
        this.dialog = new YAHOO.rapidjs.component.Dialog({width:300,height:170, title:'Export', resizable:false,
            buttons : [ { text:"Export", handler:this.handleSubmit.createDelegate(this), isDefault:true, scope:this },
                { text:"Cancel", handler:this.handleCancel.createDelegate(this), scope:this } ]});
        this.body = YAHOO.ext.DomHelper.append(this.dialog.body, {tag:'div',
            html:'<form><table><tbody>' +
                 '<tr><td><label>Data to export:</label></td><td><select name="exportDataType"><option value="Visible">Visible</option><option value="Matching Query">Matching Query</option></select></td></tr>' +
                 '<tr><td><label>Export type:</label></td><td><select name="exportType"><option value="XML">XML</option><option value="CSV">CSV</option></select></td></tr>' +
                 '<tr><td><label>Max. # of records:</label></td><td><input type="text" name="max"></td></tr>' +
                 '</tbody></table></form>'+
                 '<form id="'+this.component.id+'_exportSubmitForm" method="post" action="'+ getUrlPrefix()+'search/export" style="display:none;"></form>'});
        var selects = this.body.getElementsByTagName('select')
        this.exportDataTypeInput = selects[0]
        this.exportTypeInput = selects[1]
        this.maxInput = this.body.getElementsByTagName('input')[0];
        this.maxInput.readOnly = true;
        YAHOO.util.Event.addListener(this.exportDataTypeInput, 'change', this.exportChange, this, true)
    },

    handleSubmit: function(){
        var params = this.component.getVisibleDataQueryParams();
        var dataType = this.exportDataTypeInput.options[this.exportDataTypeInput.selectedIndex].value; 
        var exportType = this.exportTypeInput.options[this.exportTypeInput.selectedIndex].value;
        if(dataType == 'Matching Query'){
            params['offset'] = 0;
            var max = this.maxInput.value;
            if(!YAHOO.lang.isNumber(parseInt(max, 10))){
                delete params['max']
            }
            else{
                params['max'] = max;
            }
        }
        var dh = YAHOO.ext.DomHelper;
        var submitForm=document.getElementById(this.component.id+'_exportSubmitForm');
        submitForm.innerHTML='';
        params.type=exportType;
        for(var param in params){
            var input=dh.append(submitForm, {tag:'input',type:'hidden',name:param,value:params[param]});
        }
        submitForm.submit();
        
        this.dialog.hide();
    },
    
    exportChange: function(){
        var exportType = this.exportDataTypeInput.options[this.exportDataTypeInput.selectedIndex].value;
        if(exportType == 'Visible'){
            this.maxInput.readOnly = true;
            this.maxInput.value = '';
        }
        else{
            this.maxInput.readOnly = false;
        }
    },
    handleCancel:function(){
        this.dialog.hide();
    },
    clear:function(){
        SelectUtils.selectTheValue(this.exportDataTypeInput, 'Visible', 0);
        SelectUtils.selectTheValue(this.exportTypeInput, 'XML', 0);
        this.maxInput.value = '';
        this.maxInput.readOnly = true;
    }

});
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
YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.tool');
YAHOO.rapidjs.component.tool.ErrorTool = function(container, component) {
    var config = {className:'r-tool-error'};
    YAHOO.rapidjs.component.tool.ErrorTool.superclass.constructor.call(this, container, component, config);
    this.component.events['success'].subscribe(this.success, this, true);
    this.component.events['error'].subscribe(this.error, this, true);
    this.errorsToBeAppended = null;
};

YAHOO.lang.extend(YAHOO.rapidjs.component.tool.ErrorTool, YAHOO.rapidjs.component.tool.BasicTool, {
    performAction : function() {
        if(!this.dialog){
            this.dialog = new YAHOO.rapidjs.component.Dialog({width:550,height:350, close:true});
            this.body = YAHOO.ext.DomHelper.append(this.dialog.body, {tag:'div', cls:'r-errordialog-body'});
            this.appendErrors(this.errorsToBeAppended);
        }
        this.dialog.show();
    },
    containerChanged: function(newContainer){
        YAHOO.util.Dom.setStyle(newContainer, 'display', 'none');
    },
    success: function(){
       YAHOO.util.Dom.setStyle(this.button.el.dom.parentNode, 'display', 'none');
    },
    error: function(component, errors, willShow){
        YAHOO.util.Dom.setStyle(this.button.el.dom.parentNode, 'display', '');
        if(this.dialog){
            this.appendErrors(errors);
        }
        else{
            this.errorsToBeAppended = errors;
        }
        if(willShow){
            this.performAction();
        }
    },
    appendErrors : function(errors){
         for(var index = 0; index < errors.length; index++) {
             YAHOO.ext.DomHelper.append(this.body, {tag:'div', cls:'r-errordialog-item', html:errors[index]});
         }
    }
});
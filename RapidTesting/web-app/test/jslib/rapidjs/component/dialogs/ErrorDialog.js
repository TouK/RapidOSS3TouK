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
YAHOO.rapidjs.component.dialogs.ErrorDialog = function(){
	var dh = YAHOO.ext.DomHelper;
	this.dialogContainer = YAHOO.ext.DomHelper.append(document.body, {tag:'div'});
	this.dialog =  dialog = new YAHOO.ext.LayoutDialog(dh.append(document.body, {tag:'div'}), { 
        modal: true,
        width:500,
        height:200,
        shadow:true,
        minWidth:100,
        minHeight:100,
        syncHeightBeforeShow: true,
        title: 'Error',
        resizable: true,
        shim:true,
        center:{
            autoScroll:true
        }
	});
	var container = YAHOO.ext.DomHelper.append(document.body, {tag:'div', style:"width:100%;height:100%;overflow:hidden;"});
	this.dialogTextArea = YAHOO.ext.DomHelper.append(container, {tag:'textarea', cls:"rapid-textarea"});
	this.dialogTextArea.readOnly=true;
	this.okButton = this.dialog.addButton('OK', this.hide, this);
	var layout = this.dialog.getLayout();
    layout.beginUpdate();
    layout.add('center',new YAHOO.rapidjs.component.layout.RapidPanel(container));
    layout.endUpdate();
    this.dialog.el.addClass('ydlg-error');
    this.dialog.mask.addClass('ydlg-error-mask');
};

YAHOO.rapidjs.component.dialogs.ErrorDialog.prototype = {
	setErrorText: function(text){
		this.dialogTextArea.value = text;
	}, 
	show: function(animTarget){
		YAHOO.ext.DialogManager.bringToFront(this.dialog);
		this.okButton.focus();
		this.dialog.show(animTarget);
	}, 
	hide: function(){
		this.dialog.hide();
	}, 
	isVisible: function(){
		return this.dialog.isVisible();
	}
};
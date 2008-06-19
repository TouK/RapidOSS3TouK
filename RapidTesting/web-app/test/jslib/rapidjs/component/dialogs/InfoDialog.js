YAHOO.rapidjs.component.dialogs.InfoDialog = function(){
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
        title: 'Info',
        resizable: true,
        shim:true,
        center:{
            autoScroll:true
        }
	});
	var container = YAHOO.ext.DomHelper.append(document.body, {tag:'div', style:"width:100%;height:100%;overflow:hidden;"});
	this.dialogTextArea = YAHOO.ext.DomHelper.append(container, {tag:'div', cls:"rapid-info-dialog-text"});
	this.okButton = this.dialog.addButton('OK', this.hide, this);
	var layout = this.dialog.getLayout();
    layout.beginUpdate();
    layout.add('center',new YAHOO.rapidjs.component.layout.RapidPanel(container));
    layout.endUpdate();
    this.dialog.el.addClass('ydlg-info');
    this.dialog.mask.addClass('ydlg-info-mask');
};

YAHOO.rapidjs.component.dialogs.InfoDialog.prototype = {
	setText: function(text){
		this.dialogTextArea.innerHTML = text;
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
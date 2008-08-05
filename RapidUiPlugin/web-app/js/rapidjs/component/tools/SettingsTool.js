YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.tool');
YAHOO.rapidjs.component.tool.SettingsTool = function(container, component) {
    this.loadingStyle = 'r-tool-refresh';
	this.nonloadingStyle = 'r-tool-loading';
	this.component = component;
    var config = {tooltip:"Settings", className:this.nonloadingStyle};
    YAHOO.rapidjs.component.tool.SettingsTool.superclass.constructor.call(this, container, component,config);
    if(component.events["loadstatechanged"])
	{
		component.events["loadstatechanged"].subscribe(this.refreshState, this, true);
	}
	this.dialog = new YAHOO.rapidjs.component.Dialog({width:300,height:200, minWidth: 300, minHeight: 200,
													  buttons:[{text:"Apply", handler:this.handleApplyClick, scope:this },
															   {text:"Cancel", handler:this.handleCancelClick, scope:this }]});
	var dh = YAHOO.ext.DomHelper;

    this.panelFields = dh.append(this.dialog.body, {tag:'div', cls:'panel-body-fields',

    html:

    '<table width="100%"><tbody>' +
    '<tr><td width="50%"><label>Set polling interval:</label></td>' +
    '<td width="50%"><input type="textbox" name="PollingInterval" style="width:100px"/></td></tr>'+
    '<tr><td width="0%"><select><option value="1">1</option><option value="2">2</option>' +
	'<option value="3">3</option><option value="4">4</option><option value="5">5</option><option value="6">6</option>' +
	'<option value="7">7</option><option value="8">8</option></select></td>' +
    '</tr>'+
    '</tbody></table>'

    },true);
    this.dialog.container.getElementsByTagName('input')[0].value = this.component.getPollingInterval();


    YAHOO.util.Event.addListener(this.dialog.container.getElementsByTagName('input')[0], 'keypress', this.applyButtonPress, this, true);


};

YAHOO.lang.extend(YAHOO.rapidjs.component.tool.SettingsTool, YAHOO.rapidjs.component.tool.BasicTool, {
    performAction : function() {
	    this.clearAllFields();
	    this.dialog.container.getElementsByTagName('input')[0].value = this.component.getPollingInterval();
        this.dialog.show();
    },
    handleApplyClick: function(){
	    if(this.dialog.container.getElementsByTagName('input')[0].value != '')
    	{
	    	var pollingInt = parseInt(this.dialog.container.getElementsByTagName('input')[0].value);
	    	if(YAHOO.lang.isNumber(pollingInt))
	    		this.component.setPollingInterval(pollingInt);

	    	this.component.poll();
      		this.dialog.hide();
  		}

    },
    handleCancelClick: function(){
	    this.dialog.hide();
    },
    applyButtonPress: function(e){
	   	if (e.type == "keypress" && e.keyCode == 13){

	  		this.handleApplyClick();
  		}
    },

    refreshState : function(component, loading) {
        if (loading == true) {
            YAHOO.util.Dom.replaceClass(this.button.inner, this.nonloadingStyle, this.loadingStyle)
        }
        else {
            YAHOO.util.Dom.replaceClass(this.button.inner, this.loadingStyle, this.nonloadingStyle)
        }
    },

    clearAllFields: function()
    {
        var formElements = this.dialog.container.getElementsByTagName('input');
        for (var i = 0; i < formElements.length; i++)
        {
            var formElement = formElements[i];
            formElement.value = '';
        }
    }
});
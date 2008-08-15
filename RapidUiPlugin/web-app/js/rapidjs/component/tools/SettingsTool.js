YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.tool');
YAHOO.rapidjs.component.tool.SettingsTool = function(container, component, config) {
    this.config = config;
	if(!this.config)
		this.config = new Object();

	this.config.className = this.config.classname || "r-tool-setting";
	this.config.tooltip = this.config.tooltip || "Configure";
	this.title = this.config.title || "Configure";
	this.width = this.config.width || 300;
	this.height = this.config.height || 100;
	this.minWidth = this.config.minWidth || 300;
	this.minHeight = this.config.minHeight || 100;
	YAHOO.rapidjs.component.tool.SettingsTool.superclass.constructor.call(this, container, component,this.config);

	this.dialog = new YAHOO.rapidjs.component.Dialog({ width:this.width,height:this.height, minWidth: this.minWidth,
													   minHeight: this.minHeight , title: this.title,
													   buttons:[{text:"Apply", handler:this.handleApplyClick, scope:this, isDefault:true },
															    {text:"Cancel", handler:this.handleCancelClick, scope:this }]});
	var dh = YAHOO.ext.DomHelper;

    this.panelFields = dh.append(this.dialog.body, {tag:'div', cls:'panel-body-fields',
	html:
    '<table width="100%"><tbody>' +
    '<tr><td width="50%"><label>Set polling interval:</label></td>' +
    '<td width="50%"><input type="textbox" name="PollingInterval" style="width:100px"/></td></tr>'+
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
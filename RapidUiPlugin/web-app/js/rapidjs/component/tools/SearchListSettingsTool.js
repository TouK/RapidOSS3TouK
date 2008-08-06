YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.tool');
YAHOO.rapidjs.component.tool.SearchListSettingsTool = function(container, component) {

    YAHOO.rapidjs.component.tool.SearchListSettingsTool.superclass.constructor.call(this, container, component,
    						{width:300,height:125}
    );

	this.panelFields = YAHOO.ext.DomHelper.append(this.dialog.body, {tag:'div', cls:'panel-body-fields',
	html:
    '<table width="100%"><tbody><tr><td style="padding-top:5px" width="50%"><label>Set line size:</label></td><td style="padding-top:5px"><select><option value="1">1</option><option value="2">2</option>' +
	'<option value="3">3</option><option value="4">4</option><option value="5">5</option><option value="6">6</option>' +
	'<option value="7">7</option><option value="8">8</option></select></td>' +
    '</tr></tbody></table>'
    },true);

    this.lineSizeSelector = this.dialog.container.getElementsByTagName('select')[0];
    SelectUtils.selectTheValue(this.lineSizeSelector, this.component.lineSize, 0);
};

YAHOO.lang.extend(YAHOO.rapidjs.component.tool.SearchListSettingsTool, YAHOO.rapidjs.component.tool.SettingsTool, {

    handleApplyClick: function(){
	    YAHOO.rapidjs.component.tool.SearchListSettingsTool.superclass.handleApplyClick.call(this);
	    this.component.handleLineSizeChange(parseInt(this.lineSizeSelector.options[this.lineSizeSelector.selectedIndex].value, 10));

    }
});
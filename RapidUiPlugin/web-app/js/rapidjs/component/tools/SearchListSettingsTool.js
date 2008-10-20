YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.tool');
YAHOO.rapidjs.component.tool.SearchListSettingsTool = function(container, component) {

    YAHOO.rapidjs.component.tool.SearchListSettingsTool.superclass.constructor.call(this, container, component,
    {height:130}
            );
};

YAHOO.lang.extend(YAHOO.rapidjs.component.tool.SearchListSettingsTool, YAHOO.rapidjs.component.tool.SettingsTool, {
    render: function() {
        var dh = YAHOO.ext.DomHelper;

        var wrap = dh.append(this.dialog.body, {tag:'div',
            html:   '<form action="javascript:void(0)"><table><tbody>' +
                    '<tr><td width="50%"><label>Set polling interval:</label></td>' +
                    '<td width="50%"><input type="textbox" name="PollingInterval" style="width:100px"/></td></tr>' +
                    '<tr><td width="50%"><label>Set line size:</label></td>' +
                    '<td width="50%"><select><option value="1">1</option><option value="2">2</option>' +
                    '<option value="3">3</option><option value="4">4</option><option value="5">5</option><option value="6">6</option>' +
                    '<option value="7">7</option><option value="8">8</option></select></td></tr>' +
                    '</tbody></table></form>'
        });
        this.pollIntervalInput = wrap.getElementsByTagName('input')[0]
        this.lineSizeSelector = wrap.getElementsByTagName('select')[0]
        this.pollIntervalInput.value = this.component.getPollingInterval();
        SelectUtils.selectTheValue(this.lineSizeSelector, this.component.lineSize, 0);
        YAHOO.util.Event.addListener(this.dialog.container.getElementsByTagName('form')[0], 'keypress', this.applyButtonPress, this, true);
    },
    handleApplyClick: function() {
        YAHOO.rapidjs.component.tool.SearchListSettingsTool.superclass.handleApplyClick.call(this);
        this.component.handleLineSizeChange(parseInt(this.lineSizeSelector.options[this.lineSizeSelector.selectedIndex].value, 10));

    }
});
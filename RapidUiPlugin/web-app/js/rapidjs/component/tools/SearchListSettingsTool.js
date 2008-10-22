YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.tool');
YAHOO.rapidjs.component.tool.SearchListSettingsTool = function(container, component) {

    YAHOO.rapidjs.component.tool.SearchListSettingsTool.superclass.constructor.call(this, container, component, {});
};

YAHOO.lang.extend(YAHOO.rapidjs.component.tool.SearchListSettingsTool, YAHOO.rapidjs.component.tool.SettingsTool, {
    render: function() {
        var dh = YAHOO.ext.DomHelper;

        var container = dh.append(document.body, {tag:'div',
            html:   '<div class="hd">' + this.title + '</div><div class="bd"><form action="javascript:void(0)"><table><tbody>' +
                    '<tr><td width="50%"><label>Set polling interval:</label></td>' +
                    '<td width="50%"><input type="textbox" name="pollingInterval" style="width:100px"/></td></tr>' +
                    '<tr><td width="50%"><label>Set line size:</label></td>' +
                    '<td width="50%"><select name="lineSize"><option value="1">1</option><option value="2">2</option>' +
                    '<option value="3">3</option><option value="4">4</option><option value="5">5</option><option value="6">6</option>' +
                    '<option value="7">7</option><option value="8">8</option></select></td></tr>' +
                    '</tbody></table><input type="hidden" name="name"></input>' +
                    '</form></div>'
        });
        this.form = new YAHOO.rapidjs.component.Form(container, {id:this.component.id + "_settingsTool", saveUrl:"componentConfig/save", width:this.width + "px", submitAction:"POST"});
        this.lineSizeSelector = this.form.dialog.form.lineSize;
        SelectUtils.selectTheValue(this.lineSizeSelector, this.component.lineSize, 0);
    },
    handleSubmit: function() {
        YAHOO.rapidjs.component.tool.SearchListSettingsTool.superclass.handleSubmit.call(this);
        this.component.handleLineSizeChange(parseInt(this.lineSizeSelector.options[this.lineSizeSelector.selectedIndex].value, 10));

    }
});
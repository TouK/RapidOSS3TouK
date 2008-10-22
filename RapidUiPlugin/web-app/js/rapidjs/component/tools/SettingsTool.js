YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.tool');
YAHOO.rapidjs.component.tool.SettingsTool = function(container, component, config) {
    this.config = config;
    if (!this.config)
        this.config = new Object();

    this.config.className = this.config.classname || "r-tool-setting";
    this.config.tooltip = this.config.tooltip || "Configure";
    this.title = this.config.title || "Configure";
    this.width = this.config.width || 230;
    YAHOO.rapidjs.component.tool.SettingsTool.superclass.constructor.call(this, container, component, this.config);
    this.render();
    this.form.events['submit'].subscribe(this.handleSubmit, this, true);
    var callback = {
        success: this.processSuccess,
        failure:this.processFailure,
        scope:this,
        timeout:30000
    }
    YAHOO.util.Connect.asyncRequest('GET', 'componentConfig/get?name=' + this.component.id, callback);

};

YAHOO.lang.extend(YAHOO.rapidjs.component.tool.SettingsTool, YAHOO.rapidjs.component.tool.BasicTool, {
    render: function() {
        var dh = YAHOO.ext.DomHelper;

        var container = dh.append(document.body, {tag:'div',
            html:   '<div class="hd">' + this.title + '</div><div class="bd"><form action="javascript:void(0)"><table><tbody>' +
                    '<tr><td width="50%"><label>Set polling interval:</label></td>' +
                    '<td width="50%"><input type="textbox" name="pollingInterval" style="width:100px"/></td></tr>' +
                    '</tbody></table><input type="hidden" name="name"></input>' +
                    '</form></div>'
        });
        this.form = new YAHOO.rapidjs.component.Form(container, {id:this.component.id + "_settingsTool", saveUrl:"componentConfig/save", width:this.width + "px", submitAction:"POST"});
    },
    performAction : function() {
        this.form.show(YAHOO.rapidjs.component.Form.CREATE_MODE, {}, {name:this.component.id, pollingInterval:this.component.getPollingInterval()})
    },
    handleSubmit: function() {
        var pollIntervalInput = this.form.dialog.form.pollingInterval;
        if (pollIntervalInput.value != '')
        {
            var pollingInt = parseInt(pollIntervalInput.value);
            if (YAHOO.lang.isNumber(pollingInt)) {
                this.component.setPollingInterval(pollingInt);
                this.component.poll();
            }
        }
    },

    processSuccess:function(response) {
        YAHOO.rapidjs.ErrorManager.serverUp();
        if (YAHOO.rapidjs.Connect.checkAuthentication(response)) {
            if (!YAHOO.rapidjs.Connect.containsError(response)) {
               var pollingInterval = response.responseXML.getElementsByTagName("ComponentConfig")[0].getAttribute("pollingInterval")
                var pollingInt = parseInt(pollingInterval);
                if (YAHOO.lang.isNumber(pollingInt)) {
                    this.component.setPollingInterval(pollingInt);
                    if (pollingInt > 0) {
                        this.component.pollTask.delay(pollingInt);
                    }
                }
            }
        }

    },

    processFailure:function(response) {
        var st = response.status;
        if (st == -1) {
            this.component.events["error"].fireDirect(this.component, ['Request received timeout.']);
            YAHOO.rapidjs.ErrorManager.errorOccurred(this.component, ['Request received timeout.']);
        }
        else if (st == 404) {
            this.component.events["error"].fireDirect(this.component, ['Specified url cannot be found.']);
            YAHOO.rapidjs.ErrorManager.errorOccurred(this.component, ['Specified url cannot be found.']);
        }
        else if (st == 0) {
            YAHOO.rapidjs.ErrorManager.serverDown();
        }
    }
});
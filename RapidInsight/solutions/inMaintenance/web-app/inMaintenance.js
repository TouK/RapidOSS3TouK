YAHOO.namespace('rapidjs', 'rapidjs.component');
YAHOO.rapidjs.component.InMaintenanceForm = function() {
    this.requester = new YAHOO.rapidjs.Requester(this.processSuccess, this.processFailure, this);
    this.events = {
        success: new YAHOO.util.CustomEvent('success'),
        error: new YAHOO.util.CustomEvent('error')
    }
    this.render();
}
YAHOO.rapidjs.component.InMaintenanceForm.prototype = {
    render: function() {
        this.body = document.getElementById('inMaintenanceForm');
        var config = {
            width:640,
            height:532,
            minWidth:100,
            minHeight:100,
            resizable: false,
            x:300,
            y:100,
            title: 'In Maintenance',
            mask:true,
            buttons:[
                {text:"Save", handler:this.handleSave, scope:this, isDefault:true },
                {text:"Cancel", handler:this.hide, scope:this }]
        }
        this.dialog = new YAHOO.rapidjs.component.Dialog(config);
        this.dialog.body.appendChild(this.body);
        var toolbar = new YAHOO.rapidjs.component.tool.ButtonToolBar(this.body, {title:""});
        toolbar.addTool(new YAHOO.rapidjs.component.tool.ErrorTool(document.body, this));
        YAHOO.util.Dom.setStyle(toolbar.el, 'display', 'none');
        YAHOO.util.Dom.setStyle(toolbar.toolsEl, 'right', '5px');
        YAHOO.util.Dom.setStyle(toolbar.toolsEl, 'width', '45px');
        this.dialog.container.appendChild(toolbar.toolsEl);
        var views = YAHOO.util.Dom.getElementsByClassName('view', 'div', this.body);
        this.maintenanceView = views[0];
        this.scheduleView = views[1];
        this.maintenanceForm = this.maintenanceView.getElementsByTagName('form')[0]
        this.scheduleForm = this.scheduleView.getElementsByTagName('form')[0]
        this.activeForm = this.maintenanceForm;
        this.infoView = YAHOO.ext.DomHelper.append(this.maintenanceView, {tag:'div'})

        var calendarDlg = new YAHOO.widget.Dialog(YAHOO.ext.DomHelper.append(document.body, {tag:'div', id:"maintenanceFormCalendarDlg"}), {
            visible:false,
            draggable:false,
            close:true,width:160

        });
        calendarDlg.setHeader('Pick A Date');
        calendarDlg.setBody('<div id="maintenanceFormCalendar"></div>')
        calendarDlg.render(document.body);
        calendarDlg.showEvent.subscribe(function() {
            calendarDlg.fireEvent("changeContent");
        });
        YAHOO.rapidjs.component.OVERLAY_MANAGER.register(calendarDlg)
        this.calendarDlg = calendarDlg;

        var calendarButtons = YAHOO.util.Dom.getElementsByClassName('calendarButton', 'button', this.scheduleView);
        for (var i = 0; i < calendarButtons.length; i++) {
            var button = calendarButtons[i]
            YAHOO.util.Event.addListener(button, 'click', this.showCalendar.createDelegate(this, [button, i]), this, true);
        }
        var dateWidth = 120;
        if (YAHOO.env.ua.ie)dateWidth = 130
        var myColumnDefs = [
            {key:'delete',label:'', className:'delete', width:25},
            {key:"type", label:"Type", width:50},
            {key:"maintStarting", label:"Maint. Start",  width:dateWidth},
            {key:"maintEnding", label:"Maint. End", width:dateWidth},
            {key:"schedStarting", label:"Sched. Start",  width:dateWidth},
            {key:"schedEnding", label:"Sched. End",  width:dateWidth},
            {key:"details", label:"", width:150}
        ];
        var myDataSource = new YAHOO.util.DataSource([]);
        myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        myDataSource.responseSchema = {
            fields: ["id","type","maintStarting", "maintEnding", 'schedStarting', "schedEnding", "details"]
        };
        this.scheduleGrid = new YAHOO.widget.ScrollingDataTable(document.getElementById('maintenanceSchedulesTable'), myColumnDefs, myDataSource, {'MSG_EMPTY':'', scrollable:true, width:'600px', height:'110px'});
        this.scheduleGrid.subscribe("cellClickEvent", function(oArgs) {
            var target = oArgs.target;
            var column = this.scheduleGrid.getColumn(target)
            if (column.getKey() == 'delete') {
                if (confirm("Are you sure ?")) {
                    var record = this.scheduleGrid.getRecord(target)
                    var scheduleId = record.getData("id");
                    var url = getUrlPrefix() + "script/run/putInMaintenance"
                    this.dialog.showMask();
                    this.requester.doPostRequest(url, {scheduleId:scheduleId, mode:'delete', maintenanceType:'schedule'}, this.scheduleDeleteSuccess)
                }
            }
        }, this, true);
        var populateHoursAndMinutes = function(select, count) {
            for (var i = 0; i < count; i++) {
                var text = '' + i
                if (i < 10) text = '0' + i
                SelectUtils.addOption(select, text, "" + i)
            }
        }
        populateHoursAndMinutes(this.scheduleForm.starting_hour, 24)
        populateHoursAndMinutes(this.scheduleForm.ending_hour, 24)
        populateHoursAndMinutes(this.scheduleForm.maintStarting_hour, 24)
        populateHoursAndMinutes(this.scheduleForm.maintEnding_hour, 24)
        populateHoursAndMinutes(this.scheduleForm.starting_minute, 60)
        populateHoursAndMinutes(this.scheduleForm.ending_minute, 60)
        populateHoursAndMinutes(this.scheduleForm.maintStarting_minute, 60)
        populateHoursAndMinutes(this.scheduleForm.maintEnding_minute, 60)
        var links = this.body.getElementsByTagName('ul')[0].getElementsByTagName('li');
        YAHOO.util.Event.addListener(links[0], 'click', this.changeView.createDelegate(this, [true, links]), this, true);
        YAHOO.util.Event.addListener(links[1], 'click', this.changeView.createDelegate(this, [false, links]), this, true);
        YAHOO.util.Event.addListener(this.scheduleForm.scheduleType, 'change', this.scheduleTypeChanged, this, true);
        var scheduleTypeViews = YAHOO.util.Dom.getElementsByClassName('scheduleType', 'div', this.scheduleView);
        YAHOO.util.Event.addListener(scheduleTypeViews[5].getElementsByTagName('div')[0], 'click', this.calculateFireTimes, this, true);
        this.fireTimesList = scheduleTypeViews[5].getElementsByTagName('select')[0];
        this.clearInputFields();
    },
    changeView: function(isMaintenance, links) {
        if (isMaintenance) {
            YAHOO.util.Dom.addClass(links[0], "selected")
            YAHOO.util.Dom.removeClass(links[1], "selected")
            YAHOO.util.Dom.setStyle(this.maintenanceView, 'display', '')
            YAHOO.util.Dom.setStyle(this.scheduleView, 'display', 'none')
            this.activeForm = this.maintenanceForm
        }
        else {
            YAHOO.util.Dom.addClass(links[1], "selected")
            YAHOO.util.Dom.removeClass(links[0], "selected")
            YAHOO.util.Dom.setStyle(this.maintenanceView, 'display', 'none')
            YAHOO.util.Dom.setStyle(this.scheduleView, 'display', '')
            this.activeForm = this.scheduleForm
        }
    },
    show: function(objectName) {
        this.currentObject = objectName;
        this.dialog.setTitle('In Maintenence for ' + objectName);
        this.maintenanceForm.objectName.value = objectName;
        this.scheduleForm.objectName.value = objectName;
        var url = getUrlPrefix() + "script/run/getMaintenanceData"
        this.dialog.show();
        this.dialog.showMask();
        this.requester.doRequest(url, {objectName:objectName}, this.getDataSuccess)
    },
    handleSave: function() {
        YAHOO.util.Connect.setForm(this.activeForm);
        var url = getUrlPrefix() + "script/run/putInMaintenance"
        this.dialog.showMask();
        this.requester.doPostRequest(url, {}, this.saveSuccess)
    },
    calculateFireTimes: function() {
        YAHOO.util.Connect.setForm(this.activeForm);
        YAHOO.util.Connect._sFormData += "&mode=calculateFireTimes"
        var url = getUrlPrefix() + "script/run/putInMaintenance"
        this.dialog.showMask();
        this.requester.doPostRequest(url, {}, this.fireTimesSuccess)
    },
    hide:function() {
        this.clearInputFields();
        this.dialog.hide()
    },
    clearInputFields: function() {
        this.maintenanceForm.objectName.value = ''
        this.maintenanceForm.inMaintenance.checked = false
        this.maintenanceForm.minutes.value = '';
        this.maintenanceForm.info.value = '';
        this.infoView.innerHTML = '';
        var current = new Date();
        var currentDate = (current.getMonth() + 1) + '/' + current.getDate() + '/' + current.getFullYear()
        this.scheduleForm.starting.value = currentDate;
        this.scheduleForm.ending.value = currentDate;
        this.scheduleForm.schedStarting.value = currentDate;
        this.scheduleForm.schedEnding.value = currentDate;
        this.scheduleForm.info.value = '';
        this.clearScheduleGrid();
        this.scheduleForm.scheduleType.selectedIndex = 0;
        this.scheduleTypeChanged();
        this.changeView(true, this.body.getElementsByTagName('ul')[0].getElementsByTagName('li'))
    },
    processSuccess: function(response) {
        this.dialog.hideMask();
        this.events["success"].fireDirect(this);
    },
    processFailure: function(errors, statusCodes) {
        this.dialog.hideMask();
        this.events["error"].fireDirect(this, errors);
    },
    getDataSuccess:function(response, containsErrors) {
        if (!containsErrors) {
            var xmlDoc = response.responseXML;
            var inMaintenanceNodes = xmlDoc.getElementsByTagName('InMaintenance');
            if (inMaintenanceNodes.length > 0) {
                var inMaintenanceNode = inMaintenanceNodes[0]
                this.maintenanceForm.objectName.value = inMaintenanceNode.getAttribute("objectName")
                this.maintenanceForm.inMaintenance.checked = true
                var source = inMaintenanceNode.getAttribute('source')
                var starting = inMaintenanceNode.getAttribute('starting')
                var ending = inMaintenanceNode.getAttribute('ending')
                this.infoView.innerHTML = '<table><tbody><tr class="prop"><td valign="top" class="name"><label>Source:</label></td><td>' + source + '</td></tr>' +
                                          '<tr class="prop"><td valign="top" class="name"><label>Started:</label></td><td valign="top">' + starting + '</td></tr>' +
                                          '<tr class="prop"><td valign="top" class="name"><label>Ending:</label></td><td valign="top">' + ending + '</td></tr></tbody></table>'
            }
            this.clearScheduleGrid();
            var scheduleNodes = xmlDoc.getElementsByTagName('Schedule');
            var data = [];
            for (var i = 0; i < scheduleNodes.length; i++) {
                var node = scheduleNodes[i];
                data[data.length] = {id:node.getAttribute('id'), type:node.getAttribute('type'), maintStarting:node.getAttribute('maintStarting'), maintEnding:node.getAttribute('maintEnding'),
                    schedStarting:node.getAttribute('schedStarting'), schedEnding:node.getAttribute('schedEnding'), details:node.getAttribute('details')}
            }
            this.scheduleGrid.addRows(data)
        }
    },
    clearScheduleGrid : function() {
        var length = this.scheduleGrid.getRecordSet().getLength()
        this.scheduleGrid.deleteRows(0, length)
    },
    saveSuccess: function(response, containsErrors) {
        if (!containsErrors) {
            this.hide();
        }
    },
    scheduleDeleteSuccess: function(response, containsErrors) {
        if (!containsErrors) {
            var url = getUrlPrefix() + "script/run/getMaintenanceData"
            this.dialog.showMask();
            this.requester.doRequest(url, {objectName:this.currentObject}, this.getDataSuccess)
        }
    },
    fireTimesSuccess : function(response, containsErrors) {
        if (!containsErrors) {
            SelectUtils.clear(this.fireTimesList);
            var fireTimes = response.responseXML.getElementsByTagName('FireTime');
            for (var i = 0; i < fireTimes.length; i++) {
                var fireTime = fireTimes[i].getAttribute('time');
                SelectUtils.addOption(this.fireTimesList, fireTime, fireTime)
            }
        }
        else{
            SelectUtils.clear(this.fireTimesList)
        }
    },
    scheduleTypeChanged: function() {
        var schedTypeSelect = this.scheduleForm.scheduleType;
        var schedType = schedTypeSelect.options[schedTypeSelect.selectedIndex].text;
        var scheduleTypeViews = YAHOO.util.Dom.getElementsByClassName('scheduleType', 'div', this.scheduleView);
        if (!this.currentScheduleTypeView) {
            this.currentScheduleTypeView = scheduleTypeViews[0];
        }
        YAHOO.util.Dom.setStyle(this.currentScheduleTypeView, 'display', 'none');
        var viewIndex = 0
        var willShowCommonView = true;
        switch (schedType) {
            case "Once": willShowCommonView = false;break;
            case "Daily": viewIndex = 2; break;
            case "Weekly": viewIndex = 3; break;
            case "Monthly": viewIndex = 4; break;
        }
        this.currentScheduleTypeView = scheduleTypeViews[viewIndex];
        YAHOO.util.Dom.setStyle(scheduleTypeViews[1], 'display', willShowCommonView ? '' : 'none');
        YAHOO.util.Dom.setStyle(scheduleTypeViews[5], 'display', willShowCommonView ? '' : 'none');
        YAHOO.util.Dom.setStyle(this.currentScheduleTypeView, 'display', '');
        SelectUtils.clear(this.fireTimesList);
    },
    showCalendar: function(buttonEl, buttonIndex) {
        if (!this.calendar) {
            var calendar = new YAHOO.widget.Calendar("maintenanceFormCalendar", {
                iframe:false,
                hide_blank_weeks:true
            });
            calendar.renderEvent.subscribe(function() {
                this.calendarDlg.fireEvent("changeContent");
            }, this, true);
            calendar.render();
            calendar.selectEvent.subscribe(function(type, args, obj) {
                var inputField;
                switch (calendar.buttonIndex) {
                    case 0:inputField = this.scheduleForm.starting;break;
                    case 1:inputField = this.scheduleForm.ending;break;
                    case 2:inputField = this.scheduleForm.schedStarting;break;
                    case 3:inputField = this.scheduleForm.schedEnding;break;
                    default:break;
                }
                var dates = args[0];
                var date = dates[0];
                var year = date[0], month = date[1], day = date[2];
                inputField.value = month + '/' + day + '/' + year;
                calendar.cfg.setProperty("pagedate", calendar.today);
                calendar.render();
                this.calendarDlg.hide();
            }, this, true);

            this.calendar = calendar;
        }
        this.calendar.buttonIndex = buttonIndex;
        this.calendarDlg.cfg.setProperty("context", [buttonEl, 'tl', 'bl']);
        this.calendarDlg.show();
        YAHOO.rapidjs.component.OVERLAY_MANAGER.bringToTop(this.calendarDlg);
    }
}

window.inMaintenanceForm = new YAHOO.rapidjs.component.InMaintenanceForm();
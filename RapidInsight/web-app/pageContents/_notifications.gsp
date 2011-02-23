<%@ page import="auth.RsUser; message.RsMessageRule" %>
<style type="text/css">
    #ruleCalendarExceptionDlg .bd form {
        clear:left;
    }
    #ruleCalendarExceptionDlg_c.yui-overlay-hidden table {
        *display:none;
    }
    #ruleCalendarExceptionDlg .bd {
        padding:0;
    }
    .r-toolbar-folderButton {
      color: #083772;
      line-height:14px;
      font-weight: bold;
      background: transparent url( ../images/rapidjs/component/tools/folder.gif) no-repeat scroll left;
    }
</style>
<script>
	var ruleTree = YAHOO.rapidjs.Components['ruleTree'];
	var calendarsGrid = YAHOO.rapidjs.Components['calendars'];

    <g:if test="${auth.RsUser.hasRole(session.username, auth.Role.ADMINISTRATOR)}">
          var ruleAddButton3 = ruleTree.addToolbarButton({
              className:'r-toolbar-addButton',
              scope:this,
              tooltip: 'Add System Rule',
              text:'Add System Rule',
              click:function() {
                  var addRuleForm = YAHOO.rapidjs.Components['addRuleForm'];
                  addRuleForm.show(createURL('rsMessageRuleForm.gsp',{mode:'create', ruleType:'system'}),'Add System Rule');
                  addRuleForm.popupWindow.show();
              }
          });
          YAHOO.util.Dom.setStyle(ruleAddButton3.inner, 'width', '120px')

          var ruleAddButton2 = ruleTree.addToolbarButton({
              className:'r-toolbar-addButton',
              scope:this,
              tooltip: 'Add Rule For User And Groups',
              text:'Add Rule For User And Groups',
              click:function() {
                  var addRuleForm = YAHOO.rapidjs.Components['addRuleForPeopleForm'];
                  addRuleForm.show(createURL('rsMessageRuleForm.gsp',{mode:'create', ruleType:'public'}),'Add Rule For User And Groups');
                  addRuleForm.popupWindow.show();
              }
          });
          YAHOO.util.Dom.setStyle(ruleAddButton2.inner, 'width', '190px')

          var ruleAddButton = ruleTree.addToolbarButton({
              className:'r-toolbar-addButton',
              scope:this,
              tooltip: 'Add Rule For ${session.username}',
              text:'Add Rule For ${session.username}',
              click:function() {
                  var addRuleForm = YAHOO.rapidjs.Components['addRuleForm'];
                  addRuleForm.show(createURL('rsMessageRuleForm.gsp',{mode:'create', ruleType:'self'}),'Add Rule For ${session.username}');
                  addRuleForm.popupWindow.show();
              }
          });
          YAHOO.util.Dom.setStyle(ruleAddButton.inner, 'width', '140px')

          var queueButton = ruleTree.addToolbarButton({
              className:'r-toolbar-folderButton',
              scope:this,
              tooltip: 'Notification Queue',
              text:'Notification Queue',
              click:function() {
                  var notificationsGrid = YAHOO.rapidjs.Components['notificationsGrid'];
                  notificationsGrid.popupWindow.show();
                  notificationsGrid.poll();
              }
          });
          YAHOO.util.Dom.setStyle(queueButton.inner, 'width', '125px')
    </g:if>
    <g:else>
       var ruleAddButton = ruleTree.addToolbarButton({
            className:'r-toolbar-addButton',
            scope:this,
            tooltip: 'Add Rule',
            text:'Add Rule',
            click:function() {
                var addRuleForm = YAHOO.rapidjs.Components['addRuleForm'];
                addRuleForm.show(createURL('rsMessageRuleForm.gsp',{mode:'create', ruleType:'self'}),'Add Rule');
                addRuleForm.popupWindow.show();
            }
        });
        YAHOO.util.Dom.setStyle(ruleAddButton.inner, 'width', '70px')
    </g:else>

    var calendarAddButton = calendarsGrid.addToolbarButton({
        className:'r-toolbar-addButton',
        scope:this,
        tooltip: 'Add Calendar',
        text:'Add Calendar',
        click:function() {
            var addCalendarForm = YAHOO.rapidjs.Components['addCalendarForm'];
        	addCalendarForm.show(createURL('rsMessageRuleCalendarForm.gsp',{}),'Add Calendar');
            addCalendarForm.popupWindow.show();
        }
    });
    YAHOO.util.Dom.setStyle(calendarAddButton.inner, 'width', '95px')

    var calendarDlg = new YAHOO.widget.Dialog(YAHOO.ext.DomHelper.append(document.body, {tag:'div', id:"ruleCalendarExceptionDlg"}), {
        visible:false,
        draggable:false,
        close:true,width:160

    });
    calendarDlg.setHeader('Pick A Date');
    calendarDlg.setBody('<div id="ruleCalendarExceptionBody"></div>')
    calendarDlg.render(document.body);
    calendarDlg.showEvent.subscribe(function() {
        calendarDlg.fireEvent("changeContent");
    });
    YAHOO.rapidjs.component.OVERLAY_MANAGER.register(calendarDlg)
    window.calendarExceptionDlg = calendarDlg;
    window.showExceptionCalendar = function(selectEl, inputEl, buttonEl) {
        window.exceptionSelectEl = selectEl;
        window.exceptionInputEl = inputEl;
        if (!window.exceptionCalendar) {
            var calendar = new YAHOO.widget.Calendar("ruleCalendarExceptionBody", {
                iframe:false,
                hide_blank_weeks:true
            });
            calendar.renderEvent.subscribe(function() {
                window.calendarExceptionDlg.fireEvent("changeContent");
            }, this, true);
            calendar.render();
            calendar.selectEvent.subscribe(function(type, args, obj) {
                var dates = args[0];
                var date = dates[0];
                var year = date[0], month = date[1], day = date[2];
                var exception = month + '/' + day + '/' + year;
                SelectUtils.addOption(window.exceptionSelectEl, exception, exception)
                var inputValueArray = [];
                var options =window.exceptionSelectEl.options 
                for(var i=0; i< options.length; i++){
                    inputValueArray.push(options[i].text)
                }
                window.exceptionInputEl.value = inputValueArray.join(',');
                calendar.cfg.setProperty("pagedate", calendar.today);
                calendar.render();
                this.calendarDlg.hide();
            }, this, true);

            window.exceptionCalendar = calendar;
        }
        window.calendarExceptionDlg.cfg.setProperty("context", [buttonEl, 'tl', 'bl']);
        window.calendarExceptionDlg.show();
        YAHOO.rapidjs.component.OVERLAY_MANAGER.bringToTop(window.calendarExceptionDlg);
    }

    var toolbarEl = calendarsGrid.toolbar.el;
    var wrps = YAHOO.util.Dom.getElementsByClassName('wrp', 'div', toolbarEl);
    for(var i = 0; i< 5; i++){
        YAHOO.util.Dom.setStyle(wrps[i], 'display', 'none');
    }
    calendarsGrid.defaultQuery = 'username:${session.username.exactQuery()} OR (username:${RsUser.RSADMIN.exactQuery()} AND isPublic:true)';
    calendarsGrid.renderCellFunction = function(key, value, data, el){
        if(key == 'starting' || key == 'ending'){
           try{
                var d = new Date();
                d.setTime(parseFloat(value))
                return d.format("H:i");
            }
            catch(e)
            {}
        }
        return value;
    }
    calendarsGrid.poll();
    ruleTree.poll();

</script>

<script>
	var notificationsGrid = YAHOO.rapidjs.Components['notificationsGrid'];
    function renderNotificationCellFunction(key, value, data, el){
        if(key == "insertedAt" || key == 'firstSentAt' || key == 'sentAt' || key == 'rsInsertedAt' || key == 'rsUpdatedAt'){
            if(value == "0" || value == "")
            {
                return "never"
            }
            else
            {
                try
                {
                    var d = new Date();
                    d.setTime(parseFloat(value))
                    return d.format("d M H:i:s");
                }
                catch(e)
                {}
            }
        }
        else if(key == "state")
        {
            switch(value)
            {
                case '0' : return "Delay";
                case '1' : return "Ready";
                case '2' : return "Aborted By Delay";
                case '3' : return "Sent";
                case '4' : return "Event Missing";
                case '5' : return "Error";
                case '6' : return "Error Limit";
                default  : return "";
            }
        }
        return value;
     }
     notificationsGrid.renderCellFunction = renderNotificationCellFunction;
	 notificationsGrid.poll();
</script>
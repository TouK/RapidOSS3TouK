YAHOO.rapidjs.designer.DesignerWizard = function() {
    this.dialog = null;
    this.scenario = null;
    this.componentNode = null;
    this.scenarios = {};
};

YAHOO.rapidjs.designer.DesignerWizard.prototype = {
    render:function() {
        var dh = YAHOO.ext.DomHelper;
        this.dialog = new YAHOO.rapidjs.component.Dialog({
            title:'Designer Wizards',
            width:500,
            height:300,
            minHeight:300,
            y:100,
            x:400,
            resizable:false,
            modal:true,
            effect:{effect:YAHOO.widget.ContainerEffect.FADE,duration:0.25},
            buttons:[
                {text:'< Back', handler:this.back, scope:this, disabled:true},
                {text:'Next >', handler:this.next, scope:this},
                {text:'Finish', handler:this.finish, scope:this, disabled:true},
                {text:'Cancel', handler:this.cancel, scope:this}
            ]
        })
        YAHOO.util.Dom.addClass(this.dialog.body, 'r-designer-wizard');
        YAHOO.util.Dom.setStyle(this.dialog.getButtons()[3].get("element"), 'margin-left', '20px');
        this.wrp = dh.append(this.dialog.body, {tag:'div', cls:'r-designer-wizards-wrp'}, true)
        this.linksWrp = dh.append(this.wrp.dom, {tag:'div', cls:'r-designer-wizards-links'})
        this.scenarioWrp = dh.append(this.wrp.dom, {tag:'div', style:'display:none'})
        YAHOO.util.Event.addListener(this.linksWrp, 'click', this.bodyClicked, this, true);
    },
    bodyClicked: function(e) {
        var target = YAHOO.util.Event.getTarget(e);
        if (target.nodeName.toLowerCase() == 'a') {
            var scenario = target.firstChild.nodeValue;
            this.startScenario(scenario)
        }
    },
    show: function(componentNode) {
        this.componentNode = componentNode;
        if (!this.dialog) {
            this.render();
        }
        this.linksWrp.innerHTML = '';
        var scenarios = UIConfig.getWizardScenariosForComponent(DesignerUtils.getItemType(componentNode));
        var html = [];
        for (var i = 0; i < scenarios.length; i++) {
            html[html.length] = '<li><a>' + scenarios[i] + '</a></li>';
        }
        this.linksWrp.innerHTML = '<ul>' + html.join('') + '</ul>';
        this.dialog.show();
        this.adjustHeight();
        this.handleWizardButtons();
    },
    hide:function() {
        this.dialog.hide();
    },
    startScenario: function(scenario) {
        YAHOO.util.Dom.setStyle(this.linksWrp, 'display', 'none')
        YAHOO.util.Dom.setStyle(this.scenarioWrp, 'display', '')
        var sc = this.scenarios[scenario];
        if (!sc) {
            var scenarioConfig = UIConfig.getWizardScenarios()[scenario];
            var scenarioConstructor = scenarioConfig.constructor
            sc = new scenarioConstructor(this.scenarioWrp);
            this.scenarios[scenario] = sc;
        }
        sc.start(this.componentNode);
        this.scenario = sc;
        this.dialog.show();
        this.adjustHeight();
        this.dialog.setTitle(scenario)
        this.handleWizardButtons();
    },
    next: function() {
        this.scenario.next();
        this.handleWizardButtons();
    },
    back: function() {
        this.scenario.back();
        if (this.scenario.currentStep == -1) {
             this.backToInit();
        }
        this.handleWizardButtons();
    },
    backToInit: function() {
        this.dialog.setTitle("Designer Wizards")
        YAHOO.util.Dom.setStyle(this.linksWrp, 'display', '')
        YAHOO.util.Dom.setStyle(this.scenarioWrp, 'display', 'none')
        this.scenario = null;
    },
    finish : function() {
        this.scenario.finish();
        this.hide();
        this.backToInit();
    },
    cancel : function() {
        if (this.scenario) {
            this.scenario.cancel();
        }
        this.hide();
        this.backToInit();
    },
    handleWizardButtons: function() {
        var buttons = this.dialog.getButtons();
        if (this.scenario) {
            YAHOO.util.Dom.setStyle(buttons[0].get('element'), 'display', '')
            YAHOO.util.Dom.setStyle(buttons[1].get('element'), 'display', '')
            YAHOO.util.Dom.setStyle(buttons[2].get('element'), 'display', '')
            if (this.scenario.canFinish()) {
                buttons[2].set('disabled', false)
                buttons[1].set('disabled', true)
            }
            else if (this.scenario.hasNext()) {
                buttons[2].set('disabled', true)
                buttons[1].set('disabled', false)
            }
            if (this.scenario.hasBack()) {
                buttons[0].set('disabled', false)
            }
        }
        else {
            YAHOO.util.Dom.setStyle(buttons[0].get('element'), 'display', 'none')
            YAHOO.util.Dom.setStyle(buttons[1].get('element'), 'display', 'none')
            YAHOO.util.Dom.setStyle(buttons[2].get('element'), 'display', 'none')
        }
    },

    adjustHeight:function() {
        this.dialog.adjustHeight(this.wrp.getHeight());
    }
}

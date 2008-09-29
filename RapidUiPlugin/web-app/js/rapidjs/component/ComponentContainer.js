YAHOO.namespace('rapidjs', 'rapidjs.component');
YAHOO.rapidjs.component.ComponentContainer = function(container, config) {
    this.id = config.id;
    if (!this.id) {
        throw new Error("Every component should have an id.");
    }
    this.container = container;
    this.config = config;
    this.url = config.url;
    this.title = config.title;
    this.toolbar = null;
    this.events = {
        'contextmenuclicked': new YAHOO.util.CustomEvent('contextmenuclicked'),
        'loadstatechanged' :new YAHOO.util.CustomEvent('loadstatechanged'),
        'success' :new YAHOO.util.CustomEvent('success'),
        'error' :new YAHOO.util.CustomEvent('error')
    };
    YAHOO.rapidjs.Components[this.id] = this;
    var bookmarkedHistoryState = YAHOO.util.History.getBookmarkedState(this.id);
    var initialHistoryState = bookmarkedHistoryState || this.getInitialHistoryState();
    YAHOO.util.History.register(this.id, initialHistoryState, function (state) {
        this._historyChanged(state);
    }, this, true);
    this.historyChangeFromSave = false;
};

YAHOO.rapidjs.component.ComponentContainer.prototype =
{


    clearData: function() {
    },

    handleVisible : function() {
    },

    handleUnvisible: function() {
    },

    addToolbarButton: function(buttonConfig) {
        if (this.toolbar) {
            return this.toolbar.addButton(buttonConfig)
        }
    },

    
    saveHistoryChange : function(newHistoryState) {
        this.historyChangeFromSave = true;
        try {
            var currentState = YAHOO.util.History.getCurrentState(this.id);
            if (newHistoryState != currentState) {
                YAHOO.util.History.navigate(this.id, newHistoryState);
            }
        }
        catch(e) {
        }
    },

    historyChanged: function(state){
        
    },

    _historyChanged: function(state){
        if(this.historyChangeFromSave){
            this.historyChangeFromSave = false;
            return;
        }
        this.historyChanged(state);
    },

    getInitialHistoryState: function() {
        return "noAction";
    },

    resize: function(width, height){

    }

};

YAHOO.rapidjs.Components = {};

YAHOO.util.Event.onDOMReady(function() {
    var firstChild = document.body.firstChild;
    if (firstChild) {
        var historyInput = document.createElement('input');
        historyInput.id = 'yui-history-field';
        historyInput.type = "hidden";
        document.body.insertBefore(historyInput, document.body.firstChild);
        if (YAHOO.util.Event.isIE) {
            var historyIframe = document.createElement('iframe');
            historyIframe.id = 'yui-history-iframe';
            historyIframe.src = "js/yui/history/assets/blank.html"
            document.body.insertBefore(historyIframe, historyInput);
        }
    }
    try {
        YAHOO.util.History.initialize("yui-history-field", "yui-history-iframe");
    } catch (e) {
    }
});




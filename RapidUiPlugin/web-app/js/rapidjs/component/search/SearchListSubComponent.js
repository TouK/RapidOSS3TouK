YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.search');
YAHOO.rapidjs.component.search.SearchListSubComponent = function(searchList) {
    this.searchList = searchList;
    this.events = {
        "pollCompleted":new YAHOO.util.CustomEvent('pollCompleted'),
        "pollStarted":new YAHOO.util.CustomEvent('pollStarted')
    }
};


YAHOO.rapidjs.component.search.SearchListSubComponent.prototype = {
    render : function(container) {
    },
    preparePoll: function()
    {

    },
    poll: function()
    {
        this.firePollStarted();
        this.firePollCompleted();
    },

    firePollStarted: function()
    {
        this.events.pollStarted.fireDirect(this);
    },
    firePollCompleted: function()
    {
        this.events.pollCompleted.fireDirect(this);        
    }
}
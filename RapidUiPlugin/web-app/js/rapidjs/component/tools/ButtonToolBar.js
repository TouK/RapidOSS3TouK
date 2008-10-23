YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.tool');
YAHOO.rapidjs.component.tool.ButtonToolBar = function(container, config){
    var title = "";
    if(config.title){
        title = config.title;
    }
    this.el = YAHOO.ext.DomHelper.append(container, {tag:'div', cls:'r-buttontoolbar',
        html:'<span class="r-buttontoolbar-text">' + title+ '</span><div class="r-buttontoolbar-tools"></div>'});
    this.titleEl = this.el.childNodes[0]; 
    this.toolsEl = this.el.childNodes[1];
};

YAHOO.rapidjs.component.tool.ButtonToolBar.prototype = {
    addButton : function(buttonConfig){
         var buttonContainer = YAHOO.ext.DomHelper.append(this.toolsEl, {tag:'div', cls:'r-buttontoolbar-button'});
         return new YAHOO.rapidjs.component.Button(buttonContainer, buttonConfig);
    },

    addTool : function(basicTool){
         var buttonContainer = YAHOO.ext.DomHelper.append(this.toolsEl, {tag:'div', cls:'r-buttontoolbar-button'});
        buttonContainer.appendChild(basicTool.button.el.dom);
        basicTool.containerChanged(buttonContainer);
    },

    setTitle: function(title){
        this.titleEl.innerHTML = title;
    }
}
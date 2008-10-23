YAHOO.namespace('rapidjs', 'rapidjs.component');
YAHOO.rapidjs.component.PopupWindow = function(component, config) {
    this.component = component;
    this.component.popupWindow = this;
    this.title = '&#160;'
    YAHOO.ext.util.Config.apply(this, config);
    this.dialog = new YAHOO.rapidjs.component.Dialog({
        width:this.width,
        height: this.height,
        minHeight:this.minHeight,
        minWidth: this.minWidth,
        maxWidth: this.maxWidth,
        maxHeight: this.maxHeight,
        x: this.x,
        y: this.y,
        title: this.title,
        close: true
    });
    this.setTitle(this.title);
    this.dialog.events['resize'].subscribe(this.windowResized, this, true);
    this.dialog.body.appendChild(this.component.container);
    this.windowResized(this.dialog.bodyEl.getWidth(), this.dialog.bodyEl.getHeight());

};

YAHOO.rapidjs.component.PopupWindow.prototype = {
   windowResized: function(width, height){
       this.component.resize(width, height);
   },
   show: function(){
       this.dialog.show();
       this.windowResized(this.dialog.bodyEl.getWidth(), this.dialog.bodyEl.getHeight());
       this.component.handleVisible();
   },
   hide: function(){
       this.dialog.hide();
       this.component.handleUnvisible();
   },

   setTitle: function(title){
      this.title = title;
      this.dialog.setTitle(title);
      if(this.component.toolbar){
          this.component.toolbar.setTitle("");
      }
   },

   isVisible: function(){
      return this.dialog.isVisible(); 
   }
}
YAHOO.widget.Layout.prototype.destroy = function(willRemoveElement) {
    var Event = YAHOO.util.Event,
        Lang = YAHOO.lang;
    var par = this.get('parent');
    if (par) {
        par.removeListener('resize', this.resize, this, true);
    }
    Event.removeListener(window, 'resize', this.resize, this, true);

    this.unsubscribeAll();
    for (var u in this._units) {
        if (Lang.hasOwnProperty(this._units, u)) {
            if (this._units[u]) {
                this._units[u].destroy(true);
            }
        }
    }

    Event.purgeElement(this.get('element'));
    if(willRemoveElement){
        this.get('parentNode').removeChild(this.get('element'));
    }
    delete YAHOO.widget.Layout._instances[this.get('id')];
            //Brutal Object Destroy
    for (var i in this) {
        if (Lang.hasOwnProperty(this, i)) {
            this[i] = null;
            delete this[i];
        }
    }

    if (par) {
        par.resize();
    }
}
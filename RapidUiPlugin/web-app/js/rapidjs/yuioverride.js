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
    if (willRemoveElement) {
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
};

YAHOO.widget.DropdownCellEditor.prototype.renderForm = function() {
    if (!this.dropdown) {
        var elDropdown = this.getContainerEl().appendChild(document.createElement("select"));
        elDropdown.style.zoom = 1;
        this.dropdown = elDropdown;
    }
    this.dropdown.innerHTML = '';
    if (YAHOO.lang.isArray(this.dropdownOptions)) {
        var dropdownOption, elOption;
        for (var i = 0, j = this.dropdownOptions.length; i < j; i++) {
            dropdownOption = this.dropdownOptions[i];
            elOption = document.createElement("option");
            elOption.value = (YAHOO.lang.isValue(dropdownOption.value)) ?
                             dropdownOption.value : dropdownOption;
            elOption.innerHTML = (YAHOO.lang.isValue(dropdownOption.label)) ?
                                 dropdownOption.label : dropdownOption;
            elOption = this.dropdown.appendChild(elOption);
        }

        if (this.disableBtns) {
            this.handleDisabledBtns();
        }
    }
}

YAHOO.widget.DataTable.prototype.findRecord = function(key, value) {
    var records = this.getRecordSet().getRecords();
    for (var i = 0; i < records.length; i++) {
        var record = records[i];
        if (value == record.getData(key)) {
            return record;
        }
    }
    return null;
}


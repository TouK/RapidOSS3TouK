YAHOO.rapidjs.data.CaseInsensitiveMap = function(){
	this.lowerCaseMap = {};
	this.data = {};
};

YAHOO.rapidjs.data.CaseInsensitiveMap.prototype = {
	put:function(key, value){
		var realKey = key;
		var ciKey = this.getCaseInsensitiveKey(key);
		if(this.lowerCaseMap[ciKey] != null){
			realKey = this.lowerCaseMap[ciKey];
		}
		else{
			this.lowerCaseMap[ciKey] = realKey;
		}
		this.data[realKey] = value;
	}, 
	get: function(key){
		var ciKey = this.getCaseInsensitiveKey(key);
		var realKey = this.lowerCaseMap[ciKey];
		if(realKey != null){
			return this.data[realKey];
		}
		else{
			return null;
		} 
	},
	remove : function(key){
		var ciKey = this.getCaseInsensitiveKey(key);
		var realKey = this.lowerCaseMap[ciKey];
		if(realKey != null){
			delete this.lowerCaseMap[ciKey];
			delete this.data[realKey];
		}
	}, 
	
	getCaseInsensitiveKey: function(key){
		return key.toLowerCase();
	}
};
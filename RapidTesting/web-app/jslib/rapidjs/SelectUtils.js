YAHOO.rapidjs.riadmin.SelectUtils = new function(){
	this.collectSelectedIndicesFromSelect = function(aSelect)
	{
		var selectedIndices = new Array();
		for(var i = 0 ; i < aSelect.options.length ; i++)
		{
			if(aSelect.options[i].selected == true)
			{
				selectedIndices[selectedIndices.length] = i;
			}
		}
		return selectedIndices;
	};
	
	this.moveFromSelectToSelect = function(index, fromSelect, toSelect)
	{
		if(index > -1)
		{
			var associatedOption = fromSelect.options[index];
			fromSelect.remove(index);
			try
			{
				toSelect.add(associatedOption, null);//to the end of the select		
			}
			catch(ex)
			{
				toSelect.add(associatedOption);//IE only
			}
			
		}
	};
	
	this.moveAllSelectedsFromSelectToSelect = function(fromSelect, toSelect)
	{
		var arrayOfSelectedIndices = this.collectSelectedIndicesFromSelect(fromSelect);
		for(var i = arrayOfSelectedIndices.length - 1 ; i >= 0 ; i--)
		{
			this.moveFromSelectToSelect(arrayOfSelectedIndices[i], fromSelect, toSelect);	
		}
	};
	
	this.collectValuesFromSelect = function(select)
	{
		var values = new Array();
		for(var i = 0 ; i < select.options.length ; i++)
		{
			values[i] = select.options[i].value;
		}
		return values;
	};
	
	this.addOptionBefore = function(select, text, value, index)
	{
		if(index < select.options.length){
			var option = document.createElement("OPTION");
			option.value = value;
			option.text = text;
			var refOption = select.options[index];
			try
			{
				select.add(option, refOption);	
			}
			catch(ex)
			{
				select.add(option, index);	//IE only
			}	
		}
		
	};
	this.addOption = function(select, text, value)
	{
		var option = document.createElement("OPTION");
		option.value = value;
		option.text = text;
		try
		{
			select.add(option, null);	
		}
		catch(ex)
		{
			select.add(option);	//IE only
		}
		
	};
	
	this.clear = function(select)
	{
		for(var i = select.length - 1 ; i >= 0 ; i--)
		{
			select.remove(i);		
		}
	};
	this.populateSelect = function(select, options){
		for(var optionText in options) {
			var optionValue = options[optionText];
			this.addOption(select, optionText, optionValue);
		}
	};
	
	this.selectTheValue =  function(select, value, defaultIndex)
	{
		for(var i = 0 ; i < select.options.length ; i++)
		{
			if(select.options[i].value == value)
			{
				select.selectedIndex = i;
				return;
			}
		}
		select.selectedIndex = defaultIndex;
	};
	this.remove =  function(select, value)
	{
		for(var i = 0 ; i < select.options.length ; i++)
		{
			if(select.options[i].value == value)
			{
				select.remove(i);
				return;
			}
		}
	};
};

//define a global shortcut for the utility class
var SelectUtils = YAHOO.rapidjs.riadmin.SelectUtils;
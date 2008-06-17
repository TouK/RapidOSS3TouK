function renderAll(login,password)
{
	requestConfiguration();
}
function render(configuration) 
{
	try
	{
		var configurationJsonobject =xml2json(configuration,"");
		configurationJsonobject = '('+configurationJsonobject+')';
		configurationJsonobject = eval(configurationJsonobject);
		if(!configurationJsonobject["Errors"])
		{
			var urls = urlCreator(eval("configurationJsonobject.TestPluginConfig[0].UiConfiguration[0].Urls"));
			createMenuActions(urls, eval("configurationJsonobject.TestPluginConfig[0].UiConfiguration[0].Actions"));
			
			var components = componentCreator(urls, eval("configurationJsonobject.TestPluginConfig[0].UiConfiguration[0].Components"));
			var dialogs = dialogCreator(components, eval("configurationJsonobject.TestPluginConfig[0].UiConfiguration[0].Dialogs"));
			linksCreator(components, eval("configurationJsonobject.TestPluginConfig[0].UiConfiguration[0].Links"));
			
			var layouts = layoutCreator(components, eval("configurationJsonobject.TestPluginConfig[0].UiConfiguration[0].Tabs"));
			contextMenuCreator(eval("configurationJsonobject.TestPluginConfig[0].UiConfiguration[0].ContextMenu"));
			getEl('loadingwrapper').remove();
		}
		else
		{
			throw configurationJsonobject.Errors[0].Error[0].Message;
		}
	}
	catch(e)
	{
		alert("Configuration Error : " + e);
	}
}



function processSuccess(response){
	var configuration = response.responseXML;
	render(configuration);
}

function processFailure(response)
{
}


function requestConfiguration() 
{
	var callback = {
		success: processSuccess,
		failure: processFailure
	};
	YAHOO.util.Connect.asyncRequest('GET',CONF_URL , callback, null);
	//this.processSuccess(null);
}




function componentCreator(urlList , componentsSection)
{
	
	var componentClassList = constructComponentList();
	var constructors = constructComponentConstructors();
	
	var components = {};
	componentTools = {};
	if(componentsSection)
	{
		componentsSection =componentsSection[0];
		for(var componentName in componentsSection) {
			var componentConfigs = componentsSection[componentName];
			var constructor = constructors[componentName];
			if(!constructor)
			{
				constructor = this.defaultCreator;
			}
			for(var index=0; index<componentConfigs.length; index++) {
				var componentConfig = componentConfigs[index];
				if(componentConfig['refreshRate']){
					componentConfig['pollInterval'] = componentConfig['refreshRate'];	
				}
				components[componentConfig["id"]]=constructor(urlList, componentClassList[componentName], componentConfig);
				componentTools[componentConfig["id"]] = componentConfig.Tools;
			}
		}
	}
	return components;
}
function urlCreator(urlsSectionArray)
{
	var urls = {};
	if(urlsSectionArray)
	{
		var urlList = urlsSectionArray[0].Url;
		for(var index=0; index<urlList.length; index++) {
				urls[urlList[index]["name"]] = urlList[index]
		}
	}
	return urls;
}
function linksCreator(components, linksSectionArray)
{
	
	if(linksSectionArray)
	{
		var linkList = linksSectionArray[0].Link;
		for(var index=0; index<linkList.length; index++) {
			var from = components[linkList[index].from];
			var to = components[linkList[index].to];
			if(!from)
			{
				throw "Link could not be defined from component with Id <"+linkList[index].from+"> to component with Id <"+linkList[index].to+">. <"+linkList[index].from+"> is not defined.";
			}
			if(!to)
			{
				throw "Link could not be defined from component with Id <"+linkList[index].from+"> to component with Id <"+linkList[index].to+">. <"+linkList[index].to+"> is not defined.";
			}
			var dynamicTileAttr = linkList[index].dynamicTitleAttribute;
			var params = createParams(linkList[index].Params);
			from.addLinkedComponent(to, params.fixedParams, params.dynamicParams, dynamicTileAttr);
		}
	}
}
function layoutCreator(components, tabsSection)
{
	
	var layout = new YAHOO.ext.BorderLayout(document.body, {
	                    center: {
	                    	minSize: 100,
	                        autoScroll: false, 
	                        tabPosition: 'top', 
	       					alwaysShowTabs:true
	                    }});
	layout.beginUpdate();
	if(tabsSection)
	{
		var layoutConfig = tabsSection[0];
		var tabList = layoutConfig["Tab"];
		for(var index=0; index<tabList.length; index++) {
			if(tabList[index]["Layout"])
			{
				layout.add("center", new YAHOO.rapidjs.component.layout.NestedLayoutPanel(_layoutCreator(components, tabList[index]["Layout"]), tabList[index]));	
			}
		}
	}
	layout.getRegion("center").getTabs().titleArea.innerHTML = '<td class="IFountainRapidSuite"><img src="images/RapidInsight.png"/></td>';
	YAHOO.rapidjs.ServerStatus.render(layout.getRegion("center").getTabs().toolsArea);
	new YAHOO.rapidjs.component.layout.LogoutTool(layout, "center");
	new YAHOO.rapidjs.component.layout.UserTool(layout, "center");
	layout.endUpdate();
	var panel =layout.regions['center'].panels.first();
	var tabPanel = layout.regions['center'].tabs;
	var tab = tabPanel.items[panel.getEl().id];
    if(tab == tabPanel.active){
    	layout.regions['center'].setActivePanel(panel);
    }
    else{
		layout.regions['center'].showPanel(panel);
	}
}


function _layoutCreator(components, config, container)
{
	config = config[0];
	var componentClassList = constructComponentList();
	var layoutTypes = ["West","East","Center","North","South"];
	var layoutContainer = container;
	if(container == null)
	{
		layoutContainer = document.createElement("div");
		document.body.appendChild(layoutContainer);
	}
	
	for(var index=0; index<layoutTypes.length; index++) {
		var innerPanel = config[layoutTypes[index]];
		if(innerPanel)
		{
			config[layoutTypes[index].toLowerCase()] = innerPanel[0];
		}
	}
	
	var layout = new YAHOO.ext.BorderLayout(layoutContainer, config);
	layout.beginUpdate();
		for(var index=0; index<layoutTypes.length; index++) {
			var regionName = layoutTypes[index];
			var regionConfig = config[regionName];
			regionName = regionName.toLowerCase();
			if(regionConfig)
			{
				regionConfig = regionConfig[0];
				var layoutComponentsConfig = regionConfig["Component"];
				if(layoutComponentsConfig)
				{
					layoutComponentsConfig = layoutComponentsConfig[0];
					var component = components[layoutComponentsConfig["id"]];
					if(component)
					{
						layout.add(regionName, component.panel);
						var tools = componentTools[layoutComponentsConfig["id"]];
						createTools(tools, layout, regionName, component)
					}
					else
					{
						throw "Component with Id <"+layoutComponentsConfig["id"]+"> is not defined. It cannot be located in layout."
					}
				}
				var nestedLayoutConfig = regionConfig["Layout"];
				if(nestedLayoutConfig)
				{
					var nestedLayout = _layoutCreator(components, nestedLayoutConfig);
					layout.add(regionName, new YAHOO.rapidjs.component.layout.NestedLayoutPanel(nestedLayout) );
				}
				
			}
		}
	layout.endUpdate();
	return layout;
}

function createTools(tools, layout, regionName, component)
{
	var componentClassList = constructComponentList();
	if(tools)
	{
		tools = tools[0];
		for(var toolName in tools) {
			var toolClass = componentClassList[toolName];
			var conf = tools[toolName];
			if(conf)
				conf = conf[0];
			new toolClass(layout, regionName, component, conf);
		}
	}
}

function constructComponentList()
{
	var components = {};
	components["ContextMenu"] = YAHOO.rapidjs.component.menu.ContextMenu;
	components["ScriptAction"] = YAHOO.rapidjs.component.menu.ScriptMenuAction;
	components["MergeAction"] = YAHOO.rapidjs.component.menu.MergeMenuAction;
	components["WindowAction"] = YAHOO.rapidjs.component.menu.WindowMenuAction;
	components["LinkAction"] = YAHOO.rapidjs.component.menu.LinkMenuAction;
	components["MenuItem"] = YAHOO.rapidjs.component.menu.MenuItem;
	
	components["Html"] = YAHOO.rapidjs.component.windows.HtmlWindow;
	components["Tree"] = YAHOO.rapidjs.component.windows.TreeWindow;
	components["FilterTree"] = YAHOO.rapidjs.component.windows.FilterTree;
	components["Grid"] = YAHOO.rapidjs.component.windows.GridWindow;
	components["DeltaGrid"] = YAHOO.rapidjs.component.windows.DeltaGridWindow;
	components["MultiGrid"] = YAHOO.rapidjs.component.windows.MultiGridWindow;
	components["GMap"] = YAHOO.rapidjs.component.windows.GMapWindow;
	components["Map"] = YAHOO.rapidjs.component.windows.MapWindow;
	components["Timeline"] = YAHOO.rapidjs.component.windows.TimelineWindow;
	components["PieChart"] = YAHOO.rapidjs.component.windows.FlexPieChartWindow;
	components["Filter"] = YAHOO.rapidjs.component.windows.FilterWindow;
	components["AutoComplete"] = YAHOO.rapidjs.component.windows.AutoCompleteWindow;
	components["ObjectDetails"] = YAHOO.rapidjs.component.windows.ContainmentWindow;
	components["PopUp"] = YAHOO.rapidjs.component.PopUpWindow;
	components["FilterTool"] = YAHOO.rapidjs.component.layout.FilterTool;
	components["MappingTool"] = YAHOO.rapidjs.component.layout.MappingTool;
	components["HelpTool"] = YAHOO.rapidjs.component.layout.HelpTool;
	components["PollingTool"] = YAHOO.rapidjs.component.layout.PollingTool;
	components["LoadingTool"] = YAHOO.rapidjs.component.layout.LoadingTool;
	components["ErrorTool"] = YAHOO.rapidjs.component.layout.ErrorTool;
	
	
	return components;
}
function constructComponentConstructors()
{
	var componentCreators = {};
	componentCreators["Tree"] = this.treeCreator;
	componentCreators["FilterTree"] = this.treeCreator;
	componentCreators["Grid"] = this.gridCreator;
	componentCreators["DeltaGrid"] = this.gridCreator;
	componentCreators["MultiGrid"] = this.multiGridCreator;
	componentCreators["Map"] = this.mapCreator;
	componentCreators["ObjectDetails"] = this.objectDetailsCreator;
	componentCreators["Filter"] = this.filterCreator;
	componentCreators["GMap"] = this.gmapCreator;
	componentCreators["Timeline"] = this.timelineCreator;
	return componentCreators;
}



function defaultCreator(urlList, componentClass, config)
{
	var compId = config["id"];
	if(urlList[config["url"]])
	{
		config["url"] = urlList[config["url"]]["address"];
	}
	else
	{
		if(componentClass != YAHOO.rapidjs.component.windows.FilterTree){
			throw "No Url with Id <"+config["url"]+"> is defined for component <" +  config["id"] + ">.";	
		}
	}
	
	var container = document.createElement("div");
	container.id = compId;
	document.body.appendChild(container);
	return new componentClass(container, config)
}
function gmapCreator(urlList, componentClass, config){
	if(config['markerActionId']){
		config['markerAction'] = YAHOO.rapidjs.Actions[config['markerActionId']];
		delete config['markerActionId'];
	}
	return defaultCreator(urlList, componentClass, config);
}
function timelineCreator(urlList, componentClass, config){
	if(config['tooltipActionId']){
		config['tooltipAction'] = YAHOO.rapidjs.Actions[config['tooltipActionId']];
		delete config['tooltipActionId'];
	}
	var bands = config.Bands[0].Band;
	delete config.Bands;
	for(var index=0; index<bands.length; index++) {
		var bandInfo = bands[index];
		bandInfo.intervalUnit = getIntervalUnit(bandInfo.intervalUnit);
		bandInfo.intervalPixels = parseInt(bandInfo.intervalPixels, 10);
		if(bandInfo.trackHeight){
			bandInfo.trackHeight = parseFloat(bandInfo.trackHeight);
		}
		if(bandInfo.trackGap){
			bandInfo.trackGap = parseFloat(bandInfo.trackGap);
		}
		if(bandInfo.textWidth != null){
			bandInfo.textWidth = parseInt(bandInfo.textWidth, 10);
		}
		if(bandInfo.syncWith != null){
			bandInfo.syncWith = parseInt(bandInfo.syncWith, 10);
		}
		if(bandInfo.layoutWith != null){
			bandInfo.layoutWith = parseInt(bandInfo.layoutWith, 10);
		}
	}
	config.Bands = bands;
	return defaultCreator(urlList, componentClass, config);
}

function filterCreator(urlList, componentClass, config)
{
	if(config.DefaultFilters)
	{
		var filters = config.DefaultFilters[0].DefaultFilter;	
		if(filters)
		{
			config['defaultFilters'] = filters;
			delete config.DefaultFilters;
		}
	}
	
	var compId = config["id"];
	if(urlList[config["url"]])
	{
		config["url"] = urlList[config["url"]]["address"];
	}
	
	var container = document.createElement("div");
	container.id = compId;
	document.body.appendChild(container);
	return new componentClass(container, config);
}


function treeCreator(urlList, componentClass, config)
{
	if(config.Columns)
	{
		var cols = config.Columns[0].Column;	
		if(cols)
		{
			for(var index=0; index<cols.length; index++) {
				cols[index]["colLabel"] = cols[index]["header"];
				delete cols[index].header;
				cols[index]['sortType'] = getSortType(cols[index]['sortType'], YAHOO.rapidjs.component.tree.sortTypes);
				if(cols[index].type == "image" || cols[index].type == "Image")
				{
					cols[index]["images"] = cols[index].Image;
					delete cols[index].Image;
				}
			}
		}
		delete config.Columns;
		config["columns"] = cols;
		config["nodeId"] = config["keyAttributeName"];
		delete config.keyAttributeName;
	}
	return defaultCreator(urlList, componentClass, config);
}
function gridCreator(urlList, componentClass, config)
{
	if(config.Columns)
	{
		var cols = config.Columns[0].Column;	
		if(cols)
		{
			for(var index=0; index<cols.length; index++) {
				cols[index]["attribute"] = cols[index]["attributeName"];
				delete cols[index].attributeName;
				cols[index]['sortType'] = getSortType(cols[index]['sortType'], YAHOO.ext.grid.DefaultColumnModel.sortTypes);
				if(cols[index].type == "image" || cols[index].type == "Image")
				{
					cols[index]["images"] = cols[index].Image;
					delete cols[index].Image;
				}
				else if(cols[index].type == "link" || cols[index].type == "Link")
				{
					cols[index]["action"]=YAHOO.rapidjs.Actions[cols[index]["actionId"]]
				}
			}
		}
		delete config.Columns;
		config["content"] = cols;
	}
	config["rowColors"] = config["RowColor"];
	delete config.RowColor;
	config["nodeId"] = config["keyAttributeName"];
	delete config.keyAttributeName;
	return defaultCreator(urlList, componentClass, config);
}
function multiGridCreator(urlList, componentClass, config)
{
	if(config.Fields)
	{
		var cols = config.Fields[0].Field;
		var fields = {};	
		if(cols)
		{
			for(var index=0; index<cols.length; index++) {
				var fieldConfig = {};
				var fieldName = cols[index]["name"];
				delete cols[index].name;
				fieldConfig['sortType'] = getSortType(cols[index]['sortType'], YAHOO.ext.grid.DefaultColumnModel.sortTypes);
				fieldConfig['type'] = cols[index]['columnType'];
				fieldConfig['width'] = cols[index]['width'];
				fieldConfig['header'] = cols[index]['header'];
				fieldConfig['align'] = cols[index]['align'];
				fieldConfig["images"] = cols[index].Image;
				delete cols[index].Image;
				if(cols[index].columnType == "link" || cols[index].columnType == "Link")
				{
					fieldConfig["action"]=YAHOO.rapidjs.Actions[cols[index]["actionId"]]
				}
				fields[fieldName] = fieldConfig;
			}
		}
		delete config.Fields;
		config["fields"] = fields;
	}
	if(config.DefaultView)
	{
		var cols = config.DefaultView[0].Column;	
		if(cols)
		{
			for(var index=0; index<cols.length; index++) {
				cols[index]["attribute"] = cols[index]["attributeName"];
				delete cols[index].attributeName;
				if(cols[index]['sortType']){
					cols[index]['sortType'] = getSortType(cols[index]['sortType'], YAHOO.ext.grid.DefaultColumnModel.sortTypes);
				}
				if(cols[index].type == "image" || cols[index].type == "Image")
				{
					cols[index]["images"] = cols[index].Image;
					delete cols[index].Image;
				}
				else if(cols[index].type == "link" || cols[index].type == "Link")
				{
					cols[index]["action"]=YAHOO.rapidjs.Actions[cols[index]["actionId"]]
				}
			}
		}
		delete config.DefaultView;
		config["defaultView"] = cols;
	}
	config["rowColors"] = config["RowColor"];
	delete config.RowColor;
	config["nodeId"] = config["keyAttributeName"];
	delete config.keyAttributeName;
	return defaultCreator(urlList, componentClass, config);
}

function mapCreator(urlList, componentClass, config){
	config["nodeId"] = config["keyAttributeName"];
	config["images"] = config.Image;
	delete config.keyAttributeName;
	return defaultCreator(urlList, componentClass, config);
}
function getSortType(sortType, sortTypes){
	if(sortType == 'Int'){
		return sortTypes.asInt;
	}
	else if(sortType == 'Date'){
		return sortTypes.asDate;
	}
	else if(sortType == 'Float'){
		return sortTypes.asFloat; 
	}
	else if(sortType == 'String'){
		return sortTypes.none; 
	}
	else if(sortType == 'UCString'){
		return sortTypes.asUCString; 
	}
	else{
		return sortTypes.none;
	}
}

function getIntervalUnit(unit){
	if(unit == 'Milisecond'){
		return Timeline.DateTime.MILLISECOND;
	}
	else if(unit == 'Second'){
		return Timeline.DateTime.SECOND;
	}
	else if(unit == 'Minute'){
		return Timeline.DateTime.MINUTE;
	}
	else if(unit == 'Hour'){
		return Timeline.DateTime.HOUR;
	}
	else if(unit == 'Day'){
		return Timeline.DateTime.DAY;
	}
	else if(unit == 'Week'){
		return Timeline.DateTime.WEEK;
	}
	else if(unit == 'Month'){
		return Timeline.DateTime.MONTH;
	}
	else if(unit == 'Year'){
		return Timeline.DateTime.YEAR;
	}
	else if(unit == 'Decade'){
		return Timeline.DateTime.DECADE;
	}
	else if(unit == 'Century'){
		return Timeline.DateTime.CENTURY;
	}
	else if(unit == 'Millennium'){
		return Timeline.DateTime.MILLENNIUM;
	}
	else{
		throw 'Invalid interval unit <' + unit + '>';
	}
}

function objectDetailsCreator(urlList, componentClass, config)
{
	var params = createParams(config.Params);
	config["onDemandParameters"] = params.dynamicParams;
	return defaultCreator(urlList, componentClass, config);
}

function evaluator(jsonNode, oldExpression, newExpression)
{
	if(jsonNode)
	{
		eval("jsonNode."+newExpression +"=jsonNode."+oldExpression);
	}
}


function dialogCreator(components, config)
{
	var dialogs=[];
	var classList = constructComponentList();
	if(config)
	{
		config = config[0];
		var dialogList = config.Dialog;
		for(var index=0; index<dialogList.length; index++) {
			var dialogConfig = dialogList[index];
			var dialogId = dialogConfig["id"];
			var component = components[dialogConfig["componentId"]];
			if(component)
			{
				var dialog =  new classList["PopUp"](component, dialogConfig);
				components[dialogId] = dialog;
				dialogs[dialogId] = dialog;;
				var tools = componentTools[dialogConfig["componentId"]];
				createTools(tools, dialog.layout, "center",component);
			}
			else
			{
				throw "No component with Id <"+dialogConfig["componentId"]+"> defined for Dialog <"+dialogId+">";
			}
		}
	}
	return dialogs;
}
function contextMenuCreator(config)
{
	if(config){
		config = config[0];
		var classList = constructComponentList();
		var ContextMenu = new classList["ContextMenu"]();
		createMenuItems(ContextMenu, config.MenuItem, classList);
		for(var compIndex in YAHOO.rapidjs.Components) {
			var component = YAHOO.rapidjs.Components[compIndex];
			if(component.events && component.events['contextmenuclicked']){
				component.events['contextmenuclicked'].subscribe(ContextMenu.contextMenuClicked, ContextMenu, true);
			}
		}
	}
}
function createMenuActions(urlList, config)
{
	if(config)
	{
		config = config[0];
		var classList = constructComponentList();
		var actionList = config.WindowAction;
		if(actionList)
		for(var index=0; index<actionList.length; index++) {
			var actionConfig = actionList[index];
			actionConfig["name"] = actionConfig["id"];
			delete actionConfig["id"];
			var params = createParams(actionConfig.Params);
			actionConfig.dynamicParams = params.dynamicParams;
			actionConfig.fixedParams = params.fixedParams;
			new classList["WindowAction"](actionConfig);
		}
		actionList = config.ScriptAction;
		if(actionList)
		for(var index=0; index<actionList.length; index++) {
			var actionConfig = actionList[index];
			actionConfig["name"] = actionConfig["id"];
			delete actionConfig["id"];
			var params = createParams(actionConfig.Params);
			actionConfig.dynamicParams = params.dynamicParams;
			actionConfig.fixedParams = params.fixedParams;
			if(urlList[actionConfig["url"]])
			{
				actionConfig["url"] = urlList[actionConfig["url"]]["address"];
			}
			else
			{
				throw "No Url with Id <"+actionConfig["url"]+"> is defined for action <" +  actionConfig["name"] + ">.";
			}
			new classList["ScriptAction"](actionConfig);
		}
		actionList = config.MergeAction;
		if(actionList)
		for(var index=0; index<actionList.length; index++) {
			var actionConfig = actionList[index];
			actionConfig["name"] = actionConfig["id"];
			delete actionConfig["id"];
			var params = createParams(actionConfig.Params);
			actionConfig.dynamicParams = params.dynamicParams;
			actionConfig.fixedParams = params.fixedParams;
			if(urlList[actionConfig["url"]])
			{
				actionConfig["url"] = urlList[actionConfig["url"]]["address"];
			}
			else
			{
				throw "No Url with Id <"+actionConfig["url"]+"> is defined for action <" +  actionConfig["name"] + ">.";
			}
			new classList["MergeAction"](actionConfig);
		}
		actionList = config.LinkAction;
		if(actionList)
		for(var index=0; index<actionList.length; index++) {
			var actionConfig = actionList[index];
			actionConfig["name"] = actionConfig["id"];
			delete actionConfig["id"];
			var params = createParams(actionConfig.Params);
			actionConfig.dynamicParams = params.dynamicParams;
			actionConfig.fixedParams = params.fixedParams;
			if(urlList[actionConfig["url"]])
			{
				actionConfig["url"] = urlList[actionConfig["url"]]["address"];
			}
			else
			{
				throw "No Url with Id <"+actionConfig["url"]+"> is defined for action <" +  actionConfig["name"] + ">.";
			}
			new classList["LinkAction"](actionConfig);
		}
	}
}
function createMenuItems(ContextMenu, config, classList)
{
	if(config)
	{
		for(var index=0; index<config.length; index++) {
			var menuConfig = config[index];
			var menuLabel = menuConfig.label;
			var menuExp = menuConfig.expression;
			var subMenusConfig = menuConfig.SubMenuItem;
			if(subMenusConfig){
				if(menuConfig.actionId){
					throw 'Menu items which have sub menus cannot execute actions. MenuItem: <' + menuLabel + '>.'; 
				}
				else{
					var menuItem = ContextMenu.addMenuItem(menuLabel, null, menuExp);
					for(var i=0; i<subMenusConfig.length; i++) {
						var submenuConfig = subMenusConfig[i];
						var submenuLabel = submenuConfig.label;
						var submenuExp = submenuConfig.expression;
						var subactionId = submenuConfig.actionId;
						menuItem.addSubMenuItem(submenuLabel, YAHOO.rapidjs.Actions[subactionId], submenuExp);
					}	
				}
			}
			else{
				var actionId = menuConfig.actionId;
				ContextMenu.addMenuItem(menuLabel, YAHOO.rapidjs.Actions[actionId], menuExp);	
			}
		}
	}
}


function createParams(config)
{
	var params = {fixedParams:{},dynamicParams:{}};
	if(config)
	{
		var paramsConfig = config[0].Param;
		if(paramsConfig)
		{
			for(var index1=0; index1<paramsConfig.length; index1++) {
				var paramConfig = paramsConfig[index1];
				if(paramConfig.attributeName)
				{
					params.dynamicParams[paramConfig.name] = paramConfig.attributeName;	
				}
				else if(paramConfig.value)
				{
					params.fixedParams[paramConfig.name] = paramConfig.value;	
				}
				
				
			}
		}
	}
	return params;
}


	
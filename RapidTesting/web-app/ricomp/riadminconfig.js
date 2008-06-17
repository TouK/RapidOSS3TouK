function renderAll(login,password)
{
	YAHOO.rapidjs.Login.checkLogin(checkLoginResponseSuccess,login,password);
}
checkLoginResponseSuccess = function(o){
	if(YAHOO.rapidjs.Login.loggedIn(o.responseXML))
	{
		checkAdminStatus();
	}
}

function checkAdminStatus(){
	var url = "/RapidInsight/Group/getInfo";
	var callback=
	{
		success:checkAdminStatusSuccess,
		failure:checkAdminStatusFailure	
	}
	YAHOO.util.Connect.asyncRequest('GET',url,callback,null);
}

function checkAdminStatusSuccess(response){
	var errorIndex = response.responseText.indexOf('AUTH-0015');
	adminLayout = new YAHOO.ext.BorderLayout(document.body, {
		center:{
			autoScroll: false, tabPosition: 'top',minTabWidth: 50, alwaysShowTabs:true
		}
	});
	if(errorIndex > -1){
		renderAuthError();
		
	}
	else{
		render();
	}
	getEl('loadingwrapper').remove();
}
function checkAdminStatusFailure(response){
	
}
function renderAuthError(){
	adminLayout.beginUpdate();
	authErrorPanel = new YAHOO.rapidjs.component.layout.RapidPanel('autherror');
	adminLayout.add('center', authErrorPanel);
	adminLayout.regions['center'].hidePanel(authErrorPanel);
	adminLayout.getRegion("center").getTabs().titleArea.innerHTML = '<td class="IFountainRapidSuite"><img src="../images/RapidInsightAdmin.png"/></td>';
	YAHOO.rapidjs.ServerStatus.render(adminLayout.getRegion("center").getTabs().toolsArea);
	new YAHOO.rapidjs.component.layout.LogoutTool(adminLayout, "center");
	adminLayout.endUpdate();
	adminLayout.regions['center'].hidePanel(authErrorPanel);
}
function render(){
	var errorDialog = new YAHOO.rapidjs.component.dialogs.ErrorDialog();
	adminLayout.beginUpdate();
	connectors = new YAHOO.rapidjs.riadmin.Connectors(errorDialog);
	adminLayout.add('center', connectors.panel);
	users = new YAHOO.rapidjs.riadmin.Users(errorDialog);
	adminLayout.add('center', users.panel);
	groups = new YAHOO.rapidjs.riadmin.Groups(errorDialog);
	adminLayout.add('center', groups.panel);
//	filters =  new YAHOO.rapidjs.riadmin.Filters(errorDialog);
//	adminLayout.add('center',filters.panel);
	datasources = new YAHOO.rapidjs.riadmin.Datasources(errorDialog);
	adminLayout.add('center',datasources.panel);
	scripts = new YAHOO.rapidjs.riadmin.Scripts(errorDialog);
	adminLayout.add('center',scripts.panel);
	adminLayout.getRegion("center").getTabs().titleArea.innerHTML = '<td class="IFountainRapidSuite"><img src="../images/RapidInsightAdmin.png"/></td>';
	YAHOO.rapidjs.ServerStatus.render(adminLayout.getRegion("center").getTabs().toolsArea);
	new YAHOO.rapidjs.component.layout.LogoutTool(adminLayout, "center");
	adminLayout.endUpdate();
	handleRIAdminUI(adminLayout, connectors, users, groups, datasources, scripts);
	adminLayout.regions['center'].showPanel(connectors.panel);
}

function handleRIAdminUI(adminLayout, connectors, users, groups, datasources, scripts){
	var tabPanel = adminLayout.regions['center'].tabs;
	
	var conPanelItem = tabPanel.getTab(connectors.panel.getEl().id);
	YAHOO.ext.DomHelper.append(conPanelItem.inner.dom, {tag:'div', unselectable:'on', cls:'riadmin-con-tabicon'});
	conPanelItem.setWidth(conPanelItem.pnode.getWidth() + 20);
	
	var usersPanelItem = tabPanel.getTab(users.panel.getEl().id);
	YAHOO.ext.DomHelper.append(usersPanelItem.inner.dom, {tag:'div', unselectable:'on', cls:'riadmin-users-tabicon'});
	usersPanelItem.setWidth(usersPanelItem.pnode.getWidth() + 20);
	
	var grPanelItem = tabPanel.getTab(groups.panel.getEl().id);
	YAHOO.ext.DomHelper.append(grPanelItem.inner.dom, {tag:'div', unselectable:'on', cls:'riadmin-groups-tabicon'});
	grPanelItem.setWidth(grPanelItem.pnode.getWidth() + 20);
	
//	var fPanelItem = tabPanel.getTab(filters.panel.getEl().id);
//	YAHOO.ext.DomHelper.append(fPanelItem.inner.dom, {tag:'div', unselectable:'on', cls:'riadmin-filters-tabicon'});
//	fPanelItem.setWidth(fPanelItem.pnode.getWidth() + 20);
	
	var dataPanelItem = tabPanel.getTab(datasources.panel.getEl().id);
	YAHOO.ext.DomHelper.append(dataPanelItem.inner.dom, {tag:'div', unselectable:'on', cls:'riadmin-data-tabicon'});
	dataPanelItem.setWidth(dataPanelItem.pnode.getWidth() + 20);
	
	var sPanelItem = tabPanel.getTab(scripts.panel.getEl().id);
	YAHOO.ext.DomHelper.append(sPanelItem.inner.dom, {tag:'div', unselectable:'on', cls:'riadmin-script-tabicon'});
	sPanelItem.setWidth(sPanelItem.pnode.getWidth() + 20);
}
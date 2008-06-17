function renderAll()
{
	YAHOO.rapidjs.Login.checkLogin(checkLoginResponseSuccess);
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
	var errorIndex = response.responseText.indexOf('AUTH-0016');
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
	filters =  new YAHOO.rapidjs.riadmin.Filters(errorDialog);
	adminLayout.add('center',filters.panel);
	adminLayout.getRegion("center").getTabs().titleArea.innerHTML = '<td class="IFountainRapidSuite"><img src="../images/RapidInsightAdmin.png"/></td>';
	YAHOO.rapidjs.ServerStatus.render(adminLayout.getRegion("center").getTabs().toolsArea);
	new YAHOO.rapidjs.component.layout.LogoutTool(adminLayout, "center");
	adminLayout.endUpdate();
	handleRIAdminUI(adminLayout, filters);
	adminLayout.regions['center'].setActivePanel(filters.panel);
}

function handleRIAdminUI(adminLayout, filters){
	var tabPanel = adminLayout.regions['center'].tabs;
	
	var fPanelItem = tabPanel.getTab(filters.panel.getEl().id);
	YAHOO.ext.DomHelper.append(fPanelItem.inner.dom, {tag:'div', unselectable:'on', cls:'riadmin-filters-tabicon'});
	fPanelItem.setWidth(fPanelItem.pnode.getWidth() + 20);
}
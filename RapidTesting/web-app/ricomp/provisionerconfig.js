function renderAll()
{
	YAHOO.rapidjs.Login.checkLogin(checkLoginResponseSuccess);
}
checkLoginResponseSuccess = function(o){
	if(YAHOO.rapidjs.Login.loggedIn(o.responseXML))
	{
		requestDomainNames();
	}
}



function requestDomainNames(){
	var url = "/RapidInsight/ManagedObject/invoke?Operation=admin/getDatasourceData";
	var callback=
	{
		success:processDomainNames,
		failure:processDomainNamesFailure	
	}
	YAHOO.util.Connect.asyncRequest('GET',url,callback,null);
}

function processDomainNames(response){
	var errorIndex = response.responseText.indexOf('AUTH-0016');
	provisonerLayout = new YAHOO.ext.BorderLayout(document.body, {
		center:{
			autoScroll: false, tabPosition: 'top',minTabWidth: 50, alwaysShowTabs:true
		}
	});
	if(errorIndex > -1){
		renderAuthError();
		
	}
	else{
		render(response);
	}
	getEl('loadingwrapper').remove();
}
function processDomainNamesFailure(response){
	
}

function renderAuthError(){
	provisonerLayout.beginUpdate();
	authErrorPanel = new YAHOO.rapidjs.component.layout.RapidPanel('autherror');
	provisonerLayout.add('center', authErrorPanel);
	provisonerLayout.regions['center'].hidePanel(authErrorPanel);
	provisonerLayout.getRegion("center").getTabs().titleArea.innerHTML = '<td class="IFountainRapidSuite"><img src="../images/RapidInsightAdmin.png"/></td>';
	YAHOO.rapidjs.ServerStatus.render(provisonerLayout.getRegion("center").getTabs().toolsArea);
	new YAHOO.rapidjs.component.layout.LogoutTool(provisonerLayout, "center");
	provisonerLayout.endUpdate();
	provisonerLayout.regions['center'].hidePanel(authErrorPanel);
}
function render(response){
	var rapidXmlDoc = new YAHOO.rapidjs.data.RapidXmlDocument(response);
	var domains = rapidXmlDoc.getElementsByTagName("Datasource");
	provisonerLayout.beginUpdate();
	provisoner = new YAHOO.rapidjs.provisioner.Provisioner(domains);
	provisonerLayout.add('center', provisoner.panel);
	provisonerLayout.getRegion("center").getTabs().titleArea.innerHTML = '<td class="IFountainRapidSuite"><img src="../images/RapidInsightAdmin.png"/></td>';
	YAHOO.rapidjs.ServerStatus.render(provisonerLayout.getRegion("center").getTabs().toolsArea);
	new YAHOO.rapidjs.component.layout.LogoutTool(provisonerLayout, "center");
	provisonerLayout.endUpdate();
	handleRIAdminUI(provisonerLayout, provisoner);
	provisonerLayout.regions['center'].showPanel(provisoner.panel);
}

function handleRIAdminUI(provisonerLayout, provisoner){
	var tabPanel = provisonerLayout.regions['center'].tabs;
	var conPanelItem = tabPanel.getTab(provisoner.panel.getEl().id);
	YAHOO.ext.DomHelper.append(conPanelItem.inner.dom, {tag:'div', unselectable:'on', cls:'riadmin-con-tabicon'});
	conPanelItem.setWidth(conPanelItem.pnode.getWidth() + 20);
}
/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
YAHOO.rapidjs.Login = new function(){
	this.originalWindow = null;
	this.doRequest = function(url, postData, successFunction, failureFunction, scope){
		var callback=
		{
			success:successFunction,
			failure:failureFunction, 
			scope:scope || window
		}
		YAHOO.util.Connect.asyncRequest('POST',url,callback,postData);
	};
	this.checkLoginResponseFailure = function(response){
		return "";
	};
	this.checkLogin = function(successDelegate,username,password){
		this.originalWindow = window.location.pathname;
		var url = "/RapidManager/User/login";
		var postData = null;
		if(username != null && password != null)
		{
			postData = "submission=credentials&login="+username+"&password="+password;
		}
		this.doRequest(url,postData, successDelegate,this.checkLoginResponseFailure);
	};
	this.loggedIn = function(responseXML){
		if(responseXML.getElementsByTagName("Authenticate")[0] || responseXML.getElementsByTagName("authenticate")[0])
	 	{
		 	window.location = "login.html?page=" + this.originalWindow;
		 	return false;
	 	}
	 	return true;
	};
	this.responseSuccess = function(o){
		var responseXML = o.responseXML;
		if(responseXML.getElementsByTagName("Successful").length > 0)
		{
	 		window.location = this.originalWindow;
		}
		else
		{
		 	alert("Login error.");
		}
	};

	this.responseFailure = function(o){
		alert("Response to login request can not be received.");
	};
	this.login = function(){
		var windowParam = window.location.search;
		if(windowParam){
			this.originalWindow = windowParam.substr(windowParam.indexOf('=') + 1);
		}
		else{
			this.originalWindow = 'index.html';
		}
		
		USERNAME = document.getElementById("username").value;
		var password = document.getElementById("password").value;
		
		var url = "/RapidManager/User/login";
		var postData = 'submission=credentials&login=' + USERNAME + '&password=' + password;
		this.doRequest(url, postData, this.responseSuccess, this.responseFailure, this);
	};

	this.logoutSuccess = function(o){
		var responseXML = o.responseXML;
		if(responseXML.getElementsByTagName("Authenticate").length > 0)
		{
			window.location = "login.html?page=" + this.originalWindow;
			return;
		}
		
		if(responseXML.getElementsByTagName("Successful").length > 0)
		{
	 		window.location = "login.html?page=" + this.originalWindow;
		}
		else
		{
		 	alert("Logout error.");
		}
	};
	
	this.logoutFailure = function(o){
		alert("Could not logout.");
	};
	this.logout = function(){
		this.originalWindow = window.location.pathname;
		var url = "/RapidManager/User/logout";
		this.doRequest(url, null, this.logoutSuccess, this.logoutFailure, this);
	};
	
	
	
	this.setFocus = function(){
		document.getElementById('username').focus();
	};
}();
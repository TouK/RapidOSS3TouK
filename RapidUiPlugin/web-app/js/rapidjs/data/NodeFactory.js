YAHOO.namespace('rapidjs', 'rapidjs.data');
YAHOO.rapidjs.data.NodeFactory = new function(){
	this.getRootNode = function(xmlDoc,isCaseSensitive){
		var response = xmlDoc.response;
		var indexOfXmlStart = response.responseText.indexOf("<");
	 	var indexOfJsonStart = response.responseText.indexOf("{");
	 	if(isCaseSensitive){
	 		if(indexOfXmlStart>= 0 && indexOfXmlStart < indexOfJsonStart || indexOfJsonStart == -1)
			{
				return new YAHOO.rapidjs.data.CaseInsensitiveXmlNode(xmlDoc, response.responseXML, 1, "rootNode", xmlDoc.indexingAttributes);
			}
			else if(indexOfJsonStart >= 0 && indexOfXmlStart > indexOfJsonStart || indexOfXmlStart == -1)
			{
				var obj = eval('(' + response.responseText + ')' );
				return new YAHOO.rapidjs.data.CaseInsensitiveJsonNode(xmlDoc, obj, "rootNode", xmlDoc.indexingAttributes);
			}
	 	}
	 	else{
	 		if(indexOfXmlStart>= 0 && indexOfXmlStart < indexOfJsonStart || indexOfJsonStart == -1)
			{
				return new YAHOO.rapidjs.data.RapidXmlNode(xmlDoc, response.responseXML, 1, "rootNode", xmlDoc.indexingAttributes);
			}
			else if(indexOfJsonStart >= 0 && indexOfXmlStart > indexOfJsonStart || indexOfXmlStart == -1)
			{
				var obj = eval('(' + response.responseText + ')' );
				return new YAHOO.rapidjs.data.RapidJsonNode(xmlDoc, obj, "rootNode", xmlDoc.indexingAttributes);
			}
	 	}
	};
};
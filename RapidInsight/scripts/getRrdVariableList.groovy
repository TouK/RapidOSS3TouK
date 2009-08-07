import groovy.xml.*;

def sw = new StringWriter();
def mb = new MarkupBuilder(sw);

def resources=RrdVariable.propertySummary("alias:*",["resource"]).resource;

mb.Objects{
	resources.each{ resourceName, count ->
		mb.Object(id: resourceName, name:resourceName, displayName: resourceName, nodeType: 'Container'){
			RrdVariable.searchEvery("resource:${resourceName}",[sort:"name",order:"asc"]).each{
				mb.Object(id:it.id, name:it.name, resource:resourceName, displayName:it.name, nodeType:"Object", rsDatasource:"");
			}
		}
	}

}

return sw.toString();
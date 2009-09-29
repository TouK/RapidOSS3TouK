/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Sep 29, 2009
 * Time: 10:20:57 AM
 * To change this template use File | Settings | File Templates.
 */

import groovy.xml.MarkupBuilder

if(!params.offset){
    params.offset=0
}
if(!params.max){
    params.max=100
}

def eventSearchResults=[total:0, offset: params.offset,results:[]];

def objectName = params.name;
def domainObject = RsTopologyObject.get(name: objectName)
if(domainObject!=null)
{
	def query="elementName:${domainObject.name.exactQuery()}";
	if(domainObject instanceof RsComputerSystem)
	{
		def links = domainObject.connectedVia.name;        
        if (links.size() > 0) {
            def queryArray = [];
            links.each {
                queryArray.add("elementName:${it.exactQuery()}")
            }
            query += " OR ${queryArray.join(' OR ')}";
        }

	}
	eventSearchResults = RsEvent.search(query, params);
}

def sw = new StringWriter();
def builder = new MarkupBuilder(sw);
builder.Objects(total: eventSearchResults.total, offset: eventSearchResults.offset) {
	eventSearchResults.results.each{ event ->
		builder.Object(event.asMap());
	}
}


return sw.toString();

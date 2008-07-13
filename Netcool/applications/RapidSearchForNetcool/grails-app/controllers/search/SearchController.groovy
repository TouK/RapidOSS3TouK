package search

import org.compass.core.engine.SearchEngineQueryParseException
import grails.converters.XML
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.commons.GrailsDomainClass

/**
* Created by IntelliJ IDEA.
* User: mustafa
* Date: Jun 1, 2008
* Time: 4:09:42 PM
* To change this template use File | Settings | File Templates.
*/
class SearchController {
    def searchableService;
    def static Map propertyConfiguration = null;
    def index = {
        def searchResults = searchableService.search(params.query, params);
        StringWriter sw = new StringWriter();
        def builder = new MarkupBuilder(sw);
        builder.Objects(total:searchResults.total, offset:searchResults.offset)
        {
            searchResults.results.each{result->
                GrailsDomainClass grailsDomainClass = grailsApplication.getDomainClass(result.class.name);
                def props = [:];
                grailsDomainClass.getProperties().each{resultProperty->
                    props[resultProperty.name] = result[resultProperty.name];
                }
                builder.Object(props);
            }

        }
        render(text:sw.toString(), contentType:"text/xml");
    }
}
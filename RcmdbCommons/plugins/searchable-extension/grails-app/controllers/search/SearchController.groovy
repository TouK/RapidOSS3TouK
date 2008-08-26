package search

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
        def sortOrder = 0;
        def query = params.query;
        if(query == "" || query == null)
        {
            query = "id:[0 TO *]";
        }
        StringWriter sw = new StringWriter();
        def builder = new MarkupBuilder(sw);
        try
        {
            def searchResults = searchableService.search(query, params);
            def grailsClassProperties = [:]
            builder.Objects(total:searchResults.total, offset:searchResults.offset)
            {
                searchResults.results.each{result->
                    def className = result.getClass().name;
                    def grailsObjectProps = grailsClassProperties[className]
                    if(grailsObjectProps == null)
                    {
                        GrailsDomainClass grailsClass = grailsApplication.getDomainClass(result.getClass().name);
                        grailsObjectProps = grailsClass.getProperties();
                        grailsClassProperties[result.getClass().name] =  grailsObjectProps;
                    }
                    def props = [:];
                    grailsObjectProps.each{resultProperty->
                        props[resultProperty.name] = result[resultProperty.name];
                    }
                    props.put("sortOrder", sortOrder++)
                    builder.Object(props);
                }

            }
        }
        catch(Throwable t)
        {
             builder.Objects(total:0, offset:0);
        }
        render(text:sw.toString(), contentType:"text/xml");
    }
}
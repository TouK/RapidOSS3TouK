package search

import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.rcmdb.domain.util.DomainClassUtils

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
        if (query == "" || query == null)
        {
            query = "alias:*";
        }
        StringWriter sw = new StringWriter();
        def builder = new MarkupBuilder(sw);
        try
        {
            def searchResults;
            if(params.searchIn != null){
                 GrailsDomainClass grailsClass = grailsApplication.getDomainClass(params.searchIn);
                 def mc = grailsClass.metaClass;
                 searchResults =  mc.invokeStaticMethod(mc.theClass, "search", [query, params] as Object[])
            }
            else{
                searchResults = searchableService.search(query, params);
            }
            def grailsClassProperties = [:]
            builder.Objects(total: searchResults.total, offset: searchResults.offset){
                searchResults.results.each {result ->
                    def className = result.getClass().name;
                    def grailsObjectProps = grailsClassProperties[className]
                    if (grailsObjectProps == null)
                    {
                        grailsObjectProps = DomainClassUtils.getFilteredProperties(className);
                        grailsClassProperties[result.getClass().name] = grailsObjectProps;
                    }
                    def props = [:];
                    grailsObjectProps.each {resultProperty ->
                        props[resultProperty.name] = result[resultProperty.name];
                    }
                    props.put("sortOrder", sortOrder++)
                    props.put("rsAlias", result.getClass().name)
                    builder.Object(props);
                }
            }
            render(text: sw.toString(), contentType: "text/xml");
        }
        catch (Throwable t)
        {
            addError("invalid.search.query", [query, t.getMessage()]);
            render(text: errorsToXml(errors), contentType: "text/xml")
        }

    }
}
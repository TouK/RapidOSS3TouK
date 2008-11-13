package search

import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import org.compass.core.engine.SearchEngineQueryParseException;

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
        def searchResults;
        if (params.searchIn != null) {
            GrailsDomainClass grailsClass = grailsApplication.getDomainClass(params.searchIn);
            if(grailsClass == null)
            {
                addError("invalid.search.searchIn", [params.searchIn]);
                withFormat {
                    xml {render(text: errorsToXml(errors), contentType: "text/xml")}
                }
                return;
            }
            def mc = grailsClass.metaClass;
            try {
                searchResults = mc.invokeStaticMethod(mc.theClass, "search", [query, params] as Object[])
            }
            catch (Throwable e) {
                addError("invalid.search.query", [query, e.getMessage()]);
                withFormat {
                    xml {render(text: errorsToXml(errors), contentType: "text/xml")}
                }
                return;
            }

        }
        else {
            try {
                searchResults = searchableService.search(query, params);
            }
            catch (Throwable e) {
                addError("invalid.search.query", [query, e.getMessage()]);
                withFormat {
                    xml {render(text: errorsToXml(errors), contentType: "text/xml")}
                }
                return;
            }
        }
        def grailsClassProperties = [:]
        def grailsClassRelations = [:]
        builder.Objects(total: searchResults.total, offset: searchResults.offset) {
            searchResults.results.each {result ->
                def className = result.getClass().name;
                def grailsObjectProps = grailsClassProperties[className]
                if (grailsObjectProps == null)
                {
                    grailsObjectProps = DomainClassUtils.getFilteredProperties(className);
                    grailsClassProperties[result.getClass().name] = grailsObjectProps;
                }
                def grailsObjectRelations = grailsClassRelations[className];
                if(grailsObjectRelations == null){
                    grailsObjectRelations = DomainClassUtils.getRelations(className);
                    grailsClassRelations[result.getClass().name] = grailsObjectRelations;
                }
                def props = [:];
                grailsObjectProps.each {resultProperty ->
                    if(!grailsObjectRelations.containsKey(resultProperty.name)){
                        props[resultProperty.name] = result[resultProperty.name];    
                    }
                }
                props.put("sortOrder", sortOrder++)
                props.put("rsAlias", result.getClass().name)
                builder.Object(props);
            }
        }
        render(text: sw.toString(), contentType: "text/xml");

    }
}
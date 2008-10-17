import org.codehaus.groovy.grails.commons.GrailsDomainClass
import search.SearchQuery
import search.SearchQueryGroup
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import ui.GridView;

/**
* Created by IntelliJ IDEA.
* User: mustafa sener
* Date: Aug 26, 2008
* Time: 1:47:21 PM
* To change this template use File | Settings | File Templates.
*/

def searchQuery = SearchQuery.get([id: params.queryId])
if (!searchQuery) {
    web.addError("default.object.not.found", [SearchQuery.class.name, params.queryId]);
    web.render(text: web.errorsToXml(web.errors), contentType: "text/xml");
}
else {
    def userName = web.session.username;
    def queryType = params.queryType;
    def extraFilteredProps = ["rsDatasource"];
    def gridViews = GridView.search("username:\"${userName}\"", [sort:"name"]).results;
    if (queryType == "notification" || queryType == "historicalnotification") {
        def className = queryType == "notification" ? "RsSmartsNotification":"RsSmartsHistoricalNotification"
        def sortProperties = DomainClassUtils.getFilteredProperties(className, extraFilteredProps);
        def searchQueryGroups = SearchQueryGroup.list().findAll {queryGroup ->
            queryGroup.username == userName && queryGroup.isPublic == false && (queryGroup.type == queryType || queryGroup.type =="default")
        };
        def sortProps = sortProperties.sort{it.name}
        web.render(contentType: 'text/xml') {
            Edit {
                id(searchQuery.id)
                name(searchQuery.name)
                query(searchQuery.query)
                sortProperty {
                    sortProps.each {
                        option(selected: it.name == searchQuery.sortProperty, it.name)
                    }
                }
                sortOrder {
                    option(selected: searchQuery.sortOrder == 'desc', 'desc')
                    option(selected: searchQuery.sortOrder == 'asc', 'asc')
                }
                group {
                    searchQueryGroups.each {
                        if (it.name == searchQuery.group.name) {
                            option(selected: "true", it.name)
                        }
                        else {
                            option(it.name)
                        }
                    }
                }
                viewName {
                    option(selected: searchQuery.viewName == 'default', 'default')
                    gridViews.each {
                        option(selected: searchQuery.viewName == it.name, it.name)
                    }
                }
            }
        }

    }
    else if (queryType == "topology") {
        def sortProperties = [];
        GrailsDomainClass domainClass = web.grailsApplication.getDomainClass("RsTopologyObject")
        domainClass.getSubClasses().each {
            println "Subclass ${it.name}"
            sortProperties.addAll(DomainClassUtils.getFilteredProperties(it.name, extraFilteredProps));
        }
        sortProperties.addAll(DomainClassUtils.getFilteredProperties("RsTopologyObject", extraFilteredProps));
        def searchQueryGroups = SearchQueryGroup.list().findAll {queryGroup ->
            queryGroup.username == userName && queryGroup.isPublic == false && (queryGroup.type == queryType || queryGroup.type =="default")
        };
        def propertyMap = [:]
        def sortProps = sortProperties.sort{it.name}
        web.render(contentType: 'text/xml') {
            Edit {
                id(searchQuery.id)
                name(searchQuery.name)
                query(searchQuery.query)
                sortProperty {
                    sortProps.each {
                        def propertyName = it.name;
                        if(!propertyMap.containsKey(propertyName)){
                            option(selected: propertyName == searchQuery.sortProperty, propertyName)    
                        }
                        propertyMap.put(propertyName, propertyName);                                                                            
                    }
                }
                sortOrder {
                    option(selected: searchQuery.sortOrder == 'desc', 'desc')
                    option(selected: searchQuery.sortOrder == 'asc', 'asc')
                }
                group {
                    searchQueryGroups.each {
                        if (it.name == searchQuery.group.name) {
                            option(selected: "true", it.name)
                        }
                        else {
                            option(it.name)
                        }
                    }
                }
                viewName {
                    option(selected: searchQuery.viewName == 'default', 'default')
                    gridViews.each {
                        option(selected: searchQuery.viewName == it.name, it.name)
                    }
                }
            }
        }
    }
    else {
        throw new Exception("Invalid query type");
    }
    def searchQueryGroups = SearchQueryGroup.list().findAll {
        it.username == userName && it.isPublic == false
    };

}
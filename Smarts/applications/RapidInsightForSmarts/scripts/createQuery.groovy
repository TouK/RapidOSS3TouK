import com.ifountain.rcmdb.domain.util.DomainClassUtils
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import search.SearchQueryGroup

/**
* Created by IntelliJ IDEA.
* User: mustafa sener
* Date: Aug 26, 2008
* Time: 2:03:53 PM
* To change this template use File | Settings | File Templates.
*/
def userName = web.session.username;
def queryType = params.queryType;
def extraFilteredProps = ["rsDatasource"];
if (queryType == "notification") {
    def sortProperties = DomainClassUtils.getFilteredProperties("RsSmartsNotification", extraFilteredProps);
    def searchQueryGroups = SearchQueryGroup.list().findAll {queryGroup ->
        queryGroup.username == userName && queryGroup.isPublic == false
    };
    def sortProps = sortProperties.sort{it.name} 
    web.render(contentType: 'text/xml') {
        Create {
            group {
                searchQueryGroups.each {
                    option(it.name)
                }
            }
            sortProperty {
                sortProps.each {
                    option(it.name)
                }
            }
        }
    }
}
else if (queryType == "topology") {
    def sortProperties = [];
    GrailsDomainClass domainClass = web.grailsApplication.getDomainClass("RsSmartsObject")
    domainClass.getSubClasses().each {
        println "Subclass ${it.name}"
        sortProperties.addAll(DomainClassUtils.getFilteredProperties(it.name, extraFilteredProps));
    }
    sortProperties.addAll(DomainClassUtils.getFilteredProperties("RsSmartsObject", extraFilteredProps));
    def searchQueryGroups = SearchQueryGroup.list().findAll {queryGroup ->
        queryGroup.username == userName && queryGroup.isPublic == false
    };
    def sortProps = sortProperties.sort{it.name}
    def propertyMap = [:]
    web.render(contentType: 'text/xml') {
        Create {
            group {
                searchQueryGroups.each {
                    option(it.name)
                }
            }
            sortProperty {
                sortProps.each {
                    def propertyName = it.name;
                    if(!propertyMap.containsKey(propertyName)){
                        option(propertyName)
                    }
                    propertyMap.put(propertyName, propertyName)
                }
            }
        }
    }
}
else {
    throw new Exception("Invalid query type");
}


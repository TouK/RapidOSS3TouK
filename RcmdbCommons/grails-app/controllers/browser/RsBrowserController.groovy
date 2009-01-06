package browser

import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler
import search.SearchQuery
import auth.RsUser

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jan 5, 2009
* Time: 10:40:01 AM
*/
class RsBrowserController {

    def index = {
        def domainClasses = grailsApplication.domainClasses;
        def domainClassList = domainClasses;
        if (params.sort && params.order == "desc") {
            domainClassList = domainClasses.sort {first, second ->
                return first.fullName < second.fullName ? 1 : -1;
            }
        }
        else {
            domainClassList = domainClasses.sort {it.fullName}
        }
        withFormat {
            html {
                return [domainClassList: domainClassList]
            }
            xml {
                render(contentType: "text/xml") {
                    Classes() {
                        domainClassList.each {
                            Class(name: it.fullName)
                        }
                    }
                }
            }
        }

    }

    def listDomain = {
        if (params.max == null) {
            params.max = 20;
        }
        def domainClass = grailsApplication.getArtefactByLogicalPropertyName(DomainClassArtefactHandler.TYPE, params.domain)
        if (domainClass) {
            def count = domainClass.clazz."count"();
            def propertyList = getPropertiesWhichCanBeListed(domainClass);
            def objectList = domainClass.clazz."list"(params)
            withFormat {
                html {
                    render(view: "listDomain", model: [objectList: objectList, propertyList: propertyList, count: count, domainName: domainClass.fullName])
                }
                xml {
                    render(contentType: "text/xml") {
                        Objects(total: count, offset: params.offset ? params.offset : 0) {
                            objectList.each {object ->
                                def props = ["id": object.id]
                                propertyList.each {p ->
                                    props.put(p.name, object[p.name])
                                }
                                Object(props)
                            }
                        }
                    }
                }
            }

        }
        else {
            addError("default.class.not.found", [params.domain]);
            withFormat {
                html {
                    flash.errors = errors
                    redirect(action: index);
                }
                xml {render(text: errorsToXml(errors), contentType: "text/xml")}
            }

        }

    }

    def show = {
        def domainClass = grailsApplication.getArtefactByLogicalPropertyName(DomainClassArtefactHandler.TYPE, params.domain)
        if (domainClass) {
            def domainObject = domainClass.clazz."get"(id: params.id);
            if (domainObject) {
                def objectClass = grailsApplication.getDomainClass(domainObject.class.name)
                def properties = objectClass.clazz."getPropertiesList"();
                def keySet = objectClass.clazz."keySet"();
                withFormat {
                    html {
                        render(view: "show", model: [keys: keySet, propertyList: properties, domainObject: domainObject])
                    }
                    xml {
                        render(contentType: "text/xml") {
                            Object() {
                                id(domainObject.id)
                                keySet.each {key ->
                                    if (key.name != "id") {
                                        "${key.name}"(domainObject[key.name])
                                    }
                                }
                                properties.each {p ->
                                    if (p.name != "id" && !p.isKey) {
                                        if (!p.isRelation) {
                                            "${p.name}"(domainObject[p.name])
                                        }
                                        else {
                                            if (p.isOneToMany() || p.isManyToMany()) {
                                                "${p.name}"() {
                                                    def relatedObjects = domainObject[p.name];
                                                    relatedObjects.each {relatedObject ->
                                                        Object(relatedObject)
                                                    }
                                                }
                                            }
                                            else {
                                                def relatedObject = domainObject[p.name]
                                                "${p.name}"(relatedObject ? relatedObject : "")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else {
                addError("default.object.not.found", [domainClass.fullName, params.id]);
                withFormat {
                    html {
                        flash.errors = errors
                        redirect(action: params.domain);
                    }
                    xml {
                        xml {render(text: errorsToXml(errors), contentType: "text/xml")}
                    }
                }
            }

        }
        else {
            addError("default.class.not.found", [params.domain]);
            withFormat {
                html {
                    flash.errors = errors
                    redirect(action: index);
                }
                xml {
                    xml {render(text: errorsToXml(errors), contentType: "text/xml")}
                }
            }
        }
    }

    def search = {
        if (params.max == null) {
            params.max = 20;
        }
        def domainClass = grailsApplication.getArtefactByLogicalPropertyName(DomainClassArtefactHandler.TYPE, params.domain)
        if (domainClass) {
            SearchQuery query = null;
            def queryList = SearchQuery.searchEvery("name:\"${params.query}\" AND username:${RsUser.RSADMIN} AND isPusblic:true");
            if (queryList.size() == 0) {
                query = SearchQuery.get(name: params.query, username: session.username);
            }
            else {
                query = queryList[0]
            }
            if (query) {
                def searchResults = null;
                try {
                    searchResults = domainClass.clazz."search"(query.query, params);
                }
                catch (Throwable e) {
                    addError("invalid.search.query", [query.query, e.getMessage()]);
                    withFormat {
                        html {
                            flash.errors = errors
                            redirect(action: params.domain);
                        }
                        xml {
                            xml {render(text: errorsToXml(errors), contentType: "text/xml")}
                        }
                    }
                    return;
                }
                def objectList = searchResults.results
                withFormat {
                    html {
                        def propertyList = getPropertiesWhichCanBeListed(domainClass);
                        render(view: "search", model: [objectList: objectList, propertyList: propertyList, count: searchResults.total, domainName: domainClass.fullName])
                    }
                    xml {
                        def grailsClassProperties = [:]
                        render(contentType: "text/xml") {
                            Objects(total: searchResults.total, offset: searchResults.offset) {
                                searchResults.results.each {result ->
                                    def className = result.getClass().name;
                                    def grailsObjectProps = grailsClassProperties[className]
                                    if (grailsObjectProps == null)
                                    {
                                        def objectDomainClass = grailsApplication.getDomainClass(className);
                                        grailsObjectProps = objectDomainClass.clazz."getPropertiesList"();
                                        def grailsObjectProperties = [];
                                        grailsObjectProps.each {
                                            if (!it.isRelation && !it.isOperationProperty) {
                                                grailsObjectProperties.add(it);
                                            }
                                        }
                                        grailsClassProperties[result.getClass().name] = grailsObjectProperties;
                                    }
                                    def props = [:];
                                    grailsObjectProps.each {resultProperty ->
                                        props[resultProperty.name] = result[resultProperty.name];
                                    }
                                    Object(props);
                                }
                            }
                        }
                    }
                }

            }
            else {
                addError("default.query.not.found", [params.query]);
                withFormat {
                    html {
                        flash.errors = errors
                        redirect(action: params.domain);
                    }
                    xml {
                        xml {render(text: errorsToXml(errors), contentType: "text/xml")}
                    }
                }
            }
        }
        else {
            addError("default.class.not.found", [params.domain]);
            withFormat {
                html {
                    flash.errors = errors
                    redirect(action: index);
                }
                xml {
                    xml {render(text: errorsToXml(errors), contentType: "text/xml")}
                }
            }
        }
    }

    def getPropertiesWhichCanBeListed(domainClass) {
        def propertyList = [];
        def properties = domainClass.clazz."getPropertiesList"();
        def propertiesCanBeListed = properties.findAll {it.name != "id" && !it.isKey && !it.isRelation && !it.isOperationProperty || (it.isRelation && (it.isOneToOne() || it.isManyToOne()))}
        def keySet = domainClass.clazz."keySet"();
        if (propertiesCanBeListed.size() + keySet.size() > 5) {
            propertyList = keySet;
        }
        else {
            propertyList.addAll(keySet)
            propertyList.addAll(propertiesCanBeListed)
        }
        return propertyList;
    }
}
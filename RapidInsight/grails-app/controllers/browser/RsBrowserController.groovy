package browser

import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler
import search.SearchQuery
import auth.RsUser
import groovy.xml.MarkupBuilder

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jan 5, 2009
* Time: 10:40:01 AM
*/
class RsBrowserController {

    def index = {
        redirect(uri: "/browser.gsp")
    }

    def classes = {
        def domainClasses = grailsApplication.domainClasses;
        withFormat {
            xml {
                def classesMap = [system: [:], application: [:]]
                def domainClassesMap = [:]
                domainClasses.each {domainClass ->
                    domainClassesMap[domainClass.fullName] = domainClass;
                    def classType = "system";
                    if (domainClass.fullName.indexOf(".") < 0) {
                        classType = "application"
                    }
                    if (domainClass.clazz.superclass == Object.class) {
                        _getChildClasses(domainClass, classesMap[classType], null)
                    }
                }
                def sw = new StringWriter();
                def builder = new MarkupBuilder(sw);
                builder.Classes() {
                    builder.Class(name: "System") {
                        _getChildClasesXml(builder, classesMap["system"], domainClassesMap)
                    }
                    builder.Class(name: "Application") {
                        _getChildClasesXml(builder, classesMap["application"], domainClassesMap)
                    }
                }
                render(contentType: "text/xml", text: sw.toString())
            }
            html {
                def domainClassList = domainClasses;
                if (params.sort && params.order == "desc") {
                    domainClassList = domainClasses.sort {first, second ->
                        return first.fullName < second.fullName ? 1 : -1;
                    }
                }
                else {
                    domainClassList = domainClasses.sort {it.fullName}
                }
                return [domainClassList: domainClassList];
            }
        }
    }
    def _getChildClasses(domainClass, parentMap, parentDomainClass) {
        def classMap = [:]
        if(parentDomainClass != null ){
            if(domainClass.clazz.superclass == parentDomainClass.clazz){
                parentMap.put(domainClass.fullName, classMap);
            }
        }
        else{
            parentMap.put(domainClass.fullName, classMap);    
        }
        domainClass.subClasses.each {
            _getChildClasses(it, classMap, domainClass);
        }
    }
    def _getChildClasesXml(builder, subclasses, domainClassesMap) {
        subclasses.each {className, sclasses ->
            builder.Class(name: className, logicalName: domainClassesMap[className].logicalPropertyName) {
                _getChildClasesXml(builder, sclasses, domainClassesMap);
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
            def propertyList = getPropertiesWhichCanBeListed(domainClass, 5);
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
                    redirect(action: classes);
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
                    xml {render(text: errorsToXml(errors), contentType: "text/xml")}
                }
            }

        }
        else {
            addError("default.class.not.found", [params.domain]);
            withFormat {
                html {
                    flash.errors = errors
                    redirect(action: classes);
                }
                xml {render(text: errorsToXml(errors), contentType: "text/xml")}
            }
        }
    }

    def searchWithQuery = {
        return search();
    }
    def search = {
        if (params.max == null) {
            params.max = 20;
        }
        def domainClass = grailsApplication.getArtefactByLogicalPropertyName(DomainClassArtefactHandler.TYPE, params.domain)
        if (domainClass) {
            def query = params.query;
            if (query == null) {
                SearchQuery searchQuery = null;
                def queryList = SearchQuery.searchEvery("name:\"${params.searchQuery}\" AND username:${RsUser.RSADMIN} AND isPusblic:true");
                if (queryList.size() == 0) {
                    searchQuery = SearchQuery.get(name: params.searchQuery, username: session.username);
                }
                else {
                    searchQuery = queryList[0]
                }
                if (searchQuery) {
                    query = searchQuery.query;
                }
            }
            if (query != null) {
                if (query == "")
                {
                    query = "alias:*";
                }
                def searchResults = null;
                try {
                    searchResults = domainClass.clazz."search"(query, params);
                }
                catch (Throwable e) {
                    addError("invalid.search.query", [query, e.getMessage()]);
                    withFormat {
                        html {
                            flash.errors = errors
                            redirect(action: params.domain);
                        }
                        xml {render(text: errorsToXml(errors), contentType: "text/xml")}
                    }
                    return;
                }
                def objectList = searchResults.results
                withFormat {
                    html {
                        def propertyList = getPropertiesWhichCanBeListed(domainClass, 5);
                        render(view: "search", model: [objectList: objectList, propertyList: propertyList, count: searchResults.total, domainName: domainClass.fullName])
                    }
                    xml {
                        def grailsClassProperties = [:]
                        def sortOrder = 0;
                        render(contentType: "text/xml") {
                            Objects(total: searchResults.total, offset: searchResults.offset) {
                                searchResults.results.each {result ->
                                    def className = result.getClass().name;
                                    def grailsObjectProps = grailsClassProperties[className]
                                    if (grailsObjectProps == null)
                                    {
                                        def objectDomainClass = grailsApplication.getDomainClass(className);
                                        grailsObjectProps = getPropertiesWhichCanBeListed(objectDomainClass, 10)
                                        grailsClassProperties[result.getClass().name] = grailsObjectProps;
                                    }
                                    def props = ["id":result.id];
                                    grailsObjectProps.each {resultProperty ->
                                        if(resultProperty.name != "id"){
                                            props[resultProperty.name] = result[resultProperty.name];    
                                        }
                                    }
                                    props.put("rsAlias", className)
                                    props.put("sortOrder", sortOrder ++)
                                    Object(props);
                                }
                            }
                        }
                    }
                }

            }
            else {
                addError("default.query.not.found", [params.searchQuery]);
                withFormat {
                    html {
                        flash.errors = errors
                        redirect(action: params.domain);
                    }
                    xml {render(text: errorsToXml(errors), contentType: "text/xml")}
                }
            }
        }
        else {
            addError("default.class.not.found", [params.domain]);
            withFormat {
                html {
                    flash.errors = errors
                    redirect(action: classes);
                }
                xml {render(text: errorsToXml(errors), contentType: "text/xml")}
            }
        }
    }

    def getPropertiesWhichCanBeListed(domainClass, max) {
        def propertyList = [];
        def properties = domainClass.clazz."getPropertiesList"();
        def propertiesCanBeListed = properties.findAll {it.name != "id" && !it.isKey && !it.isRelation && !it.isOperationProperty || (it.isRelation && (it.isOneToOne() || it.isManyToOne()))}
        def keySet = domainClass.clazz."keySet"();
        if (propertiesCanBeListed.size() + keySet.size() > max) {
            propertyList = keySet;
        }
        else {
            propertyList.addAll(keySet)
            propertyList.addAll(propertiesCanBeListed)
        }
        return propertyList;
    }
}
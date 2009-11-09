package browser

import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler
import search.SearchQuery
import auth.RsUser
import groovy.xml.MarkupBuilder
import com.ifountain.rcmdb.converter.RapidConvertUtils

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
        if (params.format == 'xml') {
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
        else {
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
    def _getChildClasses(domainClass, parentMap, parentDomainClass) {
        def classMap = [:]
        if (parentDomainClass != null) {
            if (domainClass.clazz.superclass == parentDomainClass.clazz) {
                parentMap.put(domainClass.fullName, classMap);
            }
        }
        else {
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
            def isAll = params.all == "true"
            def propertyList = getPropertiesWhichCanBeListed(domainClass, 5, isAll);
            def propertyNames = propertyList.name;
            def objectList = domainClass.clazz."search"(params).results

            if (params.format == 'xml') {
                def sw = new StringWriter();
                def builder = new MarkupBuilder(sw);
                builder.Objects(total: count, offset: params.offset ? params.offset : 0) {
                    objectList.each {object ->
                        builder.Object(object.asStringMap(propertyNames))
                    }
                }
                render(contentType: "text/xml", text: sw.toString())
            }
            else {
                render(view: "listDomain", model: [objectList: objectList, propertyList: propertyList, count: count, domainName: domainClass.fullName])
            }
        }
        else {
            addError("default.class.not.found", [params.domain]);
            if (params.format == 'xml') {
                render(text: errorsToXml(errors), contentType: "text/xml")
            }
            else {
                flash.errors = errors
                redirect(action: classes);
            }
        }

    }

    def show = {
        def domainClass = grailsApplication.getArtefactByLogicalPropertyName(DomainClassArtefactHandler.TYPE, params.domain)
        if (domainClass) {
            def domainObject = domainClass.clazz."get"(id: params.id);
            if (domainObject) {
                def objectClass = grailsApplication.getDomainClass(domainObject.class.name)
                def includeFederated = params.federatedProperties != "false";
                def includeOperational = params.operationProperties == "true";
                def includeRelations = params.relations == "true";
                def properties = objectClass.clazz."getPropertiesList"().findAll {
                    (includeFederated || !it.isFederated) && (includeOperational || !it.isOperationProperty) && (includeRelations || !it.isRelation)};
                def keySet = objectClass.clazz."keySet"();
                if (params.format == 'xml') {
                    def sw = new StringWriter();
                    def builder = new MarkupBuilder(sw);
                    builder.Object() {
                        builder.id(domainObject.id)
                        keySet.each {key ->
                            if (key.name != "id") {
                                builder."${key.name}"(domainObject[key.name])
                            }
                        }
                        properties.each {p ->
                            if (p.name != "id" && !p.isKey) {
                                if (!p.isRelation) {
                                    builder."${p.name}"(domainObject[p.name])
                                }
                                else {
                                    if (p.isOneToMany() || p.isManyToMany()) {
                                        builder."${p.name}"() {
                                            def relatedPropertyValues = getRelatedObjectProperties(domainObject, p)
                                            relatedPropertyValues.each {relatedPropertyValue ->
                                                builder.Object(relatedPropertyValue)
                                            }
                                        }
                                    }
                                    else {
                                        def relatedPropertyValues = getRelatedObjectProperties(domainObject, p)
                                        builder."${p.name}"(relatedPropertyValues.size() > 0 ? relatedPropertyValues[0] : "")
                                    }
                                }
                            }
                        }
                    }
                    render(contentType: "text/xml", text: sw.toString())
                }
                else {
                    render(view: "show", model: [keys: keySet, propertyList: properties, domainObject: domainObject])
                }
            }
            else {
                addError("default.object.not.found", [domainClass.fullName, params.id]);
                if (params.format == 'xml') {
                    render(text: errorsToXml(errors), contentType: "text/xml")
                }
                else {
                    flash.errors = errors
                    redirect(action: params.domain);
                }
            }

        }
        else {
            addError("default.class.not.found", [params.domain]);
            if (params.format == 'xml') {
                render(text: errorsToXml(errors), contentType: "text/xml")
            }
            else {
                flash.errors = errors
                redirect(action: classes);
            }
        }
    }

    def getRelatedObjectProperties(object, p) {
        def relatedDomainClass = grailsApplication.getDomainClass(p.relatedModel.name);
        def propertyList = getPropertiesWhichCanBeListed(relatedDomainClass, 5);
        return object.getRelatedModelPropertyValues(p.name, propertyList.name)
    }

    def propertiesAndOperations = {
        return _propertiesAndOperations(params);
    }

    def _propertiesAndOperations(params) {
        def domainClass = grailsApplication.getArtefactByLogicalPropertyName(DomainClassArtefactHandler.TYPE, params.domain)
        if (domainClass) {
            def operations = domainClass.clazz.getOperations();
            def keys = domainClass.clazz.keySet();
            def pureProps = domainClass.clazz.getNonFederatedPropertyList().findAll {return !it.isKey}
            def federatedProps = domainClass.clazz.getFederatedPropertyList()
            def relations = domainClass.clazz.getRelationPropertyList();
            def sw = new StringWriter();
            def builder = new MarkupBuilder(sw);
            builder.Class(name: domainClass.clazz.name) {
                builder.Properties() {
                    builder.Keys() {
                        keys.each {key ->
                            def type = key.isRelation ? key.relatedModel.name : key.type.name
                            builder.Property(name: key.name, type: type);
                        }
                    }
                    builder.SimpleProperties() {
                        pureProps.each {simpleProp ->
                            builder.Property(name: simpleProp.name, type: simpleProp.type.name);
                        }
                    }
                    builder.Relations() {
                        relations.each {relation ->
                            builder.Property(name: relation.name, type: relation.relatedModel.name);
                        }
                    }
                    builder.FederatedProperties() {
                        federatedProps.each{federatedProp ->
                            builder.Property(name: federatedProp.name, type: federatedProp.type.name);
                        }
                    }
                }
                builder.Operations() {
                    operations.each{operation ->
                        builder.Operation(name:operation.name, description:operation.description, returnType:operation.returnType, parameters:operation.parameters.name.join(","))    
                    }

                }
            }
            render(text: sw.toString(), contentType: "text/xml")
        }
        else {
            addError("default.class.not.found", [params.domain]);
            render(text: errorsToXml(errors), contentType: "text/xml")
        }

    }

    def searchWithQuery = {
        return _search(params);
    }
    def search = {
        return _search(params)
    }

    def _search(params) {
        if (params.max == null) {
            params.max = 20;
        }
        def domainClass = grailsApplication.getArtefactByLogicalPropertyName(DomainClassArtefactHandler.TYPE, params.domain)
        if (domainClass) {
            def query = params.query;
            if (query == null) {
                SearchQuery searchQuery = null;
                def queryList = SearchQuery.searchEvery("name:${params.searchQuery.exactQuery()} AND username:${RsUser.RSADMIN.exactQuery()} AND isPublic:true");
                if (queryList.size() == 0) {
                    searchQuery = SearchQuery.searchEvery("name:${params.searchQuery.exactQuery()} AND username:${session.username}")[0];
                }
                else {
                    searchQuery = queryList[0]
                }
                if (searchQuery) {
                    query = searchQuery.query;
                    if (params.sort == null) {
                        params.sort = searchQuery.sortProperty
                    }
                    if (params.order == null) {
                        params.order = searchQuery.sortOrder;
                    }
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
                    if (params.format == 'xml') {
                        render(text: errorsToXml(errors), contentType: "text/xml")
                    }
                    else {
                        flash.errors = errors
                        redirect(action: params.domain);
                    }
                    return;
                }
                def objectList = searchResults.results
                if (params.format == 'xml') {
                    def grailsClassProperties = [:]
                    def grailsClassPropertyNames = [:]
                    def sortOrder = 0;
                    def sw = new StringWriter();
                    def builder = new MarkupBuilder(sw);
                    builder.Objects(total: searchResults.total, offset: searchResults.offset) {
                        searchResults.results.each {result ->
                            def className = result.getClass().name;
                            def grailsObjectProps = grailsClassProperties[className]
                            def grailsObjectPropNames = grailsClassPropertyNames[className]
                            if (grailsObjectProps == null)
                            {
                                def objectDomainClass = grailsApplication.getDomainClass(className);
                                grailsObjectProps = getPropertiesWhichCanBeListed(objectDomainClass, 10)
                                grailsClassProperties[result.getClass().name] = grailsObjectProps;
                                grailsObjectPropNames = grailsObjectProps.name;
                                grailsClassPropertyNames[result.getClass().name] = grailsObjectPropNames;
                            }
                            def props =  result.asStringMap(grailsObjectPropNames);
                            props.put("rsAlias", className)
                            props.put("sortOrder", sortOrder++)
                            builder.Object(props);
                        }
                    }
                    render(contentType: "text/xml", text: sw.toString())
                }
                else {
                    def propertyList = getPropertiesWhichCanBeListed(domainClass, 5);
                    render(view: "search", model: [objectList: objectList, propertyList: propertyList, count: searchResults.total, domainName: domainClass.fullName])
                }
            }
            else {
                addError("default.query.not.found", [params.searchQuery]);
                if (params.format == 'xml') {
                    render(text: errorsToXml(errors), contentType: "text/xml")
                }
                else {
                    flash.errors = errors
                    redirect(action: params.domain);
                }
            }
        }
        else {
            addError("default.class.not.found", [params.domain]);
            if (params.format == 'xml') {
                render(text: errorsToXml(errors), contentType: "text/xml")
            }
            else {
                flash.errors = errors
                redirect(action: classes);
            }
        }
    }

    def getSearchClasses = {
        def domainClasses = grailsApplication.domainClasses;
        def sw = new StringWriter();
        def builder = new MarkupBuilder(sw);
        builder.Classes() {
            domainClasses.each {
                builder.Class(name: it.fullName);
            }
        }
        render(contentType: "text/xml", text: sw.toString())
    }
    def getPropertiesWhichCanBeListed(domainClass, max) {
        return getPropertiesWhichCanBeListed(domainClass, max, false)
    }
    def getPropertiesWhichCanBeListed(domainClass, max, isAll) {
        def propertyList = [];
        def properties = domainClass.clazz."getPropertiesList"();
        def idProperty = properties.find {it.name == "id"}
        propertyList.add(idProperty);
        def nonKeyProps = properties.findAll {it.name != "id" && !it.isFederated && !it.isKey && !it.isRelation && !it.isOperationProperty}
        def keySet = domainClass.clazz."keySet"(); 
        if (isAll || (nonKeyProps.size() + keySet.size() <= max)) {
            propertyList.addAll(keySet)
            propertyList.addAll(nonKeyProps)
        }
        else {
            propertyList.addAll(keySet);
        }
        return propertyList;
    }
}
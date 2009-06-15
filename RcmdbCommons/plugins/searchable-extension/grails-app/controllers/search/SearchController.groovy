/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
package search

import com.ifountain.rcmdb.domain.util.DomainClassUtils
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import com.ifountain.rcmdb.converter.RapidConvertUtils

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

        def searchResults = search(params);
        if (searchResults == null) {
            withFormat {
                xml {render(text: errorsToXml(errors), contentType: "text/xml")}
            }
            return;
        }
        StringWriter sw = new StringWriter();
        def builder = new MarkupBuilder(sw);
        def grailsClassProperties = [:]
        def grailsClassRelations = [:]
        def sortOrder = 0;
        def stringConverter = RapidConvertUtils.getInstance().lookup(String);
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
                if (grailsObjectRelations == null) {
                    grailsObjectRelations = DomainClassUtils.getRelations(className);
                    grailsClassRelations[result.getClass().name] = grailsObjectRelations;
                }
                def props = [:];
                grailsObjectProps.each {resultProperty ->
                    if (!grailsObjectRelations.containsKey(resultProperty.name)) {
                        props[resultProperty.name] = stringConverter.convert(String, result[resultProperty.name]);
                    }
                }
                props.put("sortOrder", sortOrder++)
                props.put("rsAlias", result.getClass().name)
                builder.Object(props);
            }
        }
        render(text: sw.toString(), contentType: "text/xml");

    }

    def export = {
        def searchResults = search(params);
        def type = params.type;
        if (type == null) {
            type = "xml";
        }
        type = type.toLowerCase();
        if (searchResults == null) {
            return;
        }

        def grailsClassProperties = [:]
        def grailsClassRelations = [:]
        String exportResult = null;
        if (type == "xml") {
            StringWriter sw = new StringWriter();
            def builder = new MarkupBuilder(sw);
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
                    if (grailsObjectRelations == null) {
                        grailsObjectRelations = DomainClassUtils.getRelations(className);
                        grailsClassRelations[result.getClass().name] = grailsObjectRelations;
                    }
                    def props = [:];
                    grailsObjectProps.each {resultProperty ->
                        if (!grailsObjectRelations.containsKey(resultProperty.name)) {
                            props[resultProperty.name] = result[resultProperty.name];
                        }
                    }
                    props.put("rsAlias", result.getClass().name)
                    builder.Object(props);
                }
            }
            exportResult = sw.toString();
        }
        else {
            def sb = new StringBuffer();
            def propertyMap = [:]
            if(searchResults.total == 0 && params.searchIn != null){
                 def objectProps = DomainClassUtils.getFilteredProperties(params.searchIn);
                 def objectRelations = DomainClassUtils.getRelations(params.searchIn);
                 objectProps.each{property ->
                     if(!objectRelations.containsKey(property.name)){
                         propertyMap.put(property.name, "\"${property.name}\"")
                     }
                 }
            }
            def results = [];
            searchResults.results.each {result ->
                def className = result.getClass().name;
                def grailsObjectProps = grailsClassProperties[className]
                def grailsObjectRelations = grailsClassRelations[className];
                if (grailsObjectProps == null)
                {
                    grailsObjectProps = DomainClassUtils.getFilteredProperties(className);
                    grailsClassProperties[result.getClass().name] = grailsObjectProps;
                    grailsObjectRelations = DomainClassUtils.getRelations(className);
                    grailsClassRelations[result.getClass().name] = grailsObjectRelations;
                    grailsObjectProps.each {resultProperty ->
                        if (!grailsObjectRelations.containsKey(resultProperty.name)) {
                            propertyMap[resultProperty.name] = "\"${resultProperty.name}\"";
                        }
                    }
                }
                def props = [:];
                grailsObjectProps.each {resultProperty ->
                    if (!grailsObjectRelations.containsKey(resultProperty.name)) {
                        props[resultProperty.name] = result[resultProperty.name];
                    }
                }
                props.put("rsAlias", result.getClass().name)
                results.add(props);
            }
            propertyMap["rsAlias"] = "\"rsAlias\""
            sb.append("${propertyMap.values().join(',')}\n");
            results.each{props ->
                def properties = [:]
                propertyMap.each{key, value ->
                    if(props.containsKey(key)){
                        properties.put(key, "\"${props.get(key)}\"");
                    }
                    else{
                        properties.put(key, "\"\"");
                    }
                }
                sb.append("${properties.values().join(',')}\n")
            }
            exportResult = sb.toString();
        }

        byte[] bytes = exportResult.getBytes();
        response.setHeader("Content-Disposition", "attachment; filename=search.${type}")
        response.outputStream << bytes
        response.outputStream.flush()
    }

    def search(params) {
        def query = params.query;
        if (query == "" || query == null)
        {
            query = "alias:*";
        }
        if(params.max == null){
            params.max = "1000"
        }
        def searchResults;
        if (params.searchIn != null) {
            GrailsDomainClass grailsClass = grailsApplication.getDomainClass(params.searchIn);
            if (grailsClass == null)
            {
                addError("invalid.`.searchIn", [params.searchIn]);
                return;
            }
            def mc = grailsClass.metaClass;
            try {
                searchResults = mc.invokeStaticMethod(mc.theClass, "search", [query, params] as Object[])
            }
            catch (Throwable e) {
                addError("invalid.search.query", [query, e.getMessage()]);
                return;
            }

        }
        else {
            try {
                searchResults = searchableService.search(query, params);
            }
            catch (Throwable e) {
                addError("invalid.search.query", [query, e.getMessage()]);
                return;
            }
        }
        return searchResults;
    }

}
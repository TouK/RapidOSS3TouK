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

import com.ifountain.rcmdb.converter.RapidConvertUtils
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

        def searchResults = search(params);
        if (searchResults == null) {
            withFormat {
                xml {render(text: errorsToXml(errors), contentType: "text/xml")}
            }
            return;
        }
        StringWriter sw = new StringWriter();
        def builder = new MarkupBuilder(sw);
        def sortOrder = 0;
        def stringConverter = RapidConvertUtils.getInstance().lookup(String);
        builder.Objects(total: searchResults.total, offset: searchResults.offset) {
            searchResults.results.each {result ->
                def className = result.getClass().name;
                def grailsObjectProps = result.getNonFederatedPropertyList();
                def props = [:];
                grailsObjectProps.each {resultProperty ->
                    props[resultProperty.name] = stringConverter.convert(String, result[resultProperty.name]);
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
                    def grailsObjectProps = result.getNonFederatedPropertyList();
                    def props = [:];
                    grailsObjectProps.each {resultProperty ->
                        props[resultProperty.name] = result[resultProperty.name];
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
                 def domainClass = grailsApplication.getDomainClass(params.searchIn);
                 domainClass.getNonFederatedPropertyList().each{property ->
                     propertyMap.put(property.name, "\"${property.name}\"")
                 }
            }
            def results = [];
            def processedClasses =[:]
            searchResults.results.each {result ->
                def className = result.getClass().name;
                def grailsobjectProps = result.getNonFederatedPropertyList();
                if(processedClasses[className] == null)
                {
                    processedClasses[className] =className;
                    grailsobjectProps.each{property ->
                         propertyMap.put(property.name, "\"${property.name}\"")
                     }
                }
                def props = [:];
                grailsobjectProps.each {resultProperty ->
                    props[resultProperty.name] = result[resultProperty.name];
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
                        String escapedProperty = props.get(key).toString().replaceAll("\"", "\"\"");
                        properties.put(key, "\"${escapedProperty}\"");
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
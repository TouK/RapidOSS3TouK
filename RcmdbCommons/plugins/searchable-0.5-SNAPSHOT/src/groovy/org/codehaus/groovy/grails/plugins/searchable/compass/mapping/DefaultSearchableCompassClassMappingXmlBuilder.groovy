/*
* Copyright 2007 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.codehaus.groovy.grails.plugins.searchable.compass.mapping

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import com.ifountain.compass.CompassConstants

/**
 * Builds the Compass class mapping XML. Done with Groovy cos it's so easy compared with Java
 *
 * @author Maurice Nicholson
 */
class DefaultSearchableCompassClassMappingXmlBuilder implements SearchableCompassClassMappingXmlBuilder {
    private static final Log LOG = LogFactory.getLog(DefaultSearchableCompassClassMappingXmlBuilder.class)

    /** Legal attribute names for known XML elements */
    static final PROPERTY_ATTR_NAMES = ['accessor', 'analyzer', 'boost', 'class', 'converter', 'exclude-from-all', 'managed-id', 'managed-id-index', 'managed-id-converter', 'name', 'override']
    static final META_DATA_ATTR_NAMES = ['analyzer', 'boost', 'converter', 'exclude-from-all', 'format', 'index', 'reverse', 'store', 'term-vector']
    static final REFERENCE_ATTR_NAMES = ['accessor', 'cascade', 'converter', 'name', 'ref-alias', 'ref-comp-alias']
    static final COMPONENT_ATTR_NAMES = ['accessor', 'cascade', 'converter', 'max-depth', 'name', 'override', 'ref-alias']

    /** Mapping from input option names to output XML attribute names */
    static final OPTION_ATTR_MAP = [type: 'class', propertyConverter: 'converter', refComponentAlias: 'ref-comp-alias']

    /** Mapping from class mapping option to XML attribute name */
    static final CLASS_MAPPING_ATTR_MAP = [
            alias: 'alias', subIndex: 'sub-index', analyzer: 'analyzer', root: 'root', poly: 'poly', extend: 'extends',
            supportUnmarshall: 'support-unmarshall', boost: 'boost', converter: 'converter',
            enableAll: 'all', all: 'all', allName: 'all-metadata', allAnalyzer: 'all-analyzer', allTermVector: 'all-term-vector'
    ]

    /**
     * Returns an InputStream for the given mapping description
     *
     * @param description describes the class mapping
     * @return an InputStream for the Compass class mapping XML
     */
    InputStream buildClassMappingXml(CompassClassMapping description) {
        def writer = new StringWriter()
        def mkp = new groovy.xml.MarkupBuilder(writer)

        def className = description.mappedClass.name
        LOG.debug("Building Compass mapping XML for [${className}] from description [${description}]")
        def self = this
        mkp."compass-core-mapping" {
            def classAttrs = [name: className, alias: description.alias, root: description.root]
            CLASS_MAPPING_ATTR_MAP.each { propertyName, attrName ->
                if (description[propertyName] != null) {
                    classAttrs[attrName] = description[propertyName]
                }
            }
            "class"(classAttrs) {
                if (description.subIndexHash) {
                    "sub-index-hash"(type: description.subIndexHash.type.getName()) {
                        if (description.subIndexHash.settings) {
                            for (name in description.subIndexHash.settings.keySet()) {
                                "setting"(name: name, value: description.subIndexHash.settings[name])
                            }
                        }
                    }
                }
                // TODO support other "id" properties?
                def idPropName = "id";
                id(name: idPropName, "managed-id":"true"){
                    def idMetaDataAttributes =[:];
                    idMetaDataAttributes.converter = "unformattedlong"
                    'meta-data'(idMetaDataAttributes, idPropName);
                    def untokenizedMetaDataAttributes = new HashMap(idMetaDataAttributes);
                    untokenizedMetaDataAttributes.put("converter", "long");
                    untokenizedMetaDataAttributes.remove ("analyzer");
                    untokenizedMetaDataAttributes.put("exclude-from-all", "true");
                    untokenizedMetaDataAttributes.put("index", "un_tokenized");
                    String untokenizedPropertyName = CompassConstants.UN_TOKENIZED_FIELD_PREFIX+idPropName
                    "meta-data"(untokenizedMetaDataAttributes, untokenizedPropertyName)
                }

                for (constantMetaData in description.constantMetaData) {
                    def metaData = new HashMap(constantMetaData) // clone to avoid corruption
                    def name = metaData.name
                    def attributes = self.transformAttrNames(metaData.attributes)
                    validateAttributes("meta-data", attributes, META_DATA_ATTR_NAMES)
                    constant {
                        'meta-data'(attributes, name)
                        for (value in metaData.values) {
                            'meta-data-value'(value)
                        }
                    }
                }

                for (CompassClassPropertyMapping propertyMapping in description.propertyMappings) {
                    def propertyName = propertyMapping.propertyName
                    def attributes = propertyMapping.attributes
                    LOG.debug("Mapping '${className}.${propertyName}' with attributes ${attributes}")

                    def attrs = [name: propertyName]
                    if (propertyMapping.reference) {
                        def refAttrs = new HashMap(attrs)
                        refAttrs.putAll(self.transformAttrNames(attributes))
                        validateAttributes("reference", refAttrs, REFERENCE_ATTR_NAMES)
                        reference(refAttrs)
                    }
                    if (propertyMapping.component) {
                        def compAttrs = new HashMap(attrs)
                        compAttrs.putAll(self.transformAttrNames(attributes))
                        validateAttributes("component", compAttrs, COMPONENT_ATTR_NAMES)
                        component(compAttrs)
                    }
                    if (propertyMapping.property) {
                        def metaDataAttrs = [:]
                        def tmp = self.transformAttrNames(attributes)
                        validateAttributes("property", tmp, PROPERTY_ATTR_NAMES + META_DATA_ATTR_NAMES)
                        tmp.each { k, v ->
                            if (META_DATA_ATTR_NAMES.contains(k)) {
                                metaDataAttrs[k] = v
                            } else {
                                assert PROPERTY_ATTR_NAMES.contains(k)
                                attrs[k] = v
                            }
                        }
                        def converterName = getConverterName(propertyMapping.propertyType, propertyName)
                        if(converterName != null){
                            metaDataAttrs["converter"] = converterName;
                        }
                        property(attrs) {
                            "meta-data"(metaDataAttrs, propertyName)
                            def untokenizedMetaDataAttributes = new HashMap(metaDataAttrs);
                            untokenizedMetaDataAttributes.remove ("analyzer");
                            untokenizedMetaDataAttributes.put("index", "un_tokenized");
                            untokenizedMetaDataAttributes.put("exclude-from-all", "true");
                            String untokenizedPropertyName = CompassConstants.UN_TOKENIZED_FIELD_PREFIX+propertyName
                            converterName = getConverterName(propertyMapping.propertyType, untokenizedPropertyName)
                            if(converterName != null){
                                untokenizedMetaDataAttributes["converter"] = converterName;
                            }
                            "meta-data"(untokenizedMetaDataAttributes, untokenizedPropertyName)
                        }
                    }
                }
           }
       }

       def xml = """<?xml version="1.0"?>
<!DOCTYPE compass-core-mapping PUBLIC 
    "-//Compass/Compass Core Mapping DTD 2.0//EN"
    "http://www.compass-project.org/dtd/compass-core-mapping-2.2.dtd">
""" + writer.toString()

//       System.out.println("${className} xml [${xml}]")
       LOG.debug("${className} xml [${xml}]")
       return new ByteArrayInputStream(xml.getBytes())
    }

    private getConverterName(Class propertyType, String propName)
    {
        if(propertyType.name == Double.name || propertyType.name == double.name || propertyType.name == Float.name || propertyType.name == float.name)
        {
            if(propName.startsWith(CompassConstants.UN_TOKENIZED_FIELD_PREFIX))
            {
                return  "double";
            }
            else
            {
                return "unformatteddouble"
            }
        }
        else if(Number.isAssignableFrom(propertyType))
        {
            if(propName.startsWith(CompassConstants.UN_TOKENIZED_FIELD_PREFIX))
            {
                return  "long";
            }
            else
            {
                return "unformattedlong"
            }
        }
        else if(propertyType.name == String.class.name)
        {
            if(propName.startsWith(CompassConstants.UN_TOKENIZED_FIELD_PREFIX))
            {
                return  "lowercasedstring";
            }
        }
        return null;
    }

    private validateAttributes(elementName, attributeMap, validAttrNames) {
        def invalidAttrs = attributeMap.keySet() - validAttrNames
        if (invalidAttrs) {
            throw new IllegalArgumentException("Invalid attribute(s) for $elementName element: ${invalidAttrs}. Valid attributes are ${validAttrNames.unique().sort()}. Given attributes are ${attributeMap}")
        }
    }

    private transformAttrNames(value) {
        if (value == true) {
            return [:]
        }
        assert value instanceof Map, "attrs should be value of Map"
        def attrs = [:]
        value.each { k, v ->
            if (OPTION_ATTR_MAP[k]) {
                k = OPTION_ATTR_MAP[k]
            } else {
                k = convertCamelCaseToLowerCaseDashed(k)
            }
            attrs[k] = v
        }
        return attrs
    }

    // TODO extract to utils class if needed elsewhere
    public convertCamelCaseToLowerCaseDashed(String string) {
        def buf = new StringBuffer()
        for (i in 0..<string.size()) {
            def ch = string[i]
            if (Character.isUpperCase(ch as char)) {
                if (i != 0) {
                    buf.append("-")
                }
                ch = ch.toLowerCase()
            }
            buf.append(ch)
        }
        return buf.toString()
    }
}
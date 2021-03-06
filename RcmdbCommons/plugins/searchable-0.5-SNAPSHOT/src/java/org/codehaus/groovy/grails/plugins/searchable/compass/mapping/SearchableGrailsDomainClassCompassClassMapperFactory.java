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
package org.codehaus.groovy.grails.plugins.searchable.compass.mapping;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.grails.plugins.searchable.compass.converter.DefaultCompassConverterLookupHelper;
import org.compass.core.config.CompassConfigurationFactory;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassEnvironment;
import org.compass.core.converter.ConverterLookup;
import org.compass.core.spi.InternalCompass;
import org.compass.core.Compass;
import org.springframework.util.ClassUtils;

import java.util.List;
import java.util.Map;

/**
 * @author Maurice Nicholson
 */
public class SearchableGrailsDomainClassCompassClassMapperFactory {
    private static final Log LOG = LogFactory.getLog(SearchableGrailsDomainClassCompassClassMapperFactory.class);

    public static CompositeSearchableGrailsDomainClassCompassClassMapper getDefaultSearchableGrailsDomainClassCompassClassMapper(List defaultExcludedProperties, Map defaultFormats) {

        CompassConfiguration conf = CompassConfigurationFactory.newConfiguration();
        conf.setConnection("ram://dummy");
        conf.getSettings().setSetting(CompassEnvironment.REGISTER_SHUTDOWN_HOOK, "false");
        InternalCompass compass = ((InternalCompass) conf.buildCompass());
        try{
            ConverterLookup lookup = compass.getConverterLookup();
            return getDefaultSearchableGrailsDomainClassCompassClassMapper(defaultExcludedProperties, defaultFormats, lookup);
        }finally{
            compass.close();
        }
    }
    public static CompositeSearchableGrailsDomainClassCompassClassMapper getDefaultSearchableGrailsDomainClassCompassClassMapper(List defaultExcludedProperties, Map defaultFormats, ConverterLookup lookup) {
        DefaultCompassConverterLookupHelper converterLookupHelper = new DefaultCompassConverterLookupHelper();
        converterLookupHelper.setConverterLookup(lookup);

        SearchableGrailsDomainClassPropertyMappingFactory domainClassPropertyMappingFactory = new SearchableGrailsDomainClassPropertyMappingFactory();
        domainClassPropertyMappingFactory.setDefaultFormats(defaultFormats);
        domainClassPropertyMappingFactory.setConverterLookupHelper(converterLookupHelper);

        CompositeSearchableGrailsDomainClassCompassClassMapper classMapper = new CompositeSearchableGrailsDomainClassCompassClassMapper();
        classMapper.setDefaultExcludedProperties(defaultExcludedProperties);
        SearchableGrailsDomainClassCompassClassMapper[] classMappers = getActualSearchableGrailsDomainClassCompassClassMappers(domainClassPropertyMappingFactory, classMapper);
        classMapper.setSearchableGrailsDomainClassCompassMappingDescriptionProviders(classMappers);
        return classMapper;
    }

    private static SearchableGrailsDomainClassCompassClassMapper[] getActualSearchableGrailsDomainClassCompassClassMappers(SearchableGrailsDomainClassPropertyMappingFactory domainClassPropertyMappingFactory, CompositeSearchableGrailsDomainClassCompassClassMapper parent) {
        SearchableGrailsDomainClassCompassClassMapper[] classMappers;
        try {
            SimpleSearchableGrailsDomainClassCompassClassMapper simpleClassMapper = new SimpleSearchableGrailsDomainClassCompassClassMapper();
            simpleClassMapper.setDomainClassPropertyMappingStrategyFactory(domainClassPropertyMappingFactory);
            simpleClassMapper.setParent(parent);

            AbstractSearchableGrailsDomainClassCompassClassMapper closureClassMapper = (AbstractSearchableGrailsDomainClassCompassClassMapper) ClassUtils.forName("org.codehaus.groovy.grails.plugins.searchable.compass.mapping.ClosureSearchableGrailsDomainClassCompassClassMapper").newInstance();
            closureClassMapper.setDomainClassPropertyMappingStrategyFactory(domainClassPropertyMappingFactory);
            closureClassMapper.setParent(parent);

            classMappers = new SearchableGrailsDomainClassCompassClassMapper[] {
                simpleClassMapper, closureClassMapper
            };
        } catch (Exception ex) {
            // Log and throw runtime exception
            LOG.error("Failed to find or create closure mapping provider class instance", ex);
            throw new IllegalStateException("Failed to find or create closure mapping provider class instance: " + ex);
        }
        return classMappers;
    }
}

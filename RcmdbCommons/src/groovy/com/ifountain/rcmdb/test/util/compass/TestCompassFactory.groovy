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
package com.ifountain.rcmdb.test.util.compass

import org.codehaus.groovy.grails.commons.DefaultGrailsApplication
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.searchable.compass.config.SearchableCompassConfiguratorFactory
import org.codehaus.groovy.grails.plugins.searchable.compass.config.mapping.SearchableGrailsDomainClassMappingConfigurator
import org.codehaus.groovy.grails.plugins.searchable.compass.config.mapping.SearchableGrailsDomainClassMappingConfiguratorFactory
import org.codehaus.groovy.grails.plugins.searchable.compass.mapping.DefaultSearchableCompassClassMappingXmlBuilder
import org.compass.core.CompassCallback
import org.compass.core.CompassException
import org.compass.core.CompassSession
import org.compass.core.CompassTemplate
import org.compass.core.config.CompassConfiguration
import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.ifountain.compass.index.WrapperIndexDeletionPolicy;
/**
 *
 * @author Maurice Nicholson
 */
class TestCompassFactory {
    static indexDirectory = "../testindex";
    static getGrailsApplication(Collection classes) {
        def grailsApplication = new DefaultGrailsApplication(classes as Class[], new GroovyClassLoader(Thread.currentThread().getContextClassLoader())) //new GroovyClassLoader())
        grailsApplication.initialise()
        return grailsApplication
    }

    static getCompass(Collection classes, Collection instances = null, boolean willPersist = false) {
        def grailsApplication = getGrailsApplication(classes)
        return getCompass(grailsApplication, instances, willPersist)
    }

    static getPersistedCompass(Collection classes, Collection instances = null) {
        def grailsApplication = getGrailsApplication(classes)
        return getCompass(grailsApplication, instances, true)
    }


    static getCompass(GrailsApplication grailsApplication, Collection instances = null, boolean willPersist) {
        ApplicationHolder.application = grailsApplication;
        def configurator = SearchableCompassConfiguratorFactory.getDomainClassMappingConfigurator(
            grailsApplication,
            [SearchableGrailsDomainClassMappingConfiguratorFactory.getSearchableClassPropertyMappingConfigurator([(Long):"#000000000000000000000000000000"], [], new DefaultSearchableCompassClassMappingXmlBuilder())] as SearchableGrailsDomainClassMappingConfigurator[]
        )
        def config = new CompassConfiguration()
        if(willPersist)
        {
            config.setConnection(indexDirectory)
        }
        else
        {
            config.setConnection("ram://testindex")
        }
//        config.getSettings().setSetting ("compass.transaction.isolation", "batch_insert");
        config.getSettings().setSetting ("compass.transaction.disableThreadBoundLocalTransaction", "true");
        config.getSettings().setSetting ("compass.cache.first", "org.compass.core.cache.first.NullFirstLevelCache");
        config.getSettings().setSetting ("compass.engine.store.wrapper.wrapper1.type", "com.ifountain.compass.CompositeDirectoryWrapperProvider");
        config.getSettings().setSetting ("compass.engine.store.wrapper.wrapper1.awaitTermination", "10000000");
        config.getSettings().setSetting ("compass.engine.store.indexDeletionPolicy.type", WrapperIndexDeletionPolicy.name);
        configurator.configure(config, [:])
        def compass = config.buildCompass()

        if (instances) {
            CompassTemplate template = new CompassTemplate(compass)
            template.execute(new SaveInstancesCompassCallback(instances))
        }

        return compass
    }
}

// Save all object instances to compass
class SaveInstancesCompassCallback implements CompassCallback {
    def instances

    SaveInstancesCompassCallback(instances) {
        this.instances = instances
    }

    public Object doInCompass(CompassSession session) throws CompassException {
        instances.each { session.save(it) }
    }
}
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
package org.codehaus.groovy.grails.plugins.searchable.compass;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.plugins.searchable.SearchableMethod;
import org.codehaus.groovy.grails.plugins.searchable.SearchableMethodFactory;
import org.codehaus.groovy.grails.plugins.searchable.compass.index.DefaultIndexMethod;
import org.codehaus.groovy.grails.plugins.searchable.compass.index.DefaultReindexMethod;
import org.codehaus.groovy.grails.plugins.searchable.compass.index.DefaultUnindexMethod;
import org.codehaus.groovy.grails.plugins.searchable.compass.search.*;
import org.codehaus.groovy.grails.plugins.searchable.compass.support.AbstractSearchableMethod;
import org.compass.core.Compass;
import org.compass.gps.CompassGps;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of creating SearchableMethod instances
 *
 * @author Maurice Nicholson
 */
public class DefaultSearchableMethodFactory implements SearchableMethodFactory {
    private static final Log LOG = LogFactory.getLog(DefaultSearchableMethodFactory.class);
    private static final Map DEFAULT_SEARCH_DEFAULTS = new HashMap() {{
        put("escape", Boolean.FALSE);
        put("offset", new Integer(0));
        put("max", new Integer(10));
        put("reload", Boolean.FALSE);
    }};
    private static final Map DEFAULT_TERM_FREQ_DEFAULTS = new HashMap() {{
        put("properties", new String[] {"zzz-all"}); // todo make it respect the configured name
    }};

    private Map searchDefaults;
    private Compass compass;
    private CompassGps compassGps;
    private GrailsApplication grailsApplication;

    public SearchableMethod getMethod(final Class clazz, String methodName) {
        AbstractSearchableMethod method = (AbstractSearchableMethod) getMethod(methodName);
        method.setDefaultOptions(new HashMap(method.getDefaultOptions()) {{ // clone to avoid corrupting original
            put("class", clazz);
        }});
        return method;
    }

    public SearchableMethod getMethod(String methodName) {
        // TODO refactor to (injected) lookup map
        if (methodName.equals("indexAll")) {
            System.out.println("The Searchable Plugin 'indexAll' method is deprecated and will be removed in the next version: please use 'index' instead");
            LOG.warn("The Searchable Plugin 'indexAll' method is deprecated and will be removed in the next version: please use 'index' instead");
            return new DefaultIndexMethod(methodName, compass, compassGps);
        }
        if (methodName.equals("index")) {
            return new DefaultIndexMethod(methodName, compass, compassGps);
        }
        if (methodName.equals("unindexAll")) {
            System.out.println("The Searchable Plugin 'unindexAll' method is deprecated and will be removed in the next version: please use 'unindex' instead");
            LOG.warn("The Searchable Plugin 'unindexAll' method is deprecated and will be removed in the next version: please use 'unindex' instead");
            return new DefaultUnindexMethod(methodName, compass);
        }
        if (methodName.equals("unindex")) {
            return new DefaultUnindexMethod(methodName, compass);
        }
        if (methodName.equals("reindexAll")) {
            System.out.println("The Searchable Plugin 'reindexAll' method is deprecated and will be removed in the next version: please use 'reindex' instead");
            LOG.warn("The Searchable Plugin 'reindexAll' method is deprecated and will be removed in the next version: please use 'reindex' instead");
            return new DefaultReindexMethod(methodName, compass, compassGps);
        }
        if (methodName.equals("reindex")) {
            return new DefaultReindexMethod(methodName, compass, compassGps);
        }

        if (methodName.equals("termFreqs")) {
            return new DefaultTermFreqsMethod(methodName, compass, grailsApplication, DEFAULT_TERM_FREQ_DEFAULTS);
        }

        DefaultSearchMethod searchMethod = new DefaultSearchMethod(methodName, compass, grailsApplication, buildSearchDefaults());
        if (methodName.equals("search")) {
            searchMethod.setCompassQueryBuilder(new DefaultSearchableCompassQueryBuilder(compass));
            searchMethod.setHitCollector(new DefaultSearchableSubsetHitCollector());
            searchMethod.setSearchResultFactory(new SearchableSubsetSearchResultFactory());
        }
        else if (methodName.equals("searchTop")) {
            searchMethod.setCompassQueryBuilder(new DefaultSearchableCompassQueryBuilder(compass));
            searchMethod.setHitCollector(new DefaultSearchableTopHitCollector());
            searchMethod.setSearchResultFactory(new SearchableHitsOnlySearchResultFactory());
        }
        else if (methodName.equals("searchEvery")) {
            searchMethod.setCompassQueryBuilder(new DefaultSearchableCompassQueryBuilder(compass));
            searchMethod.setHitCollector(new DefaultSearchableEveryHitCollector());
            searchMethod.setSearchResultFactory(new SearchableHitsOnlySearchResultFactory());
        }
        else if (methodName.equals("countHits")) {
            searchMethod.setCompassQueryBuilder(new DefaultSearchableCompassQueryBuilder(compass));
            searchMethod.setHitCollector(new CountOnlyHitCollector());
            searchMethod.setSearchResultFactory(new SearchableHitsOnlySearchResultFactory());
        }
        return searchMethod;
    }

    private Map buildSearchDefaults() {
        Map m = new HashMap(DEFAULT_SEARCH_DEFAULTS);
        if (searchDefaults != null) {
            m.putAll(searchDefaults);
        }
        LOG.debug("search defaults: " + m);
        return m;
    }

    public Map getSearchDefaults() {
        return searchDefaults;
    }

    public void setSearchDefaults(Map searchDefaults) {
        this.searchDefaults = searchDefaults;
    }

    public Compass getCompass() {
        return compass;
    }

    public void setCompass(Compass compass) {
        this.compass = compass;
    }

    public CompassGps getCompassGps() {
        return compassGps;
    }

    public void setCompassGps(CompassGps compassGps) {
        this.compassGps = compassGps;
    }

    public void setGrailsApplication(GrailsApplication grailsApplication) {
        this.grailsApplication = grailsApplication;
    }
}

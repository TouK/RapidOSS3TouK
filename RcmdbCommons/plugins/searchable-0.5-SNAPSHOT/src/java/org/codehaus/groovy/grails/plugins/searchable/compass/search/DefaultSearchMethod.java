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
package org.codehaus.groovy.grails.plugins.searchable.compass.search;

import groovy.lang.Closure;
import groovy.lang.GString;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.plugins.searchable.SearchableMethod;
import org.codehaus.groovy.grails.plugins.searchable.compass.support.AbstractSearchableMethod;
import org.codehaus.groovy.grails.plugins.searchable.compass.support.SearchableMethodUtils;
import org.compass.core.*;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

/**
 * The default search method implementation
 *
 * @author Maurice Nicholson
 */
public class DefaultSearchMethod extends AbstractSearchableMethod implements SearchableMethod {
    private static Log LOG = LogFactory.getLog(DefaultSearchMethod.class);

    private GrailsApplication grailsApplication;
    private SearchableCompassQueryBuilder compassQueryBuilder;
    private SearchableHitCollector hitCollector;
    private SearchableSearchResultFactory searchResultFactory;
    
    public DefaultSearchMethod(String methodName, Compass compass, GrailsApplication grailsApplication, Map defaultOptions) {
        super(methodName, compass, defaultOptions);
        this.grailsApplication = grailsApplication;
    }

    public Object invoke(Object[] args) {
        Assert.notNull(args, "args cannot be null");
        Assert.notEmpty(args, "args cannot be empty");

        final Object query = getQuery(args);
        Assert.notNull(query, "No query String or Closure argument given to " + getMethodName() + "(): you must supply one");
        final Map options = SearchableMethodUtils.getOptionsArgument(args, getDefaultOptions());

        SearchCompassCallback searchCallback = new SearchCompassCallback(options, query);
        searchCallback.setGrailsApplication(grailsApplication);
        searchCallback.setCompassQueryBuilder(compassQueryBuilder);
        searchCallback.setHitCollector(hitCollector);
        searchCallback.setSearchResultFactory(searchResultFactory);
        return doInCompass(searchCallback);
    }

    private Object getQuery(Object[] args) {
        for (int i = 0, max = args.length; i < max; i++) {
            if (args[i] instanceof Closure || args[i] instanceof String) {
                return args[i];
            }
            else if(args[i] instanceof GString)
            {
                return args[i].toString();                
            }
        }
        return null;
    }

    public void setCompassQueryBuilder(SearchableCompassQueryBuilder compassQueryBuilder) {
        this.compassQueryBuilder = compassQueryBuilder;
    }

    public void setHitCollector(SearchableHitCollector hitCollector) {
        this.hitCollector = hitCollector;
    }

    public void setSearchResultFactory(SearchableSearchResultFactory searchResultFactory) {
        this.searchResultFactory = searchResultFactory;
    }

    public void setGrailsApplication(GrailsApplication grailsApplication) {
        this.grailsApplication = grailsApplication;
    }

    public static class SearchCompassCallback implements CompassCallback {
        private final Map options;
        private final Object query;

        private GrailsApplication grailsApplication;
        private SearchableCompassQueryBuilder compassQueryBuilder;
        private SearchableHitCollector hitCollector;
        private SearchableSearchResultFactory searchResultFactory;

        public SearchCompassCallback(Map options, Object query) {
            this.options = options;
            this.query = query;
        }

        public Object _doInCompass(CompassSession session) throws CompassException {
            Map tempOptions = new HashMap();
            tempOptions.putAll(options);
            Closure rawProcessor = (Closure) tempOptions.get("raw");
            tempOptions.put("raw", rawProcessor != null);
            CompassQuery compassQuery = compassQueryBuilder.buildQuery(grailsApplication, session, tempOptions, query);
            long start = System.currentTimeMillis();
            CompassHits hits = compassQuery.hits();
            if (LOG.isDebugEnabled()) {
                long time = System.currentTimeMillis() - start;
                LOG.debug("query: [" + compassQuery + "], [" + hits.length() + "] hits, took [" + time + "] millis");
            }
//                long time = System.currentTimeMillis() - start;
//                System.out.println("query: [" + compassQuery + "], [" + hits.length() + "] hits, took [" + time + "] millis");

            if(rawProcessor != null)
            {
                return doWithRawDataProcessor(session, rawProcessor, hits);
            }
            else
            {
                Object collectedHits = hitCollector.collect(hits, tempOptions);
                Object searchResult = searchResultFactory.buildSearchResult(hits, collectedHits, tempOptions);
                doWithHighlighter(collectedHits, hits, searchResult, tempOptions);
                return searchResult;
            }
        }
        public Object doInCompass(CompassSession session) throws CompassException {
            try
            {
                return _doInCompass(session);
            }
            catch(RuntimeException t)
            {
                //TODO:if compass cannot determine sort type, it throws exception. In this case, we will try to perform
                // string sort. This will give correct results since all of the values are formatted with same formatter
                if(options.get("sortType") == null && (t.toString().indexOf("does not appear to be indexed") >= 0 || t.toString().indexOf("java.lang.ClassCastException") >= 0))
                {
                    options.put("sortType", "string");
                    return _doInCompass(session);
                }
                throw t;
            }
        }

        public void doWithHighlighter(Object collectedHits, CompassHits hits, Object searchResult, Map searchOptions) {
            if (!(collectedHits instanceof Collection)) {
                return;
            }
            Closure withHighlighter = (Closure) searchOptions.get("withHighlighter");
            if (withHighlighter == null) {
                return;
            }
            withHighlighter = (Closure) withHighlighter.clone();
            int offset = org.apache.commons.collections.MapUtils.getIntValue(searchOptions, "offset");
            for (int i = 0, length = ((Collection) collectedHits).size(); i < length; i++) {
                withHighlighter.call(new Object[] {
                    hits.highlighter(offset + i), new Integer(i), searchResult
                });
            }
        }

        public Object doWithRawDataProcessor(CompassSession session, Closure rawProcessor, CompassHits hits) {
            rawProcessor = (Closure) rawProcessor.clone();
            return rawProcessor.call(new Object[]{hits, session});
        }

        public void setGrailsApplication(GrailsApplication grailsApplication) {
            this.grailsApplication = grailsApplication;
        }

        public void setCompassQueryBuilder(SearchableCompassQueryBuilder compassQueryBuilder) {
            this.compassQueryBuilder = compassQueryBuilder;
        }

        public void setHitCollector(SearchableHitCollector hitCollector) {
            this.hitCollector = hitCollector;
        }

        public void setSearchResultFactory(SearchableSearchResultFactory searchResultFactory) {
            this.searchResultFactory = searchResultFactory;
        }
    }
}

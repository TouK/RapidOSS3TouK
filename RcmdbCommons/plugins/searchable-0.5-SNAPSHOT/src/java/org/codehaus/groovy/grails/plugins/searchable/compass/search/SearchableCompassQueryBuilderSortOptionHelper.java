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

import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.compass.core.Compass;
import org.compass.core.CompassQuery;
import org.compass.core.CompassSession;
import org.springframework.util.Assert;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import com.ifountain.compass.CompassConstants;

//MODIFIED
/**
 * Post-processes a query to add sort
 *
 * @author Maurice Nicholson
 */
public class SearchableCompassQueryBuilderSortOptionHelper implements SearchableCompassQueryBuilderOptionsHelper {
    public static final String DIRECTION = "direction";
    public static final String ORDER = "order";
    public static final List VALID_SORT_DIRECTION_VALUES = Arrays.asList(new String[]{"asc", "desc", "auto", "reverse"});

    public CompassQuery applyOptions(GrailsApplication grailsApplication, Compass compass, CompassSession compassSession, CompassQuery compassQuery, Map options) {
        return addSort(compassQuery, options);
    }
    //TODO: Added on Searchable Plugin: Search with sorting on multiple fields
    
    public CompassQuery addSort(CompassQuery compassQuery, Map options) {
        Object sort = options.get("sort");
        if (sort == null) {
            return compassQuery;
        }
        Assert.isTrue(sort instanceof String || sort instanceof List, "sort option should be given as string or list");
        List<String> sorts = null;
        List<String> orders = null;
        if (sort instanceof String) {
            sorts = Arrays.asList(StringUtils.split((String) sort, ','));
        } else {
            sorts = (List) sort;
        }
        if (!options.containsKey(ORDER) && !options.containsKey(DIRECTION)) {
            orders = new ArrayList<String>();
        } else {
            Assert.isTrue((options.containsKey(ORDER) && !options.containsKey(DIRECTION)) || (!options.containsKey(ORDER) && options.containsKey(DIRECTION)), "Either specify a sort '" + ORDER + "' or '" + DIRECTION + "' or neither but not both");
            Object order = options.get(DIRECTION);
            if (order == null) {
                order = options.get(ORDER);
            }
            Assert.isTrue(order instanceof String || order instanceof List, "order/direction option should be given as string or list");
            if (order instanceof String) {
                orders = Arrays.asList(StringUtils.split((String) order, ','));
            } else {
                orders = (List) order;
            }
        }
        if (orders.size() < sorts.size()) {
            while (orders.size() != sorts.size()) {
                orders.add("auto");
            }
        }
        for (int i = 0; i < sorts.size(); i++) {
            String s = sorts.get(i);
            String o = orders.get(i);
            Object sortProperty = getSortProperty(s);
            CompassQuery.SortDirection direction = getSortDirection(sortProperty, o);
            if (sortProperty instanceof CompassQuery.SortImplicitType) {
                compassQuery = compassQuery.addSort((CompassQuery.SortImplicitType) sortProperty, direction);
            } else {
                Assert.isInstanceOf(String.class, sortProperty, "Expected string");
                CompassQuery.SortPropertyType sortType = getSortType((String) options.get("sortType"));
                compassQuery = compassQuery.addSort((String) sortProperty, sortType, direction);
            }
        }
        return compassQuery;
    }

    private Object getSortProperty(String sort) {
        Assert.notNull(sort, "sort cannot be null");
        sort = sort.trim();
        if (sort.equals("SCORE")) {
            return CompassQuery.SortImplicitType.SCORE;
        }
        return CompassConstants.UN_TOKENIZED_FIELD_PREFIX + sort;
    }

    private CompassQuery.SortPropertyType getSortType(String sortType) {
        if (sortType == null) {
            return CompassQuery.SortPropertyType.AUTO;
        } else if (sortType.equalsIgnoreCase("string")) {
            return CompassQuery.SortPropertyType.STRING;
        } else if (sortType.equalsIgnoreCase("int") || sortType.equalsIgnoreCase("int")) {
            return CompassQuery.SortPropertyType.INT;
        } else if (sortType.equalsIgnoreCase("float")) {
            return CompassQuery.SortPropertyType.FLOAT;
        } else {
            return CompassQuery.SortPropertyType.AUTO;
        }
    }

    /**
     * Get the CompassQuery.SortDirection for the given property and optional order/direction Map entry
     *
     * @param property either CompassQuery.SortImplicitType.SCORE or a class property name (String)
     * @param order 
     * @return
     */
    private CompassQuery.SortDirection getSortDirection(Object property, String order) {
        order = order.trim();
        Assert.notNull(property, "sort property cannot be null");
        Assert.isTrue(VALID_SORT_DIRECTION_VALUES.contains(order), "The sort order/direction '" + order + "' is not a valid value");
        return property.equals(CompassQuery.SortImplicitType.SCORE) ?
                order.equals("asc") || order.equals("reverse") ? CompassQuery.SortDirection.REVERSE : CompassQuery.SortDirection.AUTO :
                order.equals("asc") || order.equals("auto") ? CompassQuery.SortDirection.AUTO : CompassQuery.SortDirection.REVERSE;
    }
}

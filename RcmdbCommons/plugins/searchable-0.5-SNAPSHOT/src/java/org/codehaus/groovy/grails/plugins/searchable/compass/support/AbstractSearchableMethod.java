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
package org.codehaus.groovy.grails.plugins.searchable.compass.support;

import org.codehaus.groovy.grails.plugins.searchable.SearchableMethod;
import org.compass.core.Compass;
import org.compass.core.CompassCallback;
import org.compass.core.CompassTemplate;
import org.compass.core.CompassTransaction;
import org.compass.core.lucene.engine.transaction.readcommitted.BitSetByAliasFilter;
import org.springframework.util.Assert;

import java.util.Map;

import com.ifountain.compass.SingleCompassSessionManager;

/**
 * @author Maurice Nicholson
 */
public abstract class AbstractSearchableMethod implements SearchableMethod {
    private String methodName;
    private Compass compass;
    private Map defaultOptions;

    public AbstractSearchableMethod(String methodName, Compass compass, Map defaultOptions) {
        Assert.notNull(methodName, "methodName cannot be null");
        Assert.notNull(compass, "compass cannot be null");
        this.methodName = methodName;
        this.compass = compass;
        this.defaultOptions = defaultOptions;
    }

    public Object doCall(Object[] args) {
        return invoke(args);
    }

    // allows groovy code to call instances of this object as a method, ie, method = AbstractSearchableMethod(); method()
    public Object call(Object[] args) {
        return invoke(args);
    }

    protected Object doInCompass(CompassCallback compassCallback) {
        CompassTransaction tx=null;
        try {

	            tx = SingleCompassSessionManager.beginTransaction();
                Object result = compassCallback.doInCompass(tx.getSession());
                tx.commit();
                return result;

        } catch (RuntimeException e) {
            if (tx != null) {
                try {
                    tx.rollback();
                } catch (Exception e1) {
                }
            }
            throw e;
        } catch (Error err) {
            if (tx != null) {
                try {
                    tx.rollback();
                } catch (Exception e1) {
                }
            }
            throw err;
        }
    }

    public Map getDefaultOptions() {
        return defaultOptions;
    }

    public void setDefaultOptions(Map defaultOptions) {
        this.defaultOptions = defaultOptions;
    }

    public Compass getCompass() {
        return compass;
    }

    public String getMethodName() {
        return methodName;
    }
}

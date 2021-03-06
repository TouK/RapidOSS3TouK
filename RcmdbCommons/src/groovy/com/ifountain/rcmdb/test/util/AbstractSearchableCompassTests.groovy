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
package com.ifountain.rcmdb.test.util

import com.ifountain.rcmdb.test.util.compass.TestCompassUtils
import com.ifountain.compass.index.WrapperIndexDeletionPolicy

/**
 * 
 *
 * @author Maurice Nicholson
 */
abstract class AbstractSearchableCompassTests extends GroovyTestCase {
    
    public void setUp() {
        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
        WrapperIndexDeletionPolicy.clearPolicies();
    }

    public void tearDown() {
        super.tearDown();    //To change body of overridden methods use File | Settings | File Templates.
        WrapperIndexDeletionPolicy.clearPolicies();
    }

    def withCompassQueryBuilder(closure) {
        TestCompassUtils.withCompassQueryBuilder(compass, closure)
    }

    def withCompassSession(closure) {
        TestCompassUtils.withCompassSession(compass, closure)
    }

    def numberIndexed(clazz) {
        TestCompassUtils.numberIndexed(compass, clazz)
    }

    def countHits(closure) {
        TestCompassUtils.countHits(compass, closure)
    }

    def saveToCompass(Object[] objects) {
        TestCompassUtils.saveToCompass(compass, objects)
    }

    def loadFromCompass(clazz, id) {
        TestCompassUtils.loadFromCompass(compass, clazz, id)
    }

    def clearIndex() {
        TestCompassUtils.clearIndex(compass)
    }
}
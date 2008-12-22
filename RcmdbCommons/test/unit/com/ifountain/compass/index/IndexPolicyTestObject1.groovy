package com.ifountain.compass.index

import com.ifountain.compass.CompositeDirectoryWrapperProvider

/**
 * Created by IntelliJ IDEA.
 * User: mustafa seker
 * Date: Dec 22, 2008
 * Time: 8:53:40 PM
 * To change this template use File | Settings | File Templates.
 */
class IndexPolicyTestObject1
{
     static searchable = {
        storageType CompositeDirectoryWrapperProvider.MIRRORED_DIR_TYPE
    }
    Long id
    Long version;
    String prop1 = "";
}
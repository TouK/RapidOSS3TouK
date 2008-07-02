package com.ifountain.rcmdb.domain.generation

import model.Model

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 16, 2008
* Time: 3:38:30 PM
* To change this template use File | Settings | File Templates.
*/
class MockModel extends Model
{
    def static childModels;

    Set datasources;
    Set modelProperties
    Set fromRelations;
    Set toRelations;
    public MockModel()
    {
        datasources = Collections.emptySet();
        modelProperties = Collections.emptySet()
        fromRelations = Collections.emptySet();
        toRelations = Collections.emptySet();
    }
}
package com.ifountain.rcmdb.domain

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

    def numberOfSaveCalls = 0;
    def datasources = Collections.emptySet();
    def modelProperties = Collections.emptySet()
    def fromRelations = Collections.emptySet();
    def toRelations = Collections.emptySet();
    def getModelFile()
    {
        return new File("../testoutput/${name}.groovy");
    }

    def getControllerFile()
    {
        return new File("../testoutput/${name}Controller.groovy");
    }

    def getOperationsFile()
    {
        return new File("../testoutput/${name}${ModelUtils.OPERATIONS_CLASS_EXTENSION}.groovy");
    }

    def save()
    {
        numberOfSaveCalls++;
    }


}
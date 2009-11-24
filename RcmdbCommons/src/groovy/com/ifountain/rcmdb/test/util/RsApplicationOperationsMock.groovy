package com.ifountain.rcmdb.test.util

import org.codehaus.groovy.grails.commons.ApplicationHolder

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: May 11, 2009
* Time: 5:46:51 PM
* To change this template use File | Settings | File Templates.
*/
class RsApplicationOperationsMock extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation{

    public static def getUtility(utilityName)
    {
       return RsApplicationTestUtils.loadUtility(utilityName).newInstance();
    }

    public static def getModelClass(String modelName)
    {
        def modelClass=ApplicationHolder.application.getDomainClass(modelName);
        if(modelClass)
        {
            return modelClass.clazz;
        }
        else
        {
            throw new Exception("Model Class ${modelName} does not exist");
        }
    }
}
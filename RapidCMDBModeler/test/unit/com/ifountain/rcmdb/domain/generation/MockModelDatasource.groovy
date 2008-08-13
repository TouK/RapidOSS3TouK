package com.ifountain.rcmdb.domain.generation

import model.ModelDatasource

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 16, 2008
* Time: 3:39:12 PM
* To change this template use File | Settings | File Templates.
*/
class MockModelDatasource extends ModelDatasource
{
    public MockModelDatasource()
    {
    }
    def getModelFile()
    {
        return new File("../testoutput/${name}.groovy");
    }
}
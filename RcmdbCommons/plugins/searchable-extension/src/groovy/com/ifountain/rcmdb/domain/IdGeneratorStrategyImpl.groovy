package com.ifountain.rcmdb.domain

import application.ObjectId
import com.ifountain.rcmdb.domain.method.CompassMethodInvoker


/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Jun 18, 2008
* Time: 11:52:32 AM
* To change this template use File | Settings | File Templates.
*/
class IdGeneratorStrategyImpl implements IdGeneratorStrategy
{
    private long nextId = 0;
    private long numberOfRemainingIds = 0;
    private int INCREMENT_AMOUNT = 1000;
    public long getNextId() {
        if(numberOfRemainingIds == 0)
        {
            def res = CompassMethodInvoker.search (ObjectId.metaClass, [name:"generalObjectId"])
            def objectId = res.results[0];
            if(!objectId)
            {
                objectId = new ObjectId(name:"generalObjectId", nextId:INCREMENT_AMOUNT);
                objectId.id = 0;
                ObjectId.index(objectId);
                nextId = 0;
            }
            else
            {
                objectId.nextId = objectId.nextId+INCREMENT_AMOUNT;
                ObjectId.index(objectId);
                nextId = objectId.nextId;
            }

            numberOfRemainingIds = INCREMENT_AMOUNT;
        }
        numberOfRemainingIds--;
        return nextId++;
    }
}

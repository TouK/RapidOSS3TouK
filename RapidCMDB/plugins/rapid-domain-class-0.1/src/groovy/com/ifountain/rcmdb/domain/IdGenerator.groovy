package com.ifountain.rcmdb.domain

import application.ObjectId
import com.ifountain.rcmdb.domain.method.CompassMethodInvoker

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 13, 2008
* Time: 5:09:50 PM
* To change this template use File | Settings | File Templates.
*/
class IdGenerator {
    private static IdGenerator idGenerator;
    IdGeneratorStrategy strategy;
    private IdGenerator(IdGeneratorStrategy strategy)
    {
        this.strategy = strategy
    }

    public static void initialize(IdGeneratorStrategy strategy)
    {
        if(!idGenerator)
        {
            idGenerator = new IdGenerator(strategy);
        }
    }
    public static void destroy()
    {
        idGenerator = null;
    }
    public static IdGenerator getInstance()
    {
        return idGenerator;
    }
    public synchronized getNextId()
    {
        return strategy.getNextId();   
    }
}

interface IdGeneratorStrategy
{
    public long getNextId();
}

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

class MockIdGeneratorStrategy implements IdGeneratorStrategy
{
    private long nextId = 0;
    public long getNextId() {
        return nextId++; //To change body of implemented methods use File | Settings | File Templates.
    }

}
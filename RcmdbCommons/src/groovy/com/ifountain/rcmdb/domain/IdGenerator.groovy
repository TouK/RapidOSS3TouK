package com.ifountain.rcmdb.domain

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

class MockIdGeneratorStrategy implements IdGeneratorStrategy
{
    private long nextId = 0;
    public long getNextId() {
        return nextId++; //To change body of implemented methods use File | Settings | File Templates.
    }

}
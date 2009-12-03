package com.ifountain.rcmdb.domain

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import application.ObjectId

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Dec 3, 2009
* Time: 11:35:46 AM
* To change this template use File | Settings | File Templates.
*/
class IdGeneratorStrategyImplTest extends RapidCmdbWithCompassTestCase {

    public void setUp() {
        super.setUp();
        initialize([ObjectId], []);
    }

    public void tearDown() {
        super.tearDown();

    }

    public void testGetNextIdWithNoObjectIdBefore()
    {
        IdGenerator.destroy();
        IdGenerator.initialize (new IdGeneratorStrategyImpl(0));

        assertEquals(0,ObjectId.count());

        def nextId=IdGenerator.getInstance().getNextId();
        assertEquals(1,nextId);

        def objectIds=ObjectId.list();
        assertEquals(1,objectIds.size());

        assertEquals(0,objectIds[0].id);
        assertEquals("generalObjectId",objectIds[0].name);
        assertEquals(1000,objectIds[0].nextId);

        def nextId2=IdGenerator.getInstance().getNextId();
        assertEquals(2,nextId2);

        objectIds=ObjectId.list();
        assertEquals(1,objectIds.size());

        assertEquals(0,objectIds[0].id);
        assertEquals("generalObjectId",objectIds[0].name);
        assertEquals(1000,objectIds[0].nextId);
    }
    public void testGetNextIdWithObjectIdBefore()
    {
        IdGenerator.destroy();
        IdGenerator.initialize (new IdGeneratorStrategyImpl(0));

        def nextId=IdGenerator.getInstance().getNextId();
        assertEquals(1,nextId);

        def objectId=ObjectId.get(id:0);
        assertEquals(1000,objectId.nextId);

        //now objectId is updated and IdGenerator is initialized again
        objectId.update(nextId:5000);

        IdGenerator.destroy();
        IdGenerator.initialize (new IdGeneratorStrategyImpl(0));

        nextId=IdGenerator.getInstance().getNextId();
        assertEquals(5001,nextId);
    }
    public void testGetNextIdWithStartId()
    {
        IdGenerator.destroy();
        IdGenerator.initialize (new IdGeneratorStrategyImpl(3000));

        def nextId=IdGenerator.getInstance().getNextId();
        assertEquals(3001,nextId);

        def objectIds=ObjectId.list();
        assertEquals(1,objectIds.size());

        assertEquals(0,objectIds[0].id);
        assertEquals("generalObjectId",objectIds[0].name);
        assertEquals(4000,objectIds[0].nextId);

        1000.times{
            assertEquals(3002+it,IdGenerator.getInstance().getNextId());
        }

        objectIds=ObjectId.list();
        assertEquals(1,objectIds.size());

        assertEquals(0,objectIds[0].id);
        assertEquals("generalObjectId",objectIds[0].name);
        assertEquals(5000,objectIds[0].nextId);

    }

    public void testGetNextIdWithStartIdAsString()
    {
        IdGenerator.destroy();
        IdGenerator.initialize (new IdGeneratorStrategyImpl("4000"));


        def nextId=IdGenerator.getInstance().getNextId();
        assertEquals(4001,nextId);
        

        ObjectId.removeAll();
        IdGenerator.destroy();
        IdGenerator.initialize (new IdGeneratorStrategyImpl(null));

        nextId=IdGenerator.getInstance().getNextId();
        assertEquals(1,nextId);


        ObjectId.removeAll();
        IdGenerator.destroy();
        IdGenerator.initialize (new IdGeneratorStrategyImpl("dededed"));

        nextId=IdGenerator.getInstance().getNextId();
        assertEquals(1,nextId);
    }

    public void testGetNextIdWithIncrementCountExceeded()
    {
        IdGenerator.destroy();
        IdGenerator.initialize (new IdGeneratorStrategyImpl(0));

        assertEquals(0,ObjectId.count());
        
        1000.times{
            def nextId=IdGenerator.getInstance().getNextId();
            print nextId+",";
            assertEquals(1+it,nextId);
        }
        println ""

        def objectId=ObjectId.get(id:0);
        assertEquals(1000,objectId.nextId);

        
        1000.times{
            def nextId=IdGenerator.getInstance().getNextId();
            print nextId+",";
            assertEquals(1001+it,nextId);
        }
        println ""
        objectId=ObjectId.get(id:0);
        assertEquals(2000,objectId.nextId);


        1000.times{
            def nextId=IdGenerator.getInstance().getNextId();
            print nextId+",";
            assertEquals(2001+it,nextId);
        }
        println ""

        objectId=ObjectId.get(id:0);
        assertEquals(3000,objectId.nextId);
    }

}
package com.ifountain.rcmdb.util

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: May 14, 2009
* Time: 12:04:57 PM
* To change this template use File | Settings | File Templates.
*/
class CollectionUtilsTest extends RapidCmdbTestCase{
    public void testExecuteForEachBatch()
    {
        List elements = [];
        for(int i=0; i < 100; i++)
        {
            elements.add("el"+i);
        }
        checkWithSpecifiedBatchSize(elements, 10);
        checkWithSpecifiedBatchSize(elements, 5);
        checkWithSpecifiedBatchSize(elements, 80);
        checkWithSpecifiedBatchSize(elements, 1100);
    }

    private void checkWithSpecifiedBatchSize(List elements, int batchSize)
    {
        def currentPositon = 0;
        CollectionUtils.executeForEachBatch (elements, batchSize){List elsToBeProcessed->
            assertTrue(elsToBeProcessed.size() <= batchSize);
            for(int i=0; i < elsToBeProcessed.size(); i++)
            {
                assertEquals (elements[currentPositon], elsToBeProcessed[i]);
                currentPositon++;
            }
        }
        assertEquals(elements.size(), currentPositon);    
    }
}
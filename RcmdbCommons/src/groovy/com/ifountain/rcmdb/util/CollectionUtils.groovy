package com.ifountain.rcmdb.util
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: May 14, 2009
 * Time: 11:57:44 AM
 * To change this template use File | Settings | File Templates.
 */
class CollectionUtils {
    public static executeForEachBatch(List elements, int numberOfElementsInABatch, Closure closureToBeExecute)
    {
        def listSize = elements.size();
        for(int i=0; i < listSize;)
        {
            def newI = Math.min(i + numberOfElementsInABatch, listSize);
            closureToBeExecute(elements.subList (i, newI));
            i = newI;
        }
    }
}
package com.ifountain.rcmdb.domain.batch
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Dec 7, 2009
 * Time: 4:40:01 PM
 */
interface BatchExecutionContext {
   public void batchStarted();
   public void batchFinished();
}
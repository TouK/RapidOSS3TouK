package com.ifountain.comp.utils;

import junit.framework.AssertionFailedError;

import com.ifountain.comp.test.util.WaitAction;

public class SmartWait {
    public static void waitFor(WaitAction waitAction) throws InterruptedException{
        waitFor(waitAction, 100);
    }
    
    public static void waitFor(WaitAction waitAction, int maxNumberOfIterations) throws InterruptedException {
        //I decreased the sleep amount, and increased the iteration count, so nothing changed.
        long sleepAmount = 10;
        Throwable lastError = null;
        for (int i = 0; i < maxNumberOfIterations * 10; i++) {
            try
            {
                waitAction.check();
                return;
                
            }
            catch(AssertionFailedError e)
            {
                lastError = e;
            }
            catch(Throwable t)
            {
                lastError = t;
            }
            Thread.sleep(sleepAmount);
        }
        if(lastError != null)
        {
            if(lastError instanceof AssertionFailedError)
            {
                throw (AssertionFailedError)lastError;
            }
            else
            {
                throw new RuntimeException(lastError);
            }
        }
        throw new RuntimeException("Wait for action failed :(");
    }
}

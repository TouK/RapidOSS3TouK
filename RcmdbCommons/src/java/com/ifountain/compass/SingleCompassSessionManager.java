package com.ifountain.compass;

import org.compass.core.Compass;
import org.compass.core.CompassTransaction;
import org.compass.core.CompassSession;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 25, 2008
 * Time: 5:48:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class SingleCompassSessionManager {
    private static String lastOpenedSession;
    private static Compass compassInstance;
    private static int maxNumberOfTrs;
    private static long sessionCheckingInterval;
    private static int numberOfExecutedTrs;
    private static CompassSession compassSessionInstance;
    private static CompassTransaction compassTransactionInstance;
    private static Map uncommittedTransactions = Collections.synchronizedMap(new HashMap());
    private static Timer timer = new Timer();
    private static boolean timeout = false;
    private static boolean isDestroyed = true;
    private static Object sessionLock = new Object();
    private static Object beginTrLock = new Object();
    private static Object sessionCapacityIsFullLock = new Object();

    static TransactionListener listenerInstance = new TransactionListener()
    {
        public void transactionStarted(RapidCompassTransaction tr) {
            if(isBatchInsertMode())
            {
                synchronized (sessionLock)
                {
                    uncommittedTransactions.put(tr.id, tr);
                    numberOfExecutedTrs++;
                }
            }
        }

        public void transactionCommitted(RapidCompassTransaction tr)
        {
            if(isBatchInsertMode())
            {
                synchronized (sessionLock)
                {
                    uncommittedTransactions.remove(tr.id);
                    if(isDestroyed)
                    {
                        SingleCompassSessionManager.closeSession(tr.getSession());
                    }
                }
            }
            else
            {
                tr.transaction.commit();
                tr.transaction.getSession().close();
            }

        }

        public void transactionRolledback(RapidCompassTransaction tr)
        {
            if(isBatchInsertMode())
            {
                synchronized (sessionLock)
                {
                    uncommittedTransactions.remove(tr.id);
                    if(isDestroyed)
                    {
                        SingleCompassSessionManager.closeSession(tr.getSession());
                    }
                }
            }
            else
            {
                tr.transaction.rollback();
                tr.transaction.getSession().close();
            }

        }
    };

    private static boolean isBatchInsertMode()
    {
        return maxNumberOfTrs > 0;
    }
    public static void initialize(Compass compass)
    {
        SingleCompassSessionManager.initialize(compass, 0, 0);
    }
    public static void initialize(Compass compass, int maxNumberOfTrs)
    {
        SingleCompassSessionManager.initialize(compass, maxNumberOfTrs, 10);
    }
    public static void initialize(Compass compass, int maxNumberOfTrs, long sessionCheckingInterval)
    {
        if(sessionCheckingInterval <= 0) sessionCheckingInterval = 10;
        isDestroyed = false;
        numberOfExecutedTrs = 0;
        uncommittedTransactions.clear();
        SingleCompassSessionManager.maxNumberOfTrs = maxNumberOfTrs;
        SingleCompassSessionManager.sessionCheckingInterval = sessionCheckingInterval;
        compassInstance = compass;
        if(isBatchInsertMode()){
            synchronized (sessionLock)
            {
                lastOpenedSession = new Date().toString();
                compassSessionInstance = compassInstance.openSession();
                compassTransactionInstance = compassSessionInstance.beginTransaction();
            }
            startTimer();
        }
    }

    private static void startTimer()
    {
        if(sessionCheckingInterval <= 0) return;
        timer = new Timer();
        TimerTask closeTask = new TimerTask()
        {
            public void run() {
                synchronized (sessionLock)
                {
                    if(!isDestroyed)
                    {
                        timeout = true;
                        SingleCompassSessionManager.closeSession(SingleCompassSessionManager.compassSessionInstance);
                        timeout = false;
                    }
                }
            }
        };
        timer.schedule(closeTask, sessionCheckingInterval, sessionCheckingInterval);
    }

    private static void closeSession(CompassSession session)
    {
        synchronized (sessionLock)
        {
            if(maxNumberOfTrs > 0 && numberOfExecutedTrs >= maxNumberOfTrs && uncommittedTransactions.isEmpty() || numberOfExecutedTrs > 0 && timeout && uncommittedTransactions.isEmpty() || isDestroyed && !timeout && uncommittedTransactions.isEmpty())
            {
                compassTransactionInstance.commit();
                session.close();                
                numberOfExecutedTrs = 0;
                synchronized (sessionCapacityIsFullLock)
                {
                    sessionCapacityIsFullLock.notifyAll();
                }
            }
        }
    }

    public static CompassTransaction beginTransaction()
    {
        if (isDestroyed) throw new UnInitializedSessionManagerException();
        synchronized (beginTrLock) {
            if (isBatchInsertMode() && numberOfExecutedTrs >= maxNumberOfTrs) {
                try {
                    synchronized (sessionCapacityIsFullLock) {
                        sessionCapacityIsFullLock.wait();
                    }
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            synchronized (sessionLock) {
                if (isBatchInsertMode()) {
                    if (compassSessionInstance.isClosed()) {
                        lastOpenedSession = new Date().toString();
                        compassSessionInstance = compassInstance.openSession();
                        compassTransactionInstance = compassSessionInstance.beginTransaction();
                    }
                    return new RapidCompassTransaction(compassTransactionInstance, listenerInstance);
                } else {
                    return new RapidCompassTransaction(compassInstance.openSession().beginTransaction(), listenerInstance);
                }
            }
        }

    }

    public static List getUncommittedTransactions()
    {
        return new ArrayList(uncommittedTransactions.values());        
    }

    public static void destroy()
    {
        destroy(15000);
    }
    public static void destroy(long maxWaitTime)
    {
        synchronized (sessionLock)
        {
            if(timer != null)
            {
                timer.purge();

            }
            isDestroyed = true;
            if(uncommittedTransactions.isEmpty())
            {
                SingleCompassSessionManager.forceCloseSession();                   
            }
        }
        long numberOfIterations = maxWaitTime/10;
        try
        {
            for(int i=0; i < numberOfIterations; i++)
            {
                if(!SingleCompassSessionManager.isClosedLastSession())
                {
                    Thread.sleep (10);
                }
                else
                {
                    break;
                }
            }
        }
        catch(InterruptedException ex)
        {
        }
        if(!SingleCompassSessionManager.isClosedLastSession())
        {
            SingleCompassSessionManager.forceCloseSession();
        }
    }

    public static void forceCloseSession()
    {
        synchronized (sessionLock)
        {
            if(compassSessionInstance != null && !compassSessionInstance.isClosed())
            {
                compassTransactionInstance.commit();
                compassSessionInstance.close();       
            }
        }
    }
    public static boolean isClosedLastSession()
    {
        synchronized (sessionLock)
        {

            return compassSessionInstance == null || compassSessionInstance.isClosed();
        }
    }
}
